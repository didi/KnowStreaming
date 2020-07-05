-- CREATE DATABASE kafka_manager;
-- \c kafka_manager;
SET TIME ZONE 'Asia/Chongqing';
SET CLIENT_ENCODING TO 'UTF-8';

CREATE OR REPLACE FUNCTION on_update_timestamp() RETURNS TRIGGER AS
$$
BEGIN
    new.gmt_modify = current_timestamp;
    return new;
END;
$$ LANGUAGE plpgsql;

-- 账号表
CREATE TABLE account
(
    id         bigserial    NOT NULL,                                  -- 'ID',
    username   varchar(64)  NOT NULL UNIQUE DEFAULT '',                -- '用户名',
    password   varchar(128) NOT NULL        DEFAULT '',                -- '密码',
    role       int          NOT NULL        DEFAULT 0,                 -- '角色类型, 0:普通用户',
    status     int          NOT NULL        DEFAULT 0,                 -- '0标识使用中，-1标识已废弃',
    gmt_create timestamp    NOT NULL        DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify timestamp    NOT NULL        DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT account_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX account_uniq_username ON account (username);
INSERT INTO account(username, password, role)
VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 2);
CREATE TRIGGER account_trig_gmt_modify
    BEFORE UPDATE
    ON account
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- 告警规则表
CREATE TABLE alarm_rule
(
    id                   bigserial,                                       -- '自增ID',
    alarm_name           varchar(128) NOT NULL DEFAULT '',                -- '告警名字',
    strategy_expressions text,                                            -- '表达式',
    strategy_filters     text,                                            -- '过滤条件',
    strategy_actions     text,                                            -- '响应',
    principals           varchar(512) NOT NULL DEFAULT '',                -- '负责人',
    status               int2         NOT NULL DEFAULT 1,                 -- '-1:逻辑删除,  0:关闭,  1:正常',
    gmt_create           timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify           timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT alarm_rule_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX alarm_rule_uniq_alarm_name ON alarm_rule (alarm_name);
CREATE TRIGGER alarm_rule_trig_gmt_modify
    BEFORE UPDATE
    ON alarm_rule
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- Broker信息表
CREATE TABLE broker
(
    id         bigserial,                                       -- 'id',
    cluster_id bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    broker_id  int          NOT NULL DEFAULT '-1',              -- 'BrokerID',
    host       varchar(128) NOT NULL DEFAULT '',                -- 'Broker主机名',
    port       int          NOT NULL DEFAULT '-1',              -- 'Broker端口',
    timestamp  bigint       NOT NULL DEFAULT '-1',              -- '启动时间',
    status     int          NOT NULL DEFAULT '0',               -- '状态0有效，-1无效',
    gmt_create timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT broker_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX broker_uniq_cluster_id_broker_id ON broker (cluster_id, broker_id);
CREATE TRIGGER broker_trig_gmt_modify
    BEFORE UPDATE
    ON broker
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- BrokerMetric信息表
CREATE TABLE broker_metrics
(
    id                             bigserial,                                         -- '自增id',
    cluster_id                     bigint         NOT NULL DEFAULT '-1',              -- '集群ID',
    broker_id                      int            NOT NULL DEFAULT '-1',              -- 'BrokerID',
    bytes_in                       decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒字节流入',
    bytes_out                      decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒字节流出',
    bytes_rejected                 decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒被拒绝字节数',
    messages_in                    decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒消息数流入',
    fail_fetch_request             decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒消费失败数',
    fail_produce_request           decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒失败生产数',
    fetch_consumer_request         decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒消费请求数',
    produce_request                decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒生产数',
    request_handler_idl_percent    decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '请求处理器繁忙百分比',
    network_processor_idl_percent  decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '网络处理器繁忙百分比',
    request_queue_size             bigint         NOT NULL DEFAULT '0',               -- '请求列表大小',
    response_queue_size            bigint         NOT NULL DEFAULT '0',               -- '响应列表大小',
    log_flush_time                 decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '刷日志时间',
    total_time_produce_mean        decimal(53, 2) NOT NULL DEFAULT '0.00',            -- 'produce请求处理总时间-平均值',
    total_time_produce_99th        decimal(53, 2) NOT NULL DEFAULT '0.00',            -- 'produce请求处理总时间-99分位',
    total_time_fetch_consumer_mean decimal(53, 2) NOT NULL DEFAULT '0.00',            -- 'fetch请求总时间-平均值',
    total_time_fetch_consumer_99th decimal(53, 2) NOT NULL DEFAULT '0.00',            -- 'fetch请求总时间-99分位',
    gmt_create                     timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    CONSTRAINT broker_metrics_pk PRIMARY KEY (id)
);
CREATE INDEX broker_metrics_idx_cluster_id_broker_id_gmt_create ON broker_metrics (cluster_id, broker_id, gmt_create);

-- Cluster表
CREATE TABLE cluster
(
    id                bigserial,                                       -- '集群ID',
    cluster_name      varchar(128) NOT NULL DEFAULT '',                -- '集群名称',
    zookeeper         varchar(512) NOT NULL DEFAULT '',                -- 'ZK地址',
    bootstrap_servers varchar(512) NOT NULL DEFAULT '',                -- 'Server地址',
    kafka_version     varchar(32)  NOT NULL DEFAULT '',                -- 'Kafka版本',
    alarm_flag        int2         NOT NULL DEFAULT '0',               -- '0:不开启告警, 1开启告警',
    security_protocol varchar(512) NOT NULL DEFAULT '',                -- '安全协议',
    sasl_mechanism    varchar(512) NOT NULL DEFAULT '',                -- '安全机制',
    sasl_jaas_config  varchar(512) NOT NULL DEFAULT '',                -- 'Jaas配置',
    status            int2         NOT NULL DEFAULT '0',               -- '删除标记, 0表示未删除, -1表示删除',
    gmt_create        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT cluster_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX cluster_uniq_cluster_name ON cluster (cluster_name);
CREATE TRIGGER cluster_trig_gmt_modify
    BEFORE UPDATE
    ON cluster
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- ClusterMetrics信息
CREATE TABLE cluster_metrics
(
    id             bigserial,                                         -- '自增id',
    cluster_id     bigint         NOT NULL DEFAULT '0',               -- '集群ID',
    topic_num      int            NOT NULL DEFAULT '0',               -- 'Topic数',
    partition_num  int            NOT NULL DEFAULT '0',               -- '分区数',
    broker_num     int            NOT NULL DEFAULT '0',               -- 'Broker数',
    bytes_in       decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒流入(B)',
    bytes_out      decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒流出(B)',
    bytes_rejected decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒拒绝(B)',
    messages_in    decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒消息数(条)',
    gmt_create     timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    CONSTRAINT cluster_metrics_pk PRIMARY KEY (id)
);
CREATE INDEX cluster_metrics_idx_cluster_id_gmt_create ON cluster_metrics (cluster_id, gmt_create);

-- Controller历史变更记录表
CREATE TABLE controller
(
    id         bigserial,                                       -- '自增id',
    cluster_id bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    broker_id  int          NOT NULL DEFAULT '-1',              -- 'BrokerId',
    host       varchar(256) NOT NULL DEFAULT '',                -- '主机名',
    timestamp  bigint       NOT NULL DEFAULT '-1',              -- 'Controller变更时间',
    version    int          NOT NULL DEFAULT '-1',              -- 'Controller格式版本',
    gmt_create timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    CONSTRAINT controller_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX controller_uniq_cluster_id_broker_id_timestamp ON controller (cluster_id, broker_id, timestamp);

-- Topic迁移信息
CREATE TABLE migration_task
(
    id                bigserial,                                       -- '自增id',
    cluster_id        bigint       NOT NULL DEFAULT '0',               -- '集群ID',
    topic_name        varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    reassignment_json text,                                            -- '任务参数',
    real_throttle     bigint       NOT NULL DEFAULT '0',               -- '实际限流值(B/s)',
    operator          varchar(128) NOT NULL DEFAULT '',                -- '操作人',
    description       varchar(256) NOT NULL DEFAULT '',                -- '备注说明',
    status            int          NOT NULL DEFAULT '0',               -- '任务状态',
    gmt_create        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '任务创建时间',
    gmt_modify        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '任务修改时间',
    CONSTRAINT migration_task_pk PRIMARY KEY (id)
);
CREATE TRIGGER migration_task_trig_gmt_modify
    BEFORE UPDATE
    ON migration_task
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

CREATE TABLE operation_history
(
    id         bigserial,                                       -- 'id',
    cluster_id bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    topic_name varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    operator   varchar(128) NOT NULL DEFAULT '',                -- '操作人',
    operation  varchar(256) NOT NULL DEFAULT '',                -- '操作描述',
    gmt_create timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    PRIMARY KEY (id)
);
--='操作记录表';

-- 分区申请工单
CREATE TABLE order_partition
(
    id            bigserial,                                       -- 'id',
    cluster_id    bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    cluster_name  varchar(128) NOT NULL DEFAULT '',                -- '集群名称',
    topic_name    varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    applicant     varchar(128) NOT NULL DEFAULT '',                -- '申请人',
    peak_bytes_in bigint       NOT NULL DEFAULT '0',               -- '峰值流量',
    description   text,                                            -- '备注信息',
    order_status  int          NOT NULL DEFAULT '0',               -- '工单状态',
    approver      varchar(128) NOT NULL DEFAULT '',                -- '审批人',
    opinion       varchar(256) NOT NULL DEFAULT '',                -- '审批意见',
    status        int          NOT NULL DEFAULT '0',               -- '状态，0标识有效，-1无效',
    gmt_create    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT order_partition_pk PRIMARY KEY (id)
);
CREATE TRIGGER order_partition_trig_gmt_modify
    BEFORE UPDATE
    ON order_partition
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- Topic申请工单
CREATE TABLE order_topic
(
    id             bigserial,                                       -- 'ID',
    cluster_id     bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    cluster_name   varchar(128) NOT NULL DEFAULT '',                -- '集群名称',
    topic_name     varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    retention_time bigint       NOT NULL DEFAULT '-1',              -- '保留时间(ms)',
    partition_num  int          NOT NULL DEFAULT '-1',              -- '分区数',
    replica_num    int          NOT NULL DEFAULT '-1',              -- '副本数',
    regions        varchar(128) NOT NULL DEFAULT '',                -- 'RegionId列表',
    brokers        varchar(128) NOT NULL DEFAULT '',                -- 'Broker列表',
    peak_bytes_in  bigint       NOT NULL DEFAULT '0',               -- '峰值流入流量(KB)',
    applicant      varchar(128) NOT NULL DEFAULT '',                -- '申请人',
    principals     varchar(256) NOT NULL DEFAULT '',                -- '负责人',
    description    text,                                            -- '备注信息',
    order_status   int          NOT NULL DEFAULT '0',               -- '工单状态',
    approver       varchar(128) NOT NULL DEFAULT '',                -- '审批人',
    opinion        varchar(256) NOT NULL DEFAULT '',                -- '审批意见',
    status         int          NOT NULL DEFAULT '0',               -- '状态，0标识有效，-1无效',
    gmt_create     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT order_topic_pk PRIMARY KEY (id)
);
CREATE TRIGGER order_topic_trig_gmt_modify
    BEFORE UPDATE
    ON order_topic
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- Region信息表
CREATE TABLE region
(
    id          bigserial,                                       -- 'id',
    cluster_id  bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    region_name varchar(128) NOT NULL DEFAULT '',                -- 'Region名称',
    broker_list varchar(256) NOT NULL DEFAULT '',                -- 'Broker列表',
    level       int          NOT NULL DEFAULT '0',               -- 'Region重要等级, 0级普通, 1极重要，2级极重要',
    operator    varchar(45)  NOT NULL DEFAULT '',                -- '操作人',
    description text,                                            -- '备注说明',
    status      int          NOT NULL DEFAULT '0',               -- '状态，0正常，-1废弃，1容量已满',
    gmt_create  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT region_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX region_uniq_cluster_id_region_name ON region (cluster_id, region_name);
CREATE TRIGGER region_trig_gmt_modify
    BEFORE UPDATE
    ON region
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- Topic信息表
CREATE TABLE topic
(
    id          bigserial,                                       -- 'ID',
    cluster_id  bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    topic_name  varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    applicant   varchar(256) NOT NULL DEFAULT '',                -- '申请人',
    principals  varchar(256) NOT NULL DEFAULT '',                -- '负责人',
    description text,                                            -- '备注信息',
    status      int          NOT NULL DEFAULT '0',               -- '0标识使用中，-1标识已废弃',
    gmt_create  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT topic_pk PRIMARY KEY (id)
); --='';
CREATE UNIQUE INDEX topic_uniq_cluster_id_topic_name ON topic (cluster_id, topic_name);
CREATE TRIGGER topic_trig_gmt_modify
    BEFORE UPDATE
    ON topic
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- 用户收藏的Topic表
CREATE TABLE topic_favorite
(
    id         bigserial,                                       -- '自增Id',
    username   varchar(64)  NOT NULL DEFAULT '',                -- '用户名',
    cluster_id bigint       NOT NULL DEFAULT '-1',              -- '集群ID',
    topic_name varchar(192) NOT NULL DEFAULT '',                -- 'Topic名称',
    status     int          NOT NULL DEFAULT '0',               -- '删除标记, 0表示未删除, -1表示删除',
    gmt_create timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    gmt_modify timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '修改时间',
    CONSTRAINT topic_favorite_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX topic_favorite_uniq_username_cluster_id_topic_name ON topic_favorite (username, cluster_id, topic_name);
CREATE TRIGGER topic_favorite_trig_gmt_modify
    BEFORE UPDATE
    ON topic_favorite
    FOR EACH ROW
EXECUTE PROCEDURE on_update_timestamp();

-- TopicMetrics表
CREATE TABLE topic_metrics
(
    id                     bigserial,                                         -- '自增id',
    cluster_id             bigint         NOT NULL DEFAULT '-1',              -- '集群ID',
    topic_name             varchar(192)   NOT NULL DEFAULT '',                -- 'Topic名称',
    messages_in            decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒进入消息条数',
    bytes_in               decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒字节流入',
    bytes_out              decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒字节流出',
    bytes_rejected         decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒拒绝字节数',
    total_produce_requests decimal(53, 2) NOT NULL DEFAULT '0.00',            -- '每秒请求数',
    gmt_create             timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP, -- '创建时间',
    CONSTRAINT topic_metrics_pk PRIMARY KEY (id)
);
CREATE INDEX topic_metrics_idx_cluster_id_topic_name_gmt_create ON topic_metrics (cluster_id, topic_name, gmt_create);
