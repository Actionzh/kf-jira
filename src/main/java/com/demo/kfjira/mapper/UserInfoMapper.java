package com.demo.kfjira.mapper;


import com.demo.kfjira.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {


    void insertBatch(@Param("datas") List<UserInfo> datas);

}
