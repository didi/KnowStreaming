package com.xiaojukeji.know.streaming.km.common.enums.group;

import lombok.Getter;
import org.apache.kafka.common.ConsumerGroupState;

import java.util.Arrays;
import java.util.List;

@Getter
public enum GroupStateEnum {
    UNKNOWN(-1, "Unknown", Arrays.asList(ConsumerGroupState.UNKNOWN)),

    PREPARING_RE_BALANCE(0, "ReBalance", Arrays.asList(ConsumerGroupState.PREPARING_REBALANCE, ConsumerGroupState.COMPLETING_REBALANCE)),

    ACTIVE(1, "Active", Arrays.asList(ConsumerGroupState.STABLE)),

    DEAD(2, "Dead", Arrays.asList(ConsumerGroupState.DEAD)),

    EMPTY(3, "Empty", Arrays.asList(ConsumerGroupState.EMPTY)),

    ;

    private final Integer code;

    private final String state;

    private final List<ConsumerGroupState> rawStatList;

    GroupStateEnum(Integer code, String state, List<ConsumerGroupState> rawStatList) {
        this.code = code;
        this.state = state;
        this.rawStatList = rawStatList;
    }

    public static GroupStateEnum getByRawState(ConsumerGroupState rawGroupState) {
        if (rawGroupState == null) {
            return GroupStateEnum.UNKNOWN;
        }

        for (GroupStateEnum groupStateEnum: GroupStateEnum.values()) {
            if (groupStateEnum.rawStatList.contains(rawGroupState)) {
                return groupStateEnum;
            }
        }

        return GroupStateEnum.UNKNOWN;
    }

    public static GroupStateEnum getByState(String state) {
        if (state == null) {
            return GroupStateEnum.UNKNOWN;
        }

        for (GroupStateEnum groupStateEnum: GroupStateEnum.values()) {
            if (groupStateEnum.getState().contains(state)) {
                return groupStateEnum;
            }
        }

        return GroupStateEnum.UNKNOWN;
    }
}
