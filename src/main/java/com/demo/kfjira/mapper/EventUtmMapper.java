package com.demo.kfjira.mapper;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EventUtmMapper {


    void insertBatch(@Param("datas") List<EventUtmMapper> datas);

    int insertOne(EventUtmMapper contactIdentityEntity);

}
