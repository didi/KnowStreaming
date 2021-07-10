package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.RegionDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RegionVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class RegionModelConverter {
    private static RegionVO convert2RegionVO(RegionDO regionDO) {
        if (ValidateUtils.isNull(regionDO)) {
            return null;
        }
        RegionVO regionVO = new RegionVO();
        regionVO.setId(regionDO.getId());
        regionVO.setClusterId(regionDO.getClusterId());
        regionVO.setName(regionDO.getName());
        regionVO.setBrokerIdList(ListUtils.string2IntList(regionDO.getBrokerList()));
        regionVO.setDescription(regionDO.getDescription());
        regionVO.setCapacity(regionDO.getCapacity());
        regionVO.setRealUsed(regionDO.getRealUsed());
        regionVO.setEstimateUsed(regionDO.getEstimateUsed());
        regionVO.setStatus(regionDO.getStatus());
        regionVO.setGmtCreate(regionDO.getGmtCreate());
        regionVO.setGmtModify(regionDO.getGmtModify());
        return regionVO;
    }

    public static List<RegionVO> convert2RegionVOList(List<RegionDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<RegionVO> voList = new ArrayList<>();
        for (RegionDO elem: doList) {
            voList.add(convert2RegionVO(elem));
        }
        return voList;
    }

    public static RegionDO convert2RegionDO(RegionDTO dto) {
        RegionDO regionDO = new RegionDO();
        regionDO.setName(dto.getName());
        regionDO.setClusterId(dto.getClusterId());
        regionDO.setBrokerList(ListUtils.intList2String(dto.getBrokerIdList()));
        regionDO.setDescription(dto.getDescription());
        regionDO.setId(dto.getId());
        regionDO.setStatus(dto.getStatus());
        return regionDO;
    }
}