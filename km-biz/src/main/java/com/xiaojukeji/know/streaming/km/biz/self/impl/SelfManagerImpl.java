package com.xiaojukeji.know.streaming.km.biz.self.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.service.UserService;
import com.xiaojukeji.know.streaming.km.biz.self.SelfManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.self.SelfMetricsVO;
import com.xiaojukeji.know.streaming.km.common.utils.GitPropUtil;
import com.xiaojukeji.know.streaming.km.common.utils.NetUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.km.KmNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class SelfManagerImpl implements SelfManager {
    private static final ILog log = LogFactory.getLog(SelfManagerImpl.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private UserService userService;

    @Autowired
    private KmNodeService kmNodeService;

    @Override
    public Result<SelfMetricsVO> metrics() {
        SelfMetricsVO vo = new SelfMetricsVO();

        // ks自身信息
        vo.setKsIp(NetUtils.localIp());
        vo.setKsClusterKey(NetUtils.localMac());

        List<UserBriefVO> userBriefVOList = userService.getAllUserBriefList();
        vo.setKsUserCount(ValidateUtils.isNull(userBriefVOList)? 0: userBriefVOList.size());
        vo.setKsServerIps(kmNodeService.listKmHosts());

        // 纳管集群信息
        vo.setKafkaClusterCount(clusterPhyService.listAllClusters().size());
        vo.setKafkaBrokerCount(brokerService.countAllBrokers());

        return Result.buildSuc(vo);
    }

    @Override
    public Result<Properties> version() {
        return Result.buildSuc(GitPropUtil.getProps());
    }
}
