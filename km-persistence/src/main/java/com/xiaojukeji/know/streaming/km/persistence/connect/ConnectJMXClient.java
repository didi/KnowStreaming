package com.xiaojukeji.know.streaming.km.persistence.connect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectWorkerPO;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import com.xiaojukeji.know.streaming.km.persistence.mysql.connect.ConnectWorkerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wyb
 * @date 2022/10/31
 */
@Component
public class ConnectJMXClient extends AbstractConnectClusterChangeHandler {
    private static final ILog log = LogFactory.getLog(ConnectJMXClient.class);

    private static final Map<Long, Map<String, JmxConnectorWrap>> JMX_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ConnectWorkerDAO connectWorkerDAO;


    public JmxConnectorWrap getClientWithCheck(Long connectClusterId, String workerId) {
        JmxConnectorWrap jmxConnectorWrap = this.getClient(connectClusterId, workerId);

        if (ValidateUtils.isNull(jmxConnectorWrap) || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
            log.error("method=getClientWithCheck||connectClusterId={}||workerId={}||msg=get jmx connector failed!", connectClusterId, workerId);
            return null;
        }

        return jmxConnectorWrap;
    }

    public JmxConnectorWrap getClient(Long connectorClusterId, String workerId) {
        Map<String, JmxConnectorWrap> jmxMap = JMX_MAP.getOrDefault(connectorClusterId, new ConcurrentHashMap<>());

        JmxConnectorWrap jmxConnectorWrap = jmxMap.get(workerId);
        if (jmxConnectorWrap != null) {
            // 已新建成功，则直接返回
            return jmxConnectorWrap;
        }

        // 未创建，则进行创建
        return this.createJmxConnectorWrap(connectorClusterId, workerId);
    }

    private JmxConnectorWrap createJmxConnectorWrap(Long connectorClusterId, String workerId) {
        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectorClusterId);
        if (connectCluster == null) {
            return null;
        }
        return this.createJmxConnectorWrap(connectCluster, workerId);
    }

    private JmxConnectorWrap createJmxConnectorWrap(ConnectCluster connectCluster, String workerId) {
        ConnectWorker connectWorker = this.getConnectWorkerFromDB(connectCluster.getId(), workerId);
        if (connectWorker == null) {
            return null;
        }

        try {
            modifyClientMapLock.lock();

            JmxConnectorWrap jmxConnectorWrap = JMX_MAP.getOrDefault(connectCluster.getId(), new ConcurrentHashMap<>()).get(workerId);
            if (jmxConnectorWrap != null) {
                return jmxConnectorWrap;
            }

            log.info("method=createJmxConnectorWrap||connectClusterId={}||workerId={}||msg=create JmxConnectorWrap starting", connectCluster.getId(), workerId);

            JmxConfig jmxConfig = ConvertUtil.str2ObjByJson(connectCluster.getJmxProperties(), JmxConfig.class);
            if (jmxConfig == null) {
                jmxConfig = new JmxConfig();
            }

            jmxConnectorWrap = new JmxConnectorWrap(
                    String.format("clusterPhyId=%s,workerId=%s", connectCluster.getId(), workerId),
                    null,
                    connectWorker.getHost(),
                    jmxConfig.getFinallyJmxPort(workerId, connectWorker.getJmxPort()),
                    jmxConfig
            );

            Map<String, JmxConnectorWrap> workerMap = JMX_MAP.getOrDefault(connectCluster.getId(), new ConcurrentHashMap<>());
            workerMap.put(workerId, jmxConnectorWrap);
            JMX_MAP.put(connectCluster.getId(), workerMap);

            log.info("method=createJmxConnectorWrap||clusterPhyId={}||workerId={}||msg=create JmxConnectorWrap success", connectCluster.getId(), workerId);

            return jmxConnectorWrap;
        } catch (Exception e) {
            log.error("method=createJmxConnectorWrap||connectClusterId={}||workerId={}||msg=create JmxConnectorWrap failed||errMsg=exception||", connectCluster.getId(), workerId, e);
        } finally {
            modifyClientMapLock.unlock();
        }

        return null;
    }


    private ConnectWorker getConnectWorkerFromDB(Long connectorClusterId, String workerId) {
        LambdaQueryWrapper<ConnectWorkerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ConnectWorkerPO::getConnectClusterId, connectorClusterId);
        lambdaQueryWrapper.eq(ConnectWorkerPO::getWorkerId, workerId);
        ConnectWorkerPO connectWorkerPO = connectWorkerDAO.selectOne(lambdaQueryWrapper);
        if (connectWorkerPO == null) {
            return null;
        }
        return ConvertUtil.obj2Obj(connectWorkerPO, ConnectWorker.class);
    }


    @Override
    protected void add(ConnectCluster connectCluster) {
        JMX_MAP.putIfAbsent(connectCluster.getId(), new ConcurrentHashMap<>());
    }

    @Override
    protected void modify(ConnectCluster newConnectCluster, ConnectCluster oldConnectCluster) {
        if (newConnectCluster.getJmxProperties().equals(oldConnectCluster.getJmxProperties())) {
            return;
        }
        this.remove(newConnectCluster);
        this.add(newConnectCluster);
    }

    @Override
    protected void remove(ConnectCluster connectCluster) {
        Map<String, JmxConnectorWrap> jmxMap = JMX_MAP.remove(connectCluster.getId());
        if (jmxMap == null) {
            return;
        }
        for (JmxConnectorWrap jmxConnectorWrap : jmxMap.values()) {
            jmxConnectorWrap.close();
        }
    }
}
