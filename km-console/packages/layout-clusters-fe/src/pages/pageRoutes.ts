import ClusterManage from './MutliClusterPage/HomePage';

import { NoMatch } from '.';
import CommonRoute from './CommonRoute';

import BrokerBoard from './BrokerDashboard/index';
import BrokerList from './BrokerList';
import BrokerControllerChangeLog from './BrokerControllerChangeLog';

import TopicBoard from './TopicDashboard';
import TopicList from './TopicList';

import Consumers from './ConsumerGroup';

import Jobs from './Jobs';

import TestingConsumer from './TestingConsumer';
import TestingProduce from './TestingProduce';
import SingleClusterDetail from './SingleClusterDetail';
import CommonConfig, { ClustersPermissionMap } from './CommonConfig';
import SecurityACLs from './SecurityACLs';
import SecurityUsers from './SecurityUsers';
import LoadRebalance from './LoadRebalance';

import Zookeeper from './Zookeeper';
import ZookeeperDashboard from './ZookeeperDashboard';

import ConnectDashboard from './ConnectDashboard';
import Connectors from './Connect';
import Workers from './Connect/Workers';

import MirrorMaker2 from './MirrorMaker2';
import MirrorMakerDashboard from './MirrorMakerDashBoard';

const pageRoutes = [
  {
    path: '/',
    exact: true,
    component: ClusterManage,
    commonRoute: CommonConfig,
    noSider: true,
  },
  // 集群首页
  {
    path: '/cluster/:clusterId',
    component: SingleClusterDetail,
    commonRoute: CommonRoute,
    noSider: false,
    children: [
      {
        path: 'cluster',
        exact: true,
        component: SingleClusterDetail,
        noSider: false,
      },
      // broker相关页面
      {
        path: 'broker',
        exact: true,
        component: BrokerBoard,
        noSider: false,
      },
      {
        path: 'broker/list',
        exact: true,
        component: BrokerList,
        noSider: false,
      },
      {
        path: 'broker/controller-changelog',
        exact: true,
        component: BrokerControllerChangeLog,
        noSider: false,
      },
      // topic相关页面
      {
        path: 'topic',
        exact: true,
        component: TopicBoard,
        noSider: false,
      },
      {
        path: 'topic/list',
        exact: true,
        component: TopicList,
        noSider: false,
      },
      // testing 生产 消费相关页面
      process.env.BUSINESS_VERSION
        ? {
            path: 'testing/consumer',
            exact: true,
            component: TestingConsumer,
            noSider: false,
            permissionNode: ClustersPermissionMap.TEST_CONSUMER,
          }
        : undefined,
      process.env.BUSINESS_VERSION
        ? {
            path: 'testing/producer',
            exact: true,
            component: TestingProduce,
            permissionNode: ClustersPermissionMap.TEST_PRODUCER,
          }
        : undefined,
      // consumers消费组相关页面
      {
        path: 'consumers',
        exact: true,
        component: Consumers,
        noSider: false,
      },
      // 负载均衡
      process.env.BUSINESS_VERSION
        ? {
            path: 'operation/balance',
            exact: true,
            component: LoadRebalance,
            noSider: false,
          }
        : undefined,
      {
        path: 'operation/jobs',
        exact: true,
        component: Jobs,
        noSider: false,
      },
      {
        path: 'zookeeper',
        exact: true,
        component: ZookeeperDashboard,
        noSider: false,
      },
      {
        path: 'zookeeper/servers',
        exact: true,
        component: Zookeeper,
        noSider: false,
      },
      {
        path: 'connect',
        exact: true,
        component: ConnectDashboard,
        noSider: false,
      },
      {
        path: 'connect/connectors',
        exact: true,
        component: Connectors,
        noSider: false,
      },
      {
        path: 'connect/workers',
        exact: true,
        component: Workers,
        noSider: false,
      },
      {
        path: 'replication',
        exact: true,
        component: MirrorMakerDashboard,
        noSider: false,
      },
      {
        path: 'replication/mirror-maker',
        exact: true,
        component: MirrorMaker2,
        noSider: false,
      },
      {
        path: 'security/acls',
        exact: true,
        component: SecurityACLs,
        noSider: false,
      },
      {
        path: 'security/users',
        exact: true,
        component: SecurityUsers,
        noSider: false,
      },
      {
        path: '*',
        component: () => NoMatch,
      },
    ].filter((p) => p),
  },
];

export { pageRoutes };
