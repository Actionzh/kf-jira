package com.demo.kfjira.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUtmEntity implements Serializable {
    private static final long serialVersionUID = 7385654222199158043L;


    private Long id;
    private String source;
    private String campaign;
    private String medium;
    private String content;

    private String term;
    private Long tenantId;
    private DateTime lastUpdated;
    private DateTime dateCreated;
    private Long version;

    private String semCampaign;
    private String semGroup;
    private String semAccount;

    private String semKeyword;


}