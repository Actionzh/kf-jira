package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactEntity;
import com.demo.kfjira.entity.ContactGroupMemberEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactGroupMemberMapper {


    void insertBatch(@Param("datas") List<ContactGroupMemberEntity> datas);

    int insertOne(ContactEntity contactEntity);
}
