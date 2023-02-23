package com.xiaojukeji.know.streaming.km.biz.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterConnectorsManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterConnectorsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectorsDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterConnectorOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.converter.ConnectConverter;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationMetricsUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import org.apache.kafka.connect.runtime.AbstractStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClusterConnectorsManagerImpl implements ClusterConnectorsManager {
    private static final ILog LOGGER = LogFactory.getLog(ClusterConnectorsManagerImpl.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private ConnectorMetricService connectorMetricService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Override
    public PaginationResult<ClusterConnectorOverviewVO> getClusterConnectorsOverview(Long clusterPhyId, ClusterConnectorsOverviewDTO dto) {
        List<ConnectCluster> clusterList = connectClusterService.listByKafkaCluster(clusterPhyId);

        List<ConnectorPO> poList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId);

        // 查询实时指标
        Result<List<ConnectorMetrics>> latestMetricsResult = connectorMetricService.getLatestMetricsFromES(
                clusterPhyId,
                poList.stream().map(elem -> new ClusterConnectorDTO(elem.getConnectClusterId(), elem.getConnectorName())).collect(Collectors.toList()),
                dto.getLatestMetricNames()
        );

        if (latestMetricsResult.failed()) {
            LOGGER.error("method=getClusterConnectorsOverview||clusterPhyId={}||result={}||errMsg=get latest metric failed", clusterPhyId, latestMetricsResult);
            return PaginationResult.buildFailure(latestMetricsResult, dto);
        }

        // 转换成vo
        List<ClusterConnectorOverviewVO> voList = ConnectConverter.convert2ClusterConnectorOverviewVOList(clusterList, poList,latestMetricsResult.getData());

        // 请求分页信息
        PaginationResult<ClusterConnectorOverviewVO> voPaginationResult = this.pagingConnectorInLocal(voList, dto);
        if (voPaginationResult.failed()) {
            LOGGER.error("method=getClusterConnectorsOverview||clusterPhyId={}||result={}||errMsg=pagination in local failed", clusterPhyId, voPaginationResult);

            return PaginationResult.buildFailure(voPaginationResult, dto);
        }

        // 查询历史指标
        Result<List<MetricMultiLinesVO>> lineMetricsResult = connectorMetricService.listConnectClusterMetricsFromES(
                clusterPhyId,
                this.buildMetricsConnectorsDTO(
                        voPaginationResult.getData().getBizData().stream().map(elem -> new ClusterConnectorDTO(elem.getConnectClusterId(), elem.getConnectorName())).collect(Collectors.toList()),
                        dto.getMetricLines()
                )
        );


        return PaginationResult.buildSuc(
                ConnectConverter.supplyData2ClusterConnectorOverviewVOList(
                        voPaginationResult.getData().getBizData(),
                        lineMetricsResult.getData()
                ),
                voPaginationResult
        );
    }

    @Override
    public ConnectStateVO getClusterConnectorsState(Long clusterPhyId) {
        //获取Connect集群Id列表
        List<ConnectCluster> connectClusterList = connectClusterService.listByKafkaCluster(clusterPhyId);
        List<ConnectorPO> connectorPOList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId);
        List<WorkerConnector> workerConnectorList = workerConnectorService.listByKafkaClusterIdFromDB(clusterPhyId);
        List<ConnectWorker> connectWorkerList = workerService.listByKafkaClusterIdFromDB(clusterPhyId);

        return convert2ConnectStateVO(connectClusterList, connectorPOList, workerConnectorList, connectWorkerList);
    }

    /**************************************************** private method ****************************************************/

    private MetricsConnectorsDTO buildMetricsConnectorsDTO(List<ClusterConnectorDTO> connectorDTOList, MetricDTO metricDTO) {
        MetricsConnectorsDTO dto = ConvertUtil.obj2Obj(metricDTO, MetricsConnectorsDTO.class);
        dto.setConnectorNameList(connectorDTOList == null? new ArrayList<>(): connectorDTOList);

        return dto;
    }

    private ConnectStateVO convert2ConnectStateVO(List<ConnectCluster> connectClusterList, List<ConnectorPO> connectorPOList, List<WorkerConnector> workerConnectorList, List<ConnectWorker> connectWorkerList) {
        ConnectStateVO connectStateVO = new ConnectStateVO();
        connectStateVO.setConnectClusterCount(connectClusterList.size());
        connectStateVO.setTotalConnectorCount(connectorPOList.size());
        connectStateVO.setAliveConnectorCount(connectorPOList.stream().filter(elem -> elem.getState().equals(AbstractStatus.State.RUNNING.name())).collect(Collectors.toList()).size());
        connectStateVO.setWorkerCount(connectWorkerList.size());
        connectStateVO.setTotalTaskCount(workerConnectorList.size());
        connectStateVO.setAliveTaskCount(workerConnectorList.stream().filter(elem -> elem.getState().equals(AbstractStatus.State.RUNNING.name())).collect(Collectors.toList()).size());
        return connectStateVO;
    }

    private PaginationResult<ClusterConnectorOverviewVO> pagingConnectorInLocal(List<ClusterConnectorOverviewVO> connectorVOList, ClusterConnectorsOverviewDTO dto) {
        //模糊匹配
        connectorVOList = PaginationUtil.pageByFuzzyFilter(connectorVOList, dto.getSearchKeywords(), Arrays.asList("connectorName"));

        //排序
        if (!dto.getLatestMetricNames().isEmpty()) {
            PaginationMetricsUtil.sortMetrics(connectorVOList, "latestMetrics", dto.getSortMetricNameList(), "connectorName", dto.getSortType());
        } else {
            PaginationUtil.pageBySort(connectorVOList, dto.getSortField(), dto.getSortType(), "connectorName", dto.getSortType());
        }

        //分页
        return PaginationUtil.pageBySubData(connectorVOList, dto);
    }

}
