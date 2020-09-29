package com.xiaojukeji.kafka.manager.kcm.common.entry.ao;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaBrokerRoleEnum;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/26
 */
public class ClusterTaskSubStatus {
    private Integer groupNum;

    private String hostname;

    private ClusterTaskSubStateEnum status;

    private List<KafkaBrokerRoleEnum> roleList;

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public ClusterTaskSubStateEnum getStatus() {
        return status;
    }

    public void setStatus(ClusterTaskSubStateEnum status) {
        this.status = status;
    }

    public List<KafkaBrokerRoleEnum> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<KafkaBrokerRoleEnum> roleList) {
        this.roleList = roleList;
    }

    @Override
    public String toString() {
        return "ClusterTaskSubStatus{" +
                "groupNum=" + groupNum +
                ", hostname='" + hostname + '\'' +
                ", status=" + status +
                ", roleList=" + roleList +
                '}';
    }
}