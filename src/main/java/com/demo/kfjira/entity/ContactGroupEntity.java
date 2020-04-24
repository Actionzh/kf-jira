package com.demo.kfjira.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author edz
 */
@Data
@Builder
public class ContactGroupEntity implements Serializable {
    private static final long serialVersionUID = -5855955557187888308L;

    private long id;

    private long version;

    private String dateCreated;

    private String lastUpdated;

    private String name;

    private String status;

    private long tenantId;

    private String total;

    private String groupType;

    private String ext1;

    private String ext2;
}