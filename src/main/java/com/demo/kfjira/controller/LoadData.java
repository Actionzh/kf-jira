package com.demo.kfjira.controller;

import com.demo.kfjira.service.LoadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

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
    public void createUser() throws FileNotFoundException {
        loadDataService.loadUserInfo();
    }

    @GetMapping("groupmember")
    public void createGroupMember() throws FileNotFoundException {
        loadDataService.createGroupMember();
    }

    @GetMapping("qrcodeevent")
    public void createQrcodeEvent() throws FileNotFoundException {
        loadDataService.createQrcodeEvent();
    }


    @GetMapping("qrcode")

    public void createQrcode() throws FileNotFoundException {
        loadDataService.createGroupMember();
    }

    @GetMapping("userdata")
    public void loadUserData() {
        loadDataService.LoadUserData();
    }

}
