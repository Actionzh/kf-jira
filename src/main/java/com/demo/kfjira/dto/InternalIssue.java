package com.demo.kfjira.dto;

import lombok.Data;

@Data
public class InternalIssue {
    /**
     * g概要
     */
    String summary;

    /**
     * 描述
     */
    String description;
}
