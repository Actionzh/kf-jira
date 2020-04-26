package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactContentEntity;
import com.demo.kfjira.entity.ContactIdentityEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactContentMapper {


    void insertBatch(@Param("datas") List<ContactContentEntity> datas);

    int insertOne(ContactIdentityEntity contactIdentityEntity);

    ContactIdentityEntity selectByOpenId(@Param("openId") String openId,
                                         @Param("tenantId") long tenantId,
                                         @Param("channelId") long channelId);

    ContactIdentityEntity update(ContactIdentityEntity contactIdentityEntity);
}
