package com.xiaojukeji.know.streaming.km.common.bean.entity.kafka;

import lombok.Getter;
import org.apache.kafka.connect.runtime.distributed.ConnectProtocol;


@Getter
public class KSMemberConnectAssignment extends KSMemberBaseAssignment {
    private final ConnectProtocol.Assignment assignment;

    private final ConnectProtocol.WorkerState workerState;

    public KSMemberConnectAssignment(ConnectProtocol.Assignment assignment, ConnectProtocol.WorkerState workerState) {
        this.assignment = assignment;
        this.workerState = workerState;
    }

    @Override
    public String toString() {
        return "KSMemberConnectAssignment{" +
                "assignment=" + assignment +
                ", workerState=" + workerState +
                '}';
    }
}
