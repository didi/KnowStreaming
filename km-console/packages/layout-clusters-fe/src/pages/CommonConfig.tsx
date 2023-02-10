import React, { useLayoutEffect } from 'react';
import { Utils, AppContainer } from 'knowdesign';
import api, { MetricType } from '../api';
import { goLogin } from '@src/constants/axiosConfig';

// 权限对应表
export enum ClustersPermissionMap {
  CLUSTERS_MANAGE = '多集群管理',
  CLUSTERS_MANAGE_VIEW = '多集群管理查看',
  // Cluster
  CLUSTER_ADD = '接入集群',
  CLUSTER_DEL = '删除集群',
  CLUSTER_CHANGE_HEALTHY = 'Cluster-修改健康规则',
  CLUSTER_CHANGE_INFO = 'Cluster-修改集群信息',
  // LoadReBalance
  REBALANCE_CYCLE = 'Cluster-LoadReBalance-周期均衡',
  REBALANCE_IMMEDIATE = 'Cluster-LoadReBalance-立即均衡',
  REBALANCE_SETTING = 'Cluster-LoadReBalance-设置集群规格',
  // Broker
  BROKER_CHANGE_CONFIG = 'Broker-修改Broker配置',
  // Topic
  TOPIC_CHANGE_CONFIG = 'Topic-修改Topic配置',
  TOPIC_RESET_OFFSET = 'Topic-重置Offset',
  TOPIC_DEL = 'Topic-删除Topic',
  TOPIC_EXPOND = 'Topic-扩分区',
  TOPIC_ADD = 'Topic-新增Topic',
  TOPIC_MOVE_REPLICA = 'Topic-迁移副本',
  TOPIC_CHANGE_REPLICA = 'Topic-扩缩副本',
  TOPIC_REPLICATOR = 'Topic-新增Topic复制',
  TOPIC_CANCEL_REPLICATOR = 'Topic-详情-取消Topic复制',
  // Consumers
  CONSUMERS_RESET_OFFSET = 'Consumers-重置Offset',
  // Test
  TEST_CONSUMER = 'Test-Consumer',
  TEST_PRODUCER = 'Test-Producer',
  // MM2
  MM2_ADD = 'MM2-新增',
  MM2_CHANGE_CONFIG = 'MM2-编辑',
  MM2_DELETE = 'MM2-删除',
  MM2_RESTART = 'MM2-重启',
  MM2_STOP_RESUME = 'MM2-暂停&恢复',
}

export interface PermissionNode {
  id: number;
  permissionName: ClustersPermissionMap | null;
  parentId: number | null;
  has: boolean;
  leaf: boolean;
  childList: PermissionNode[];
}

export interface MetricsDefine {
  [metricName: string]: {
    category: string;
    type: number;
    name: string;
    desc: string;
    unit: string;
    support: boolean | null;
    minVersion: number;
    maxVersion: number;
  };
}

const CommonConfig = () => {
  const [global, setGlobal] = AppContainer.useGlobalValue();
  const userInfo = localStorage.getItem('userInfo');

  // 获取权限树
  const getPermissions = (userId: number) => {
    const getUserInfo = Utils.request(api.getUserInfo(userId), {
      retryTimes: 2,
    });
    const getPermissionTree = Utils.request(api.getPermissionTree, {
      retryTimes: 2,
    });

    Promise.all([getPermissionTree, getUserInfo]).then(([permissionTree, userDetail]: [PermissionNode, any]) => {
      const allPermissions = permissionTree.childList;

      // 获取用户在多集群管理拥有的权限
      const userPermissionTree = userDetail.permissionTreeVO.childList;
      const clustersPermissions = userPermissionTree.find(
        (sys: PermissionNode) => sys.permissionName === ClustersPermissionMap.CLUSTERS_MANAGE
      );
      const userPermissions: ClustersPermissionMap[] = [];
      clustersPermissions &&
        clustersPermissions.childList.forEach((node: PermissionNode) => node.has && userPermissions.push(node.permissionName));

      const hasPermission = (permissionName: ClustersPermissionMap) => permissionName && userPermissions.includes(permissionName);

      setGlobal((curState: any) => ({ ...curState, permissions: allPermissions, userPermissions, hasPermission, userInfo }));
    });

    return true;
  };

  // 获取指标信息
  const getMetricsDefine = () => {
    Utils.request(api.getKafkaVersionItems(), {
      retryTimes: 2,
    }).then((metricsDefine: MetricsDefine) => {
      const getMetricDefine = (type: MetricType, metricName: string) => metricsDefine[`${type}@${metricName}`] || null;
      setGlobal((curState: any) => ({ ...curState, metricsDefine, getMetricDefine }));
    });
  };

  useLayoutEffect(() => {
    // 如果未登录，直接跳转到登录页
    const userInfo = localStorage.getItem('userInfo');
    let userId: number;

    try {
      userId = JSON.parse(userInfo).id;
      if (!userId) throw 'err';
    } catch (_) {
      goLogin();
      return;
    }

    // 仅获取一次全局权限
    if (!global.permissions) {
      getPermissions(userId);
    }
    if (!global.metricsDefine) {
      getMetricsDefine();
    }
  }, []);

  return <></>;
};

export default CommonConfig;
