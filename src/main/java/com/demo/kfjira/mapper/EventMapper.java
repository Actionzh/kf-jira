package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.EventEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EventMapper {


    void insertBatch(@Param("datas") List<EventEntity> datas);

    int insertOne(EventEntity contactIdentityEntity);

}
