package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReassignTask {
    /**
     * topic名称
     */
    private String topic;

    /**
     * 分区id
     */
    private int partition;

    /**
     * 目标副本
     */
    private List<Integer> replicas;

}
