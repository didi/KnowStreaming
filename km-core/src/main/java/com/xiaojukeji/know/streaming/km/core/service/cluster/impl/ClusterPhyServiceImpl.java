package com.xiaojukeji.know.streaming.km.core.service.cluster.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.ClusterPhyAddedEvent;
import com.xiaojukeji.know.streaming.km.common.bean.event.cluster.connect.ClusterPhyDeletedEvent;
import com.xiaojukeji.know.streaming.km.common.bean.po.cluster.ClusterPhyPO;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.DuplicateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.ParamErrorException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.cluster.ClusterPhyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author didi
 */
@Service
public class ClusterPhyServiceImpl implements ClusterPhyService {
    private static final ILog log = LogFactory.getLog(ClusterPhyServiceImpl.class);

    @Autowired
    private ClusterPhyDAO clusterPhyDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    public String getVersionFromCacheFirst(Long clusterPhyId) {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        if (clusterPhy != null) {
            return clusterPhy.getKafkaVersion();
        }

        ClusterPhyPO clusterPhyPO = clusterPhyDAO.selectById(clusterPhyId);
        if(null == clusterPhyPO) {
            return "";
        }

        return clusterPhyPO.getKafkaVersion();
    }

    @Override
    public ClusterPhy getClusterByCluster(Long clusterId) {
        return ConvertUtil.obj2Obj(clusterPhyDAO.selectById(clusterId), ClusterPhy.class);
    }

    @Override
    public ClusterPhy getClusterByClusterName(String clusterPhyName) {
        LambdaQueryWrapper<ClusterPhyPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClusterPhyPO::getName, clusterPhyName);
        ClusterPhyPO phyPO = clusterPhyDAO.selectOne(lambdaQueryWrapper);
        if (phyPO == null) {
            return null;
        }

        return ConvertUtil.obj2Obj(phyPO, ClusterPhy.class);
    }

    @Override
    public List<ClusterPhy> listAllClusters() {
        List<ClusterPhyPO> clusterPhyPOS = clusterPhyDAO.selectList(null);
        return ConvertUtil.list2List(clusterPhyPOS, ClusterPhy.class);
    }

    @Override
    public Long addClusterPhy(ClusterPhyPO clusterPhyPO, String operator) throws ParamErrorException, DuplicateException, AdminOperateException {
        log.info("method=addClusterPhy||clusterPhyPO={}||operator={}||msg=add cluster start", clusterPhyPO, operator);

        // 检查字段
        Result<Void> rv = ClusterPhyPO.checkFieldAddToDBLegal(clusterPhyPO, operator);
        if (rv.failed()) {
            log.warn("method=addClusterPhy||clusterPhyPO={}||operator={}||result={}||errMsg=add cluster failed", clusterPhyPO, operator, rv);

            // 字段非法，则直接抛出异常
            throw new ParamErrorException(rv.getMessage());
        }

        try {
            clusterPhyDAO.addAndSetId(clusterPhyPO);

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.ADD.getDesc(),
                    ModuleEnum.KAFKA_CLUSTER.getDesc(),
                    MsgConstant.getClusterBizStr(clusterPhyPO.getId(), clusterPhyPO.getName()),
                    String.format("新增集群:%s",clusterPhyPO.toString()));
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            log.info("method=addClusterPhy||clusterPhyId={}||operator={}||msg=add cluster finished", clusterPhyPO.getId(), operator);

            // 发布添加集群事件
            SpringTool.publish(new ClusterPhyAddedEvent(this, clusterPhyPO.getId()));
            return clusterPhyPO.getId();
        } catch (DuplicateKeyException dke) {
            log.warn("method=addClusterPhy||clusterPhyId={}||operator={}||msg=add cluster failed||errMsg=duplicate data", clusterPhyPO.getId(), operator);

            throw new DuplicateException(String.format("clusterName:%s duplicated", clusterPhyPO.getName()));
        } catch (Exception e) {
            log.error("method=addClusterPhy||clusterPhyId={}||operator={}||msg=add cluster failed||errMsg=exception!", clusterPhyPO.getId(), operator, e);

            throw new AdminOperateException("add cluster failed", e, ResultStatus.MYSQL_OPERATE_FAILED);
        }
    }

    @Override
    public Result<Void> removeClusterPhyById(Long clusterPhyId, String operator) {
        log.info("method=removeClusterPhyById||clusterPhyId={}||operator={}||msg=remove cluster start",
                clusterPhyId, operator);

        try {
            ClusterPhy clusterPhy = this.getClusterByCluster(clusterPhyId);
            if (clusterPhy == null) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
            }

            clusterPhyDAO.deleteById(clusterPhyId);

            log.info("method=removeClusterPhyById||clusterPhyId={}||operator={}||msg=remove cluster finished",
                    clusterPhyId, operator);

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_CLUSTER.getDesc(),
                    MsgConstant.getClusterBizStr(clusterPhy.getId(), clusterPhy.getName()),
                    String.format("删除集群:%s",clusterPhy.toString()));
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            // 发布删除集群事件
            SpringTool.publish(new ClusterPhyDeletedEvent(this, clusterPhyId));

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=removeClusterPhyById||clusterPhyId={}||operator={}||msg=remove cluster failed||errMsg=exception!",
                    clusterPhyId, operator, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public void modifyClusterPhyById(ClusterPhyPO clusterPhyPO, String operator) throws
            ParamErrorException,
            DuplicateException,
            NotExistException,
            AdminOperateException {
        log.info("method=modifyClusterPhyById||clusterPhyPO={}||operator={}||msg=modify cluster start",
                clusterPhyPO, operator);

        // 检查字段
        Result<Void> rv = ClusterPhyPO.checkFieldModifyToDBLegal(clusterPhyPO, operator);
        if (rv.failed()) {
            log.warn("method=modifyClusterPhyById||clusterPhyPO={}||result={}||operator={}||msg=modify cluster failed||errMsg=param illegal",
                    clusterPhyPO, rv, operator);

            // 字段非法，则直接抛出异常
            throw new ParamErrorException(rv.getMessage());
        }

        try {
            int affectRow = clusterPhyDAO.updateById(clusterPhyPO);
            if (affectRow <= 0) {
                throw new NotExistException(MsgConstant.getClusterPhyNotExist(clusterPhyPO.getId()));
            }

            // 记录操作
            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.KAFKA_CLUSTER.getDesc(),
                    MsgConstant.getClusterBizStr(clusterPhyPO.getId(), clusterPhyPO.getName()),
                    String.format("修改集群, 详情参数信息:%s", clusterPhyPO.toString()))
            );

            log.info("method=modifyClusterPhyById||clusterPhyId={}||operator={}||msg=modify cluster finished",
                    clusterPhyPO.getId(), operator);
        } catch (DuplicateKeyException dke) {
            log.warn("method=modifyClusterPhyById||clusterPhyPO={}||operator={}||msg=modify cluster failed||errMsg=duplicate clusterName",
                    clusterPhyPO, operator);

            throw new DuplicateException(String.format("clusterName:%s duplicated", clusterPhyPO.getName()));
        } catch (NotExistException nee) {
            log.error("method=modifyClusterPhyById||clusterPhyPO={}||operator={}||msg=modify cluster failed||errMsg=cluster not exist",
                    clusterPhyPO, operator);

            throw nee;
        } catch (Exception e) {
            log.error("method=modifyClusterPhyById||clusterPhyPO={}||operator={}||msg=modify cluster failed||errMsg=exception!",
                    clusterPhyPO, operator, e);

            throw new AdminOperateException("modify cluster failed", e, ResultStatus.MYSQL_OPERATE_FAILED);
        }
    }

    @Override
    public List<String> getClusterVersionList() {
        List<ClusterPhy> clusterPhyList = this.listAllClusters();

        List<String> versionList = new ArrayList<>(clusterPhyList.stream().map(elem -> elem.getKafkaVersion()).collect(Collectors.toSet()));
        Collections.sort(versionList);

        return versionList;
    }
}
