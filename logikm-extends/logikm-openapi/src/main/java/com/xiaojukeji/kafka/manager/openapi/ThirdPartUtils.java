package com.xiaojukeji.kafka.manager.openapi;

import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;

/**
 * @author zhongyuankai
 * @date 2020/08/31
 */
public class ThirdPartUtils {

    public static String getOrderLimitKey(OrderTypeEnum orderTypeEnum, String systemCode) {
        return orderTypeEnum.getOrderName() + "_" + systemCode;
    }

}
