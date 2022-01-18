-- create database
CREATE DATABASE logi_kafka_manager;

USE logi_kafka_manager;

--
-- Table structure for table `account`
--

-- DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
  `role` tinyint(8) NOT NULL DEFAULT '0' COMMENT '角色类型, 0:普通用户 1:研发 2:运维',
  `department` varchar(256) DEFAULT '' COMMENT '部门名',
  `display_name` varchar(256) DEFAULT '' COMMENT '用户姓名',
  `mail` varchar(256) DEFAULT '' COMMENT '邮箱',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '0标识使用中，-1标识已废弃',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账号表';
INSERT INTO account(username, password, role) VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 2);

--
-- Table structure for table `app`
--

-- DROP TABLE IF EXISTS `app`;
CREATE TABLE `app` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_id` varchar(128) NOT NULL DEFAULT '' COMMENT '应用id',
  `name` varchar(192) NOT NULL DEFAULT '' COMMENT '应用名称',
  `password` varchar(256) NOT NULL DEFAULT '' COMMENT '应用密码',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '类型, 0:普通用户, 1:超级用户',
  `applicant` varchar(64) NOT NULL DEFAULT '' COMMENT '申请人',
  `principals` text COMMENT '应用负责人',
  `description` text COMMENT '应用描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`),
  UNIQUE KEY `uniq_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用信息';


--
-- Table structure for table `authority`
--

-- DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_id` varchar(128) NOT NULL DEFAULT '' COMMENT '应用id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'topic名称',
  `access` int(11) NOT NULL DEFAULT '0' COMMENT '0:无权限, 1:读, 2:写, 3:读写',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_app_id_cluster_id_topic_name` (`app_id`,`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限信息(kafka-manager)';

--
-- Table structure for table `broker`
--

-- DROP TABLE IF EXISTS `broker`;
CREATE TABLE `broker` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerid',
  `host` varchar(128) NOT NULL DEFAULT '' COMMENT 'broker主机名',
  `port` int(16) NOT NULL DEFAULT '-1' COMMENT 'broker端口',
  `timestamp` bigint(20) NOT NULL DEFAULT '-1' COMMENT '启动时间',
  `max_avg_bytes_in` bigint(20) NOT NULL DEFAULT '-1' COMMENT '峰值的均值流量',
  `version` varchar(128) NOT NULL DEFAULT '' COMMENT 'broker版本',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态: 0有效，-1无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_broker_id` (`cluster_id`,`broker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='broker信息表';

--
-- Table structure for table `broker_metrics`
--

-- DROP TABLE IF EXISTS `broker_metrics`;
CREATE TABLE `broker_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerid',
  `metrics` text COMMENT '指标',
  `messages_in` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '每秒消息数流入',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_broker_id_gmt_create` (`cluster_id`,`broker_id`,`gmt_create`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='broker-metric信息表';

--
-- Table structure for table `cluster`
--

-- DROP TABLE IF EXISTS `cluster`;
CREATE TABLE `cluster` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '集群id',
  `cluster_name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
  `zookeeper` varchar(512) NOT NULL DEFAULT '' COMMENT 'zk地址',
  `bootstrap_servers` varchar(512) NOT NULL DEFAULT '' COMMENT 'server地址',
  `kafka_version` varchar(32) NOT NULL DEFAULT '' COMMENT 'kafka版本',
  `security_properties` text COMMENT 'Kafka安全认证参数',
  `jmx_properties` text COMMENT 'JMX配置',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT ' 监控标记, 0表示未监控, 1表示监控中',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_name` (`cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cluster信息表';

--
-- Table structure for table `cluster_metrics`
--

-- DROP TABLE IF EXISTS `cluster_metrics`;
CREATE TABLE `cluster_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `metrics` text COMMENT '指标',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_gmt_create` (`cluster_id`,`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='clustermetrics信息';

--
-- Table structure for table `cluster_tasks`
--

-- DROP TABLE IF EXISTS `cluster_tasks`;
CREATE TABLE `cluster_tasks` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `uuid` varchar(128) NOT NULL DEFAULT '' COMMENT '任务UUID',
  `cluster_id` bigint(128) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `task_type` varchar(128) NOT NULL DEFAULT '' COMMENT '任务类型',
  `kafka_package` text COMMENT 'kafka包',
  `kafka_package_md5` varchar(128) NOT NULL DEFAULT '' COMMENT 'kafka包的md5',
  `server_properties` text COMMENT 'kafkaserver配置',
  `server_properties_md5` varchar(128) NOT NULL DEFAULT '' COMMENT '配置文件的md5',
  `agent_task_id` bigint(128) NOT NULL DEFAULT '-1' COMMENT '任务id',
  `agent_rollback_task_id` bigint(128) NOT NULL DEFAULT '-1' COMMENT '回滚任务id',
  `host_list` text COMMENT '升级的主机',
  `pause_host_list` text COMMENT '暂停点',
  `rollback_host_list` text COMMENT '回滚机器列表',
  `rollback_pause_host_list` text COMMENT '回滚暂停机器列表',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `task_status` int(11) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集群任务(集群升级部署)';

--
-- Table structure for table `config`
--

-- DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `config_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '配置key',
  `config_value` text COMMENT '配置value',
  `config_description` text COMMENT '备注说明',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '0标识使用中，-1标识已废弃',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表';

--
-- Table structure for table `controller`
--

-- DROP TABLE IF EXISTS `controller`;
CREATE TABLE `controller` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerid',
  `host` varchar(256) NOT NULL DEFAULT '' COMMENT '主机名',
  `timestamp` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'controller变更时间',
  `version` int(16) NOT NULL DEFAULT '-1' COMMENT 'controller格式版本',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_broker_id_timestamp` (`cluster_id`,`broker_id`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='controller记录表';

--
-- Table structure for table `gateway_config`
--

-- DROP TABLE IF EXISTS `gateway_config`;
CREATE TABLE `gateway_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(128) NOT NULL DEFAULT '' COMMENT '配置类型',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '配置名称',
  `value` text COMMENT '配置值',
  `version` bigint(20) unsigned NOT NULL DEFAULT '1' COMMENT '版本信息',
  `description` text COMMENT '描述信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_type_name` (`type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='gateway配置';
INSERT INTO gateway_config(type, name, value, `version`, `description`) values('SD_QUEUE_SIZE', 'SD_QUEUE_SIZE', 100000000, 1, '任意集群队列大小');
INSERT INTO gateway_config(type, name, value, `version`, `description`) values('SD_APP_RATE', 'SD_APP_RATE', 100000000, 1, '任意一个App限速');
INSERT INTO gateway_config(type, name, value, `version`, `description`) values('SD_IP_RATE', 'SD_IP_RATE', 100000000, 1, '任意一个IP限速');
INSERT INTO gateway_config(type, name, value, `version`, `description`) values('SD_SP_RATE', 'app_01234567', 100000000, 1, '指定App限速');
INSERT INTO gateway_config(type, name, value, `version`, `description`) values('SD_SP_RATE', '192.168.0.1', 100000000, 1, '指定IP限速');

--
-- Table structure for table `heartbeat`
--

-- DROP TABLE IF EXISTS `heartbeat`;
CREATE TABLE `heartbeat` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ip` varchar(128) NOT NULL DEFAULT '' COMMENT '主机ip',
  `hostname` varchar(256) NOT NULL DEFAULT '' COMMENT '主机名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='心跳信息';

--
-- Table structure for table `kafka_acl`
--

-- DROP TABLE IF EXISTS `kafka_acl`;
CREATE TABLE `kafka_acl` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `access` int(11) NOT NULL DEFAULT '0' COMMENT '0:无权限, 1:读, 2:写, 3:读写',
  `operation` int(11) NOT NULL DEFAULT '0' COMMENT '0:创建, 1:更新 2:删除, 以最新的一条数据为准',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限信息(kafka-broker)';

--
-- Table structure for table `kafka_bill`
--

-- DROP TABLE IF EXISTS `kafka_bill`;
CREATE TABLE `kafka_bill` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `principal` varchar(64) NOT NULL DEFAULT '' COMMENT '负责人',
  `quota` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '配额, 单位mb/s',
  `cost` double(53,2) NOT NULL DEFAULT '0.00' COMMENT '成本, 单位元',
  `cost_type` int(16) NOT NULL DEFAULT '0' COMMENT '成本类型, 0:共享集群, 1:独享集群, 2:独立集群',
  `gmt_day` varchar(64) NOT NULL DEFAULT '' COMMENT '计价的日期, 例如2019-02-02的计价结果',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name_gmt_day` (`cluster_id`,`topic_name`,`gmt_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='kafka账单';

--
-- Table structure for table `kafka_file`
--

-- DROP TABLE IF EXISTS `kafka_file`;
CREATE TABLE `kafka_file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `storage_name` varchar(128) NOT NULL DEFAULT '' COMMENT '存储位置',
  `file_name` varchar(128) NOT NULL DEFAULT '' COMMENT '文件名',
  `file_md5` varchar(256) NOT NULL DEFAULT '' COMMENT '文件md5',
  `file_type` int(16) NOT NULL DEFAULT '-1' COMMENT '0:kafka压缩包, 1:kafkaserver配置',
  `description` text COMMENT '备注信息',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '创建用户',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态, 0:正常, -1:删除',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_file_name_storage_name` (`cluster_id`,`file_name`,`storage_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件管理';

--
-- Table structure for table `kafka_user`
--

-- DROP TABLE IF EXISTS `kafka_user`;
CREATE TABLE `kafka_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '应用id',
  `password` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密码',
  `user_type` int(11) NOT NULL DEFAULT '0' COMMENT '0:普通用户, 1:超级用户',
  `operation` int(11) NOT NULL DEFAULT '0' COMMENT '0:创建, 1:更新 2:删除, 以最新一条的记录为准',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='kafka用户表';
INSERT INTO app(app_id, name, password, type, applicant, principals, description) VALUES ('dkm_admin', 'KM管理员', 'km_kMl4N8as1Kp0CCY', 1, 'admin', 'admin', 'KM管理员应用-谨慎对外提供');
INSERT INTO kafka_user(app_id, password, user_type, operation) VALUES ('dkm_admin', 'km_kMl4N8as1Kp0CCY', 1, 0);


--
-- Table structure for table `logical_cluster`
--

CREATE TABLE `logical_cluster` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(192) NOT NULL DEFAULT '' COMMENT '逻辑集群名称',
    `identification` varchar(192) NOT NULL DEFAULT '' COMMENT '逻辑集群标识',
    `mode` int(16) NOT NULL DEFAULT '0' COMMENT '逻辑集群类型, 0:共享集群, 1:独享集群, 2:独立集群',
    `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT '所属应用',
    `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
    `region_list` varchar(256) NOT NULL DEFAULT '' COMMENT 'regionid列表',
    `description` text COMMENT '备注说明',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`),
    UNIQUE KEY `uniq_identification` (`identification`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='逻辑集群信息表';


--
-- Table structure for table `monitor_rule`
--

-- DROP TABLE IF EXISTS `monitor_rule`;
CREATE TABLE `monitor_rule` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(192) NOT NULL DEFAULT '' COMMENT '告警名称',
  `strategy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '监控id',
  `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'appid',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监控规则';

--
-- Table structure for table `operate_record`
--

-- DROP TABLE IF EXISTS `operate_record`;
CREATE TABLE `operate_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `module_id` int(16) NOT NULL DEFAULT '-1' COMMENT '模块类型, 0:topic, 1:应用, 2:配额, 3:权限, 4:集群, -1:未知',
  `operate_id` int(16) NOT NULL DEFAULT '-1' COMMENT '操作类型, 0:新增, 1:删除, 2:修改',
  `resource` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称、app名称',
  `content` text COMMENT '操作内容',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_module_id_operate_id_operator` (`module_id`,`operate_id`,`operator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作记录';

--
-- Table structure for table `reassign_task`
--

-- DROP TABLE IF EXISTS `reassign_task`;
CREATE TABLE `reassign_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `task_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '任务ID',
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '任务名称',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `partitions` text COMMENT '分区',
  `reassignment_json` text COMMENT '任务参数',
  `real_throttle` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流值',
  `max_throttle` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流上限',
  `min_throttle` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流下限',
  `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `description` varchar(256) NOT NULL DEFAULT '' COMMENT '备注说明',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  `original_retention_time` bigint(20) NOT NULL DEFAULT '86400000' COMMENT 'Topic存储时间',
  `reassign_retention_time` bigint(20) NOT NULL DEFAULT '86400000' COMMENT '迁移时的存储时间',
  `src_brokers` text COMMENT '源Broker',
  `dest_brokers` text COMMENT '目标Broker',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic迁移信息';

--
-- Table structure for table `region`
--

-- DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(192) NOT NULL DEFAULT '' COMMENT 'region名称',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `broker_list` varchar(256) NOT NULL DEFAULT '' COMMENT 'broker列表',
  `capacity` bigint(20) NOT NULL DEFAULT '0' COMMENT '容量(B/s)',
  `real_used` bigint(20) NOT NULL DEFAULT '0' COMMENT '实际使用量(B/s)',
  `estimate_used` bigint(20) NOT NULL DEFAULT '0' COMMENT '预估使用量(B/s)',
  `description` text COMMENT '备注说明',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态，0正常，1已满',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='region信息表';

--
-- Table structure for table `topic`
--

-- DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'topic所属appid',
  `peak_bytes_in` bigint(20) NOT NULL DEFAULT '0' COMMENT '峰值流量',
  `description` text COMMENT '备注信息',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name` (`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic信息表';

--
-- Table structure for table `topic_app_metrics`
--

-- DROP TABLE IF EXISTS `topic_app_metrics`;
CREATE TABLE `topic_app_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'appid',
  `metrics` text COMMENT '指标',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_topic_name_app_id_gmt_create` (`cluster_id`,`topic_name`,`app_id`,`gmt_create`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic app metrics';

--
-- Table structure for table `topic_connections`
--

-- DROP TABLE IF EXISTS `topic_connections`;
CREATE TABLE `topic_connections` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT '应用id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT 'producer or consumer',
  `ip` varchar(32) NOT NULL DEFAULT '' COMMENT 'ip地址',
  `client_version` varchar(8) NOT NULL DEFAULT '' COMMENT '客户端版本',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_app_id_cluster_id_topic_name_type_ip_client_version` (`app_id`,`cluster_id`,`topic_name`,`type`,`ip`,`client_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic连接信息表';

--
-- Table structure for table `topic_expired`
--

-- DROP TABLE IF EXISTS `topic_expired`;
CREATE TABLE `topic_expired` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `produce_connection_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '发送连接数',
  `fetch_connection_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '消费连接数',
  `expired_day` bigint(20) NOT NULL DEFAULT '0' COMMENT '过期天数',
  `gmt_retain` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保留截止时间',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '-1:可下线, 0:过期待通知, 1+:已通知待反馈',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name` (`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic过期信息表';

--
-- Table structure for table `topic_metrics`
--

-- DROP TABLE IF EXISTS `topic_metrics`;
CREATE TABLE `topic_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'topic名称',
  `metrics` text COMMENT '指标数据JSON',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_topic_name_gmt_create` (`cluster_id`,`topic_name`,`gmt_create`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topicmetrics表';

--
-- Table structure for table `topic_report`
--

-- DROP TABLE IF EXISTS `topic_report`;
CREATE TABLE `topic_report` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始上报时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束上报时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name` (`cluster_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开启jmx采集的topic';

--
-- Table structure for table `topic_request_time_metrics`
--

-- DROP TABLE IF EXISTS `topic_request_time_metrics`;
CREATE TABLE `topic_request_time_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `metrics` text COMMENT '指标',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_topic_name_gmt_create` (`cluster_id`,`topic_name`,`gmt_create`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic请求耗时信息';

--
-- Table structure for table `topic_statistics`
--

-- DROP TABLE IF EXISTS `topic_statistics`;
CREATE TABLE `topic_statistics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic名称',
  `offset_sum` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'offset和',
  `max_avg_bytes_in` double(53,2) NOT NULL DEFAULT '-1.00' COMMENT '峰值的均值流量',
  `gmt_day` varchar(64) NOT NULL DEFAULT '' COMMENT '日期2020-03-30的形式',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `max_avg_messages_in` double(53,2) NOT NULL DEFAULT '-1.00' COMMENT '峰值的均值消息条数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_topic_name_gmt_day` (`cluster_id`,`topic_name`,`gmt_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic统计信息表';

--
-- Table structure for table `topic_throttled_metrics`
--

-- DROP TABLE IF EXISTS `topic_throttled_metrics`;
CREATE TABLE `topic_throttled_metrics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'topic name',
  `app_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'app',
  `produce_throttled` tinyint(8) NOT NULL DEFAULT '0' COMMENT '是否是生产耗时',
  `fetch_throttled` tinyint(8) NOT NULL DEFAULT '0' COMMENT '是否是消费耗时',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_id_topic_name_app_id` (`cluster_id`,`topic_name`,`app_id`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='topic限流信息';

--
-- Table structure for table `work_order`
--

-- DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int(16) NOT NULL DEFAULT '-1' COMMENT '工单类型',
  `title` varchar(512) NOT NULL DEFAULT '' COMMENT '工单标题',
  `applicant` varchar(64) NOT NULL DEFAULT '' COMMENT '申请人',
  `description` text COMMENT '备注信息',
  `approver` varchar(64) NOT NULL DEFAULT '' COMMENT '审批人',
  `gmt_handle` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
  `opinion` varchar(256) NOT NULL DEFAULT '' COMMENT '审批信息',
  `extensions` text COMMENT '扩展信息',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '工单状态, 0:待审批, 1:通过, 2:拒绝, 3:取消',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工单表';