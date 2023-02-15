-- KS-KM自身的SQL，KS-KM依赖 Logi-Job 和 Logi-Security，因此另外两个ddl sql文件也需要执行

DROP TABLE IF EXISTS `ks_km_broker`;
CREATE TABLE `ks_km_broker` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '物理集群ID',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerId',
  `host` varchar(128) NOT NULL DEFAULT '' COMMENT 'broker主机名',
  `port` int(16) NOT NULL DEFAULT '-1' COMMENT 'broker端口',
  `jmx_port` int(16) NOT NULL DEFAULT '-1' COMMENT 'Jmx端口',
  `start_timestamp` bigint(20) NOT NULL DEFAULT '-1' COMMENT '启动时间',
  `rack` varchar(128) NOT NULL DEFAULT '' COMMENT 'Rack信息',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态: 1存活，0未存活',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `endpoint_map` varchar(1024)  NOT NULL DEFAULT '' COMMENT '监听信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_phy_id_broker_id` (`cluster_phy_id`,`broker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Broker信息表';



DROP TABLE IF EXISTS `ks_km_broker_config`;
CREATE TABLE `ks_km_broker_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerId',
  `config_name` varchar(192) NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_value` text COMMENT '配置值',
  `diff_type` int(16) NOT NULL DEFAULT '-1' COMMENT '差异类型',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_broker_name` (`cluster_phy_id`,`broker_id`,`config_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Broker配置信息表';



DROP TABLE IF EXISTS `ks_km_cluster_balance_job`;
CREATE TABLE `ks_km_cluster_balance_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `brokers` varchar(1024) NOT NULL DEFAULT '' COMMENT '均衡节点',
  `topic_black_list` varchar(4096) NOT NULL DEFAULT '' COMMENT 'topic黑名单',
  `type` int(16) NOT NULL DEFAULT '0' COMMENT '1:周期均衡，2：立即均衡',
  `balance_interval_json` text COMMENT '均衡区间详情',
  `metric_calculation_period` int(16) NOT NULL DEFAULT '0' COMMENT '指标计算周期，单位分钟',
  `reassignment_json` text COMMENT '迁移脚本',
  `parallel_num` int(16) NOT NULL DEFAULT '0' COMMENT '任务并行数',
  `execution_strategy` int(16) NOT NULL DEFAULT '0' COMMENT '执行策略， 1：优先最大副本，2：优先最小副本',
  `throttle_unit_b` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流值',
  `total_reassign_size` double NOT NULL DEFAULT '0' COMMENT '总迁移大小',
  `total_reassign_replica_num` int(16) NOT NULL DEFAULT '0' COMMENT '总迁移副本数',
  `move_in_topic_list` varchar(4096) NOT NULL DEFAULT '' COMMENT '移入topic',
  `broker_balance_detail` text COMMENT '节点均衡详情',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '任务状态 1：进行中，2：准备，3，成功，4：失败，5：取消',
  `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务开始时间',
  `finished_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务完成时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  `description` text COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集群均衡任务';



DROP TABLE IF EXISTS `ks_km_cluster_balance_job_config`;
CREATE TABLE `ks_km_cluster_balance_job_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `brokers` varchar(256) NOT NULL DEFAULT '' COMMENT '均衡节点',
  `topic_black_list` varchar(4096) NOT NULL DEFAULT '' COMMENT 'topic黑名单',
  `task_cron` varchar(64) NOT NULL DEFAULT '' COMMENT '任务周期',
  `balance_interval_json` text COMMENT '均衡区间详情',
  `metric_calculation_period` int(16) NOT NULL DEFAULT '0' COMMENT '指标计算周期，单位分钟',
  `reassignment_json` text COMMENT '迁移脚本',
  `parallel_num` int(16) NOT NULL DEFAULT '0' COMMENT '任务并行数',
  `execution_strategy` int(16) NOT NULL DEFAULT '0' COMMENT '执行策略， 1：优先最大副本，2：优先最小副本',
  `throttle_unit_b` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流值',
  `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '任务状态 0：未开启，1：开启',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集群均衡任务';



DROP TABLE IF EXISTS `ks_km_cluster_balance_reassign`;
CREATE TABLE `ks_km_cluster_balance_reassign` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `job_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '父任务ID',
  `cluster_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `partition_id` int(11) NOT NULL DEFAULT '-1' COMMENT '分区ID',
  `original_broker_ids` text COMMENT '源BrokerId列表',
  `reassign_broker_ids` text COMMENT '目标BrokerId列表',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务开始时间',
  `finished_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务完成时间',
  `extend_data` text COMMENT '扩展数据',
  `status` int(16) NOT NULL DEFAULT '2' COMMENT '任务状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集群平衡迁移详情';



DROP TABLE IF EXISTS `ks_km_group_member`;
CREATE TABLE `ks_km_group_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `group_name` varchar(192) NOT NULL DEFAULT '' COMMENT 'Group名称',
  `kafka_user` varchar(192) NOT NULL DEFAULT '' COMMENT 'Kafka用户',
  `state` varchar(64) NOT NULL DEFAULT '' COMMENT '状态',
  `member_count` int(11) NOT NULL DEFAULT '0' COMMENT '成员数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_topic_group` (`cluster_phy_id`,`topic_name`,`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='GroupMember信息表';



DROP TABLE IF EXISTS `ks_km_health_check_result`;
CREATE TABLE `ks_km_health_check_result` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `dimension` int(11) NOT NULL DEFAULT '0' COMMENT '检查维度(0:未知，1:Cluster，2:Broker，3:Topic，4:Group)',
  `config_name` varchar(192) NOT NULL DEFAULT '' COMMENT '配置名',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '物理集群ID',
  `res_name` varchar(192) NOT NULL DEFAULT '' COMMENT '资源名称',
  `passed` int(11) NOT NULL DEFAULT '0' COMMENT '检查通过(0:未通过，1:通过)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_dimension_config_cluster_res` (`dimension`,`config_name`,`cluster_phy_id`,`res_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='健康检查结果';



DROP TABLE IF EXISTS `ks_km_job`;
CREATE TABLE `ks_km_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `job_name` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '任务名称',
  `job_type` tinyint(10) NOT NULL COMMENT '任务类型',
  `job_status` tinyint(10) NOT NULL COMMENT '任务状态',
  `job_data` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '任务的详细信息',
  `job_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '任务描述',
  `cluster_id` int(11) NOT NULL COMMENT 'kafka集群id',
  `target` varchar(8192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '任务执行对象',
  `running_status` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '任务运行详细状态(json), Success：7  Fail：1  Doing：2',
  `creator` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `plan_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '计划执行时间',
  `start_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '实际执行时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_cluster_id` (`cluster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Job信息';



DROP TABLE IF EXISTS `ks_km_kafka_acl`;
CREATE TABLE `ks_km_kafka_acl` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `principal` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Kafka用户',
  `operation` int(11) NOT NULL DEFAULT '0' COMMENT '操作',
  `permission_type` int(11) NOT NULL DEFAULT '0' COMMENT '权限类型(0:未知，1:任意，2:拒绝，3:允许)',
  `host` varchar(192) NOT NULL DEFAULT '127.0.0.1' COMMENT '机器',
  `resource_type` int(11) NOT NULL DEFAULT '0' COMMENT '资源类型(0:未知，1:任意，2:Topic，3:Group，4:Cluster，5:事务，6:Token)',
  `resource_name` varchar(192) NOT NULL DEFAULT '' COMMENT '资源名称',
  `pattern_type` int(11) NOT NULL COMMENT '匹配类型(0:未知，1:任意，2:Match，3:Literal，4:prefixed)',
  `unique_field` varchar(1024) NOT NULL DEFAULT '' COMMENT '唯一字段，由cluster_phy_id等字段组成',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_uniq_field` (`unique_field`),
  KEY `idx_cluster_phy_id_principal_res_name` (`cluster_phy_id`,`principal`,`resource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ACL信息';



DROP TABLE IF EXISTS `ks_km_kafka_change_record`;
CREATE TABLE `ks_km_kafka_change_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `res_type` int(11) NOT NULL DEFAULT '-1' COMMENT '资源类型',
  `res_name` varchar(192) NOT NULL DEFAULT '' COMMENT '资源名称',
  `operate_type` int(11) NOT NULL DEFAULT '-1' COMMENT '操作类型',
  `operate_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `unique_field` varchar(1024) NOT NULL DEFAULT '' COMMENT '唯一键字段',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_field` (`unique_field`),
  KEY `idx_cluster_update_time` (`cluster_phy_id` ASC, `update_time` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Kafka变更记录表';



DROP TABLE IF EXISTS `ks_km_kafka_controller`;
CREATE TABLE `ks_km_kafka_controller` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
  `broker_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'brokerId',
  `broker_host` varchar(256) NOT NULL DEFAULT '' COMMENT '主机名',
  `broker_rack` varchar(256) NOT NULL DEFAULT '' COMMENT 'BrokerRack信息',
  `timestamp` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'controller变更时间，-1表示未存活',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_broker_timestamp` (`cluster_phy_id`,`broker_id`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='controller记录表';



DROP TABLE IF EXISTS `ks_km_kafka_user`;
CREATE TABLE `ks_km_kafka_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '名称',
  `token` varchar(8192) NOT NULL DEFAULT '' COMMENT '密钥',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_phy_id_name` (`cluster_phy_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Kafka-User信息表';



DROP TABLE IF EXISTS `ks_km_partition`;
CREATE TABLE `ks_km_partition` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `partition_id` int(11) NOT NULL DEFAULT '-1' COMMENT '分区ID',
  `leader_broker_id` int(11) NOT NULL DEFAULT '-1' COMMENT '分区的LeaderBroker，-1表示无Leader',
  `in_sync_replicas` varchar(512) NOT NULL DEFAULT '-1' COMMENT 'ISR',
  `assign_replicas` varchar(512) NOT NULL DEFAULT '-1' COMMENT 'AR',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_topic_partition` (`cluster_phy_id`,`topic_name`,`partition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Partition信息表';



DROP TABLE IF EXISTS `ks_km_physical_cluster`;
CREATE TABLE `ks_km_physical_cluster` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '集群id',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
  `zookeeper` varchar(2048) NOT NULL DEFAULT '' COMMENT 'zk地址',
  `bootstrap_servers` varchar(2048) NOT NULL DEFAULT '' COMMENT 'server地址',
  `kafka_version` varchar(32) NOT NULL DEFAULT '' COMMENT 'kafka版本',
  `client_properties` text COMMENT 'Kafka客户端配置',
  `jmx_properties` text COMMENT 'JMX配置',
  `zk_properties` text COMMENT 'ZK配置',
  `description` text COMMENT '备注',
  `auth_type` int(11) NOT NULL DEFAULT '0' COMMENT '认证类型，-1未知，0:无认证，',
  `run_state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '运行状态, 0表示未监控, 1监控中，有ZK，2:监控中，无ZK',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物理集群信息表';



DROP TABLE IF EXISTS `ks_km_platform_cluster_config`;
CREATE TABLE `ks_km_platform_cluster_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `value_group` varchar(100) NOT NULL DEFAULT '' COMMENT '配置项组',
  `value_name` varchar(100) NOT NULL DEFAULT '' COMMENT '配置项名字',
  `value` text COMMENT '配置项的值',
  `description` text COMMENT '备注',
  `operator` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '操作者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_id_group_name` (`cluster_id`,`value_group`,`value_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='KS平台集群配置';



DROP TABLE IF EXISTS `ks_km_reassign_job`;
CREATE TABLE `ks_km_reassign_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `reassignment_json` text COMMENT '迁移计划',
  `throttle_unit_byte` bigint(20) NOT NULL DEFAULT '0' COMMENT '限流值',
  `start_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '任务开始时间',
  `finished_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '任务完成时间',
  `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `description` text COMMENT '备注',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='迁移Job信息';



DROP TABLE IF EXISTS `ks_km_reassign_sub_job`;
CREATE TABLE `ks_km_reassign_sub_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `job_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '父任务ID',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '集群id',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `partition_id` int(11) NOT NULL DEFAULT '-1' COMMENT '分区ID',
  `original_broker_ids` text COMMENT '源BrokerId列表',
  `reassign_broker_ids` text COMMENT '目标BrokerId列表',
  `start_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '任务开始时间',
  `finished_time` timestamp NOT NULL DEFAULT '1971-1-1 00:00:00' COMMENT '任务完成时间',
  `extend_data` text COMMENT '扩展数据',
  `status` int(16) NOT NULL DEFAULT '0' COMMENT '任务状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='迁移SubJob信息';



DROP TABLE IF EXISTS `ks_km_topic`;
CREATE TABLE `ks_km_topic` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群ID',
  `topic_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Topic名称',
  `replica_num` int(11) NOT NULL DEFAULT '-1' COMMENT '副本数',
  `partition_num` int(11) NOT NULL DEFAULT '-1' COMMENT '分区数',
  `broker_ids` varchar(2048) NOT NULL DEFAULT '' COMMENT 'BrokerId列表',
  `partition_map` text COMMENT '分区分布信息',
  `retention_ms` bigint(20) NOT NULL DEFAULT '-2' COMMENT '保存时间，-2：未知，-1：无限制，>=0对应时间，单位ms',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Topic类型，默认0，0:普通，1:Kafka内部',
  `description` text COMMENT '备注信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(尽量与Topic实际创建时间一致)',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间(尽量与Topic实际创建时间一致)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_cluster_phy_id_topic_name` (`cluster_phy_id`,`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Topic信息表';


DROP TABLE IF EXISTS `ks_km_app_node`;
CREATE TABLE `ks_km_app_node` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `host_name` varchar(256) NOT NULL DEFAULT '' COMMENT 'host',
    `ip` varchar(256) NOT NULL DEFAULT '' COMMENT 'ip',
    `beat_time`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'node 的心跳时间',
    `app_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'km 集群的应用名',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_app_host` (`app_name`,`host_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='km集群部署的node信息';


DROP TABLE IF EXISTS `ks_km_zookeeper`;
CREATE TABLE `ks_km_zookeeper` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '物理集群ID',
    `host` varchar(128) NOT NULL DEFAULT '' COMMENT 'zookeeper主机名',
    `port` int(16) NOT NULL DEFAULT '-1' COMMENT 'zookeeper端口',
    `role` varchar(16) NOT NULL DEFAULT '-1' COMMENT '角色, leader follower observer',
    `version` varchar(128) NOT NULL DEFAULT '' COMMENT 'zookeeper版本',
    `status` int(16) NOT NULL DEFAULT '0' COMMENT '状态: 1存活，0未存活，11存活但是4字命令使用不了',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_cluster_phy_id_host_port` (`cluster_phy_id`,`host`, `port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Zookeeper信息表';


DROP TABLE IF EXISTS `ks_km_group`;
CREATE TABLE `ks_km_group` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
     `cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '集群id',
     `name` varchar(192) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group名称',
     `member_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '成员数',
     `topic_members` text CHARACTER SET utf8 COMMENT 'group消费的topic列表',
     `partition_assignor` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '分配策略',
     `coordinator_id` int(11) NOT NULL COMMENT 'group协调器brokerId',
     `type` int(11) NOT NULL COMMENT 'group类型 0：consumer 1：connector',
     `state` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '状态',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_cluster_phy_id_name` (`cluster_phy_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Group信息表';


DROP TABLE IF EXISTS `ks_kc_connect_cluster`;
CREATE TABLE `ks_kc_connect_cluster` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Connect集群ID',
     `kafka_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Kafka集群ID',
     `name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群名称',
     `group_name` varchar(128) NOT NULL DEFAULT '' COMMENT '集群Group名称',
     `cluster_url` varchar(1024) NOT NULL DEFAULT '' COMMENT '集群地址',
     `member_leader_url` varchar(1024) NOT NULL DEFAULT '' COMMENT 'URL地址',
     `version` varchar(64) NOT NULL DEFAULT '' COMMENT 'connect版本',
     `jmx_properties` text COMMENT 'JMX配置',
     `state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '集群使用的消费组状态，也表示集群状态:-1 Unknown,0 ReBalance,1 Active,2 Dead,3 Empty',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接入时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uniq_id_group_name` (`id`,`group_name`),
     UNIQUE KEY `uniq_name_kafka_cluster` (`name`,`kafka_cluster_phy_id`),
     KEY `idx_kafka_cluster_phy_id` (`kafka_cluster_phy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Connect集群信息表';


DROP TABLE IF EXISTS `ks_kc_connector`;
CREATE TABLE `ks_kc_connector` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
     `kafka_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Kafka集群ID',
     `connect_cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Connect集群ID',
     `connector_name` varchar(512) NOT NULL DEFAULT '' COMMENT 'Connector名称',
     `connector_class_name` varchar(512) NOT NULL DEFAULT '' COMMENT 'Connector类',
     `connector_type` varchar(32) NOT NULL DEFAULT '' COMMENT 'Connector类型',
     `state` varchar(45) NOT NULL DEFAULT '' COMMENT '状态',
     `topics` text COMMENT '访问过的Topics',
     `task_count` int(11) NOT NULL DEFAULT '0' COMMENT '任务数',
     `heartbeat_connector_name` varchar(512) DEFAULT '' COMMENT '心跳检测connector名称',
     `checkpoint_connector_name` varchar(512) DEFAULT '' COMMENT '进度确认connector名称',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uniq_connect_cluster_id_connector_name` (`connect_cluster_id`,`connector_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Connector信息表';


DROP TABLE IF EXISTS `ks_kc_worker`;
CREATE TABLE `ks_kc_worker` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
     `kafka_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Kafka集群ID',
     `connect_cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Connect集群ID',
     `member_id` varchar(512) NOT NULL DEFAULT '' COMMENT '成员ID',
     `host` varchar(128) NOT NULL DEFAULT '' COMMENT '主机名',
     `jmx_port` int(16) NOT NULL DEFAULT '-1' COMMENT 'Jmx端口',
     `url` varchar(1024) NOT NULL DEFAULT '' COMMENT 'URL信息',
     `leader_url` varchar(1024) NOT NULL DEFAULT '' COMMENT 'leaderURL信息',
     `leader` int(16) NOT NULL DEFAULT '0' COMMENT '状态: 1是leader，0不是leader',
     `worker_id` varchar(128) NOT NULL COMMENT 'worker地址',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uniq_cluster_id_member_id` (`connect_cluster_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker信息表';


DROP TABLE IF EXISTS `ks_kc_worker_connector`;
CREATE TABLE `ks_kc_worker_connector` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
     `kafka_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Kafka集群ID',
     `connect_cluster_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'Connect集群ID',
     `connector_name` varchar(512) NOT NULL DEFAULT '' COMMENT 'Connector名称',
     `worker_member_id` varchar(256) NOT NULL DEFAULT '',
     `task_id` int(16) NOT NULL DEFAULT '-1' COMMENT 'Task的ID',
     `state` varchar(128) DEFAULT NULL COMMENT '任务状态',
     `worker_id` varchar(128) DEFAULT NULL COMMENT 'worker信息',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uniq_relation` (`connect_cluster_id`,`connector_name`,`task_id`,`worker_member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Worker和Connector关系表';


DROP TABLE IF EXISTS `ks_ha_active_standby_relation`;
CREATE TABLE `ks_ha_active_standby_relation` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `active_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '主集群ID',
    `standby_cluster_phy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '备集群ID',
    `res_name` varchar(192) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '资源名称',
    `res_type` int(11) NOT NULL DEFAULT '-1' COMMENT '资源类型，0：集群，1：镜像Topic，2：主备Topic',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_cluster_res` (`res_type`,`active_cluster_phy_id`,`standby_cluster_phy_id`,`res_name`),
    UNIQUE KEY `uniq_res_type_standby_cluster_res_name` (`res_type`,`standby_cluster_phy_id`,`res_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='HA主备关系表';