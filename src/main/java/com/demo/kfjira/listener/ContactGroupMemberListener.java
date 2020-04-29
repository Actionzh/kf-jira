package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.ContactGroupEntity;
import com.demo.kfjira.entity.ContactGroupMemberEntity;
import com.demo.kfjira.entity.ContactIdentityEntity;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.mapper.ContactGroupMemberMapper;
import com.demo.kfjira.mapper.ContactIdentityMapper;
import com.demo.kfjira.mapper.ContactMapper;
import com.demo.kfjira.mapper.TagMapper;
import com.demo.kfjira.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ContactGroupMemberListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1500;
    private static final int THREAD_NUM = 15;

    private ContactMapper contactMapper;
    private ContactIdentityMapper contactIdentityMapper;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private Vector<ContactGroupMemberEntity> contactGroupMemberEntities = new Vector<>();
    private List<UserInfo> userInfos = new ArrayList<>();

    private ContactGroupMemberMapper contactGroupMemberMapper;
    private TagMapper tagMapper;
    private ExecutorService executor;

    public ContactGroupMemberListener(ExecutorService executor,
                                      TagMapper tagMapper,
                                      ContactGroupMemberMapper contactGroupMemberMapper,
                                      ContactMapper contactMapper,
                                      ContactIdentityMapper contactIdentityMapper) {
        this.executor = executor;
        this.tagMapper = tagMapper;
        this.contactGroupMemberMapper = contactGroupMemberMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        //System.out.println("当前行：" + context.currentReadHolder());
        //System.out.println(userInfo);
        userInfos.add(userInfo);

        if (userInfos.size() >= BATCH_COUNT) {
            log.info("enter batch exec:" + context.readRowHolder().getRowIndex());
            long Start = System.currentTimeMillis();
            CountDownLatch cd = new CountDownLatch(THREAD_NUM);
            for (int i = 1; i <= THREAD_NUM; i++) {
                List<UserInfo> subUserInfos = this.userInfos.subList(BATCH_COUNT / THREAD_NUM * (i - 1), BATCH_COUNT / THREAD_NUM * i);

                executor.submit(() -> {
                    ListIterator<UserInfo> subUserInfoListIterator = subUserInfos.listIterator();

                    while (subUserInfoListIterator.hasNext()) {
                        UserInfo next = subUserInfoListIterator.next();
                        if (next != null && next.getTag() != null) {
                            ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(next.getOpenId(), 417, 1394);
                            if (contactIdentityEntity != null && contactIdentityEntity.getContactId() != null) {
                                convertToContactGroupMemberMapper(contactIdentityEntity.getContactId(), next);
                            }
                        }
                    }
                    cd.countDown();
                });
            }
            try {
                cd.await();
                userInfos.clear();
                System.out.println("ContactGroupMemberListener times=====" + (System.currentTimeMillis() - Start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //批量插入ContactContent
        if (contactGroupMemberEntities.size() >= BATCH_COUNT) {
            saveContactGroupMemberByMyBatis();
            contactGroupMemberEntities.clear();
        }
    }

    private void convertToContactGroupMemberMapper(Long contactId, UserInfo userInfo) {
        String tag = userInfo.getTag();
        if (tag != null) {
            String[] split = tag.split("\\|");

            for (int i = 0; i < split.length; i++) {
                String tagName = split[i];
                ContactGroupEntity contactGroupEntity = tagMapper.selectByName(tagName, 417);
                if (contactGroupEntity != null) {
                    ContactGroupMemberEntity build = ContactGroupMemberEntity.builder()
                            .version(0L)
                            .contactId(contactId)
                            .contactGroupId(contactGroupEntity.getId())
                            .tenantId(417L)
                            .dateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                            .lastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                            .sequence(0)
                            .build();
                    contactGroupMemberEntities.add(build);
                }
            }
        }
    }

    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveContactGroupMemberByMyBatis() {
        contactGroupMemberMapper.insertBatch(contactGroupMemberEntities);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveContactGroupMemberByMyBatis();
        contactGroupMemberEntities.clear();
    }
}
