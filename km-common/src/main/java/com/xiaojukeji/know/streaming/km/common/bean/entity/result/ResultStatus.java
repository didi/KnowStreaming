package com.xiaojukeji.know.streaming.km.common.bean.entity.result;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Getter;

/**
 * 返回状态
 * @author zengqiao
 * @date 20/4/16
 */
@Getter
public enum ResultStatus {
    GATEWAY_INVALID_REQUEST(-1, "invalid request"),

    SUCCESS(Constant.SUCCESS, "成功"),
    FAIL(1, "失败"),

    /**
     * 操作错误，[1000, 2000)
     * ------------------------------------------------------------------------------------------
     */
    OPERATION_FAILED(1001, "操作失败"),
    OPERATION_FORBIDDEN(1002, "操作禁止"),

    /**
     * 参数错误，[2000, 3000)
     */
    PARAM_ILLEGAL(2000, "参数错误"),
    NO_FIND_SUB_CLASS(2055, "找不到实现类"),
    ADMIN_TASK_ERROR(2056, "admin任务异常"),
    NO_FIND_METHOD(2057, "找不到实现方法"),

    /**
     * 资源不存在，[3000, 4000)
     */
    BROKER_NOT_EXIST(3010, "Broker 不存在"),
    CONTROLLER_NOT_EXIST(3011, "Controller 不存在"),
    TOPIC_NOT_EXIST(3012, "Topic 不存在"),
    CLUSTER_NOT_EXIST(3013, "cluster 不存在"),
    BROKER_SPEC_NOT_EXIST(3014, "Broker 规格信息不存在"),
    CLUSTER_SPEC_INCOMPLETE(3015, "cluster 规格信息不完整,请维护好集群规格信息。"),



    /**
     * 检查错误，[7000, 8000)
     */
    NOT_EXIST(7100, "不存在"),
    DUPLICATION(7200, "已存在"),
    IN_USE(7400, "已被使用"),


    /**
     * 调用错误, [8000, 9000)
     */
    KAFKA_OPERATE_FAILED(8010, "Kafka操作失败"),
    KAFKA_CONNECTOR_OPERATE_FAILED(8011, "KafkaConnect操作失败"),
    KAFKA_CONNECTOR_READ_FAILED(8012, "KafkaConnect读失败"),
    MYSQL_OPERATE_FAILED(8020, "MySQL操作失败"),
    ZK_OPERATE_FAILED(8030, "ZK操作失败"),
    ZK_FOUR_LETTER_CMD_FORBIDDEN(8031, "ZK四字命令被禁止"),
    ES_OPERATE_ERROR(8040, "ES操作失败"),
    HTTP_REQ_ERROR(8050, "第三方http请求异常"),

    /**
     * 版本兼容相关错误，【9000，10000）
     */
    VC_ITEM_NOT_EXIST(9010, "版本兼容控制项不存在"),
    VC_HANDLE_NOT_EXIST(9011, "版本兼容控制项对应的处理方法不存在"),
    VC_HANDLE_NOT_MATCH(9012, "版本兼容控制项对应的处理方法适配"),
    VC_ITEM_JMX_NOT_EXIST(9013, "版本兼容控制项中jmx信息不存在"),
    VC_JMX_CONNECT_ERROR(9014, "版本兼容控制所需要的jmx链接查询失败"),
    VC_JMX_INIT_ERROR(9015, "版本兼容控制所需要的jmx链接初始化失败"),
    VC_KAFKA_CLIENT_ERROR(9016, "版本兼容控制所需要的kafkaClient查询失败"),
    VC_JMX_INSTANCE_NOT_FOUND(9017, "版本兼容控制所需要的jmx指标不存在"),

    ;

    private final int code;

    private final String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
