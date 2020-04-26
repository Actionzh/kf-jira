package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.ContactGroupMemberEntity;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.mapper.ContactIdentityMapper;
import com.demo.kfjira.mapper.ContactMapper;
import com.demo.kfjira.mapper.EventMapper;
import com.demo.kfjira.mapper.EventUtmMapper;
import com.demo.kfjira.utils.JsonMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class QrcodeEventListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 1000;

    private ContactMapper contactMapper;
    private ContactIdentityMapper contactIdentityMapper;
    private static ExecutorService executor = null;
    private final static JsonMapper jsonMapper = JsonMapper.INSTANCE;
    private List<ContactGroupMemberEntity> contactGroupMemberEntities = new ArrayList<>();
    private EventMapper eventMapper;
    private EventUtmMapper eventUtmMapper;

    @PostConstruct
    public void init() {
        ThreadFactory factory = (new ThreadFactoryBuilder())
                .setNameFormat("identity-svc-%d")
                .build();
        executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    public QrcodeEventListener(EventMapper eventMapper,
                               EventUtmMapper eventUtmMapper,
                               ContactMapper contactMapper,
                               ContactIdentityMapper contactIdentityMapper) {
        this.eventMapper = eventMapper;
        this.eventUtmMapper = eventUtmMapper;
        this.contactMapper = contactMapper;
        this.contactIdentityMapper = contactIdentityMapper;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(userInfo);
    }


    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveContactGroupMemberByMyBatis() {

    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveContactGroupMemberByMyBatis();
    }
}
