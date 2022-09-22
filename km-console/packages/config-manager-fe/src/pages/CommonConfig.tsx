import React, { useLayoutEffect } from 'react';
import { Utils, AppContainer } from 'knowdesign';
import { goLogin } from '@src/constants/axiosConfig';

// 权限对应表
export enum ConfigPermissionMap {
  SYS_MANAGE = '系统管理',
  // 配置管理
  CONFIG_ADD = '配置管理-新增配置',
  CONFIG_EDIT = '配置管理-编辑配置',
  CONFIG_DEL = '配置管理-删除配置',
  // 用户管理
  USER_DEL = '用户管理-删除人员',
  USER_CHANGE_PASS = '用户管理-修改人员密码',
  USER_EDIT = '用户管理-编辑人员',
  USER_ADD = '用户管理-新增人员',
  // 角色管理
  ROLE_DEL = '用户管理-删除角色',
  ROLE_ASSIGN = '用户管理-分配用户角色',
  ROLE_EDIT = '用户管理-编辑角色',
  ROLE_ADD = '用户管理-新增角色',
}

export interface PermissionNode {
  id: number;
  permissionName: ConfigPermissionMap | null;
  parentId: number | null;
  has: boolean;
  leaf: boolean;
  childList: PermissionNode[];
}

const CommonConfig = (): JSX.Element => {
  const [, setGlobal] = AppContainer.useGlobalValue();

  // 获取权限树
  const getPermissionTree = (userInfo, userId: number) => {
    const getUserInfo = Utils.request(`/logi-security/api/v1/user/${userId}`);
    const getPermissionTree = Utils.request('/logi-security/api/v1/permission/tree');

    Promise.all([getPermissionTree, getUserInfo]).then(([permissionTree, userDetail]: [PermissionNode, any]) => {
      const allPermissions = permissionTree.childList;

      // 获取用户在系统管理拥有的权限
      const userPermissionTree = userDetail.permissionTreeVO.childList;
      const configPermissions = userPermissionTree.find((sys) => sys.permissionName === ConfigPermissionMap.SYS_MANAGE);
      const userPermissions: ConfigPermissionMap[] = [];
      configPermissions && configPermissions.childList.forEach((node) => node.has && userPermissions.push(node.permissionName));

      const hasPermission = (permissionName: ConfigPermissionMap) => permissionName && userPermissions.includes(permissionName);

      setGlobal((curState: any) => ({ ...curState, permissions: allPermissions, userPermissions, hasPermission, userInfo }));
    });
  };

  useLayoutEffect(() => {
    // 如果未登录，直接跳转到登录页
    const userInfoStorage = localStorage.getItem('userInfo');

    try {
      const userInfo = JSON.parse(userInfoStorage);
      const userId = userInfo.id;
      if (!userId) throw 'err';
      getPermissionTree(userInfo, userId);
    } catch (_) {
      goLogin();
    }
  }, []);

  return <></>;
};

export default CommonConfig;
