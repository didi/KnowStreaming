package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign;

import lombok.Data;

import java.util.List;

@Data
public class ReassignState {
    private List<Integer> currentReplicas;

    private List<Integer> targetReplicas;

    private boolean done;
}
