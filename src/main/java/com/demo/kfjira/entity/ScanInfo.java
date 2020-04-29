package com.demo.kfjira.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class ScanInfo extends BaseRowModel {

    @ExcelProperty(index = 0)
    private String nickname;

    @ExcelProperty(index = 1)
    private String openId;

    @ExcelProperty(index = 2)
    private String name;

    @ExcelProperty(index = 3)
    private String createdAt;

    @ExcelProperty(index = 4)
    private String scene;
}