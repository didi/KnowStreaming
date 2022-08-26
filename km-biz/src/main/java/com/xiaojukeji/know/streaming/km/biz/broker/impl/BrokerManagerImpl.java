package com.xiaojukeji.know.streaming.km.biz.broker.impl;

import com.xiaojukeji.know.streaming.km.biz.broker.BrokerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.vo.broker.BrokerBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.log.LogDirVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import org.apache.kafka.clients.admin.LogDirDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BrokerManagerImpl implements BrokerManager {
    @Autowired
    private BrokerService brokerService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Override
    public Result<BrokerBasicVO> getBrokerBasic(Long clusterPhyId, Integer brokerId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        Broker broker = brokerService.getBroker(clusterPhyId, brokerId);
        if (broker == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getBrokerNotExist(clusterPhyId, brokerId));
        }

        return Result.buildSuc(new BrokerBasicVO(brokerId, broker.getHost(), clusterPhy.getName()));
    }

    @Override
    public PaginationResult<LogDirVO> getBrokerLogDirs(Long clusterPhyId, Integer brokerId, PaginationBaseDTO dto) {
        Result<Map<String, LogDirDescription>> dirDescResult = brokerService.getBrokerLogDirDescFromKafka(clusterPhyId, brokerId);
        if (dirDescResult.failed()) {
            return PaginationResult.buildFailure(dirDescResult, dto);
        }

        Map<String, LogDirDescription> dirDescMap = dirDescResult.hasData()? dirDescResult.getData(): new HashMap<>();

        List<LogDirVO> voList = new ArrayList<>();
        for (Map.Entry<String, LogDirDescription> entry: dirDescMap.entrySet()) {
            entry.getValue().replicaInfos().entrySet().stream().forEach(elem -> {
                LogDirVO vo = new LogDirVO();
                vo.setDir(entry.getKey());
                vo.setTopicName(elem.getKey().topic());
                vo.setPartitionId(elem.getKey().partition());
                vo.setOffsetLag(elem.getValue().offsetLag());
                vo.setLogSizeUnitB(elem.getValue().size());
                voList.add(vo);
            });
        }

        return PaginationUtil.pageBySubData(
                PaginationUtil.pageByFuzzyFilter(voList, dto.getSearchKeywords(), Arrays.asList("topicName")),
                dto
        );
    }

    /**************************************************** private method ****************************************************/

}
