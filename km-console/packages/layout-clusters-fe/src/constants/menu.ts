import { ClustersPermissionMap } from '@src/pages/CommonConfig';

const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;

export const leftMenus = (clusterId?: string) => ({
  name: `${systemKey}`,
  icon: 'icon-jiqun',
  path: `cluster/${clusterId}`,
  children: [
    {
      name: 'cluster',
      path: 'cluster',
      icon: 'icon-Cluster',
      children: [
        {
          name: 'overview',
          path: '',
          icon: '#icon-luoji',
        },
        process.env.BUSINESS_VERSION
          ? {
              name: 'balance',
              path: 'balance',
              icon: '#icon-luoji',
            }
          : undefined,
      ].filter((m) => m),
    },
    {
      name: 'broker',
      path: 'broker',
      icon: 'icon-Brokers',
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
      icon: 'icon-Topics',
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
    {
      name: 'consumer-group',
      path: 'consumers',
      icon: 'icon-ConsumerGroups',
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
    process.env.BUSINESS_VERSION
      ? {
          name: 'produce-consume',
          path: 'testing',
          icon: 'icon-a-ProduceConsume',
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
      icon: 'icon-ACLs',
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
    {
      name: 'jobs',
      path: 'jobs',
      icon: 'icon-Jobs',
    },
  ].filter((m) => m),
});

// key值需要与locale zh 中key值一致
export const permissionPoints = {
  [`menu.${systemKey}.home`]: true,
};

export const ROUTER_CACHE_KEYS = {
  home: `menu.${systemKey}.home`,
};
