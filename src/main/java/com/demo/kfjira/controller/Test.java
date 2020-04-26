package com.demo.kfjira.controller;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class Test {

    public static void main(String[] args) {
        ;
        DateTime parse = DateTime.parse("2017-08-23 14:40:46", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(parse);

    }
}
