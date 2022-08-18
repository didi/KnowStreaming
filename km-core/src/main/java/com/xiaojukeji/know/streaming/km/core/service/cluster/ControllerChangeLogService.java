package com.xiaojukeji.know.streaming.km.core.service.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.ControllerChangeLogPO;

import java.util.List;

public interface ControllerChangeLogService {

    /**
     * 添加Controller变化日志
     * @param controllerChangeLogPO 待添加Controller变化日志对象
     * @return
     */
    Long addControllerChangeLog(ControllerChangeLogPO controllerChangeLogPO);

    /**
     * 分页查询结果条件查询出的结果集总数
     * @param clusterPhyId kafka 集群id
     * @param dto 封装分页查询条件的对象
     * @return 分页查询结果条件查询出的结果集总数
     */
    Long queryCount(Long clusterPhyId, PaginationSortDTO dto);

    /**
     * 分页查询
     * @param clusterPhyId kafka 集群id
     * @param dto 封装分页查询条件的对象
     * @return 分页查询结果集
     */
    List<ControllerChangeLogPO> paginationQuery(Long clusterPhyId, PaginationSortDTO dto);

}
