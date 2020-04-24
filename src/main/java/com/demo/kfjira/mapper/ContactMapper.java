package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactEntity;
import com.demo.kfjira.entity.QrCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactMapper {


    void insertBatch(@Param("datas") List<QrCodeEntity> datas);

    int insertOne(ContactEntity contactEntity);

    void updatePhoneById(@Param("id") Long id,
                         @Param("phone") String phone);

    ContactEntity SelectByOpenId(@Param("openId") String openId,
                                 @Param("tenantId") String tenantId,
                                 @Param("channelId") String channelId);
}
