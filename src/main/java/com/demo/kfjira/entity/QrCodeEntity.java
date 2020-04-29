package com.demo.kfjira.entity;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

@Data
@Builder
public class QrCodeEntity {

    private long id;

    private long version;


    private long tenantId;

    private String uuid;

    private String term;

    private String url;

    private String ticket;

    private String source;

    private String campaign;

    private String medium;

    private String content;

    private String wechatAccount;
    private DateTime dateCreated;

    private DateTime lastUpdated;


}