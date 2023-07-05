package com.xiaojukeji.know.streaming.km.core.service.zookeeper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.ServerCmdData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser.ServerCmdDataParser;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.zookeeper.ZKRoleEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.common.bean.po.zookeeper.ZookeeperInfoPO;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.FourLetterWordUtil;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.ZookeeperUtils;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.zookeeper.ZookeeperDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZookeeperServiceImpl implements ZookeeperService {
    private static final ILog LOGGER = LogFactory.getLog(ZookeeperServiceImpl.class);

    @Autowired
    private ZookeeperDAO zookeeperDAO;

    @Override
    public Result<List<ZookeeperInfo>> listFromZookeeper(Long clusterPhyId, String zookeeperAddress, ZKConfig zkConfig) {
        List<Tuple<String, Integer>> addressList = null;
        try {
            addressList = ZookeeperUtils.connectStringParser(zookeeperAddress);
        } catch (Exception e) {
            LOGGER.error(
                    "method=listFromZookeeperCluster||clusterPhyId={}||zookeeperAddress={}||errMsg=exception!",
                    clusterPhyId, zookeeperAddress, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, e.getMessage());
        }

        List<ZookeeperInfo> aliveZKList = new ArrayList<>();
        for (Tuple<String, Integer> hostPort: addressList) {
            aliveZKList.add(this.getFromZookeeperCluster(
                    clusterPhyId,
                    hostPort.getV1(),
                    hostPort.getV2(),
                    zkConfig
            ));
        }
        return Result.buildSuc(aliveZKList);
    }

    @Override
    public void batchReplaceDataInDB(Long clusterPhyId, List<ZookeeperInfo> infoList) {
        // DB 中的信息
        List<ZookeeperInfoPO> dbInfoList = this.listRawFromDBByCluster(clusterPhyId);
        Map<String, ZookeeperInfoPO> dbMap = new HashMap<>();
        dbInfoList.stream().forEach(elem -> dbMap.put(elem.getHost() + elem.getPort(), elem));

        // 新获取到的信息
        List<ZookeeperInfoPO> newInfoList = ConvertUtil.list2List(infoList, ZookeeperInfoPO.class);
        for (ZookeeperInfoPO newInfo: newInfoList) {
            try {
                ZookeeperInfoPO oldInfo = dbMap.remove(newInfo.getHost() + newInfo.getPort());
                if (oldInfo == null) {
                    zookeeperDAO.insert(newInfo);
                } else if (!Constant.DOWN.equals(newInfo.getStatus())) {
                    // 存活时，直接使用获取到的数据
                    newInfo.setId(oldInfo.getId());
                    zookeeperDAO.updateById(newInfo);
                } else {
                    // 如果挂了，则版本和角色信息，使用先前的信息。
                    // 挂掉之后，如果角色是leader，则需要调整一下
                    newInfo.setId(oldInfo.getId());
                    newInfo.setRole(ZKRoleEnum.LEADER.getRole().equals(oldInfo.getRole())? ZKRoleEnum.FOLLOWER.getRole(): oldInfo.getRole());
                    newInfo.setVersion(oldInfo.getVersion());
                    zookeeperDAO.updateById(newInfo);
                }
            } catch (Exception e) {
                LOGGER.error("method=batchReplaceDataInDB||clusterPhyId={}||newInfo={}||errMsg=exception", clusterPhyId, newInfo, e);
            }
        }

        // 删除剩余的ZK节点
        dbMap.entrySet().forEach(entry -> {
            try {
                zookeeperDAO.deleteById(entry.getValue().getId());
            } catch (Exception e) {
                LOGGER.error("method=batchReplaceDataInDB||clusterPhyId={}||expiredInfo={}||errMsg=exception", clusterPhyId, entry.getValue(), e);
            }
        });
    }

    @Override
    public List<ZookeeperInfo> listFromDBByCluster(Long clusterPhyId) {
        return ConvertUtil.list2List(this.listRawFromDBByCluster(clusterPhyId), ZookeeperInfo.class);
    }

    @Override
    public boolean allServerDown(Long clusterPhyId) {
        List<ZookeeperInfo> infoList = this.listFromDBByCluster(clusterPhyId);
        if (ValidateUtils.isEmptyList(infoList)) {
            return false;
        }

        // 所有服务挂掉
        return infoList.stream().filter(elem -> !elem.alive()).count() == infoList.size();
    }

    @Override
    public boolean existServerDown(Long clusterPhyId) {
        List<ZookeeperInfo> infoList = this.listFromDBByCluster(clusterPhyId);
        if (ValidateUtils.isEmptyList(infoList)) {
            // 不存在挂掉的服务
            return false;
        }

        // 存在挂掉的服务
        return infoList.stream().filter(elem -> !elem.alive()).count() > 0;
    }

    /**************************************************** private method ****************************************************/

    private List<ZookeeperInfoPO> listRawFromDBByCluster(Long clusterPhyId) {
        LambdaQueryWrapper<ZookeeperInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ZookeeperInfoPO::getClusterPhyId, clusterPhyId);

        return zookeeperDAO.selectList(lambdaQueryWrapper);
    }

    private ZookeeperInfo getFromZookeeperCluster(Long clusterPhyId, String host, Integer port, ZKConfig zkConfig) {
        ZookeeperInfo zookeeperInfo = new ZookeeperInfo();
        zookeeperInfo.setClusterPhyId(clusterPhyId);
        zookeeperInfo.setHost(host);
        zookeeperInfo.setPort(port);
        zookeeperInfo.setRole("");
        zookeeperInfo.setVersion("");
        zookeeperInfo.setStatus(Constant.DOWN);

        Result<ServerCmdData> serverCmdDataResult = FourLetterWordUtil.executeFourLetterCmd(
                clusterPhyId,
                host,
                port,
                zkConfig != null ? zkConfig.getOpenSecure(): false,
                zkConfig != null ? zkConfig.getRequestTimeoutUnitMs(): Constant.DEFAULT_REQUEST_TIMEOUT_UNIT_MS,
                new ServerCmdDataParser()
        );
        if (serverCmdDataResult.hasData()) {
            zookeeperInfo.setRole(serverCmdDataResult.getData().getZkServerState());
            zookeeperInfo.setVersion(serverCmdDataResult.getData().getZkVersion());
            zookeeperInfo.setStatus(Constant.ALIVE);
        } else if (serverCmdDataResult.getCode().equals(ResultStatus.ZK_FOUR_LETTER_CMD_FORBIDDEN.getCode())) {
            zookeeperInfo.setStatus(Constant.ZK_ALIVE_BUT_4_LETTER_FORBIDDEN);
        } else {
            return zookeeperInfo;
        }

        return zookeeperInfo;
    }
}
