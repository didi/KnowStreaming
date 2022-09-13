-- Logi-Job模块的sql，安装KS-KM需要执行该sql


DROP TABLE IF EXISTS `logi_job`;
CREATE TABLE `logi_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'task taskCode',
  `task_code` varchar(255) NOT NULL DEFAULT '' COMMENT '任务code',
  `class_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类的全限定名',
  `try_times` int(10) NOT NULL DEFAULT '0' COMMENT '第几次重试',
  `worker_code` varchar(200) NOT NULL DEFAULT '' COMMENT '执行机器',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '被调度的应用名称',
  `start_time` datetime DEFAULT '1971-01-01 00:00:00' COMMENT '开始时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_code` (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='正在执行的job信息';



DROP TABLE IF EXISTS `logi_job_log`;
CREATE TABLE `logi_job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'job taskCode',
  `task_code` varchar(255) NOT NULL DEFAULT '' COMMENT '任务code',
  `task_name` varchar(255) NOT NULL DEFAULT '' COMMENT '任务名称',
  `task_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '任务描述',
  `task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '任务id',
  `class_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类的全限定名',
  `try_times` int(10) NOT NULL DEFAULT '0' COMMENT '第几次重试',
  `worker_code` varchar(200) NOT NULL DEFAULT '' COMMENT '执行机器',
  `worker_ip` varchar(200) NOT NULL DEFAULT '' COMMENT '执行机器ip',
  `start_time` datetime DEFAULT '1971-01-01 00:00:00' COMMENT '开始时间',
  `end_time` datetime DEFAULT '1971-01-01 00:00:00' COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行结果 1成功 2失败 3取消',
  `error` text NOT NULL COMMENT '错误信息',
  `result` text NOT NULL COMMENT '执行结果',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '被调度的应用名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_job_code` (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job执行历史日志';



DROP TABLE IF EXISTS `logi_task`;
CREATE TABLE `logi_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'task taskCode',
  `task_name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `task_desc` varchar(1000) NOT NULL DEFAULT '' COMMENT '任务描述',
  `cron` varchar(100) NOT NULL DEFAULT '' COMMENT 'cron 表达式',
  `class_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类的全限定名',
  `params` varchar(1000) NOT NULL DEFAULT '' COMMENT '执行参数 map 形式{key1:value1,key2:value2}',
  `retry_times` int(10) NOT NULL DEFAULT '0' COMMENT '允许重试次数',
  `last_fire_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上次执行时间',
  `timeout` bigint(20) NOT NULL DEFAULT '0' COMMENT '超时 毫秒',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1等待 2运行中 3暂停',
  `sub_task_codes` varchar(1000) NOT NULL DEFAULT '' COMMENT '子任务code列表,逗号分隔',
  `consensual` varchar(200) NOT NULL DEFAULT '' COMMENT '执行策略',
  `owner` varchar(200) NOT NULL DEFAULT '' COMMENT '责任人',
  `task_worker_str` varchar(3000) NOT NULL DEFAULT '' COMMENT '机器执行信息',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '被调度的应用名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_code` (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务信息';



DROP TABLE IF EXISTS `logi_task_lock`;
CREATE TABLE `logi_task_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'task taskCode',
  `worker_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'worker taskCode',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '被调度的应用名称',
  `expire_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务锁';



DROP TABLE IF EXISTS `logi_worker`;
CREATE TABLE `logi_worker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `worker_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'worker taskCode',
  `worker_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'worker名',
  `ip` varchar(100) NOT NULL DEFAULT '' COMMENT 'worker的ip',
  `cpu` int(11) NOT NULL DEFAULT '0' COMMENT 'cpu数量',
  `cpu_used` double NOT NULL DEFAULT '0' COMMENT 'cpu使用率',
  `memory` double NOT NULL DEFAULT '0' COMMENT '内存,以M为单位',
  `memory_used` double NOT NULL DEFAULT '0' COMMENT '内存使用率',
  `jvm_memory` double NOT NULL DEFAULT '0' COMMENT 'jvm堆大小，以M为单位',
  `jvm_memory_used` double NOT NULL DEFAULT '0' COMMENT 'jvm堆使用率',
  `job_num` int(10) NOT NULL DEFAULT '0' COMMENT '正在执行job数',
  `heartbeat` datetime DEFAULT '1971-01-01 00:00:00' COMMENT '心跳时间',
  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '被调度的应用名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `worker_code` (`worker_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker信息';



DROP TABLE IF EXISTS `logi_worker_blacklist`;
CREATE TABLE `logi_worker_blacklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `worker_code` varchar(100) NOT NULL DEFAULT '' COMMENT 'worker taskCode',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `worker_code` (`worker_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker黑名单列表';
