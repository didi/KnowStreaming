package com.xiaojukeji.know.streaming.km.core.service.cluster.impl;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.ControllerChangeLogPO;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ControllerChangeLogService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.ControllerChangeLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ControllerChangeLogServiceImpl implements ControllerChangeLogService {

    @Autowired
    private ControllerChangeLogDAO controllerChangeLogDAO;

    @Override
    public Long addControllerChangeLog(ControllerChangeLogPO controllerChangeLogPO) {
        controllerChangeLogDAO.insert(controllerChangeLogPO);
        return controllerChangeLogPO.getId();
    }

    @Override
    public Long queryCount(Long clusterPhyId, PaginationSortDTO dto) {
        return controllerChangeLogDAO.queryCount(getPaginationQueryParams(clusterPhyId, dto));
    }

    @Override
    public List<ControllerChangeLogPO> paginationQuery(Long clusterPhyId, PaginationSortDTO dto) {
        return controllerChangeLogDAO.paginationQuery(getPaginationQueryParams(clusterPhyId, dto));
    }

    private Map<String, Object> getPaginationQueryParams(Long clusterPhyId, PaginationSortDTO dto) {
        Map<String, Object> params = new HashMap<>();
        params.put("clusterPhyId", clusterPhyId);
        params.put("sortField", dto.getSortField());
        params.put("sortType", dto.getSortType());
        params.put("pageNo", dto.getPageNo());
        params.put("pageSize", dto.getPageSize());
        params.put("searchKeywords", dto.getSearchKeywords());
        return params;
    }

}
