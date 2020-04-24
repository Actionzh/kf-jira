package com.demo.kfjira.service;

import com.alibaba.excel.EasyExcel;
import com.demo.kfjira.entity.LoadInfo;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.listener.QrCodeListener;
import com.demo.kfjira.listener.TagInfoListener;
import com.demo.kfjira.listener.UserInfoListener;
import com.demo.kfjira.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Service
public class LoadDataService implements InitializingBean {

    @Autowired
    private QrCodeMapper qrCodeMapper;

    @Autowired
    private ContactIdentityMapper contactIdentityMapper;

    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    protected TransactionTemplate transactionTemplate;

    @Override
    public void afterPropertiesSet() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void loadQrcode() throws FileNotFoundException {
        long l = System.currentTimeMillis();

        FileInputStream inputStream = new FileInputStream("/Users/edz/Desktop/lacoste_qrcode 二维码配置.xlsx");
        try {
            // 解析每行结果在listener中处理
           /* ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS, null, listener);
            excelReader.read();*/

            EasyExcel.read(inputStream, LoadInfo.class, new QrCodeListener(qrCodeMapper, transactionTemplate)).sheet("QR code").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis() - l);
    }

    public void loadUserInfo() {
        long l = System.currentTimeMillis();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/edz/Desktop/用户列表_20200421164055 - for linkflow.xlsx");

            EasyExcel.read(inputStream, UserInfo.class, new UserInfoListener(userInfoMapper, contactMapper, contactIdentityMapper, transactionTemplate)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis() - l);
    }


    public void loadTag() {
        long l = System.currentTimeMillis();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/edz/Desktop/用户列表_20200421164055 - for linkflow.xlsx");

            EasyExcel.read(inputStream, UserInfo.class, new TagInfoListener(tagMapper, transactionTemplate)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis() - l);
    }

}
