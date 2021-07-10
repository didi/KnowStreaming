package com.xiaojukeji.kafka.manager.common.entity.ao.config;

/**
 * @author zengqiao
 * @date 20/8/23
 */
public class TopicAnomalyFlowConfig {
    private Long minTopicBytesInUnitB;

    private Double bytesInIncUnitB;

    private Long minTopicProduceQps;

    private Double produceQpsInc;

    public Long getMinTopicBytesInUnitB() {
        return minTopicBytesInUnitB;
    }

    public void setMinTopicBytesInUnitB(Long minTopicBytesInUnitB) {
        this.minTopicBytesInUnitB = minTopicBytesInUnitB;
    }

    public Double getBytesInIncUnitB() {
        return bytesInIncUnitB;
    }

    public void setBytesInIncUnitB(Double bytesInIncUnitB) {
        this.bytesInIncUnitB = bytesInIncUnitB;
    }

    public Long getMinTopicProduceQps() {
        return minTopicProduceQps;
    }

    public void setMinTopicProduceQps(Long minTopicProduceQps) {
        this.minTopicProduceQps = minTopicProduceQps;
    }

    public Double getProduceQpsInc() {
        return produceQpsInc;
    }

    public void setProduceQpsInc(Double produceQpsInc) {
        this.produceQpsInc = produceQpsInc;
    }

    @Override
    public String toString() {
        return "TopicAnomalyFlowConfig{" +
                "minTopicBytesInUnitB=" + minTopicBytesInUnitB +
                ", bytesInIncUnitB=" + bytesInIncUnitB +
                ", minTopicProduceQps=" + minTopicProduceQps +
                ", produceQpsInc=" + produceQpsInc +
                '}';
    }
}