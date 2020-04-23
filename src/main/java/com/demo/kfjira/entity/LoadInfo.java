package com.demo.kfjira.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class LoadInfo extends BaseRowModel {

    @ExcelProperty(index = 0)
    private String name;

    @ExcelProperty(index = 1)
    private String scene;

    @ExcelProperty(index = 2)
    private String qrcode_img_url;

    @ExcelProperty(index = 3)
    private String qrcode_url;

    @ExcelProperty(index = 4)
    private String ticket;

    @ExcelProperty(index = 5, format = "yyyy/MM/dd")
    private String created_at;

    @ExcelProperty(index = 6)
    private String channel;

    @ExcelProperty(index = 7)
    private String segmentation;


    @ExcelProperty(index = 8)
    private String effect_name;

    @ExcelProperty(index = 9)
    private String effect;

}