package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser;

/**
 * 四字命令结果解析类
 */
public interface FourLetterWordDataParser<T> {
    String getCmd();

    T parseAndInitData(Long clusterPhyId, String host, int port, String cmdData);
}
