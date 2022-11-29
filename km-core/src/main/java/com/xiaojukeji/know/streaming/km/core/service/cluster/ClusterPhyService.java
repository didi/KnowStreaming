package com.xiaojukeji.know.streaming.km.core.service.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.cluster.ClusterPhyPO;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.DuplicateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.ParamErrorException;

import java.util.List;

/**
 * @author didi
 */
public interface ClusterPhyService {
    /**
     * 先从cache中获取，如果获取不到再从DB获取
     */
    String getVersionFromCacheFirst(Long clusterPhyId);

    /**
     */
    ClusterPhy getClusterByCluster(Long clusterId);

    ClusterPhy getClusterByClusterName(String clusterPhyName);

    /**
     *
     * @return
     */
    List<ClusterPhy> listAllClusters();

    /**
     * 添加集群
     * @param clusterPhyPO 集群信息
     * @param operator 操作人
     * @return
     * @throws ParamErrorException 参数异常
     * @throws DuplicateException 数据重复异常
     * @throws AdminOperateException 操作异常
     */
    Long addClusterPhy(ClusterPhyPO clusterPhyPO, String operator) throws
            ParamErrorException,
            DuplicateException,
            AdminOperateException;

    /**
     * 移除集群
     * @param clusterPhyId 集群ID
     * @param operator 操作人
     * @return
     */
    Result<Void> removeClusterPhyById(Long clusterPhyId, String operator);

    /**
     * 修改集群
     * @param clusterPhyPO 集群信息
     * @param operator 操作人
     * @throws ParamErrorException 参数错误
     * @throws DuplicateException 数据重复异常
     * @throws NotExistException 集群不存在
     * @throws AdminOperateException 操作错误
     */
    void modifyClusterPhyById(ClusterPhyPO clusterPhyPO, String operator) throws
            ParamErrorException,
            DuplicateException,
            NotExistException,
            AdminOperateException;

    /**
     * 获取系统已存在的kafka版本列表
     * @return
     */
    List<String> getClusterVersionList();
}
