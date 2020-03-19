package com.xiaojukeji.kafka.manager.common.entity.po;

/**
 * migrate topic task do
 * @author zengqiao
 * @date 19/4/16
 */
public class MigrationTaskDO extends BaseDO {
    private Long clusterId;

    private String topicName;

    private String reassignmentJson;

    private Long throttle;

    private String operator;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getReassignmentJson() {
        return reassignmentJson;
    }

    public void setReassignmentJson(String reassignmentJson) {
        this.reassignmentJson = reassignmentJson;
    }

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
    }

    @Override
    public String toString() {
        return "MigrationTaskDO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", reassignmentJson='" + reassignmentJson + '\'' +
                ", throttle=" + throttle +
                ", id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }

    public static MigrationTaskDO createInstance(Long clusterId,
                                                 String topicName,
                                                 String reassignmentJson,
                                                 Long throttle,
                                                 String description) {
        MigrationTaskDO migrationTaskDO = new MigrationTaskDO();
        migrationTaskDO.setClusterId(clusterId);
        migrationTaskDO.setTopicName(topicName);
        migrationTaskDO.setReassignmentJson(reassignmentJson);
        migrationTaskDO.setThrottle(throttle);
        migrationTaskDO.setDescription(description);
        return migrationTaskDO;
    }
}
