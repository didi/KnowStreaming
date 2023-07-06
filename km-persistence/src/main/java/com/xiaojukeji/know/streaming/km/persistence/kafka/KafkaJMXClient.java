package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerPO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.persistence.AbstractClusterLoadedChangedHandler;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.broker.BrokerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class KafkaJMXClient extends AbstractClusterLoadedChangedHandler {
    private static final ILog log = LogFactory.getLog(KafkaJMXClient.class);

    @Autowired
    private BrokerDAO brokerDAO;

    private static final Map<Long, Map<Integer, JmxConnectorWrap>> JMX_MAP = new ConcurrentHashMap<>();

    public JmxConnectorWrap getClientWithCheck(Long clusterPhyId, Integer brokerId){
        JmxConnectorWrap jmxConnectorWrap = this.getClient(clusterPhyId, brokerId);

        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            log.error("method=getClientWithCheck||clusterPhyId={}||brokerId={}||msg=get jmx connector failed!", clusterPhyId, brokerId);
            return null;
        }

        return jmxConnectorWrap;
    }

    public JmxConnectorWrap getClient(Long clusterPhyId, Integer brokerId) {
        Map<Integer, JmxConnectorWrap> jmxMap = JMX_MAP.getOrDefault(clusterPhyId, new ConcurrentHashMap<>());

        JmxConnectorWrap jmxConnectorWrap = jmxMap.get(brokerId);
        if (jmxConnectorWrap != null) {
            // 已新建成功，则直接返回
            return jmxConnectorWrap;
        }

        // 未创建，则进行创建
        return this.createJmxConnectorWrap(clusterPhyId, brokerId);
    }

    public void checkAndRemoveIfIllegal(Long clusterPhyId, List<Broker> allAliveBrokerList) {
        Map<Integer, JmxConnectorWrap> jmxMap = JMX_MAP.get(clusterPhyId);
        if (jmxMap == null) {
            return;
        }

        // 转换格式
        Map<Integer, Long> brokerIdAndStartTimeMap = allAliveBrokerList.stream().collect(Collectors.toMap(Broker::getBrokerId, Broker::getStartTimestamp));

        // 获取jmx客户端非法的brokerId
        Set<Integer> illegalBrokerIdSet = jmxMap.entrySet()
                .stream()
                .filter(entry -> {
                    Long startTime = brokerIdAndStartTimeMap.get(entry.getKey());
                    if (startTime == null) {
                        // broker不存在
                        return true;
                    }

                    return entry.getValue().brokerChanged(startTime);
                })
                .map(elem -> elem.getKey())
                .collect(Collectors.toSet());

        for (Integer brokerId: illegalBrokerIdSet) {
            log.warn("method=checkAndRemoveIfIllegal||clusterPhyId={}||brokerId={}||msg=remove jmx-client", clusterPhyId, brokerId);

            JmxConnectorWrap jmxConnectorWrap = jmxMap.remove(brokerId);
            if (jmxConnectorWrap == null) {
                continue;
            }

            jmxConnectorWrap.close();
        }
    }

    /**************************************************** private method ****************************************************/

    @Override
    protected void add(ClusterPhy clusterPhy) {
        JMX_MAP.putIfAbsent(clusterPhy.getId(), new ConcurrentHashMap<>());
    }

    @Override
    protected void modify(ClusterPhy newClusterPhy, ClusterPhy oldClusterPhy) {
        if (newClusterPhy.getClientProperties().equals(oldClusterPhy.getClientProperties())
                && newClusterPhy.getZookeeper().equals(oldClusterPhy.getZookeeper())
                && newClusterPhy.getBootstrapServers().equals(oldClusterPhy.getBootstrapServers())
                && newClusterPhy.getJmxProperties().equals(oldClusterPhy.getJmxProperties())) {
            // 集群信息虽然变化，但是相关没有变化，则直接返回
            return;
        }

        // 存在变化时, 则先移除, 再重新加入
        this.remove(newClusterPhy);

        this.add(newClusterPhy);
    }

    @Override
    protected void remove(ClusterPhy clusterPhy) {
        Map<Integer, JmxConnectorWrap> jmxMap = JMX_MAP.remove(clusterPhy.getId());
        if (jmxMap == null) {
            return;
        }

        for (JmxConnectorWrap jmxConnectorWrap: jmxMap.values()) {
            jmxConnectorWrap.close();
        }
    }

    private JmxConnectorWrap createJmxConnectorWrap(Long clusterPhyId, Integer brokerId) {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        if (clusterPhy == null) {
            // 集群不存在
            return null;
        }

        return this.createJmxConnectorWrap(clusterPhy, brokerId);
    }

    private JmxConnectorWrap createJmxConnectorWrap(ClusterPhy clusterPhy, Integer brokerId) {
        Broker broker = this.getBrokerFromDB(clusterPhy.getId(), brokerId);
        if (broker == null) {
            return null;
        }

        try {
            modifyClientMapLock.lock();

            JmxConnectorWrap jmxMap = JMX_MAP.getOrDefault(clusterPhy.getId(), new ConcurrentHashMap<>()).get(brokerId);
            if (jmxMap != null) {
                return jmxMap;
            }

            log.info("method=createJmxConnectorWrap||clusterPhyId={}||brokerId={}||msg=create JmxConnectorWrap starting", clusterPhy.getId(), brokerId);

            JmxConfig jmxConfig = ConvertUtil.str2ObjByJson(clusterPhy.getJmxProperties(), JmxConfig.class);
            if (jmxConfig == null) {
                jmxConfig = new JmxConfig();
            }

            JmxConnectorWrap jmxConnectorWrap = new JmxConnectorWrap(
                    String.format("clusterPhyId=%s,brokerId=%d", clusterPhy.getId(), brokerId),
                    broker.getStartTimestamp(),
                    broker.getJmxHost(jmxConfig.getUseWhichEndpoint()),
                    jmxConfig.getFinallyJmxPort(String.valueOf(brokerId), broker.getJmxPort()),
                    jmxConfig
            );

            JMX_MAP.get(clusterPhy.getId()).put(brokerId, jmxConnectorWrap);

            log.info("method=createJmxConnectorWrap||clusterPhyId={}||brokerId={}||msg=create JmxConnectorWrap success", clusterPhy.getId(), brokerId);

            return jmxConnectorWrap;
        } catch (Exception e) {
            log.error("method=createJmxConnectorWrap||clusterPhyId={}||brokerId={}msg=create JmxConnectorWrap failed||errMsg=exception||", clusterPhy.getId(), brokerId, e);
        } finally {
            modifyClientMapLock.unlock();
        }

        return null;
    }

    private Broker getBrokerFromDB(Long clusterPhyId, Integer brokerId) {
        LambdaQueryWrapper<BrokerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BrokerPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(BrokerPO::getBrokerId, brokerId);
        lambdaQueryWrapper.eq(BrokerPO::getStatus, Constant.ALIVE);

        BrokerPO brokerPO = brokerDAO.selectOne(lambdaQueryWrapper);
        if (brokerPO == null) {
            return null;
        }

        return Broker.buildFrom(brokerPO);
    }
}
