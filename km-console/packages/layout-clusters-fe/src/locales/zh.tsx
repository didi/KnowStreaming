import { systemKey } from '../constants/menu';

export default {
  yes: '是',
  no: '否',
  login: '登录',
  logout: '退出登录',
  register: '注册',
  'login.title': '账户登录',
  'login.ldap': '使用LDAP账号登录',

  'add-task': '添加任务',
  'sider.footer.hide': '收起',
  'sider.footer.expand': '展开',

  'test.result': '测试结果',
  'add.task': '添加任务',
  'test.client.stop': 'Stop',
  'test.client.clear': 'Clear',
  'test.client.run': 'Run',

  [`menu.${systemKey}.cluster`]: 'Cluster',
  [`menu.${systemKey}.cluster.overview`]: 'Overview',

  [`menu.${systemKey}.broker`]: 'Broker',
  [`menu.${systemKey}.broker.dashbord`]: 'Overview',
  [`menu.${systemKey}.broker.list`]: 'Brokers',
  [`menu.${systemKey}.broker.controller-changelog`]: 'Controller',

  [`menu.${systemKey}.topic`]: 'Topic',
  [`menu.${systemKey}.topic.dashbord`]: 'Overview',
  [`menu.${systemKey}.topic.list`]: 'Topics',

  [`menu.${systemKey}.produce-consume`]: 'Message',
  [`menu.${systemKey}.produce-consume.producer`]: 'Produce',
  [`menu.${systemKey}.produce-consume.consumer`]: 'Consume',

  [`menu.${systemKey}.security`]: 'Security',
  [`menu.${systemKey}.security.acls`]: 'ACLs',
  [`menu.${systemKey}.security.users`]: 'Users',

  [`menu.${systemKey}.consumer-group`]: 'Consumer',
  [`menu.${systemKey}.consumer-group.operating-state`]: 'Operating State',
  [`menu.${systemKey}.consumer-group.group-list`]: 'GroupList',

  [`menu.${systemKey}.operation`]: 'Operation',
  [`menu.${systemKey}.operation.balance`]: 'Rebalance',
  [`menu.${systemKey}.operation.jobs`]: 'Job',

  [`menu.${systemKey}.connect`]: 'Connect',
  [`menu.${systemKey}.connect.dashboard`]: 'Overview',
  [`menu.${systemKey}.connect.connectors`]: 'Connectors',
  [`menu.${systemKey}.connect.workers`]: 'Workers',

  [`menu.${systemKey}.replication`]: 'Replication',
  [`menu.${systemKey}.replication.dashboard`]: 'Overview',
  [`menu.${systemKey}.replication.mirror-maker`]: 'Mirror Makers',

  [`menu.${systemKey}.acls`]: 'ACLs',

  [`menu.${systemKey}.jobs`]: 'Job',

  [`menu.${systemKey}.zookeeper`]: 'Zookeeper',
  [`menu.${systemKey}.zookeeper.dashboard`]: 'Overview',
  [`menu.${systemKey}.zookeeper.servers`]: 'Servers',

  'access.cluster': '接入集群',
  'access.cluster.low.version.tip': '监测到当前Version较低，建议维护Zookeeper信息以便得到更好的产品体验',
  'edit.cluster': '编辑集群',
  'check.detail': '查看详情',
  'healthy.setting': '健康度设置',
  'delete.cluster.confirm.title': '确定要删除此集群吗？',
  'delete.cluster.confirm.tip': '删除集群不会删除集群内的资源，仅解除平台的纳管关系！请再次输入集群名称进行确认',
  'delete.cluster.confirm.cluster': '请再次输入集群名称进行确认',
  'btn.delete': '删除',
  'btn.cancel': '取消',
  'btn.ok': '确定',
};
