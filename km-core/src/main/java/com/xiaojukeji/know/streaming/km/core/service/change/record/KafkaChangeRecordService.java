package com.xiaojukeji.know.streaming.km.core.service.change.record;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;


public interface KafkaChangeRecordService {
    int insertAndIgnoreDuplicate(KafkaChangeRecordPO recordPO);

    IPage<KafkaChangeRecordPO> pagingByCluster(Long clusterPhyId, PaginationBaseDTO dto);
}
