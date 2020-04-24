package com.demo.kfjira.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class UserInfo extends BaseRowModel {

    @ExcelProperty(index = 0)
    private String num;

    @ExcelProperty(index = 1)
    private String nickName;

    @ExcelProperty(index = 2)
    private String gender;

    @ExcelProperty(index = 3)
    private String phone;

    @ExcelProperty(index = 4)
    private String sheng;

    @ExcelProperty(index = 5)
    private String city;

    @ExcelProperty(index = 6)
    private String attentionStatus;

    @ExcelProperty(index = 7)
    private String bindStatus;


    @ExcelProperty(index = 8)
    private String attentionTime;

    @ExcelProperty(index = 9)
    private String messageTimes;

    @ExcelProperty(index = 10)
    private String tag;

    @ExcelProperty(index = 11)
    private String openId;


    @ExcelProperty(index = 12)
    private String bindTime;

    @ExcelProperty(index = 13)
    private String unAttentionTime;


    @ExcelProperty(index = 14)
    private String unionId;

}