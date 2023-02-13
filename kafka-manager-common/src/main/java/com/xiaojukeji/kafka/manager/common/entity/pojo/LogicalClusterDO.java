package com.xiaojukeji.kafka.manager.common.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/6/29
 */
@Data
@ToString
@NoArgsConstructor
public class LogicalClusterDO {
    private Long id;

    private String name;

    private String identification;

    private Integer mode;

    private String appId;

    private Long clusterId;

    private String regionList;

    private String description;

    private Date gmtCreate;

    private Date gmtModify;

    public LogicalClusterDO(String name,
                            String identification,
                            Integer mode,
                            String appId,
                            Long clusterId,
                            String regionList) {
        this.name = name;
        this.identification = identification;
        this.mode = mode;
        this.appId = appId;
        this.clusterId = clusterId;
        this.regionList = regionList;
    }
}