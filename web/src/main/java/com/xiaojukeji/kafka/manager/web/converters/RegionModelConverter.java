package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.web.model.RegionModel;
import com.xiaojukeji.kafka.manager.web.vo.RegionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class RegionModelConverter {
    private static RegionVO convert2RegionVO(RegionDO regionDO) {
        if (regionDO == null) {
            return null;
        }
        RegionVO regionVO = new RegionVO();
        regionVO.setRegionId(regionDO.getId());
        regionVO.setClusterId(regionDO.getClusterId());
        regionVO.setRegionName(regionDO.getRegionName());
        regionVO.setLevel(regionDO.getLevel());
        regionVO.setBrokerIdList(ListUtils.string2IntList(regionDO.getBrokerList()));
        regionVO.setDescription(regionDO.getDescription());
        regionVO.setOperator(regionDO.getOperator());
        regionVO.setStatus(regionDO.getStatus());
        regionVO.setGmtCreate(regionDO.getGmtCreate().getTime());
        regionVO.setGmtModify(regionDO.getGmtModify().getTime());
        return regionVO;
    }


    public static List<RegionVO> convert2RegionVOList(List<RegionDO> regionDOList) {
        if (regionDOList == null) {
            return new ArrayList<>();
        }
        List<RegionVO> regionInfoVOList = new ArrayList<>();
        for (RegionDO regionDO: regionDOList) {
            regionInfoVOList.add(convert2RegionVO(regionDO));
        }
        return regionInfoVOList;
    }

    public static RegionDO convert2RegionDO(RegionModel regionModel, String operator) {
        RegionDO regionDO = new RegionDO();
        regionDO.setId(regionModel.getRegionId());
        regionDO.setBrokerList(ListUtils.intList2String(regionModel.getBrokerIdList()));
        regionDO.setClusterId(regionModel.getClusterId());
        regionDO.setLevel(regionModel.getLevel());
        regionDO.setDescription(regionModel.getDescription());
        regionDO.setOperator(operator);
        regionDO.setStatus(regionModel.getStatus() == null? 0: regionModel.getStatus());
        regionDO.setRegionName(regionModel.getRegionName());
        return regionDO;
    }
}