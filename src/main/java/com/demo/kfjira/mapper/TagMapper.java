package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.ContactGroupEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {


    void insertBatch(@Param("datas") List<ContactGroupEntity> datas);

    ContactGroupEntity selectByName(@Param("name") String name,
                                    @Param("tenantId") long tenantId);
}
