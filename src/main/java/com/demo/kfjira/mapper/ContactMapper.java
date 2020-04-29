package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactMapper {


    void updateBatch(@Param("datas") List<ContactEntity> datas);

    int insertOne(ContactEntity contactEntity);

    void insertBatch(@Param("datas") List<ContactEntity> datas);

    void updatePhoneById(@Param("id") Long id,
                         @Param("phone") String phone);

    ContactEntity selectById(@Param("tenantId") Long tenantId,
                             @Param("id") Long id);
}
