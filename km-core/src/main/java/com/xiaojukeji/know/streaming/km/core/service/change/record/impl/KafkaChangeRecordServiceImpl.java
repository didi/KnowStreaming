package com.xiaojukeji.know.streaming.km.core.service.change.record.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.changerecord.KafkaChangeRecordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class KafkaChangeRecordServiceImpl implements KafkaChangeRecordService {
    private static final ILog log = LogFactory.getLog(KafkaChangeRecordServiceImpl.class);

    @Autowired
    private KafkaChangeRecordDAO kafkaChangeRecordDAO;

    private static final Cache<String, String> recordCache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();

    @Override
    public int insertAndIgnoreDuplicate(KafkaChangeRecordPO recordPO) {
        try {
            String cacheData = recordCache.getIfPresent(recordPO.getUniqueField());
            if (cacheData != null || this.checkExistInDB(recordPO.getUniqueField())) {
                // 已存在时，则直接返回
                return 0;
            }

            recordCache.put(recordPO.getUniqueField(), recordPO.getUniqueField());

            return kafkaChangeRecordDAO.insert(recordPO);
        } catch (Exception e) {
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

    private boolean checkExistInDB(String uniqueField) {
        LambdaQueryWrapper<KafkaChangeRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaChangeRecordPO::getUniqueField, uniqueField);

        List<KafkaChangeRecordPO> poList = kafkaChangeRecordDAO.selectList(lambdaQueryWrapper);

        return poList != null && !poList.isEmpty();
    }
}
