package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterPhyAddDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterPhyModifyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.po.cluster.ClusterPhyPO;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.Properties;

public class ClusterConverter {
    private ClusterConverter() {
    }

    public static ClusterPhyPO convert2ClusterPhyPO(ClusterPhyAddDTO dto) {
        ClusterPhyPO clusterPhyPO = ConvertUtil.obj2Obj(dto, ClusterPhyPO.class);
        clusterPhyPO.setClientProperties(ConvertUtil.obj2Json(dto.getClientProperties()));
        clusterPhyPO.setJmxProperties(ConvertUtil.obj2Json(dto.getJmxProperties()));
        if (ValidateUtils.isNull(dto.getZkProperties())) {
            clusterPhyPO.setZkProperties("");
        } else {
            clusterPhyPO.setZkProperties(ConvertUtil.obj2Json(dto.getZkProperties()));
        }
        clusterPhyPO.setRunState(
                ValidateUtils.isBlank(dto.getZookeeper())?
                        ClusterRunStateEnum.RUN_RAFT.getRunState() :
                        ClusterRunStateEnum.RUN_ZK.getRunState()
        );
        clusterPhyPO.setAuthType(ClusterConverter.getAuthType(dto.getClientProperties()));
        return clusterPhyPO;
    }

    public static ClusterPhyPO convert2ClusterPhyPO(ClusterPhyModifyDTO dto) {
        ClusterPhyPO clusterPhyPO = ConvertUtil.obj2Obj(dto, ClusterPhyPO.class);
        clusterPhyPO.setClientProperties(ConvertUtil.obj2Json(dto.getClientProperties()));
        clusterPhyPO.setJmxProperties(ConvertUtil.obj2Json(dto.getJmxProperties()));
        if (ValidateUtils.isNull(dto.getZkProperties())) {
            clusterPhyPO.setZkProperties("");
        } else {
            clusterPhyPO.setZkProperties(ConvertUtil.obj2Json(dto.getZkProperties()));
        }
        clusterPhyPO.setRunState(
                ValidateUtils.isBlank(dto.getZookeeper())?
                        ClusterRunStateEnum.RUN_RAFT.getRunState() :
                        ClusterRunStateEnum.RUN_ZK.getRunState()
        );
        clusterPhyPO.setAuthType(ClusterConverter.getAuthType(dto.getClientProperties()));
        return clusterPhyPO;
    }

    private static Integer getAuthType(Properties properties) {
        if (properties == null || properties.isEmpty()) {
            return ClusterAuthTypeEnum.NO_AUTH.getAuthType();
        }

        return ClusterAuthTypeEnum.getAuthBySaslMechanism(properties.getProperty(SaslConfigs.SASL_MECHANISM)).getAuthType();
    }
}
