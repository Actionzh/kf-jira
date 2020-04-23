package com.demo.kfjira.entity;

import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QrCodeEntity extends BaseRowModel {


    private long version;

    private String dateCreated;

    private String lastUpdated;

    private long tenantId;

    private String uuid;

    private String term;

    private String url;

    private String ticket;

    private String source;

    private String campaign;

    private String wechatAccount;

}