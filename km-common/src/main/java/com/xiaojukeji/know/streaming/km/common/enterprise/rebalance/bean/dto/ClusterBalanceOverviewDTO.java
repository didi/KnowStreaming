package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;


@Data
@EnterpriseLoadReBalance
public class ClusterBalanceOverviewDTO extends PaginationBaseDTO {

    @ApiModelProperty("host")
    private String host;

    @ApiModelProperty("key:disk,bytesOut,bytesIn value:均衡状态 0：已均衡；2：未均衡")
    private Map<String, Integer> stateParam;
}
