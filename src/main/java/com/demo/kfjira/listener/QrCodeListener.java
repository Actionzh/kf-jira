package com.demo.kfjira.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.kfjira.entity.LoadInfo;
import com.demo.kfjira.entity.QrCodeEntity;
import com.demo.kfjira.mapper.QrCodeMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

public class QrCodeListener extends AnalysisEventListener<LoadInfo> {

    private static final int BATCH_COUNT = 1000;
    //自定义用于暂时存储data。
    //可以通过实例获取该值
    private List<QrCodeEntity> datas = new ArrayList<>();

    private QrCodeMapper qrCodeMapper;
    protected TransactionTemplate transactionTemplate;


    public QrCodeListener(QrCodeMapper qrCodeMapper, TransactionTemplate transactionTemplate) {
        this.qrCodeMapper = qrCodeMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void invoke(LoadInfo object, AnalysisContext context) {
        System.out.println("当前行：" + context.currentReadHolder());
        System.out.println(object);
        //根据自己业务做处理
        QrCodeEntity qrCodeEntity = convertToEntity(object);
        datas.add(qrCodeEntity);

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (datas.size() >= BATCH_COUNT) {
            //saveExcelByMyBatis();
            // 存储完成清理 list
            datas.clear();
        }

    }

    private QrCodeEntity convertToEntity(LoadInfo object) {
        return QrCodeEntity.builder().version(0)
                .dateCreated(DateTime.parse(object.getCreated_at(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .lastUpdated(DateTime.parse(object.getCreated_at(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .tenantId(417)
                .uuid(object.getScene())
                .term(object.getName())
                .url(object.getQrcode_url())
                .ticket(object.getTicket())
                .source(object.getChannel())
                .campaign(object.getSegmentation())
                .wechatAccount("wxa7f65d2ea613627a").build();

    }

    /**
     * 批量存储数据
     * 通过 mybatis
     */
    private void saveExcelByMyBatis() {
        qrCodeMapper.insertBatch(datas);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //saveExcelByMyBatis();
    }
}
