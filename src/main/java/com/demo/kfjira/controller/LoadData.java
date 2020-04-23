package com.demo.kfjira.controller;

import com.demo.kfjira.mapper.QrCodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
public class LoadData {


    @Autowired
    private QrCodeMapper qrCodeMapper;

    public static void main(String[] args) throws FileNotFoundException {
        long l = System.currentTimeMillis();

        FileInputStream inputStream = new FileInputStream("/Users/edz/Desktop/lacoste_qrcode 二维码配置.xlsx");
       /* try {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS, null, listener);
            excelReader.read();*/

        /*    EasyExcel.read(inputStream, LoadInfo.class, new ExcelListener()).sheet("QR code").doRead();

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
        System.out.println(System.currentTimeMillis() - l);*/
    }
}
