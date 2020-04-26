package com.demo.kfjira.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity implements Serializable {
    private static final long serialVersionUID = 7385654222199158043L;

    private Long id;
    private String anonymousId;
    private Long contactIdentityId;
    private Long tenantId;
    private Long version;

    private DateTime dateCreated;
    private DateTime lastUpdated;
    private DateTime eventDate;
    private String channelId;
    private String channelName;

    private String event;
    private String eventId;
    private String context;

    private String sdkVersion;
    private String sdkType;
    private String platform;
    private BigDecimal screenWidth;
    private BigDecimal screenHeight;
    private String appVersion;
    private String bundleKey;
    private String os;
    private String osVersion;
    private String browser;
    private String browserVersion;
    private String country;
    private String province;
    private String city;
    private String networkType;
    private String manufacturer;
    private String deviceModel;
    private String operator;
    private Boolean debugMode;
    private String imei;
    private String ip;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean triggerFlow;
    private Long channelAppId;

    private String attr1;
    private String attr2;
    private String attr3;
    private String attr4;
    private String attr5;
    private String attr6;
    private String attr7;
    private String attr8;
    private String attr9;
    private String attr10;
    private String attr11;
    private String attr12;
    private String attr13;
    private String attr14;
    private String attr15;
    private String attr16;
    private String attr17;
    private String attr18;
    private String attr19;
    private String attr20;
    private Long parentId;
}