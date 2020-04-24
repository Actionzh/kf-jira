package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.ContactEntity;
import com.demo.kfjira.entity.ContactIdentityEntity;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.mapper.ContactIdentityMapper;
import com.demo.kfjira.mapper.ContactMapper;
import com.demo.kfjira.mapper.UserInfoMapper;
import org.joda.time.DateTime;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInfoListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1000;
    private List<UserInfo> datas = new ArrayList<>();

    private ContactMapper contactMapper;
    protected TransactionTemplate transactionTemplate;
    private ContactIdentityMapper contactIdentityMapper;

    private UserInfoMapper userInfoMapper;

    public UserInfoListener(UserInfoMapper userInfoMapper, ContactMapper contactMapper, ContactIdentityMapper contactIdentityMapper, TransactionTemplate transactionTemplate) {
        this.userInfoMapper = userInfoMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(userInfo);
       /* ContactIdentityEntity contactIdentityEntity = contactIdentityMapper.selectByOpenId(userInfo.getOpenId(), 417, 1394);
        if (contactIdentityEntity != null) {
            updateExistContact(contactIdentityEntity, userInfo);
        } else {
            handleUnExistContact(userInfo);
        }*/
        datas.add(userInfo);
        if (datas.size() >= BATCH_COUNT) {
            saveExcelByMyBatis();
            // 存储完成清理 list
            datas.clear();
        }
    }

    private void handleUnExistContact(UserInfo userInfo) {
        transactionTemplate.execute((TransactionStatus status) -> {
            ContactEntity contactEntity = convertToContactEntity(userInfo);
            contactMapper.insertOne(contactEntity);
            ContactIdentityEntity contactIdentityEntity = convertToContactIdentityEntity(contactEntity.getId(), userInfo);
            contactIdentityMapper.insertOne(contactIdentityEntity);
            return null;
        });
    }

    private void updateExistContact(ContactIdentityEntity contactIdentityEntity, UserInfo userInfo) {
        transactionTemplate.execute((TransactionStatus status) -> {
            contactIdentityEntity.setNickname(userInfo.getNickName());
            contactIdentityEntity.setAnonymousId(userInfo.getOpenId());
            contactIdentityEntity.setExternalId(userInfo.getUnionId());
            contactIdentityMapper.update(contactIdentityEntity);
            if (contactIdentityEntity.getContactId() != null) {
                contactMapper.updatePhoneById(contactIdentityEntity.getContactId(), userInfo.getPhone());
            }
            return null;
        });
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
    private void saveExcelByMyBatis() {
        userInfoMapper.insertBatch(datas);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveExcelByMyBatis();
        datas.clear();
    }
}
