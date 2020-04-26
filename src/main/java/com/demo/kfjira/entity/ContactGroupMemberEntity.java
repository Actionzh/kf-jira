package com.demo.kfjira.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroupMemberEntity implements Serializable {
    private static final long serialVersionUID = 2638015178674713382L;

    private Long id;
    private Long version;
    private Long contactGroupId;
    private Long contactId;
    private String dateCreated;
    private String lastUpdated;
    private Integer sequence;
    private Long tenantId;

}