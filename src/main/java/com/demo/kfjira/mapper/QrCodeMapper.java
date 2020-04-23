package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.QrCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QrCodeMapper {


    void insertBatch(@Param("datas") List<QrCodeEntity> datas);
}
