package com.xiaojukeji.know.streaming.km.core.service.zookeeper.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.Znode;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ZnodeConverter;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZnodeService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ZnodeServiceImpl implements ZnodeService {
    private static final ILog LOGGER = LogFactory.getLog(ZnodeServiceImpl.class);

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private ClusterPhyService clusterPhyService;



    @Override
    public Result<List<String>> listZnodeChildren(Long clusterPhyId, String path, String keyword) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        List<String> children;
        try {
            children = kafkaZKDAO.getChildren(clusterPhyId, path, false);
        } catch (NotExistException e) {
            LOGGER.error("method=listZnodeChildren||clusterPhyId={}||errMsg={}", clusterPhyId, "create ZK client create failed");
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, "ZK客户端创建失败");
        } catch (Exception e) {
            LOGGER.error("method=listZnodeChildren||clusterPhyId={}||errMsg={}", clusterPhyId, "ZK operate failed");
            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, "ZK操作失败");
        }

        //关键字搜索
        if (keyword != null) {
            children = children.stream().filter(elem -> elem.contains(keyword)).collect(Collectors.toList());
        }
        return Result.buildSuc(children);
    }

    @Override
    public Result<Znode> getZnode(Long clusterPhyId, String path) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        //获取zookeeper上的原始数据
        Tuple<byte[], Stat> dataAndStat;
        try {
            dataAndStat = kafkaZKDAO.getDataAndStat(clusterPhyId, path);
        } catch (NotExistException e) {
            LOGGER.error("method=getZnode||clusterPhyId={}||errMsg={}", clusterPhyId, "create ZK client create failed");
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, "ZK客户端创建失败");
        } catch (Exception e) {
            LOGGER.error("method=getZnode||clusterPhyId={}||errMsg={}", clusterPhyId, "ZK operate failed");
            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, "ZK操作失败");
        }

        return Result.buildSuc(ZnodeConverter.convert2Znode(clusterPhy, dataAndStat, path));
    }
}
