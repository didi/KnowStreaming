package com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "消费组的详情")
public class ConsumerGroupDerailTotalVo {

    @ApiModelProperty(value = "consumerGroupDetailVOs")
    private List<ConsumerGroupDetailVO> consumerGroupDetailVOs;

    @ApiModelProperty(value = "totalLag")
    private Long totalLag;

    public List<ConsumerGroupDetailVO> getConsumerGroupDetailVOs() {
        return consumerGroupDetailVOs;
    }

    public void setConsumerGroupDetailVOs(List<ConsumerGroupDetailVO> consumerGroupDetailVOs) {
        this.consumerGroupDetailVOs = consumerGroupDetailVOs;
    }

    public Long getTotalLag() {
        return totalLag;
    }

    public void setTotalLag(Long totalLag) {
        this.totalLag = totalLag;
    }

    @Override
    public String toString() {
        return "ConsumerGroupDerailTotalVo{" +
                "consumerGroupDetailVOs=" + consumerGroupDetailVOs +
                ", totalLag=" + totalLag +
                '}';
    }
}
