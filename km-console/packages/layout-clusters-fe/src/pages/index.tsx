import React, { useState, useEffect, useCallback } from 'react';
import { Redirect, useHistory, useLocation } from 'react-router-dom';
import { DProLayout, AppContainer, RouteGuard, Spin } from 'knowdesign';
import notification from '@src/components/Notification';
import { pageRoutes } from './pageRoutes';
import { leftMenus, systemKey } from '@src/constants/menu';
import { ClustersPermissionMap } from './CommonConfig';
import { getLicenseInfo } from '@src/constants/common';
import { licenseEventBus } from '@src/constants/axiosConfig';
import { ClusterRunState } from './MutliClusterPage/List';

export const NoMatch = <Redirect to="/404" />;

const getCurrentPathname = (pathname: string) => {
  if (pathname[pathname.length - 1] === '/') {
    pathname = pathname.substring(0, pathname.length - 1);
  }
  return pathname;
};

const GlobalLoading = () => {
  return (
    <div style={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <Spin spinning={true} />
    </div>
  );
};

const LayoutContainer = () => {
  const history = useHistory();
  const { pathname } = useLocation();
  const [global] = AppContainer.useGlobalValue();
  const noSiderPages = pageRoutes.filter((item) => item.noSider) || [];
  const notCurrentSystemKey = window.location.pathname.split('/')?.[1] !== systemKey;
  const hasNoSiderPage = noSiderPages.findIndex((item) => item.path === getCurrentPathname(pathname)) > -1;
  const [showSider, setShowSider] = useState<boolean>(!(notCurrentSystemKey || hasNoSiderPage));
  const [handledLeftMenus, setHandledLeftMenus] = useState(leftMenus());

  const forbidenPaths = (path: string) => {
    // Raft 模式运行的集群没有 ZK 页面
    if (path.includes('zookeeper') && global.clusterInfo?.runState === ClusterRunState.Raft) {
      history.replace('/404');
    }
  };

  const isShowMenu = useCallback(
    (nodes: ClustersPermissionMap | ClustersPermissionMap[]) => {
      let isAllow = false;
      if (typeof nodes === 'string') {
        isAllow = global.hasPermission?.(nodes);
      } else {
        nodes.forEach((node) => {
          global.hasPermission?.(node) && (isAllow = true);
        });
      }
      return isAllow;
    },
    [global.hasPermission]
  );

  // 路由前置守卫
  const routeBeforeEach = useCallback(
    (path: string, permissionNode: string | number) => {
      getLicenseInfo((msg) => licenseEventBus.emit('licenseError', msg));
      // 判断进入页面的前置条件是否满足，如果不满足，则展示加载状态
      const isClusterNotExist = path.includes(':clusterId') && !global.clusterInfo;
      const isNotLoadedPermissions = typeof global.hasPermission !== 'function';
      const isNotLoadedMetricsDefine = typeof global.getMetricDefine !== 'function';

      if (isClusterNotExist || isNotLoadedPermissions || isNotLoadedMetricsDefine) {
        return Promise.reject(<GlobalLoading />);
      }

      // 判断页面是否需要权限
      if (permissionNode) {
        // 判断用户是否有当前页面的权限
        if (global.hasPermission(permissionNode)) {
          forbidenPaths(path);
          return Promise.resolve(true);
        } else {
          // 用户没有当前页面权限，跳转到多集群首页
          notification.error({
            message: '您没有权限访问当前页面',
          });
          history.replace('/403');
          return Promise.reject(false);
        }
      }
      forbidenPaths(path);
      return Promise.resolve(true);
    },
    [global.clusterInfo, global.hasPermission, global.getMetricDefine]
  );

  useEffect(() => {
    const notCurrentSystemKey = window.location.pathname.split('/')?.[1] !== systemKey;
    const hasNoSiderPage = noSiderPages.findIndex((item) => item.path === getCurrentPathname(pathname)) > -1;
    setShowSider(notCurrentSystemKey || hasNoSiderPage ? false : true);

    if (pathname.startsWith('/cluster') || pathname === '/') {
      const items = pathname.split('/');
      const clusterId = items[2];
      clusterId !== global.clusterInfo?.id &&
        setHandledLeftMenus(leftMenus(clusterId, pathname === '/' ? undefined : global.clusterInfo?.runState));
    }
  }, [pathname, global.clusterInfo]);

  return (
    <div id="sub-system" style={{ display: 'flex' }}>
      {showSider && (
        <DProLayout.Sider
          width={200}
          theme={'light'}
          systemKey={systemKey}
          prefixCls={'dcd-two-columns'}
          menuConf={handledLeftMenus}
          permissionPoints={isShowMenu}
        />
      )}
      <DProLayout.Content>
        <RouteGuard
          routeList={pageRoutes}
          beforeEach={routeBeforeEach}
          noMatch={() => {
            // 跳转到 config 子应用不走重定向逻辑
            if (!pathname.startsWith('/config')) {
              return NoMatch;
            } else {
              return <></>;
            }
          }}
        />
      </DProLayout.Content>
    </div>
  );
};

export default LayoutContainer;
