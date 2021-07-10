package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.MineTopicSummary;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicExpiredData;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicExpiredVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicMineVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class TopicMineConverter {
    public static List<TopicMineVO> convert2TopicMineVOList(List<MineTopicSummary> dtoList) {
        if (ValidateUtils.isNull(dtoList)) {
            return new ArrayList<>();
        }
        List<TopicMineVO> voList = new ArrayList<>();
        for (MineTopicSummary data: dtoList) {
            TopicMineVO vo = new TopicMineVO();
            CopyUtils.copyProperties(vo, data);
            vo.setClusterId(data.getLogicalClusterId());
            vo.setClusterName(data.getLogicalClusterName());
            vo.setBytesIn(data.getBytesIn());
            vo.setBytesOut(data.getBytesOut());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicVO> convert2TopicVOList(List<TopicDTO> dtoList) {
        if (ValidateUtils.isNull(dtoList)) {
            return new ArrayList<>();
        }
        List<TopicVO> voList = new ArrayList<>();
        for (TopicDTO data: dtoList) {
            TopicVO vo = new TopicVO();
            CopyUtils.copyProperties(vo, data);
            vo.setClusterId(data.getLogicalClusterId());
            vo.setClusterName(data.getLogicalClusterName());
            vo.setNeedAuth(data.getNeedAuth());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicExpiredVO> convert2TopicExpiredVOList(List<TopicExpiredData> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return new ArrayList<>();
        }
        List<TopicExpiredVO> voList = new ArrayList<>();
        for (TopicExpiredData elem: dataList) {
            TopicExpiredVO vo = new TopicExpiredVO();
            if (!ValidateUtils.isNull(elem.getLogicalClusterDO())) {
                vo.setClusterId(elem.getLogicalClusterDO().getClusterId());
                vo.setClusterName(elem.getLogicalClusterDO().getName());
            }
            vo.setTopicName(elem.getTopicName());
            if (!ValidateUtils.isNull(elem.getAppDO())) {
                vo.setAppId(elem.getAppDO().getAppId());
                vo.setAppName(elem.getAppDO().getName());
                vo.setAppPrincipals(elem.getAppDO().getPrincipals());
            }
            vo.setFetchConnectionNum(elem.getFetchConnectionNum());
            voList.add(vo);
        }
        return voList;
    }
}