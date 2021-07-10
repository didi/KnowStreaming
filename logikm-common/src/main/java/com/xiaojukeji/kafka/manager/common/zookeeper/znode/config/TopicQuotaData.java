package com.xiaojukeji.kafka.manager.common.zookeeper.znode.config;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class TopicQuotaData {
    private String consumer_byte_rate;

    private String producer_byte_rate;

    public String getConsumer_byte_rate() {
        return consumer_byte_rate;
    }

    public void setConsumer_byte_rate(String consumer_byte_rate) {
        this.consumer_byte_rate = consumer_byte_rate;
    }

    public String getProducer_byte_rate() {
        return producer_byte_rate;
    }

    public void setProducer_byte_rate(String producer_byte_rate) {
        this.producer_byte_rate = producer_byte_rate;
    }

    public static TopicQuotaData getClientData(Long producerByteRate, Long consumerByteRate) {
        TopicQuotaData clientData = new TopicQuotaData();
        if (!ValidateUtils.isNull(producerByteRate) && consumerByteRate != -1) {
            clientData.setConsumer_byte_rate(consumerByteRate.toString());
        }
        if (!ValidateUtils.isNull(consumerByteRate) && producerByteRate != -1) {
            clientData.setProducer_byte_rate(producerByteRate.toString());
        }
        return clientData;
    }

    @Override
    public String toString() {
        return "ClientQuotaData{" +
                "consumer_byte_rate='" + consumer_byte_rate + '\'' +
                ", producer_byte_rate='" + producer_byte_rate + '\'' +
                '}';
    }
}