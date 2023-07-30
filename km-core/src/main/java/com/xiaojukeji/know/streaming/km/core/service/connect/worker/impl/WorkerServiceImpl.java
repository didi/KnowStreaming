package com.xiaojukeji.know.streaming.km.core.service.connect.worker.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectWorkerPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterWorkerOverviewVO;
import com.xiaojukeji.know.streaming.km.common.enums.jmx.JmxEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectWorkerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkerServiceImpl implements WorkerService {
    @Autowired
    private ConnectWorkerDAO connectWorkerDAO;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Override
    public void batchReplaceInDB(Long connectClusterId, List<ConnectWorker> workerList) {
        Map<String, ConnectWorkerPO> oldMap = new HashMap<>();
        for (ConnectWorkerPO oldPO: this.listPOSFromDB(connectClusterId)) {
            oldMap.put(oldPO.getMemberId(), oldPO);
        }

        for (ConnectWorker worker: workerList) {
            try {
                ConnectWorkerPO newPO = ConvertUtil.obj2Obj(worker, ConnectWorkerPO.class);
                ConnectWorkerPO oldPO = oldMap.remove(newPO.getMemberId());
                if (oldPO == null) {
                    connectWorkerDAO.insert(newPO);
                } else {
                    newPO.setId(oldPO.getId());
                    if (JmxEnum.UNKNOWN.getPort().equals(newPO.getJmxPort())) {
                        // 如果所获取的jmx端口未知，则不更新jmx端口
                        newPO.setJmxPort(oldPO.getJmxPort());
                    }

                    connectWorkerDAO.updateById(newPO);
                }
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }

        try {
            oldMap.values().forEach(elem -> connectWorkerDAO.deleteById(elem.getId()));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public List<ConnectWorker> listFromDB(Long connectClusterId) {
        return ConvertUtil.list2List(this.listPOSFromDB(connectClusterId), ConnectWorker.class);
    }

    @Override
    public PaginationResult<ClusterWorkerOverviewVO> pageWorkByKafkaClusterPhy(Long kafkaClusterPhyId, PaginationBaseDTO dto) {
        IPage<ConnectWorkerPO> pageInfo = new Page<>(dto.getPageNo(), dto.getPageSize());

        LambdaQueryWrapper<ConnectWorkerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectWorkerPO::getKafkaClusterPhyId, kafkaClusterPhyId);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(dto.getSearchKeywords()), ConnectWorkerPO::getHost, dto.getSearchKeywords());
        connectWorkerDAO.selectPage(pageInfo, lambdaQueryWrapper);

        List<ConnectWorkerPO>           connectWorkerPOS         = pageInfo.getRecords();
        List<ClusterWorkerOverviewVO>   clusterWorkerOverviewVOS = new ArrayList<>();

        for(ConnectWorkerPO connectWorkerPO : connectWorkerPOS){
            Long connectClusterId = connectWorkerPO.getConnectClusterId();

            ClusterWorkerOverviewVO clusterWorkerOverviewVO = new ClusterWorkerOverviewVO();
            clusterWorkerOverviewVO.setConnectClusterId(connectClusterId);
            clusterWorkerOverviewVO.setWorkerHost(connectWorkerPO.getHost());
            clusterWorkerOverviewVO.setConnectorCount(connectorService.countByConnectClusterIdFromDB(connectClusterId));
            clusterWorkerOverviewVO.setConnectClusterName(connectClusterService.getClusterName(connectClusterId));
            clusterWorkerOverviewVO.setTaskCount(1);

            clusterWorkerOverviewVOS.add(clusterWorkerOverviewVO);
        }

        return PaginationResult.buildSuc(clusterWorkerOverviewVOS, pageInfo);
    }

    @Override
    public List<ConnectWorker> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId) {
        LambdaQueryWrapper<ConnectWorkerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectWorkerPO::getKafkaClusterPhyId, kafkaClusterPhyId);
        return ConvertUtil.list2List(connectWorkerDAO.selectList(lambdaQueryWrapper), ConnectWorker.class);
    }

    /**************************************************** private method ****************************************************/
    private List<ConnectWorkerPO> listPOSFromDB(Long connectClusterId) {
        LambdaQueryWrapper<ConnectWorkerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectWorkerPO::getConnectClusterId, connectClusterId);

        return connectWorkerDAO.selectList(lambdaQueryWrapper);
    }
}
