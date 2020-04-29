package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.*;
import com.demo.kfjira.mapper.*;
import com.demo.kfjira.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
public class QrcodeEventListener extends AnalysisEventListener<ScanInfo> {

    private static final int BATCH_COUNT = 1500;
    private static final int THREAD_NUM = 15;

    private ContactMapper contactMapper;
    private ContactIdentityMapper contactIdentityMapper;
    private ExecutorService executor;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private List<ScanInfo> scanInfos = new ArrayList<>();
    private EventMapper eventMapper;
    private EventUtmMapper eventUtmMapper;
    private QrCodeMapper qrCodeMapper;

    public QrcodeEventListener(ExecutorService executor,
                               QrCodeMapper qrCodeMapper,
                               EventMapper eventMapper,
                               EventUtmMapper eventUtmMapper,
                               ContactMapper contactMapper,
                               ContactIdentityMapper contactIdentityMapper) {
        this.executor = executor;
        this.qrCodeMapper = qrCodeMapper;
        this.eventMapper = eventMapper;
        this.eventUtmMapper = eventUtmMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
    }

    @Override
    public void invoke(ScanInfo scanInfo, AnalysisContext context) {
        //System.out.println("当前行：" + context.readRowHolder().getRowIndex());
        //System.out.println(scanInfo);

        scanInfos.add(scanInfo);

        if (scanInfos.size() >= BATCH_COUNT) {
            log.info("enter batch exec:" + context.readRowHolder().getRowIndex());
            long Start = System.currentTimeMillis();
            CountDownLatch cd = new CountDownLatch(THREAD_NUM);
            for (int i = 1; i <= THREAD_NUM; i++) {
                List<ScanInfo> subScanInfos = scanInfos.subList(BATCH_COUNT / THREAD_NUM * (i - 1), BATCH_COUNT / THREAD_NUM * i);

                executor.submit(() -> {
                    ListIterator<ScanInfo> scanInfoListIterator = subScanInfos.listIterator();

                    while (scanInfoListIterator.hasNext()) {
                        ScanInfo next = scanInfoListIterator.next();
                        if (next.getOpenId() != null && next.getScene() != null) {
                            ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(next.getOpenId(), 417, 1394);
                            if (contactIdentityEntity != null) {
                                createEvent(contactIdentityEntity.getId(), next);
                            }
                        }
                    }
                    cd.countDown();
                });
            }
            try {
                cd.await();
                scanInfos.clear();
                System.out.println("times=====" + (System.currentTimeMillis() - Start));
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void createEvent(Long contactIdentityId, ScanInfo userInfo) {
        QrCodeEntity qrCodeEntity = qrCodeMapper.selectByUUID(userInfo.getScene(), 417L);
        if (qrCodeEntity != null) {
            EventEntity event = EventEntity.builder()
                    .tenantId(417L)
                    .version(0L)
                    .contactIdentityId(contactIdentityId)
                    .anonymousId(userInfo.getOpenId())
                    .channelId("1394")
                    .channelName("LACOSTE")
                    .event("WECHAT__SCAN_QRCODE")
                    .attr6("ORDINARY")
                    .attr9(String.valueOf(qrCodeEntity.getId())).build();
            if (userInfo.getCreatedAt() != null) {
                event.setDateCreated(DateTime.parse(userInfo.getCreatedAt(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
                event.setLastUpdated(DateTime.parse(userInfo.getCreatedAt(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));

                event.setEventDate(DateTime.parse(userInfo.getCreatedAt(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                event.setDateCreated(DateTime.now());
                event.setLastUpdated(DateTime.now());
                event.setEventDate(DateTime.now());
            }

            eventMapper.insertOne(event);
            EventUtmEntity eventUtmEntity = EventUtmEntity.builder()
                    .id(event.getId())
                    .version(0L)
                    .source(qrCodeEntity.getSource())
                    .campaign(qrCodeEntity.getCampaign())
                    .medium(qrCodeEntity.getMedium())
                    .content(qrCodeEntity.getContent())
                    .term(qrCodeEntity.getTerm())
                    .tenantId(417L)
                    .dateCreated(event.getDateCreated())
                    .lastUpdated(event.getLastUpdated()).build();
            eventUtmMapper.insertOne(eventUtmEntity);
        }
    }

    /*
    private void saveContactGroupMemberByMyBatis() {
    }*/
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
