package com.xiaojukeji.know.streaming.km.biz.connect.mm2.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.connect.connector.ConnectorManager;
import com.xiaojukeji.know.streaming.km.biz.connect.mm2.MirrorMakerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterMirrorMakersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMakerCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.mm2.MetricsMirrorMakersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.WorkerConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.ClusterMirrorMakerOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerBaseStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectConfigInfosVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.utils.*;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.MirrorMakerUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.OpConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.connect.plugin.PluginService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.core.utils.ApiCallThreadPoolService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.kafka.connect.runtime.AbstractStatus.State.RUNNING;
import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.*;


/**
 * @author wyb
 * @date 2022/12/26
 */
@Service
public class MirrorMakerManagerImpl implements MirrorMakerManager {
    private static final ILog LOGGER = LogFactory.getLog(MirrorMakerManagerImpl.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private OpConnectorService opConnectorService;

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ConnectorManager connectorManager;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private MirrorMakerMetricService mirrorMakerMetricService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private PluginService pluginService;

    @Override
    public Result<Void> createMirrorMaker(MirrorMakerCreateDTO dto, String operator) {
        // 检查基本参数
        Result<Void> rv = this.checkCreateMirrorMakerParamAndUnifyData(dto);
        if (rv.failed()) {
            return rv;
        }

        // 创建MirrorSourceConnector
        Result<Void> sourceConnectResult = connectorManager.createConnector(
                dto,
                dto.getCheckpointConnectorConfigs() != null? MirrorMakerUtil.genCheckpointName(dto.getConnectorName()): "",
                dto.getHeartbeatConnectorConfigs() != null? MirrorMakerUtil.genHeartbeatName(dto.getConnectorName()): "",
                operator
        );
        if (sourceConnectResult.failed()) {
            // 创建失败, 直接返回
            return Result.buildFromIgnoreData(sourceConnectResult);
        }

        // 创建 checkpoint 任务
        Result<Void> checkpointResult = Result.buildSuc();
        if (dto.getCheckpointConnectorConfigs() != null) {
            checkpointResult = connectorManager.createConnector(
                    new ConnectorCreateDTO(dto.getConnectClusterId(), MirrorMakerUtil.genCheckpointName(dto.getConnectorName()), dto.getCheckpointConnectorConfigs()),
                    operator
            );
        }

        // 创建 heartbeat 任务
        Result<Void> heartbeatResult = Result.buildSuc();
        if (dto.getHeartbeatConnectorConfigs() != null) {
            heartbeatResult = connectorManager.createConnector(
                    new ConnectorCreateDTO(dto.getConnectClusterId(), MirrorMakerUtil.genHeartbeatName(dto.getConnectorName()), dto.getHeartbeatConnectorConfigs()),
                    operator
            );
        }

        // 全都成功
        if (checkpointResult.successful() && checkpointResult.successful()) {
            return Result.buildSuc();
        } else if (checkpointResult.failed() && checkpointResult.failed()) {
            return Result.buildFromRSAndMsg(
                    ResultStatus.KAFKA_CONNECTOR_OPERATE_FAILED,
                    String.format("创建 checkpoint & heartbeat 失败.%n失败信息分别为：%s%n%n%s", checkpointResult.getMessage(), heartbeatResult.getMessage())
            );
        } else if (checkpointResult.failed()) {
            return Result.buildFromRSAndMsg(
                    ResultStatus.KAFKA_CONNECTOR_OPERATE_FAILED,
                    String.format("创建 checkpoint 失败.%n失败信息分别为：%s", checkpointResult.getMessage())
            );
        } else{
            return Result.buildFromRSAndMsg(
                    ResultStatus.KAFKA_CONNECTOR_OPERATE_FAILED,
                    String.format("创建 heartbeat 失败.%n失败信息分别为：%s", heartbeatResult.getMessage())
            );
        }
    }

    @Override
    public Result<Void> deleteMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, sourceConnectorName);
        if (connectorPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectorNotExist(connectClusterId, sourceConnectorName));
        }

        Result<Void> rv = Result.buildSuc();
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName())) {
            rv = opConnectorService.deleteConnector(connectClusterId, connectorPO.getCheckpointConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName())) {
            rv = opConnectorService.deleteConnector(connectClusterId, connectorPO.getHeartbeatConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        return opConnectorService.deleteConnector(connectClusterId, sourceConnectorName, operator);
    }

    @Override
    public Result<Void> modifyMirrorMakerConfig(MirrorMakerCreateDTO dto, String operator) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(dto.getConnectClusterId(), dto.getConnectorName());
        if (connectorPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectorNotExist(dto.getConnectClusterId(), dto.getConnectorName()));
        }

        Result<Void> rv = Result.buildSuc();
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName()) && dto.getCheckpointConnectorConfigs() != null) {
            rv = opConnectorService.updateConnectorConfig(dto.getConnectClusterId(), connectorPO.getCheckpointConnectorName(), dto.getCheckpointConnectorConfigs(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName()) && dto.getHeartbeatConnectorConfigs() != null) {
            rv = opConnectorService.updateConnectorConfig(dto.getConnectClusterId(), connectorPO.getHeartbeatConnectorName(), dto.getHeartbeatConnectorConfigs(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        return opConnectorService.updateConnectorConfig(dto.getConnectClusterId(), dto.getConnectorName(), dto.getSuitableConfig(), operator);
    }

    @Override
    public Result<Void> restartMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, sourceConnectorName);
        if (connectorPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectorNotExist(connectClusterId, sourceConnectorName));
        }

        Result<Void> rv = Result.buildSuc();
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName())) {
            rv = opConnectorService.restartConnector(connectClusterId, connectorPO.getCheckpointConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName())) {
            rv = opConnectorService.restartConnector(connectClusterId, connectorPO.getHeartbeatConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        return opConnectorService.restartConnector(connectClusterId, sourceConnectorName, operator);
    }

    @Override
    public Result<Void> stopMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, sourceConnectorName);
        if (connectorPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectorNotExist(connectClusterId, sourceConnectorName));
        }

        Result<Void> rv = Result.buildSuc();
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName())) {
            rv = opConnectorService.stopConnector(connectClusterId, connectorPO.getCheckpointConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName())) {
            rv = opConnectorService.stopConnector(connectClusterId, connectorPO.getHeartbeatConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        return opConnectorService.stopConnector(connectClusterId, sourceConnectorName, operator);
    }

    @Override
    public Result<Void> resumeMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, sourceConnectorName);
        if (connectorPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectorNotExist(connectClusterId, sourceConnectorName));
        }

        Result<Void> rv = Result.buildSuc();
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName())) {
            rv = opConnectorService.resumeConnector(connectClusterId, connectorPO.getCheckpointConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName())) {
            rv = opConnectorService.resumeConnector(connectClusterId, connectorPO.getHeartbeatConnectorName(), operator);
        }
        if (rv.failed()) {
            return rv;
        }

        return opConnectorService.resumeConnector(connectClusterId, sourceConnectorName, operator);
    }

    @Override
    public Result<MirrorMakerStateVO> getMirrorMakerStateVO(Long clusterPhyId) {
        List<ConnectorPO> connectorPOList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId);
        List<WorkerConnector> workerConnectorList = workerConnectorService.listByKafkaClusterIdFromDB(clusterPhyId);
        List<ConnectWorker> workerList = workerService.listByKafkaClusterIdFromDB(clusterPhyId);

        return Result.buildSuc(convert2MirrorMakerStateVO(connectorPOList, workerConnectorList, workerList));
    }

    @Override
    public PaginationResult<ClusterMirrorMakerOverviewVO> getClusterMirrorMakersOverview(Long clusterPhyId, ClusterMirrorMakersOverviewDTO dto) {
        List<ConnectorPO> mirrorMakerList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId).stream().filter(elem -> elem.getConnectorClassName().equals(MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList());
        List<ConnectCluster> connectClusterList = connectClusterService.listByKafkaCluster(clusterPhyId);


        Result<List<MirrorMakerMetrics>> latestMetricsResult = mirrorMakerMetricService.getLatestMetricsFromES(clusterPhyId,
                mirrorMakerList.stream().map(elem -> new Tuple<>(elem.getConnectClusterId(), elem.getConnectorName())).collect(Collectors.toList()),
                dto.getLatestMetricNames());

        if (latestMetricsResult.failed()) {
            LOGGER.error("method=getClusterMirrorMakersOverview||clusterPhyId={}||result={}||errMsg=get latest metric failed", clusterPhyId, latestMetricsResult);
            return PaginationResult.buildFailure(latestMetricsResult, dto);
        }

        List<ClusterMirrorMakerOverviewVO> mirrorMakerOverviewVOList = this.convert2ClusterMirrorMakerOverviewVO(mirrorMakerList, connectClusterList, latestMetricsResult.getData());

        List<ClusterMirrorMakerOverviewVO> mirrorMakerVOList = this.completeClusterInfo(mirrorMakerOverviewVOList);

        PaginationResult<ClusterMirrorMakerOverviewVO> voPaginationResult = this.pagingMirrorMakerInLocal(mirrorMakerVOList, dto);

        if (voPaginationResult.failed()) {
            LOGGER.error("method=ClusterMirrorMakerOverviewVO||clusterPhyId={}||result={}||errMsg=pagination in local failed", clusterPhyId, voPaginationResult);

            return PaginationResult.buildFailure(voPaginationResult, dto);
        }

        // 查询历史指标
        Result<List<MetricMultiLinesVO>> lineMetricsResult = mirrorMakerMetricService.listMirrorMakerClusterMetricsFromES(
                clusterPhyId,
                this.buildMetricsConnectorsDTO(
                        voPaginationResult.getData().getBizData().stream().map(elem -> new ClusterConnectorDTO(elem.getConnectClusterId(), elem.getConnectorName())).collect(Collectors.toList()),
                        dto.getMetricLines()
                ));

        return PaginationResult.buildSuc(
                this.supplyData2ClusterMirrorMakerOverviewVOList(
                        voPaginationResult.getData().getBizData(),
                        lineMetricsResult.getData()
                ),
                voPaginationResult
        );
    }

    @Override
    public Result<MirrorMakerBaseStateVO> getMirrorMakerState(Long connectClusterId, String connectName) {
        //mm2任务
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, connectName);
        if (connectorPO == null){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        List<WorkerConnector> workerConnectorList = workerConnectorService.listFromDB(connectClusterId).stream()
                .filter(workerConnector -> workerConnector.getConnectorName().equals(connectorPO.getConnectorName())
                        || (!StringUtils.isBlank(connectorPO.getCheckpointConnectorName()) && workerConnector.getConnectorName().equals(connectorPO.getCheckpointConnectorName()))
                        || (!StringUtils.isBlank(connectorPO.getHeartbeatConnectorName()) && workerConnector.getConnectorName().equals(connectorPO.getHeartbeatConnectorName())))
                        .collect(Collectors.toList());

        MirrorMakerBaseStateVO mirrorMakerBaseStateVO = new MirrorMakerBaseStateVO();
        mirrorMakerBaseStateVO.setTotalTaskCount(workerConnectorList.size());
        mirrorMakerBaseStateVO.setAliveTaskCount(workerConnectorList.stream().filter(elem -> elem.getState().equals(RUNNING.name())).collect(Collectors.toList()).size());
        mirrorMakerBaseStateVO.setWorkerCount(workerConnectorList.stream().collect(Collectors.groupingBy(WorkerConnector::getWorkerId)).size());
        return Result.buildSuc(mirrorMakerBaseStateVO);
    }

    @Override
    public Result<Map<String, List<KCTaskOverviewVO>>> getTaskOverview(Long connectClusterId, String connectorName) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, connectorName);
        if (connectorPO == null){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        Map<String, List<KCTaskOverviewVO>> listMap = new HashMap<>();
        List<WorkerConnector> workerConnectorList = workerConnectorService.listFromDB(connectClusterId);
        if (workerConnectorList.isEmpty()){
            return Result.buildSuc(listMap);
        }
        workerConnectorList.forEach(workerConnector -> {
            if (workerConnector.getConnectorName().equals(connectorPO.getConnectorName())){
                listMap.putIfAbsent(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE, new ArrayList<>());
                listMap.get(MIRROR_MAKER_SOURCE_CONNECTOR_TYPE).add(ConvertUtil.obj2Obj(workerConnector, KCTaskOverviewVO.class));
            } else if (workerConnector.getConnectorName().equals(connectorPO.getCheckpointConnectorName())) {
                listMap.putIfAbsent(KafkaConnectConstant.MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE, new ArrayList<>());
                listMap.get(MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE).add(ConvertUtil.obj2Obj(workerConnector, KCTaskOverviewVO.class));
            } else if (workerConnector.getConnectorName().equals(connectorPO.getHeartbeatConnectorName())) {
                listMap.putIfAbsent(KafkaConnectConstant.MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE, new ArrayList<>());
                listMap.get(MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE).add(ConvertUtil.obj2Obj(workerConnector, KCTaskOverviewVO.class));
            }

        });

        return Result.buildSuc(listMap);
    }

    @Override
    public Result<List<Properties>> getMM2Configs(Long connectClusterId, String connectorName) {
        ConnectorPO connectorPO = connectorService.getConnectorFromDB(connectClusterId, connectorName);
        if (connectorPO == null){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        List<Properties> propList = new ArrayList<>();

        // source
        Result<KSConnectorInfo> connectorResult = connectorService.getConnectorInfoFromCluster(connectClusterId, connectorPO.getConnectorName());
        if (connectorResult.failed()) {
            return Result.buildFromIgnoreData(connectorResult);
        }

        Properties props = new Properties();
        props.putAll(connectorResult.getData().getConfig());
        propList.add(props);

        // checkpoint
        if (!ValidateUtils.isBlank(connectorPO.getCheckpointConnectorName())) {
            connectorResult = connectorService.getConnectorInfoFromCluster(connectClusterId, connectorPO.getCheckpointConnectorName());
            if (connectorResult.failed()) {
                return Result.buildFromIgnoreData(connectorResult);
            }

            props = new Properties();
            props.putAll(connectorResult.getData().getConfig());
            propList.add(props);
        }


        // heartbeat
        if (!ValidateUtils.isBlank(connectorPO.getHeartbeatConnectorName())) {
            connectorResult = connectorService.getConnectorInfoFromCluster(connectClusterId, connectorPO.getHeartbeatConnectorName());
            if (connectorResult.failed()) {
                return Result.buildFromIgnoreData(connectorResult);
            }

            props = new Properties();
            props.putAll(connectorResult.getData().getConfig());
            propList.add(props);
        }

        return Result.buildSuc(propList);
    }

    @Override
    public Result<List<ConnectConfigInfosVO>> validateConnectors(MirrorMakerCreateDTO dto) {
        List<ConnectConfigInfosVO> voList = new ArrayList<>();

        Result<ConnectConfigInfos> infoResult = pluginService.validateConfig(dto.getConnectClusterId(), dto.getSuitableConfig());
        if (infoResult.failed()) {
            return Result.buildFromIgnoreData(infoResult);
        }

        voList.add(ConvertUtil.obj2Obj(infoResult.getData(), ConnectConfigInfosVO.class));

        if (dto.getHeartbeatConnectorConfigs() != null) {
            infoResult = pluginService.validateConfig(dto.getConnectClusterId(), dto.getHeartbeatConnectorConfigs());
            if (infoResult.failed()) {
                return Result.buildFromIgnoreData(infoResult);
            }

            voList.add(ConvertUtil.obj2Obj(infoResult.getData(), ConnectConfigInfosVO.class));
        }

        if (dto.getCheckpointConnectorConfigs() != null) {
            infoResult = pluginService.validateConfig(dto.getConnectClusterId(), dto.getCheckpointConnectorConfigs());
            if (infoResult.failed()) {
                return Result.buildFromIgnoreData(infoResult);
            }

            voList.add(ConvertUtil.obj2Obj(infoResult.getData(), ConnectConfigInfosVO.class));
        }

        return Result.buildSuc(voList);
    }


    /**************************************************** private method ****************************************************/

    private MetricsMirrorMakersDTO buildMetricsConnectorsDTO(List<ClusterConnectorDTO> connectorDTOList, MetricDTO metricDTO) {
        MetricsMirrorMakersDTO dto = ConvertUtil.obj2Obj(metricDTO, MetricsMirrorMakersDTO.class);
        dto.setConnectorNameList(connectorDTOList == null? new ArrayList<>(): connectorDTOList);

        return dto;
    }

    public Result<Void> checkCreateMirrorMakerParamAndUnifyData(MirrorMakerCreateDTO dto) {
        ClusterPhy sourceClusterPhy = clusterPhyService.getClusterByCluster(dto.getSourceKafkaClusterId());
        if (sourceClusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getSourceKafkaClusterId()));
        }

        ConnectCluster connectCluster = connectClusterService.getById(dto.getConnectClusterId());
        if (connectCluster == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getConnectClusterNotExist(dto.getConnectClusterId()));
        }

        ClusterPhy targetClusterPhy = clusterPhyService.getClusterByCluster(connectCluster.getKafkaClusterPhyId());
        if (targetClusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(connectCluster.getKafkaClusterPhyId()));
        }

        if (!dto.getSuitableConfig().containsKey(CONNECTOR_CLASS_FILED_NAME)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "SourceConnector缺少connector.class");
        }

        if (!MIRROR_MAKER_SOURCE_CONNECTOR_TYPE.equals(dto.getSuitableConfig().getProperty(CONNECTOR_CLASS_FILED_NAME))) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "SourceConnector的connector.class类型错误");
        }

        if (dto.getCheckpointConnectorConfigs() != null) {
            if (!dto.getCheckpointConnectorConfigs().containsKey(CONNECTOR_CLASS_FILED_NAME)) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "CheckpointConnector缺少connector.class");
            }

            if (!MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE.equals(dto.getCheckpointConnectorConfigs().getProperty(CONNECTOR_CLASS_FILED_NAME))) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "Checkpoint的connector.class类型错误");
            }
        }

        if (dto.getHeartbeatConnectorConfigs() != null) {
            if (!dto.getHeartbeatConnectorConfigs().containsKey(CONNECTOR_CLASS_FILED_NAME)) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "HeartbeatConnector缺少connector.class");
            }

            if (!MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE.equals(dto.getHeartbeatConnectorConfigs().getProperty(CONNECTOR_CLASS_FILED_NAME))) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "Heartbeat的connector.class类型错误");
            }
        }

        dto.unifyData(
                sourceClusterPhy.getId(), sourceClusterPhy.getBootstrapServers(), ConvertUtil.str2ObjByJson(sourceClusterPhy.getClientProperties(), Properties.class),
                targetClusterPhy.getId(), targetClusterPhy.getBootstrapServers(), ConvertUtil.str2ObjByJson(targetClusterPhy.getClientProperties(), Properties.class)
        );

        return Result.buildSuc();
    }

    private MirrorMakerStateVO convert2MirrorMakerStateVO(List<ConnectorPO> connectorPOList,List<WorkerConnector> workerConnectorList,List<ConnectWorker> workerList){
        MirrorMakerStateVO mirrorMakerStateVO = new MirrorMakerStateVO();

        List<ConnectorPO> sourceSet = connectorPOList.stream().filter(elem -> elem.getConnectorClassName().equals(MIRROR_MAKER_SOURCE_CONNECTOR_TYPE)).collect(Collectors.toList());
        mirrorMakerStateVO.setMirrorMakerCount(sourceSet.size());

        Set<Long> connectClusterIdSet = sourceSet.stream().map(ConnectorPO::getConnectClusterId).collect(Collectors.toSet());
        mirrorMakerStateVO.setWorkerCount(workerList.stream().filter(elem -> connectClusterIdSet.contains(elem.getConnectClusterId())).collect(Collectors.toList()).size());

        List<ConnectorPO> mirrorMakerConnectorList = new ArrayList<>();
        mirrorMakerConnectorList.addAll(sourceSet);
        mirrorMakerConnectorList.addAll(connectorPOList.stream().filter(elem -> elem.getConnectorClassName().equals(MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE)).collect(Collectors.toList()));
        mirrorMakerConnectorList.addAll(connectorPOList.stream().filter(elem -> elem.getConnectorClassName().equals(MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE)).collect(Collectors.toList()));
        mirrorMakerStateVO.setTotalConnectorCount(mirrorMakerConnectorList.size());
        mirrorMakerStateVO.setAliveConnectorCount(mirrorMakerConnectorList.stream().filter(elem -> elem.getState().equals(RUNNING.name())).collect(Collectors.toList()).size());

        Set<String> connectorNameSet = mirrorMakerConnectorList.stream().map(elem -> elem.getConnectorName()).collect(Collectors.toSet());
        List<WorkerConnector> taskList = workerConnectorList.stream().filter(elem -> connectorNameSet.contains(elem.getConnectorName())).collect(Collectors.toList());
        mirrorMakerStateVO.setTotalTaskCount(taskList.size());
        mirrorMakerStateVO.setAliveTaskCount(taskList.stream().filter(elem -> elem.getState().equals(RUNNING.name())).collect(Collectors.toList()).size());

        return mirrorMakerStateVO;
    }

    private List<ClusterMirrorMakerOverviewVO> convert2ClusterMirrorMakerOverviewVO(List<ConnectorPO> mirrorMakerList, List<ConnectCluster> connectClusterList, List<MirrorMakerMetrics> latestMetric) {
        List<ClusterMirrorMakerOverviewVO> clusterMirrorMakerOverviewVOList = new ArrayList<>();
        Map<String, MirrorMakerMetrics> metricsMap = latestMetric.stream().collect(Collectors.toMap(elem -> elem.getConnectClusterId() + "@" + elem.getConnectorName(), Function.identity()));
        Map<Long, ConnectCluster> connectClusterMap = connectClusterList.stream().collect(Collectors.toMap(elem -> elem.getId(), Function.identity()));

        for (ConnectorPO mirrorMaker : mirrorMakerList) {
            ClusterMirrorMakerOverviewVO clusterMirrorMakerOverviewVO = new ClusterMirrorMakerOverviewVO();
            clusterMirrorMakerOverviewVO.setConnectClusterId(mirrorMaker.getConnectClusterId());
            clusterMirrorMakerOverviewVO.setConnectClusterName(connectClusterMap.get(mirrorMaker.getConnectClusterId()).getName());
            clusterMirrorMakerOverviewVO.setConnectorName(mirrorMaker.getConnectorName());
            clusterMirrorMakerOverviewVO.setState(mirrorMaker.getState());
            clusterMirrorMakerOverviewVO.setCheckpointConnector(mirrorMaker.getCheckpointConnectorName());
            clusterMirrorMakerOverviewVO.setTaskCount(mirrorMaker.getTaskCount());
            clusterMirrorMakerOverviewVO.setHeartbeatConnector(mirrorMaker.getHeartbeatConnectorName());
            clusterMirrorMakerOverviewVO.setLatestMetrics(metricsMap.getOrDefault(mirrorMaker.getConnectClusterId() + "@" + mirrorMaker.getConnectorName(), new MirrorMakerMetrics(mirrorMaker.getConnectClusterId(), mirrorMaker.getConnectorName())));
            clusterMirrorMakerOverviewVOList.add(clusterMirrorMakerOverviewVO);
        }
        return clusterMirrorMakerOverviewVOList;
    }

    PaginationResult<ClusterMirrorMakerOverviewVO> pagingMirrorMakerInLocal(List<ClusterMirrorMakerOverviewVO> mirrorMakerOverviewVOList, ClusterMirrorMakersOverviewDTO dto) {
        List<ClusterMirrorMakerOverviewVO> mirrorMakerVOList = PaginationUtil.pageByFuzzyFilter(mirrorMakerOverviewVOList, dto.getSearchKeywords(), Arrays.asList("connectorName"));

        //排序
        if (!dto.getLatestMetricNames().isEmpty()) {
            PaginationMetricsUtil.sortMetrics(mirrorMakerVOList, "latestMetrics", dto.getSortMetricNameList(), "connectorName", dto.getSortType());
        } else {
            PaginationUtil.pageBySort(mirrorMakerVOList, dto.getSortField(), dto.getSortType(), "connectorName", dto.getSortType());
        }

        //分页
        return PaginationUtil.pageBySubData(mirrorMakerVOList, dto);
    }

    public static List<ClusterMirrorMakerOverviewVO> supplyData2ClusterMirrorMakerOverviewVOList(List<ClusterMirrorMakerOverviewVO> voList,
                                                                                                 List<MetricMultiLinesVO> metricLineVOList) {
        Map<String, List<MetricLineVO>> metricLineMap = new HashMap<>();
        if (metricLineVOList != null) {
            for (MetricMultiLinesVO metricMultiLinesVO : metricLineVOList) {
                metricMultiLinesVO.getMetricLines()
                        .forEach(metricLineVO -> {
                            String key = metricLineVO.getName();
                            List<MetricLineVO> metricLineVOS = metricLineMap.getOrDefault(key, new ArrayList<>());
                            metricLineVOS.add(metricLineVO);
                            metricLineMap.put(key, metricLineVOS);
                        });
            }
        }

        voList.forEach(elem -> elem.setMetricLines(metricLineMap.get(elem.getConnectClusterId() + "#" + elem.getConnectorName())));

        return voList;
    }

    private List<ClusterMirrorMakerOverviewVO> completeClusterInfo(List<ClusterMirrorMakerOverviewVO> mirrorMakerVOList) {

        Map<String, KSConnectorInfo> connectorInfoMap = new ConcurrentHashMap<>();

        for (ClusterMirrorMakerOverviewVO mirrorMakerVO : mirrorMakerVOList) {
            ApiCallThreadPoolService.runnableTask(String.format("method=completeClusterInfo||connectClusterId=%d||connectorName=%s||getMirrorMakerInfo", mirrorMakerVO.getConnectClusterId(), mirrorMakerVO.getConnectorName()),
                    3000
                    , () -> {
                        Result<KSConnectorInfo> connectorInfoRet = connectorService.getConnectorInfoFromCluster(mirrorMakerVO.getConnectClusterId(), mirrorMakerVO.getConnectorName());
                        if (connectorInfoRet.hasData()) {
                            connectorInfoMap.put(mirrorMakerVO.getConnectClusterId() + mirrorMakerVO.getConnectorName(), connectorInfoRet.getData());
                        }
                    });
        }

        ApiCallThreadPoolService.waitResult();

        List<ClusterMirrorMakerOverviewVO> newMirrorMakerVOList = new ArrayList<>();
        for (ClusterMirrorMakerOverviewVO mirrorMakerVO : mirrorMakerVOList) {
            KSConnectorInfo connectorInfo = connectorInfoMap.get(mirrorMakerVO.getConnectClusterId() + mirrorMakerVO.getConnectorName());
            if (connectorInfo == null) {
                continue;
            }

            String sourceClusterAlias = connectorInfo.getConfig().get(MIRROR_MAKER_SOURCE_CLUSTER_ALIAS_FIELD_NAME);
            String targetClusterAlias = connectorInfo.getConfig().get(MIRROR_MAKER_TARGET_CLUSTER_ALIAS_FIELD_NAME);
            //先默认设置为集群别名
            mirrorMakerVO.setSourceKafkaClusterName(sourceClusterAlias);
            mirrorMakerVO.setDestKafkaClusterName(targetClusterAlias);

            if (!ValidateUtils.isBlank(sourceClusterAlias) && CommonUtils.isNumeric(sourceClusterAlias)) {
                ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(Long.valueOf(sourceClusterAlias));
                if (clusterPhy != null) {
                    mirrorMakerVO.setSourceKafkaClusterId(clusterPhy.getId());
                    mirrorMakerVO.setSourceKafkaClusterName(clusterPhy.getName());
                }
            }

            if (!ValidateUtils.isBlank(targetClusterAlias) && CommonUtils.isNumeric(targetClusterAlias)) {
                ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(Long.valueOf(targetClusterAlias));
                if (clusterPhy != null) {
                    mirrorMakerVO.setDestKafkaClusterId(clusterPhy.getId());
                    mirrorMakerVO.setDestKafkaClusterName(clusterPhy.getName());
                }
            }

            newMirrorMakerVOList.add(mirrorMakerVO);

        }

        return newMirrorMakerVOList;
    }
}
