import { systemKey } from '../constants/menu';

export default {
  yes: 'yes',
  no: 'no',
  login: 'Login',
  logout: 'Logout',
  register: 'Register',
  'login.title': 'Login',
  'login.ldap': 'Use LDAP',

  'add-task': 'Add Task',
  'sider.footer.hide': 'Hide',
  'sider.footer.expand': 'Expand',

  'test.result': 'Test Result',
  'add.task': 'Add Task',
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

  'access.cluster': 'Access Cluster',
  'access.cluster.low.version.tip': '监测到当前Version较低，建议维护Zookeeper信息以便得到更好的产品体验',
  'edit.cluster': 'Edit',
  'check.detail': 'Details',
  'healthy.setting': '健康度设置',
  'delete.cluster.confirm.title': 'Confirm to delete this cluster?',
  'delete.cluster.confirm.tip': 'Delete cluster will not release the cluster resources! It just let the cluster not managed by the platform. Please type the cluster name to confirm',
  'delete.cluster.confirm.cluster': 'Type the cluster name to confirm',
  'btn.delete': 'Delete',
  'btn.cancel': 'Cancel',
  'btn.ok': 'OK',
};
