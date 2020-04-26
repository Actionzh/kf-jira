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
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ContactGroupMemberListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1000;

    private ContactMapper contactMapper;
    private ContactIdentityMapper contactIdentityMapper;
    private static ExecutorService executor = null;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private List<ContactGroupMemberEntity> contactGroupMemberEntities = new ArrayList<>();
    private ContactGroupMemberMapper contactGroupMemberMapper;
    private TagMapper tagMapper;

    @PostConstruct
    public void init() {
        ThreadFactory factory = (new ThreadFactoryBuilder())
                .setNameFormat("identity-svc-%d")
                .build();
        executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    public ContactGroupMemberListener(TagMapper tagMapper, ContactGroupMemberMapper contactGroupMemberMapper,
                                      ContactMapper contactMapper, ContactIdentityMapper contactIdentityMapper) {
        this.tagMapper = tagMapper;
        this.contactGroupMemberMapper = contactGroupMemberMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(userInfo);
        if (userInfo.getTag() != null) {
            ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(userInfo.getOpenId(), 417, 1394);
            if (contactIdentityEntity != null && contactIdentityEntity.getContactId() != null) {
                convertToContactGroupMemberMapper(contactIdentityEntity.getContactId(), userInfo);
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
