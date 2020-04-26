package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.mapper.UserInfoMapper;

import java.util.ArrayList;
import java.util.List;

public class LoadUserDataListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1000;
    private List<UserInfo> datas = new ArrayList<>();

    private UserInfoMapper userInfoMapper;


    public LoadUserDataListener(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(userInfo);
        datas.add(userInfo);
        if (datas.size() >= BATCH_COUNT) {
            saveExcelByMyBatis();
            // 存储完成清理 list
            datas.clear();
        }
    }

    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveExcelByMyBatis() {
        userInfoMapper.insertBatch(datas);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveExcelByMyBatis();
        datas.clear();
    }
}
