#-----------------------创建表-----------------------
DROP TABLE IF EXISTS `logi_security_dept`;
CREATE TABLE `logi_security_dept`
(
    id          int auto_increment  primary key,
    dept_name   varchar(10) not null comment '部门名',
    parent_id   int         not null comment '父部门id',
    leaf        tinyint(1)  not null comment '是否叶子部门',
    level       tinyint     not null comment 'parentId为0的层级为1',
    description varchar(20) null comment '描述',
    create_time timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint(1) default 0                 null comment '逻辑删除',
    app_name    varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '部门信息表';

DROP TABLE IF EXISTS `logi_security_message`;
CREATE TABLE `logi_security_message`
(
    id          int auto_increment primary key,
    title       varchar(60)                          not null comment '标题',
    content     varchar(256)                         null comment '内容',
    read_tag    tinyint(1) default 0                 null comment '是否已读',
    oplog_id    int                                  null comment '操作日志id',
    user_id     int                                  null comment '这条消息属于哪个用户的，用户id',
    create_time timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint(1) default 0                 null comment '逻辑删除',
    app_name    varchar(16)                          null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '消息中心';

DROP TABLE IF EXISTS `logi_security_oplog`;
CREATE TABLE `logi_security_oplog`
(
    id                int auto_increment primary key,
    operator_ip       varchar(64)                          not null comment '操作者ip',
    operator          varchar(64)                          null comment '操作者账号',
    operate_page      varchar(64)                          not null default '' comment '操作页面',
    operate_type      varchar(64)                          not null comment '操作类型',
    target_type       varchar(64)                          not null comment '对象分类',
    target            varchar(1024)                        not null comment '操作对象',
    operation_methods            varchar(64)               not null default '' comment '操作方式',
    detail            text                                 null comment '日志详情',
    create_time       timestamp  default CURRENT_TIMESTAMP null,
    update_time       timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete         tinyint(1) default 0                 not null comment '逻辑删除',
    app_name          varchar(16)                          null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '操作日志';


DROP TABLE IF EXISTS `logi_security_oplog_extra`;
CREATE TABLE `logi_security_oplog_extra`
(
    id          int auto_increment primary key,
    info        varchar(16) null comment '信息',
    type        tinyint     not null comment '哪种信息：1：操作页面;2：操作类型;3：对象分类',
    create_time timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint(1) default 0                 null comment '逻辑删除',
    app_name    varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '操作日志信息（操作页面、操作类型、对象分类）';

DROP TABLE IF EXISTS `logi_security_permission`;
CREATE TABLE `logi_security_permission`
(
    id              int auto_increment primary key,
    permission_name varchar(40) not null comment '权限名字',
    parent_id       int         not null comment '父权限id',
    leaf            tinyint(1)  not null comment '是否叶子权限点（具体的操作）',
    level           tinyint     not null comment '权限点的层级（parentId为0的层级为1）',
    description     varchar(64) null comment '权限点描述',
    create_time     timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1) default 0                 null comment '逻辑删除',
    app_name        varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '权限表';

DROP TABLE IF EXISTS `logi_security_project`;
CREATE TABLE `logi_security_project`
(
    id           int auto_increment comment '项目id'     primary key,
    project_code varchar(128)                           not null comment '项目编号',
    project_name varchar(128)                           not null comment '项目名',
    description  varchar(512) default ''                not null comment '项目描述',
    dept_id      int                                    not null comment '部门id',
    running      tinyint(1)   default 1                 not null comment '启用 or 停用',
    create_time  timestamp    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint(1)   default 0                 not null comment '逻辑删除',
    app_name     varchar(16)                            null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '项目表';

DROP TABLE IF EXISTS `logi_security_resource_type`;
CREATE TABLE `logi_security_resource_type`
(
    id              int auto_increment primary key,
    type_name       varchar(16) null comment '资源类型名',
    create_time     timestamp    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1)   default 0                 not null comment '逻辑删除',
    app_name        varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '资源类型表';

DROP TABLE IF EXISTS `logi_security_role`;
CREATE TABLE `logi_security_role`
(
    id           int auto_increment primary key,
    role_code    varchar(128)                         not null comment '角色编号',
    role_name    varchar(128)                         not null comment '名称',
    description  varchar(128)                         null comment '角色描述',
    last_reviser varchar(30)                          null comment '最后修改人',
    create_time  timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint(1) default 0                 not null comment '逻辑删除',
    app_name     varchar(16)                          null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '角色信息';

DROP TABLE IF EXISTS `logi_security_role_permission`;
CREATE TABLE `logi_security_role_permission`
(
    id              int auto_increment primary key,
    role_id         int         not null comment '角色id',
    permission_id   int         not null comment '权限id',
    create_time     timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1) default 0                 not null comment '逻辑删除',
    app_name        varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '角色权限表（只保留叶子权限与角色关系）';

DROP TABLE IF EXISTS `logi_security_user`;
CREATE TABLE `logi_security_user`
(
    id          int auto_increment primary key,
    user_name   varchar(64)                            not null comment '用户账号',
    pw          varchar(2048)                          not null comment '用户密码',
    salt        char(5)      default ''                not null comment '密码盐',
    real_name   varchar(128) default ''                not null comment '真实姓名',
    phone       char(20)     default ''                not null comment 'mobile',
    email       varchar(30)  default ''                not null comment 'email',
    dept_id     int                                    null comment '所属部门id',
    is_delete   tinyint(1)   default 0                 not null comment '逻辑删除',
    create_time timestamp    default CURRENT_TIMESTAMP null comment '注册时间',
    update_time timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    app_name    varchar(16)                            null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '用户信息';

DROP TABLE IF EXISTS `logi_security_user_project`;
CREATE TABLE `logi_security_user_project`
(
    id              int auto_increment primary key,
    user_id         int         not null comment '用户id',
    project_id      int         not null comment '项目id',
    create_time     timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1) default 0                 not null comment '逻辑删除',
    app_name        varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '用户项目关系表（项目负责人）';

DROP TABLE IF EXISTS `logi_security_user_resource`;
CREATE TABLE `logi_security_user_resource`
(
    id                  int auto_increment primary key,
    user_id             int         not null comment '用户id',
    project_id          int         not null comment '资源所属项目id',
    resource_type_id    int         not null comment '资源类别id',
    resource_id         int         not null comment '资源id',
    control_level       tinyint     not null comment '管理级别：1（查看权限）2（管理权限）',
    create_time         timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time         timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete           tinyint(1) default 0                 not null comment '逻辑删除',
    app_name            varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '用户和资源关系表';

DROP TABLE IF EXISTS `logi_security_user_role`;
CREATE TABLE `logi_security_user_role`
(
    id              int auto_increment primary key,
    user_id         int         not null comment '用户id',
    role_id         int         not null comment '角色id',
    create_time     timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint(1) default 0                 not null comment '逻辑删除',
    app_name        varchar(16) null comment '应用名称'
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 comment '用户角色表';

-- ----------------------------
-- Table structure for logi_config
-- ----------------------------
DROP TABLE IF EXISTS `logi_security_config`;
CREATE TABLE `logi_security_config`
(
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
    `value_group` varchar(100) NOT NULL DEFAULT '' COMMENT '配置项组',
    `value_name` varchar(100) NOT NULL DEFAULT '' COMMENT '配置项名字',
    `value` text COMMENT '配置项的值',
    `edit` int(4) NOT NULL DEFAULT '1' COMMENT '是否可以编辑 1 不可编辑（程序获取） 2 可编辑',
    `status` int(4) NOT NULL DEFAULT '1' COMMENT '1 正常 2 禁用',
    `memo` varchar(1000) NOT NULL DEFAULT '' COMMENT '备注',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `app_name` varchar(16) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',
    `operator` varchar(16) COLLATE utf8_bin DEFAULT NULL COMMENT '操作者',
    PRIMARY KEY (`id`),
    KEY `idx_group_name` (`value_group`,`value_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1592 DEFAULT CHARSET=utf8 COMMENT='logi配置项';