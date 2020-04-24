package com.demo.kfjira.controller;

import com.demo.kfjira.service.LoadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("load")
public class LoadData {


    @Autowired
    private LoadDataService loadDataService;

    @GetMapping("tag")
    public void create() {
        loadDataService.loadTag();
    }

    @GetMapping("user")
    public void createUser() {
        loadDataService.loadUserInfo();
    }

}
