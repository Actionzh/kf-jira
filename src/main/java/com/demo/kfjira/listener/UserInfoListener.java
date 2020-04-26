package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.*;
import com.demo.kfjira.mapper.ContactContentMapper;
import com.demo.kfjira.mapper.ContactIdentityMapper;
import com.demo.kfjira.mapper.ContactMapper;
import com.demo.kfjira.mapper.EventMapper;
import com.demo.kfjira.utils.JsonMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

public class UserInfoListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1000;
    private List<UserInfo> datas = new ArrayList<>();

    private ContactMapper contactMapper;
    private TransactionTemplate transactionTemplate;
    private ContactIdentityMapper contactIdentityMapper;
    private static ExecutorService executor = null;
    private ContactContentMapper contactContentMapper;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private List<ContactContentEntity> contactContentEntities = new ArrayList<>();
    private List<ContactIdentityEntity> contactIdentityEntities = new ArrayList<>();
    private List<ContactEntity> contactEntities = new ArrayList<>();
    private List<EventEntity> eventEntities = new ArrayList<>();
    private EventMapper eventMapper;

    @PostConstruct
    public void init() {
        ThreadFactory factory = (new ThreadFactoryBuilder())
                .setNameFormat("identity-svc-%d")
                .build();
        executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    public UserInfoListener(EventMapper eventMapper, ContactContentMapper contactContentMapper, ContactMapper contactMapper, ContactIdentityMapper contactIdentityMapper, TransactionTemplate transactionTemplate) {
        this.eventMapper = eventMapper;
        this.contactContentMapper = contactContentMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(userInfo);
        ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(userInfo.getOpenId(), 417, 1394);
        Long contactId;
        if (contactIdentityEntity != null) {
            //系统中存在渠道账号
            contactId = updateExistContact(contactIdentityEntity, userInfo);
        } else {
            contactId = handleUnExistContact(userInfo);
        }

        ContactContentEntity contactContentEntity = convertToContactContentEntity(contactId, userInfo);
        contactContentEntities.add(contactContentEntity);

        //批量插入ContactContent
        if (contactContentEntities.size() >= BATCH_COUNT) {
            saveContactContentByMyBatis();
            contactContentEntities.clear();
        }

        //批量更新contactIdentity
        if (contactIdentityEntities.size() >= BATCH_COUNT) {
            saveContactIdentityEntityByMyBatis();
            contactIdentityEntities.clear();
        }
        //批量更新contact
        if (contactEntities.size() >= BATCH_COUNT) {
            saveContactEntityByMyBatis();
            contactEntities.clear();
        }

        //批量插入关注取关事件
        if (eventEntities.size() >= BATCH_COUNT) {
            saveEventEntityByMyBatis();
            eventEntities.clear();
        }
    }

    private Long handleUnExistContact(UserInfo userInfo) {
        return transactionTemplate.execute((TransactionStatus status) -> {
            ContactEntity contactEntity = convertToContactEntity(userInfo);
            contactMapper.insertOne(contactEntity);
            ContactIdentityEntity contactIdentityEntity = convertToContactIdentityEntity(contactEntity.getId(), userInfo);
            contactIdentityMapper.insertOne(contactIdentityEntity);
            if ("取消关注".equals(userInfo.getAttentionStatus())) {
                addAttentionEvent(userInfo, contactIdentityEntity.getId());
            }
            return contactEntity.getId();
        });
    }

    private void addAttentionEvent(UserInfo userInfo, Long contactIdentityEntityId) {
        //关注事件
        EventEntity build = EventEntity.builder()
                .version(0L)
                .anonymousId(userInfo.getOpenId())
                .channelId("1394")
                .channelName("LACOSTE")
                .contactIdentityId(contactIdentityEntityId)
                .event("WECHAT__SUBSCRIBE")
                .tenantId(417L).build();
        if (userInfo.getAttentionTime() != null) {
            build.setDateCreated(DateTime.parse(userInfo.getAttentionTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            build.setLastUpdated(DateTime.parse(userInfo.getAttentionTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            build.setDateCreated(DateTime.now());
            build.setLastUpdated(DateTime.now());
        }
        eventEntities.add(build);

        //取关事件
        EventEntity unBind = EventEntity.builder()
                .version(0L)
                .anonymousId(userInfo.getOpenId())
                .channelId("1394")
                .channelName("LACOSTE")
                .contactIdentityId(contactIdentityEntityId)
                .event("WECHAT__UNSUBSCRIBE")
                .tenantId(417L).build();
        if (userInfo.getUnAttentionTime() != null) {
            unBind.setDateCreated(DateTime.parse(userInfo.getUnAttentionTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            unBind.setLastUpdated(DateTime.parse(userInfo.getUnAttentionTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            unBind.setDateCreated(DateTime.now());
            unBind.setLastUpdated(DateTime.now());
        }
        eventEntities.add(unBind);
    }

    private Long updateExistContact(ContactIdentityEntity contactIdentityEntity, UserInfo userInfo) {
        contactIdentityEntity.setNickname(userInfo.getNickName());
        contactIdentityEntity.setAnonymousId(userInfo.getOpenId());
        contactIdentityEntity.setExternalId(userInfo.getUnionId());
        contactIdentityEntities.add(contactIdentityEntity);
        //contactIdentityMapper.update(contactIdentityEntity);
        if (contactIdentityEntity.getContactId() != null && userInfo.getPhone() != null) {
            //contactMapper.updatePhoneById(contactIdentityEntity.getContactId(), userInfo.getPhone());
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setMobilePhone(userInfo.getPhone());
            contactEntity.setId(contactIdentityEntity.getContactId());
            contactEntity.setTenantId(417L);
            contactEntity.setDateCreated(contactIdentityEntity.getDateCreated());
            contactEntities.add(contactEntity);
        }
        return contactIdentityEntity.getContactId();
    }

    private ContactContentEntity convertToContactContentEntity(Long contactId, UserInfo userInfo) {
        Map<String, Object> props = new HashMap<>(3);
        props.put("attr1", userInfo.getBindTime());
        props.put("attr2", userInfo.getBindStatus());
        props.put("attr3", userInfo.getMessageTimes());

        return ContactContentEntity.builder()
                .property(jsonMapper.toJson(props))
                .version(0L)
                .tenantId(417L)
                .dateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                .lastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                .id(contactId).build();
    }

    private ContactEntity convertToContactEntity(UserInfo userInfo) {
        ContactEntity contactEntity = ContactEntity.builder()
                .version(0L)
                .anonymousId(userInfo.getOpenId())
                .city(userInfo.getCity())
                .dateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                //性别
                .gender(userInfo.getGender())
                .lastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))

                .mobilePhone(userInfo.getPhone())
                .name(userInfo.getNickName())
                .state(userInfo.getSheng())
                .tenantId(417L)
                .nickname(userInfo.getNickName()).build();

        if (Objects.isNull(userInfo.getPhone()) && Objects.isNull(userInfo.getNickName())) {
            contactEntity.setIsAnonymous(true);
        }
        return contactEntity;
    }


    private ContactIdentityEntity convertToContactIdentityEntity(long contactId, UserInfo userInfo) {
        ContactIdentityEntity contactIdentityEntity = ContactIdentityEntity.builder()
                .anonymousId(userInfo.getOpenId())
                .contactId(contactId)
                .version(0L)
                .channelId(1394L)
                .mobilePhone(userInfo.getPhone())
                .tenantId(417L)
                .nickname(userInfo.getNickName())
                .externalId(userInfo.getUnionId()).build();

        if (userInfo.getAttentionTime() != null) {
            contactIdentityEntity.setDateCreated(userInfo.getAttentionTime());
            contactIdentityEntity.setLastUpdated(userInfo.getAttentionTime());
        } else {
            contactIdentityEntity.setDateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            contactIdentityEntity.setLastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }

        if ("取消关注".equals(userInfo.getAttentionStatus())
                || "未关注".equals(userInfo.getAttentionStatus())
                || "关注中".equals(userInfo.getAttentionStatus())) {
            contactIdentityEntity.setIsActive(false);
        } else {
            contactIdentityEntity.setIsActive(true);
        }

        return contactIdentityEntity;
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
        contactMapper.updateBatch(contactEntities);
    }

    private void saveEventEntityByMyBatis() {
        eventMapper.insertBatch(eventEntities);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveContactContentByMyBatis();
        saveContactIdentityEntityByMyBatis();
        saveContactEntityByMyBatis();
        saveEventEntityByMyBatis();
    }
}
