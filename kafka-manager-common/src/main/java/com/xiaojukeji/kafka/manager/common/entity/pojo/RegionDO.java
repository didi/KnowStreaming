package com.xiaojukeji.kafka.manager.common.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
public class RegionDO implements Comparable<RegionDO> {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String name;

    private Long clusterId;

    private String brokerList;

    private Long capacity;

    private Long realUsed;

    private Long estimateUsed;

    private String description;

    public RegionDO(Integer status, String name, Long clusterId, String brokerList) {
        this.status = status;
        this.name = name;
        this.clusterId = clusterId;
        this.brokerList = brokerList;
    }

    @Override
    public int compareTo(RegionDO regionDO) {
        return this.id.compareTo(regionDO.id);
    }
}