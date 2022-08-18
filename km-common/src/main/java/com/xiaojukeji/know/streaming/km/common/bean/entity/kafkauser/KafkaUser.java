package com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.ScramMechanism;

import java.util.*;

@Data
@NoArgsConstructor
public class KafkaUser {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * KafkaUser
     */
    private String name;

    /**
     * 密钥
     */
    private String token;

    /**
     * 原始数据
     */
    private Properties props;

    public KafkaUser(Long clusterPhyId, String name, String token, Properties props) {
        this.clusterPhyId = clusterPhyId;
        this.name = name;
        this.token = token;
        this.props = props;
    }

    public String getCredentialString() {
        List<String> credentialList = new ArrayList<>();
        for (ScramMechanism scramMechanism: ScramMechanism.values()) {
            if (!props.containsKey(scramMechanism.mechanismName())) {
                continue;
            }
            credentialList.add(props.getProperty(scramMechanism.mechanismName()));
        }

        return ConvertUtil.list2String(credentialList, Constant.COMMA);
    }
}
