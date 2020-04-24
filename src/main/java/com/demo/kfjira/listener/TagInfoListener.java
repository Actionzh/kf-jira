package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.ContactGroupEntity;
import com.demo.kfjira.entity.UserInfo;
import com.demo.kfjira.mapper.TagMapper;
import org.joda.time.DateTime;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagInfoListener extends AnalysisEventListener<UserInfo> {

    private static final int BATCH_COUNT = 10000;
    //自定义用于暂时存储data。
    //可以通过实例获取该值
    private List<ContactGroupEntity> datas = new ArrayList<>();
    private Set<String> tagNameSet = new HashSet<>();

    private TagMapper tagMapper;
    private TransactionTemplate transactionTemplate;


    public TagInfoListener(TagMapper userMapper, TransactionTemplate transactionTemplate) {
        this.tagMapper = userMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void invoke(UserInfo userInfo, AnalysisContext context) {

        System.out.println("当前行：" + context.readRowHolder().getRowIndex());
        System.out.println(userInfo);

        String tag = userInfo.getTag();
        if (tag == null) {
            return;
        }
        String[] split = tag.split("\\|");

        for (int i = 0; i < split.length; i++) {
            String tagName = split[i];
            //int count = tagMapper.selectByName(tagName, 417);
            //if (count == 0) {
            tagNameSet.add(tagName);
            //}
        }

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (tagNameSet.size() >= BATCH_COUNT) {
            saveData();
        }
    }

    private void saveData() {
        tagNameSet.forEach(tagName -> {
            //加入批量数据
            ContactGroupEntity qrCodeEntity = convertToEntity(tagName);
            datas.add(qrCodeEntity);
        });
        saveExcelByMyBatis();
        // 存储完成清理 list
        datas.clear();
        tagNameSet.clear();
    }

    private ContactGroupEntity convertToEntity(String tagName) {
        return ContactGroupEntity.builder().version(0)
                .dateCreated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                .lastUpdated(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"))
                .name(tagName)
                .status("ACTIVE")
                .tenantId(417)
                .groupType("static").build();

    }

    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveExcelByMyBatis() {
        tagMapper.insertBatch(datas);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }
}
