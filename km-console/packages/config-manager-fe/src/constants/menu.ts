const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;

export const leftMenus = {
  name: `${systemKey}`,
  path: '',
  icon: '#icon-kafka',
  children: [
    {
      name: 'setting',
      path: 'setting',
      icon: 'icon-Cluster',
    },
    {
      name: 'user',
      path: 'user',
      icon: 'icon-Brokers',
    },
    {
      name: 'operationLog',
      path: 'operation-log',
      icon: 'icon-Topics',
    },
  ],
};

// key值需要与locale zh 中key值一致
export const permissionPoints = {
  [`menu.${systemKey}.home`]: true,
};

export const ROUTER_CACHE_KEYS = {
  home: `menu.${systemKey}.home`,
  dev: `menu.${systemKey}.dev`,
  devDetail: `menu.${systemKey}.dev.detail`,
  devTable: `menu.${systemKey}.dev.table`,
};
