package com.xiaojukeji.know.streaming.km.core.service.change.record.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.changerecord.KafkaChangeRecordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class KafkaChangeRecordServiceImpl implements KafkaChangeRecordService {
    private static final ILog log = LogFactory.getLog(KafkaChangeRecordServiceImpl.class);

    @Autowired
    private KafkaChangeRecordDAO kafkaChangeRecordDAO;

    @Override
    public int insertAndIgnoreDuplicate(KafkaChangeRecordPO recordPO) {
        try {
            return kafkaChangeRecordDAO.insert(recordPO);
        } catch (DuplicateKeyException dke) {
            return 0;
        }
    }

    @Override
    public IPage<KafkaChangeRecordPO> pagingByCluster(Long clusterPhyId, PaginationBaseDTO dto) {
        LambdaQueryWrapper<KafkaChangeRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaChangeRecordPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.orderByDesc(KafkaChangeRecordPO::getUpdateTime);

        return kafkaChangeRecordDAO.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/

}
