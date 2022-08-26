/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
const apiPrefix = '/logi-security/api/v1';
function getApi(path: string) {
  return `${apiPrefix}${path}`;
}

const api = {
  // 公共
  permissionTree: getApi('/permission/tree'),

  // 配置
  configGroupList: getApi('/config/group/list'),
  configList: getApi('/config/page'),
  configDetail: getApi('/config/get'),
  configSwtichStatus: getApi('/config/switch'), // 切换配置开关状态
  addConfig: getApi('/config/add'),
  editConfig: getApi('/config/edit'),
  delConfig: getApi('/config/del'),

  // 用户
  userList: getApi('/user/page'),
  user: (id: number) => getApi(`/user/${id}`), // 用户详情 / 删除用户
  addUser: getApi('/user/add'),
  editUser: getApi('/user/edit'),
  getUsersByRoleId: (roleId: number) => getApi(`/user/list/role/${roleId}`),

  // 角色
  editRole: getApi('/role'),
  roleList: getApi('/role/page'),
  simpleRoleList: getApi('/role/list'),
  role: (id: number) => getApi(`/role/${id}`), // 角色详情 / 删除角色
  getAssignedUsersByRoleId: (id: number) => getApi(`/role/assign/list/${id}`), // 根据角色 id 获取已分配用户简要信息列表
  assignRoles: getApi('/role/assign'),
  checkRole: (id: number) => getApi(`/role/delete/check/${id}`), // 判断该角色是否已经分配给用户

  // 日志
  oplogTypeList: getApi('/oplog/type/list'),
  oplogList: getApi('/oplog/page'),
};

export default api;
