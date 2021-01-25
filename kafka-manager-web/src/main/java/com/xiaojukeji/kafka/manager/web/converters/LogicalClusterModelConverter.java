package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.dto.rd.LogicalClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.LogicalClusterVO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/29
 */
public class LogicalClusterModelConverter {
    public static LogicalClusterVO convert2LogicalClusterVO(LogicalClusterDO logicalClusterDO) {
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return null;
        }
        LogicalClusterVO vo = new LogicalClusterVO();
        vo.setLogicalClusterId(logicalClusterDO.getId());
        vo.setLogicalClusterName(logicalClusterDO.getName());
        vo.setLogicalClusterIdentification(logicalClusterDO.getIdentification());
        vo.setPhysicalClusterId(logicalClusterDO.getClusterId());
        vo.setMode(logicalClusterDO.getMode());
        vo.setRegionIdList(ListUtils.string2LongList(logicalClusterDO.getRegionList()));
        vo.setAppId(logicalClusterDO.getAppId());
        vo.setDescription(logicalClusterDO.getDescription());
        vo.setGmtCreate(logicalClusterDO.getGmtCreate());
        vo.setGmtModify(logicalClusterDO.getGmtModify());
        return vo;
    }

    public static List<LogicalClusterVO> convert2LogicalClusterVOList(List<LogicalClusterDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<LogicalClusterVO> voList = new ArrayList<>();
        for (LogicalClusterDO elem: doList) {
            voList.add(convert2LogicalClusterVO(elem));
        }
        return voList;
    }

    public static LogicalClusterDO convert2LogicalClusterDO(LogicalClusterDTO dto) {
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setName(dto.getName());
        logicalClusterDO.setIdentification(dto.getIdentification());
        logicalClusterDO.setClusterId(dto.getClusterId());
        logicalClusterDO.setRegionList(ListUtils.longList2String(dto.getRegionIdList()));
        logicalClusterDO.setMode(dto.getMode());
        logicalClusterDO.setAppId(dto.getAppId());
        logicalClusterDO.setDescription(dto.getDescription());
        logicalClusterDO.setId(dto.getId());
        return logicalClusterDO;
    }
}