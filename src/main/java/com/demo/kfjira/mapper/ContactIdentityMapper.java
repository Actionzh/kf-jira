package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactIdentityEntity;
import com.demo.kfjira.entity.QrCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactIdentityMapper {


    void insertBatch(@Param("datas") List<QrCodeEntity> datas);

    int insertOne(ContactIdentityEntity contactIdentityEntity);

    ContactIdentityEntity selectByOpenId(@Param("openId") String openId,
                                         @Param("tenantId") long tenantId,
                                         @Param("channelId") long channelId);

    ContactIdentityEntity update(ContactIdentityEntity contactIdentityEntity);
}
