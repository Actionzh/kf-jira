package com.demo.kfjira.controller;

import com.demo.kfjira.service.LoadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("load")
public class LoadData {


    @Autowired
    private LoadDataService loadDataService;

    /**
     * 插入标签
     */
    @GetMapping("tag")
    public void create() {
        loadDataService.loadTag();
    }

    /**
     * 插入用户
     *
     * @throws IOException
     */
    @GetMapping("user")
    public void createUser() throws IOException {
        loadDataService.loadUserInfo();
    }

    /**
     * 插入用户组
     *
     * @throws FileNotFoundException
     */
    @GetMapping("groupmember")
    public void createGroupMember() throws Exception {
        loadDataService.createGroupMember();
    }

    /**
     * 插入扫码事件
     *
     * @throws Exception
     */
    @GetMapping("qrcodeevent")
    public void createQrcodeEvent() throws Exception {
        loadDataService.createQrcodeEvent();
    }

    /**
     * 测试
     *
     * @throws Exception
     */
    @GetMapping("test")
    public void test() throws Exception {
        loadDataService.test();
    }

    /**
     * 测试
     *
     * @throws Exception
     */
    @GetMapping("testInsertIdentity")
    public void testInsertIdentity() throws Exception {
        loadDataService.testInsertIdentity();
    }

    /**
     * 插入qrcode
     *
     * @throws FileNotFoundException
     */
    @GetMapping("qrcode")
    public void createQrcode() throws FileNotFoundException {
        loadDataService.loadQrcode();
    }

    @GetMapping("userdata")
    public void loadUserData() {
        loadDataService.LoadUserData();
    }

}
