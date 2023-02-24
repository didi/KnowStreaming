package com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EnterpriseLoadReBalance
public class ClusterBalanceItemState {

    /**
     * 是否配置集群平衡:true:已配置，false:未配置
     */
    private Boolean configureBalance;

    /**
     * 是否开启均衡:true:开启，false: 未开启
     */
    private Boolean enable;

    /**
     * 子项是否均衡:key: disk,bytesIn,bytesOut,cpu ; value:true:已均衡，false:未均衡
     * @see com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource
     */
    private Map<String, Boolean> itemState;

    public Integer getResItemState(Resource res) {
        if (itemState == null || !itemState.containsKey(res.resource())) {
            return Constant.INVALID_CODE;
        }

        return itemState.get(res.resource()) ? Constant.YES: Constant.NO;
    }
}
