package com.demo.kfjira.entity;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class ContactIdentityEntity implements Serializable {

    private static final long serialVersionUID = 2705274427465084519L;
    private Long id;

    private Long contactId;
    private String mobilePhone;
    private String email;

    private Long channelId;

    private String anonymousId;

    private String nickname;

    private Boolean isActive = true;

    private String externalId;

    private Long originalId;

    private Long version;

    private String dateCreated;

    private String lastUpdated;

    private Long tenantId;
}
