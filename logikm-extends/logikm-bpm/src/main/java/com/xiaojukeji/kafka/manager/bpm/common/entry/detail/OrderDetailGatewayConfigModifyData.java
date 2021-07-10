package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

/**
 * gateway config修改
 * @author zengqiao
 * @date 2021/01/13
 */
public class OrderDetailGatewayConfigModifyData extends AbstractOrderDetailData {
    /**
     * 旧的Gateway Config
     */
    private OrderDetailGatewayConfigData oldGatewayConfig;

    /**
     * 新的Gateway Config
     */
    private OrderDetailGatewayConfigData newGatewayConfig;

    public OrderDetailGatewayConfigData getOldGatewayConfig() {
        return oldGatewayConfig;
    }

    public void setOldGatewayConfig(OrderDetailGatewayConfigData oldGatewayConfig) {
        this.oldGatewayConfig = oldGatewayConfig;
    }

    public OrderDetailGatewayConfigData getNewGatewayConfig() {
        return newGatewayConfig;
    }

    public void setNewGatewayConfig(OrderDetailGatewayConfigData newGatewayConfig) {
        this.newGatewayConfig = newGatewayConfig;
    }

    @Override
    public String toString() {
        return "OrderDetailGatewayConfigModifyData{" +
                "oldGatewayConfig=" + oldGatewayConfig +
                ", newGatewayConfig=" + newGatewayConfig +
                '}';
    }
}
