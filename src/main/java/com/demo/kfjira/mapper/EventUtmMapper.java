package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.EventUtmEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EventUtmMapper {


    void insertBatch(@Param("datas") List<EventUtmMapper> datas);

    int insertOne(EventUtmEntity eventUtmEntity);

}
