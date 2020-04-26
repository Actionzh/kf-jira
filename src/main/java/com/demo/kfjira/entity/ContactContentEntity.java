package com.demo.kfjira.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zh
 */
@Data
@Builder
public class ContactContentEntity implements Serializable {

    private static final long serialVersionUID = -2929247319871102986L;

    private Long id;

    private String property;

    private Long version;

    private String lastUpdated;

    private String dateCreated;

    private Long tenantId;

}