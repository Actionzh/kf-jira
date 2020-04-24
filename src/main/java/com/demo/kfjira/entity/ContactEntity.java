package com.demo.kfjira.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ContactEntity implements Serializable {
    private static final long serialVersionUID = 2638015178674713382L;

    private Long id;
    private Long version;
    private String anonymousId;
    private String avatar;
    private String city;
    private String comments;
    private String company;
    private String country;
    private String dateCreated;
    private String dateOfBirthday;
    private String email;
    private String gender;
    private String homePhone;
    private String industry;
    private String lastUpdated;
    private String mobilePhone;
    private String name;
    private String postalCode;
    private String state;
    private String street;
    private Long tenantId;
    private String title;
    private String userName;
    private String website;
    private String nickname;
    private String department;
    private Long mergedId;
    private Boolean isAnonymous;
    private String idCard;

}