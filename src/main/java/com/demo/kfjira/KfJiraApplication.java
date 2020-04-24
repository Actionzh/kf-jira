package com.demo.kfjira;

import com.demo.kfjira.service.LoadDataService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
@MapperScan("com.demo.kfjira.mapper")
public class KfJiraApplication implements CommandLineRunner {


    @Resource
    private LoadDataService loadDataService;

    public static void main(String[] args) {
        SpringApplication.run(KfJiraApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //loadDataService.load();
    }
}
