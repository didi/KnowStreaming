package com.xiaojukeji.know.streaming.km.biz.broker;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.broker.BrokerBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.log.LogDirVO;

public interface BrokerManager {
    Result<BrokerBasicVO> getBrokerBasic(Long clusterPhyId, Integer brokerId);

    PaginationResult<LogDirVO> getBrokerLogDirs(Long clusterPhyId, Integer brokerId, PaginationBaseDTO dto);
}
