package com.demo.kfjira.service;

import com.alibaba.excel.EasyExcel;
import com.demo.kfjira.cache.RedisCacheManager;
import com.demo.kfjira.entity.LoadInfo;
import com.demo.kfjira.entity.ScanInfo;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.listener.*;
import com.demo.kfjira.listener.other.InsertContactListener;
import com.demo.kfjira.listener.other.InsertIdentityListener;
import com.demo.kfjira.mapper.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;

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
    @Autowired
    private ContactContentMapper contactContentMapper;

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventUtmMapper eventUtmMapper;

    @Autowired
    private ContactGroupMemberMapper contactGroupMemberMapper;
    private static ExecutorService executor = null;
    @Autowired
    private RedisCacheManager redisCacheManager;


    @Override
    public void afterPropertiesSet() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        ThreadFactory factory = (new ThreadFactoryBuilder())
                .setNameFormat("identity-svc-%d")
                .build();
        executor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.AbortPolicy());

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
            throw e;
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

    public void loadUserInfo() throws IOException {
        long l = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("data/用户列表.xlsx").getInputStream();

            EasyExcel.read(inputStream, UserInfo.class, new UserInfoListener(executor, eventMapper, contactContentMapper, contactMapper, contactIdentityMapper, transactionTemplate)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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

    public void createGroupMember() throws Exception {
        long l = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("data/用户列表.xlsx").getInputStream();

            EasyExcel.read(inputStream, UserInfo.class, new ContactGroupMemberListener(executor, tagMapper, contactGroupMemberMapper, contactMapper, contactIdentityMapper)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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

    public void createQrcodeEvent() throws Exception {
        long l = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("data/二维码扫描.xlsx").getInputStream();
            EasyExcel.read(inputStream, ScanInfo.class, new QrcodeEventListener(executor, qrCodeMapper, eventMapper, eventUtmMapper, contactMapper, contactIdentityMapper)).sheet("Behavior- 扫描二维码").doRead();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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

    public void test() throws Exception {
        long l = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("data/用户列表.xlsx").getInputStream();

            EasyExcel.read(inputStream, UserInfo.class, new InsertContactListener(redisCacheManager, executor, eventMapper, contactContentMapper, contactMapper, contactIdentityMapper, transactionTemplate)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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

    public void testInsertIdentity() throws Exception {
        long l = System.currentTimeMillis();
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("data/用户列表.xlsx").getInputStream();

            EasyExcel.read(inputStream, UserInfo.class, new InsertIdentityListener(redisCacheManager, executor, eventMapper, contactContentMapper, contactMapper, contactIdentityMapper, transactionTemplate)).sheet("Sheet0").doRead();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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


    public void LoadUserData() {
        long l = System.currentTimeMillis();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/edz/Desktop/用户列表_20200421164055 - for linkflow.xlsx");

            EasyExcel.read(inputStream, UserInfo.class, new LoadUserDataListener(userInfoMapper)).sheet("Sheet0").doRead();

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
