-- 初始化权限
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1593', '多集群管理', '0', '0', '1', '多集群管理', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1595', '系统管理', '0', '0', '1', '系统管理', '0', 'know-streaming');

-- 多集群管理权限
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1597', '接入集群', '1593', '1', '2', '接入集群', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1599', '删除集群', '1593', '1', '2', '删除集群', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1601', 'Cluster-修改集群信息', '1593', '1', '2', 'Cluster-修改集群信息', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1603', 'Cluster-修改健康规则', '1593', '1', '2', 'Cluster-修改健康规则', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1605', 'Broker-修改Broker配置', '1593', '1', '2', 'Broker-修改Broker配置', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1607', 'Topic-新增Topic', '1593', '1', '2', 'Topic-新增Topic', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1609', 'Topic-扩分区', '1593', '1', '2', 'Topic-扩分区', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1611', 'Topic-删除Topic', '1593', '1', '2', 'Topic-扩分区', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1613', 'Topic-重置Offset', '1593', '1', '2', 'Topic-重置Offset', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1615', 'Topic-修改Topic配置', '1593', '1', '2', 'Topic-修改Topic配置', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1617', 'Consumers-重置Offset', '1593', '1', '2', 'Consumers-重置Offset', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1619', 'Test-Producer', '1593', '1', '2', 'Test-Producer', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1621', 'Test-Consumer', '1593', '1', '2', 'Test-Consumer', '0', 'know-streaming');

-- 系统管理权限
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1623', '配置管理-新增配置', '1595', '1', '2', '配置管理-新增配置', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1625', '配置管理-编辑配置', '1595', '1', '2', '配置管理-编辑配置', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1627', '配置管理-删除配置', '1595', '1', '2', '配置管理-删除配置', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1629', '用户管理-新增人员', '1595', '1', '2', '用户管理-新增人员', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1631', '用户管理-编辑人员', '1595', '1', '2', '用户管理-编辑人员', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1633', '用户管理-修改人员密码', '1595', '1', '2', '用户管理-修改人员密码', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1635', '用户管理-删除人员', '1595', '1', '2', '用户管理-删除人员', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1637', '用户管理-新增角色', '1595', '1', '2', '用户管理-新增角色', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1639', '用户管理-编辑角色', '1595', '1', '2', '用户管理-编辑角色', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1641', '用户管理-分配用户角色', '1595', '1', '2', '用户管理-分配用户角色', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('1643', '用户管理-删除角色', '1595', '1', '2', '用户管理-删除角色', '0', 'know-streaming');

-- 多集群管理权限2022-09-06新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2000', '多集群管理查看', '1593', '1', '2', '多集群管理查看', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2002', 'Topic-迁移副本', '1593', '1', '2', 'Topic-迁移副本', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2004', 'Topic-扩缩副本', '1593', '1', '2', 'Topic-扩缩副本', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2006', 'Cluster-LoadReBalance-周期均衡', '1593', '1', '2', 'Cluster-LoadReBalance-周期均衡', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2008', 'Cluster-LoadReBalance-立即均衡', '1593', '1', '2', 'Cluster-LoadReBalance-立即均衡', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2010', 'Cluster-LoadReBalance-设置集群规格', '1593', '1', '2', 'Cluster-LoadReBalance-设置集群规格', '0', 'know-streaming');


-- 系统管理权限2022-09-06新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('3000', '系统管理查看', '1595', '1', '2', '系统管理查看', '0', 'know-streaming');






-- 初始化用户
-- INSERT INTO `logi_security_user` (`id`, `user_name`, `pw`, `real_name`, `is_delete`, `app_name`) VALUES ('1', 'admin', 'V1ZkU2RHRlhOSGxOUkVsNVdETjBRVlp0Y0V0T1IwWnlaVEZ6YWxGRVJrRkpNVEU1VTJwYVUySkhlRzlSU0RBOWUwQldha28wWVd0N1d5TkFNa0FqWFgxS05sSnNiR2hBZlE9PXtAVmpKNGFre1sjQDNAI119SjZSbGxoQH0=Mv{#cdRgJ45Lqx}3IubEW87!==', '系统管理员', '0', 'know-streaming');
INSERT INTO `logi_security_user` (`id`, `user_name`, `pw`, `real_name`, `is_delete`, `app_name`) VALUES ('1', 'admin', 'V1ZkU2RHRlhOVGRSUmxweFUycFNhR0V6ZEdKSk1FRjRVVU5PWkdaVmJ6SlZiWGh6WVVWQ09YdEFWbXBLTkdGcmUxc2pRREpBSTExOVNqWlNiR3hvUUgwPXtAVmpKNGFre1sjQDNAI119SjZSbGxoQH0=Mv{#cdRgJ45Lqx}3IubEW87!==', '系统管理员', '0', 'know-streaming');

-- 初始化角色
INSERT INTO `logi_security_role` (`id`, `role_code`, `role_name`, `description`, `last_reviser`, `is_delete`, `app_name`) VALUES ('1677', 'r15477137', '管理员角色', '包含系统所有权限', 'admin', '0', 'know-streaming');

-- 初始化角色权限关系
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1597', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1599', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1601', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1603', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1605', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1607', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1609', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1611', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1613', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1615', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1617', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1619', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1621', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1593', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1623', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1625', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1627', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1629', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1631', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1633', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1635', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1637', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1639', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1641', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1643', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '1595', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2000', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2002', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2004', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2006', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2008', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2010', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '3000', '0', 'know-streaming');

-- 初始化 用户角色关系
INSERT INTO `logi_security_user_role` (`id`, `user_id`, `role_id`, `is_delete`, `app_name`) VALUES ('1', '1', '1677', '0', 'know-streaming');

INSERT INTO `logi_security_config`
(`value_group`,`value_name`,`value`,`edit`,`status`,`memo`,`is_delete`,`app_name`,`operator`)
VALUES
('SECURITY.LOGIN','SECURITY.TRICK_USERS','[\n  \"admin\"\n]',1,1,'允许跳过登录的用户',0,'know-streaming','admin');


-- 多集群管理权限2023-01-05新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2012', 'Topic-新增Topic复制', '1593', '1', '2', 'Topic-新增Topic复制', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2014', 'Topic-详情-取消Topic复制', '1593', '1', '2', 'Topic-详情-取消Topic复制', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2012', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2014', '0', 'know-streaming');


-- 多集群管理权限2023-01-18新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2016', 'MM2-新增', '1593', '1', '2', 'MM2-新增', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2018', 'MM2-编辑', '1593', '1', '2', 'MM2-编辑', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2020', 'MM2-删除', '1593', '1', '2', 'MM2-删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2022', 'MM2-重启', '1593', '1', '2', 'MM2-重启', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2024', 'MM2-暂停&恢复', '1593', '1', '2', 'MM2-暂停&恢复', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2016', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2018', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2020', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2022', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2024', '0', 'know-streaming');


-- 多集群管理权限2023-06-27新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2026', 'Connector-新增', '1593', '1', '2', 'Connector-新增', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2028', 'Connector-编辑', '1593', '1', '2', 'Connector-编辑', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2030', 'Connector-删除', '1593', '1', '2', 'Connector-删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2032', 'Connector-重启', '1593', '1', '2', 'Connector-重启', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2034', 'Connector-暂停&恢复', '1593', '1', '2', 'Connector-暂停&恢复', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2026', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2028', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2030', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2032', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2034', '0', 'know-streaming');


-- 多集群管理权限2023-06-29新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2036', 'Security-ACL新增', '1593', '1', '2', 'Security-ACL新增', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2038', 'Security-ACL删除', '1593', '1', '2', 'Security-ACL删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2040', 'Security-User新增', '1593', '1', '2', 'Security-User新增', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2042', 'Security-User删除', '1593', '1', '2', 'Security-User删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2044', 'Security-User修改密码', '1593', '1', '2', 'Security-User修改密码', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2036', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2038', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2040', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2042', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2044', '0', 'know-streaming');


-- 多集群管理权限2023-07-06新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2046', 'Group-删除', '1593', '1', '2', 'Group-删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2048', 'GroupOffset-Topic纬度删除', '1593', '1', '2', 'GroupOffset-Topic纬度删除', '0', 'know-streaming');
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2050', 'GroupOffset-Partition纬度删除', '1593', '1', '2', 'GroupOffset-Partition纬度删除', '0', 'know-streaming');

INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2046', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2048', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2050', '0', 'know-streaming');

-- 多集群管理权限2023-07-18新增
INSERT INTO `logi_security_permission` (`id`, `permission_name`, `parent_id`, `leaf`, `level`, `description`, `is_delete`, `app_name`) VALUES ('2052', 'Security-User查看密码', '1593', '1', '2', 'Security-User查看密码', '0', 'know-streaming');
INSERT INTO `logi_security_role_permission` (`role_id`, `permission_id`, `is_delete`, `app_name`) VALUES ('1677', '2052', '0', 'know-streaming');
