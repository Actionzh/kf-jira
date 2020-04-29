package com.demo.kfjira.listener.other;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.cache.RedisCacheManager;
import com.demo.kfjira.entity.*;
import com.demo.kfjira.mapper.ContactContentMapper;
import com.demo.kfjira.mapper.ContactIdentityMapper;
import com.demo.kfjira.mapper.ContactMapper;
import com.demo.kfjira.mapper.EventMapper;
import com.demo.kfjira.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

@Slf4j
public class InsertContactListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1500;
    private static final int THREAD_NUM = 10;

    private List<UserInfo> datas = new ArrayList<>();

    private ContactMapper contactMapper;
    private TransactionTemplate transactionTemplate;
    private ContactIdentityMapper contactIdentityMapper;
    private ExecutorService executor;
    private ContactContentMapper contactContentMapper;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private Vector<ContactContentEntity> contactContentEntities = new Vector<>();
    private Vector<ContactIdentityEntity> contactIdentityEntities = new Vector<>();

    private Vector<ContactEntity> contactEntities = new Vector<>();
    private Vector<ContactEntity> updateContactList = new Vector<>();

    private Vector<EventEntity> eventEntities = new Vector<>();
    private EventMapper eventMapper;
    private RedisCacheManager redisCacheManager;


    public InsertContactListener(RedisCacheManager redisCacheManager,
                                 ExecutorService executor, EventMapper eventMapper, ContactContentMapper contactContentMapper, ContactMapper contactMapper, ContactIdentityMapper contactIdentityMapper, TransactionTemplate transactionTemplate) {
        this.executor = executor;
        this.redisCacheManager = redisCacheManager;
        this.eventMapper = eventMapper;
        this.contactContentMapper = contactContentMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        //System.out.println("当前行：" + context.readRowHolder().getRowIndex());
        // System.out.println(userInfo);

        datas.add(userInfo);

        if (datas.size() >= BATCH_COUNT) {
            long l = System.currentTimeMillis();
            log.info("当前行：" + context.readRowHolder().getRowIndex());

            datas.forEach(data -> {
                /*long start = System.currentTimeMillis();
                ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(data.getOpenId(), 417, 1394);
                log.info("selectByOpenId time:" + (System.currentTimeMillis() - start));

                if (contactIdentityEntity != null && contactIdentityEntity.getContactId() != null) {
                    redisCacheManager.put("contactId:" + data.getOpenId(), contactIdentityEntity.getContactId());
                    if (data.getPhone() != null) {
                        ContactEntity contactEntity = contactMapper.selectById(417L, contactIdentityEntity.getContactId());
                        if (contactEntity != null) {
                            contactEntity.setMobilePhone(data.getPhone());
                            updateContactList.add(contactEntity);
                        }
                    }
                } else {*/
                ContactEntity contactEntity = convertToContactEntity(data);
                contactEntities.add(contactEntity);
                //}

            });
            log.info("getopenid time:" + (System.currentTimeMillis() - l));
            datas.clear();
        }

        if (contactEntities.size() >= BATCH_COUNT) {
            saveContactEntityByMyBatis();
            contactEntities.forEach(contactEntity -> {
                redisCacheManager.put("contactId:" + contactEntity.getAnonymousId(), contactEntity.getId());
            });
            contactEntities.clear();
        }

        if (updateContactList.size() >= BATCH_COUNT) {
            updateContactEntityByMyBatis();
            updateContactList.clear();
        }
    }


    private ContactEntity convertToContactEntity(UserInfo userInfo) {
        ContactEntity contactEntity = ContactEntity.builder()
                .version(0L)
                .anonymousId(userInfo.getOpenId())
                .city(userInfo.getCity())
                .dateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                .lastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))

                .mobilePhone(userInfo.getPhone())
                .name(userInfo.getNickName())
                .state(userInfo.getSheng())
                .tenantId(417L)
                .nickname(userInfo.getNickName()).build();

        if (Objects.isNull(userInfo.getPhone()) && Objects.isNull(userInfo.getNickName())) {
            contactEntity.setIsAnonymous(true);
        }
        if ("男".equals(userInfo.getGender())) {
            contactEntity.setGender("male");
        } else if ("女".equals(userInfo.getGender())) {
            contactEntity.setGender("female");
        } else {
            contactEntity.setGender("unknown");
        }
        return contactEntity;
    }


    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveContactContentByMyBatis() {
        contactContentMapper.insertBatch(contactContentEntities);
    }

    private void saveContactIdentityEntityByMyBatis() {
        contactIdentityMapper.updateBatch(contactIdentityEntities);
    }

    private void saveContactEntityByMyBatis() {
        contactMapper.insertBatch(contactEntities);
    }

    private void updateContactEntityByMyBatis() {
        contactMapper.updateBatch(updateContactList);
    }


    private void saveEventEntityByMyBatis() {
        eventMapper.insertBatch(eventEntities);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        datas.forEach(data -> {
            ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(data.getOpenId(), 417, 1394);
            if (contactIdentityEntity != null && contactIdentityEntity.getContactId() != null) {
                redisCacheManager.put("contactId:" + data.getOpenId(), contactIdentityEntity.getContactId());
                if (data.getPhone() != null) {
                    ContactEntity contactEntity = contactMapper.selectById(417L, contactIdentityEntity.getContactId());
                    updateContactList.add(contactEntity);
                }
            } else {
                ContactEntity contactEntity = convertToContactEntity(data);
                contactEntities.add(contactEntity);
            }
        });
        datas.clear();
        saveContactEntityByMyBatis();
        contactEntities.clear();
        updateContactEntityByMyBatis();
        updateContactList.clear();
    }


}
