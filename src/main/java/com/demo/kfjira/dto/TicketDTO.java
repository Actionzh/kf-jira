package com.demo.kfjira.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class TicketDTO {

    private String title;
    private String name;
}
