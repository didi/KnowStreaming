package com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto.ClusterBalanceOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto.ClusterBalancePreviewDTO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto.ClusterBalanceStrategyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.ClusterBalanceItemState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo.*;

@EnterpriseLoadReBalance
public interface ClusterBalanceService {

    /**
     * @param clusterPhyId
     * @return
     */
    Result<ClusterBalanceStateVO> state(Long clusterPhyId);

    /**
     * @param clusterPhyId
     * @return
     */
    Result<ClusterBalanceJobConfigVO> config(Long clusterPhyId);

    /**
     * @param clusterPhyId
     * @param dto
     * @return
     */
    PaginationResult<ClusterBalanceOverviewVO> overview(Long clusterPhyId, ClusterBalanceOverviewDTO dto);

    /**
     * @param clusterPhyId
     * @return
     */
    Result<ClusterBalanceItemState> getItemState(Long clusterPhyId);

    /**
     * @param clusterPhyId
     * @param dto
     * @return
     */
    PaginationResult<ClusterBalanceHistoryVO> history(Long clusterPhyId, PaginationBaseDTO dto);

    /**
     * @param clusterPhyId
     * @param jobId
     * @return
     */
    Result<ClusterBalancePlanVO> plan(Long clusterPhyId, Long jobId);


    /**
     * @param clusterBalancePreviewDTO
     * @return
     */
    Result<ClusterBalancePlanVO> preview(Long clusterPhyId, ClusterBalancePreviewDTO clusterBalancePreviewDTO);

    /**
     * @param jobId
     * @return
     */
    Result<ClusterBalancePlanVO> schedule(Long clusterPhyId, Long jobId);

    /**
     * @param clusterPhyId
     * @param dto
     * @return
     */
    Result<Void> strategy(Long clusterPhyId, ClusterBalanceStrategyDTO dto, String operator);


    /**
     * @param clusterPhyId
     * @return
     */
    Result<Void> createScheduleJob(Long clusterPhyId, long triggerTimeUnitMs);

}
