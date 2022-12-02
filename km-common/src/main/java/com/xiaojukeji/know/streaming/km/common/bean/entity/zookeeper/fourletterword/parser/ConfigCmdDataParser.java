package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.ConfigCmdData;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.FourLetterWordUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * clientPort=2183
 * dataDir=/data1/data/zkData2/version-2
 * dataLogDir=/data1/data/zkLog2/version-2
 * tickTime=2000
 * maxClientCnxns=60
 * minSessionTimeout=4000
 * maxSessionTimeout=40000
 * serverId=2
 * initLimit=15
 * syncLimit=10
 * electionAlg=3
 * electionPort=4445
 * quorumPort=4444
 * peerType=0
 */
@Data
public class ConfigCmdDataParser implements FourLetterWordDataParser<ConfigCmdData> {
    private static final ILog LOGGER = LogFactory.getLog(ConfigCmdDataParser.class);

    private Result<ConfigCmdData> dataResult = null;

    @Override
    public String getCmd() {
        return FourLetterWordUtil.ConfigCmd;
    }

    @Override
    public ConfigCmdData parseAndInitData(Long clusterPhyId, String host, int port, String cmdData) {
        Map<String, String> dataMap = new HashMap<>();
        for (String elem : cmdData.split("\n")) {
            if (elem.isEmpty()) {
                continue;
            }

            int idx = elem.indexOf('=');
            if (idx >= 0) {
                dataMap.put(elem.substring(0, idx), elem.substring(idx + 1).trim());
            }
        }

        ConfigCmdData configCmdData = new ConfigCmdData();
        dataMap.entrySet().stream().forEach(elem -> {
            try {
                switch (elem.getKey()) {
                    case "clientPort":
                        configCmdData.setClientPort(Long.valueOf(elem.getValue()));
                        break;
                    case "dataDir":
                        configCmdData.setDataDir(elem.getValue());
                        break;
                    case "dataLogDir":
                        configCmdData.setDataLogDir(elem.getValue());
                        break;
                    case "tickTime":
                        configCmdData.setTickTime(Long.valueOf(elem.getValue()));
                        break;
                    case "maxClientCnxns":
                        configCmdData.setMaxClientCnxns(Long.valueOf(elem.getValue()));
                        break;
                    case "minSessionTimeout":
                        configCmdData.setMinSessionTimeout(Long.valueOf(elem.getValue()));
                        break;
                    case "maxSessionTimeout":
                        configCmdData.setMaxSessionTimeout(Long.valueOf(elem.getValue()));
                        break;
                    case "serverId":
                        configCmdData.setServerId(Integer.valueOf(elem.getValue()));
                        break;
                    case "initLimit":
                        configCmdData.setInitLimit(elem.getValue());
                        break;
                    case "syncLimit":
                        configCmdData.setSyncLimit(Long.valueOf(elem.getValue()));
                        break;
                    case "electionAlg":
                        configCmdData.setElectionAlg(Long.valueOf(elem.getValue()));
                        break;
                    case "electionPort":
                        configCmdData.setElectionPort(Long.valueOf(elem.getValue()));
                        break;
                    case "quorumPort":
                        configCmdData.setQuorumPort(Long.valueOf(elem.getValue()));
                        break;
                    case "peerType":
                        configCmdData.setPeerType(Long.valueOf(elem.getValue()));
                        break;
                    default:
                        LOGGER.warn(
                                "method=parseAndInitData||name={}||value={}||msg=data not parsed!",
                                elem.getKey(), elem.getValue()
                        );
                }
            } catch (Exception e) {
                LOGGER.error(
                        "method=parseAndInitData||clusterPhyId={}||host={}||port={}||name={}||value={}||errMsg=exception!",
                        clusterPhyId, host, port, elem.getKey(), elem.getValue(), e
                );
            }
        });

        return configCmdData;
    }
}
