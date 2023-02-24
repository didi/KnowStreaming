import React from 'react';
import { ClustersPermissionMap } from '@src/pages/CommonConfig';
import { ClusterRunState } from '@src/pages/MutliClusterPage/List';
const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;

export const leftMenus = (clusterId?: string, clusterRunState?: number) => ({
  name: `${systemKey}`,
  icon: 'icon-jiqun',
  path: `cluster/${clusterId}`,
  children: [
    {
      name: 'cluster',
      path: 'cluster',
      icon: 'icon-Cluster1',
    },
    {
      name: 'broker',
      path: 'broker',
      icon: 'icon-Brokers1',
      children: [
        {
          name: 'dashbord',
          path: '',
          icon: '#icon-luoji',
        },
        {
          name: 'list',
          path: 'list',
          icon: '#icon-jiqun1',
        },
        {
          name: 'controller-changelog',
          path: 'controller-changelog',
          icon: '#icon-jiqun1',
        },
      ],
    },
    {
      name: 'topic',
      path: 'topic',
      icon: 'icon-Topics1',
      children: [
        {
          name: 'dashbord',
          path: '',
          icon: 'icon-luoji',
        },
        {
          name: 'list',
          path: 'list',
          icon: 'icon-luoji',
        },
      ],
    },
    clusterRunState && clusterRunState !== ClusterRunState.Raft
      ? {
          name: (intl: any) => {
            return <span>{intl.formatMessage({ id: 'menu.cluster.zookeeper' })}</span>;
          },
          path: 'zookeeper',
          icon: 'icon-Zookeeper',
          children: [
            {
              name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.zookeeper.dashboard' })}</span>,
              path: '',
              icon: '#icon-luoji',
            },
            {
              name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.zookeeper.servers' })}</span>,
              path: 'servers',
              icon: 'icon-Jobs',
            },
          ],
        }
      : undefined,
    {
      name: (intl: any) => {
        return (
          <div className="menu-item-with-beta-tag">
            <span>{intl.formatMessage({ id: 'menu.cluster.connect' })}</span>
          </div>
        );
      },
      path: 'connect',
      icon: 'icon-Operation',
      children: [
        {
          name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.connect.dashboard' })}</span>,
          path: '',
          icon: 'icon-luoji',
        },
        {
          name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.connect.connectors' })}</span>,
          path: 'connectors',
          icon: '#icon-luoji',
        },
        {
          name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.connect.workers' })}</span>,
          path: 'workers',
          icon: 'icon-Jobs',
        },
      ].filter((m) => m),
    },
    {
      name: (intl: any) => {
        return (
          <div className="menu-item-with-beta-tag">
            <span>{intl.formatMessage({ id: 'menu.cluster.replication' })}</span>
            <div className="beta-tag"></div>
          </div>
        );
      },
      path: 'replication',
      icon: 'icon-Operation',
      children: [
        {
          name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.replication.dashboard' })}</span>,
          path: '',
          icon: 'icon-luoji',
        },
        {
          name: (intl: any) => <span>{intl.formatMessage({ id: 'menu.cluster.replication.mirror-maker' })}</span>,
          path: 'mirror-maker',
          icon: '#icon-luoji',
        },
      ].filter((m) => m),
    },
    {
      name: 'consumer-group',
      path: 'consumers',
      icon: 'icon-Consumer',
      // children: [
      //   {
      //     name: 'operating-state',
      //     path: 'operating-state/list',
      //     icon: '#icon-luoji',
      //   },
      //   {
      //     name: 'group-list',
      //     path: 'group-list',
      //     icon: '#icon-luoji',
      //   },
      // ],
    },
    {
      name: 'operation',
      path: 'operation',
      icon: 'icon-Operation',
      children: [
        process.env.BUSINESS_VERSION
          ? {
              name: 'balance',
              path: 'balance',
              icon: '#icon-luoji',
            }
          : undefined,
        {
          name: 'jobs',
          path: 'jobs',
          icon: 'icon-Jobs',
        },
      ].filter((m) => m),
    },
    process.env.BUSINESS_VERSION
      ? {
          name: 'produce-consume',
          path: 'testing',
          icon: 'icon-Message',
          permissionPoint: [ClustersPermissionMap.TEST_CONSUMER, ClustersPermissionMap.TEST_PRODUCER],
          children: [
            {
              name: 'producer',
              path: 'producer',
              icon: 'icon-luoji',
              permissionPoint: ClustersPermissionMap.TEST_PRODUCER,
            },
            {
              name: 'consumer',
              path: 'consumer',
              icon: 'icon-luoji',
              permissionPoints: ClustersPermissionMap.TEST_CONSUMER,
            },
          ],
        }
      : undefined,
    {
      name: 'security',
      path: 'security',
      icon: 'icon-Security',
      children: [
        {
          name: 'acls',
          path: 'acls',
          icon: 'icon-luoji',
        },
        {
          name: 'users',
          path: 'users',
          icon: 'icon-luoji',
        },
      ],
    },
    // {
    //   name: 'acls',
    //   path: 'acls',
    //   icon: 'icon-wodegongzuotai',
    // },
  ].filter((m) => m),
});

// key值需要与locale zh 中key值一致
export const permissionPoints = {
  [`menu.${systemKey}.home`]: true,
};

export const ROUTER_CACHE_KEYS = {
  home: `menu.${systemKey}.home`,
};
