package com.xiaojukeji.know.streaming.km.core.service.connect.cluster.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.cluster.ConnectClusterDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectClusterMetadata;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectClusterPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectClusterDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class ConnectClusterServiceImpl implements ConnectClusterService {
    private static final ILog LOGGER = LogFactory.getLog(ConnectClusterServiceImpl.class);

    @Autowired
    private ConnectClusterDAO connectClusterDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    public int deleteInDBByKafkaClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, clusterPhyId);

        return connectClusterDAO.deleteById(lambdaQueryWrapper);
    }

    @Override
    public Long replaceAndReturnIdInDB(ConnectClusterMetadata metadata) {
        ConnectClusterPO oldPO = this.getPOFromDB(metadata.getKafkaClusterPhyId(), metadata.getGroupName());
        if (oldPO == null) {
            oldPO = new ConnectClusterPO();
            oldPO.setKafkaClusterPhyId(metadata.getKafkaClusterPhyId());
            oldPO.setGroupName(metadata.getGroupName());
            oldPO.setName(metadata.getGroupName());
            oldPO.setState(metadata.getState().getCode());
            oldPO.setMemberLeaderUrl(metadata.getMemberLeaderUrl());
            oldPO.setClusterUrl("");
            oldPO.setVersion(KafkaConstant.DEFAULT_CONNECT_VERSION);
            connectClusterDAO.insert(oldPO);

            oldPO = this.getPOFromDB(metadata.getKafkaClusterPhyId(), metadata.getGroupName());
            return oldPO == null? null: oldPO.getId();
        }

        oldPO.setKafkaClusterPhyId(metadata.getKafkaClusterPhyId());
        oldPO.setGroupName(metadata.getGroupName());
        oldPO.setState(metadata.getState().getCode());
        oldPO.setMemberLeaderUrl(metadata.getMemberLeaderUrl());
        if (ValidateUtils.isBlank(oldPO.getVersion())) {
            oldPO.setVersion(KafkaConstant.DEFAULT_CONNECT_VERSION);
        }
        if (ValidateUtils.isNull(oldPO.getClusterUrl())) {
            oldPO.setClusterUrl("");
        }

        connectClusterDAO.updateById(oldPO);
        return oldPO.getId();
    }

    @Override
    public List<ConnectCluster> listByKafkaCluster(Long kafkaClusterPhyId) {
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, kafkaClusterPhyId);

        return ConvertUtil.list2List(connectClusterDAO.selectList(lambdaQueryWrapper), ConnectCluster.class);
    }

    @Override
    public List<ConnectCluster> listAllClusters() {
        List<ConnectClusterPO> connectClusterPOList = connectClusterDAO.selectList(null);
        return ConvertUtil.list2List(connectClusterPOList, ConnectCluster.class);
    }

    @Override
    public ConnectCluster getById(Long connectClusterId) {
        return ConvertUtil.obj2Obj(connectClusterDAO.selectById(connectClusterId), ConnectCluster.class);
    }

    @Override
    public ConnectCluster getByName(Long clusterPhyId, String connectClusterName) {
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(ConnectClusterPO::getName, connectClusterName);

        return ConvertUtil.obj2Obj(connectClusterDAO.selectOne(lambdaQueryWrapper), ConnectCluster.class);
    }

    @Override
    public String getClusterVersion(Long connectClusterId) {
        ConnectClusterPO connectClusterPO = connectClusterDAO.selectById(connectClusterId);
        return null != connectClusterPO ? connectClusterPO.getVersion() : "";
    }

    @Override
    public String getClusterName(Long connectClusterId) {
        ConnectClusterPO connectClusterPO = connectClusterDAO.selectById(connectClusterId);
        return null != connectClusterPO ? connectClusterPO.getName() : "";
    }

    @Override
    public Result<Void> deleteInDB(Long connectClusterId, String operator) {
        ConnectCluster connectCluster = this.getById(connectClusterId);
        if (connectCluster == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
        }

        if (!GroupStateEnum.DEAD.getCode().equals(connectCluster.getState())) {
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, "只有集群处于Dead状态，才允许删除");
        }

        connectClusterDAO.deleteById(connectClusterId);

        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.DELETE.getDesc(),
                ModuleEnum.KAFKA_CONNECT_CONNECTOR.getDesc(),
                MsgConstant.getConnectClusterBizStr(connectCluster.getId(), connectCluster.getName()),
                ConvertUtil.obj2Json(connectCluster)
        ));

        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> batchModifyInDB(List<ConnectClusterDTO> dtoList, String operator) {
        LOGGER.info("method=batchModifyInDB||data={}||operator={}", dtoList, operator);

        for (ConnectClusterDTO dto: dtoList) {
            if (!dto.getClusterUrl().startsWith("http://") && !dto.getClusterUrl().startsWith("https://")) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "clusterUrl必须以http或者https开头");
            }
        }

        for (ConnectClusterDTO dto: dtoList) {
            try {
                ConnectClusterPO po = this.getRowById(dto.getId());
                if (po == null) {
                    // 回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(dto.getId()));
                }

                if (!ValidateUtils.isNull(dto.getName())) {
                    po.setName(dto.getName());
                }

                if (!ValidateUtils.isNull(dto.getClusterUrl())) {
                    String clusterUrl = dto.getClusterUrl();
                    if (clusterUrl.charAt(clusterUrl.length() - 1) == '/') {
                        clusterUrl = clusterUrl.substring(0, clusterUrl.length() - 1);
                    }
                    po.setClusterUrl(clusterUrl);
                }
                if (!ValidateUtils.isNull(dto.getVersion())) {
                    po.setVersion(dto.getVersion());
                }
                if (!ValidateUtils.isNull(dto.getJmxProperties())) {
                    po.setJmxProperties(dto.getJmxProperties());
                }

                connectClusterDAO.updateById(po);

                // 记录操作
                opLogWrapService.saveOplogAndIgnoreException(
                        new OplogDTO(
                            operator,
                            OperationEnum.EDIT.getDesc(),
                            ModuleEnum.KAFKA_CONNECT_CLUSTER.getDesc(),
                            MsgConstant.getConnectClusterBizStr(dto.getId(), dto.getName()),
                            ConvertUtil.obj2Json(po)
                        )
                );
            } catch (DuplicateKeyException dke) {
                LOGGER.error(
                        "method=batchModifyInDB||data={}||operator={}||errMsg=connectCluster name duplicate",
                        dtoList, operator
                );
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "connect集群name重复");

            } catch (Exception e) {
                LOGGER.error(
                        "method=batchModifyInDB||data={}||operator={}||errMsg=exception",
                        dtoList, operator, e
                );

                // 回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
            }
        }

        return Result.buildSuc();
    }

    @Override
    public Boolean existConnectClusterDown(Long kafkaClusterPhyId) {
        List<ConnectCluster> connectClusters = this.listByKafkaCluster(kafkaClusterPhyId);
        for (ConnectCluster connectCluster : connectClusters) {
            if (GroupStateEnum.getByState(String.valueOf(connectCluster.getState())) == GroupStateEnum.DEAD)
                return true;
        }
        return false;
    }

    /**************************************************** private method ****************************************************/

    private ConnectClusterPO getPOFromDB(Long kafkaClusterPhyId, String groupName) {
        LambdaQueryWrapper<ConnectClusterPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectClusterPO::getGroupName, groupName);
        lambdaQueryWrapper.eq(ConnectClusterPO::getKafkaClusterPhyId, kafkaClusterPhyId);

        return connectClusterDAO.selectOne(lambdaQueryWrapper);
    }

    public ConnectClusterPO getRowById(Long connectClusterId) {
        return connectClusterDAO.selectById(connectClusterId);
    }
}
