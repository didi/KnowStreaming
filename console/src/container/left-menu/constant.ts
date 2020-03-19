export const userMenu = [{
  href: '/',
  i: 'k-icon-iconfontzhizuobiaozhun023110',
  title: 'Topic列表',
}, {
  href: '/user/my_order',
  i: 'k-icon-order1',
  title: '工单列表',
}, {
  href: '/user/alarm',
  i: 'k-icon-gaojing',
  title: '告警配置',
}, {
  href: '/user/modify_user',
  i: 'k-icon-yonghuguanli',
  title: '密码修改',
}];

export const adminMenu = [{
  href: '/admin',
  i: 'k-icon-jiqun',
  title: '集群列表',
}, {
  href: '/admin/order',
  i: 'k-icon-order1',
  title: '资源审批',
},
// }, {
//   href: '/admin/task',
//   i: 'k-icon-renwuliebiao',
//   title: '任务列表',
// }, {
// {
//   href: '/admin/alarm',
//   i: 'k-icon-gaojing',
//   title: '告警配置',
// }, {
{
  href: '/admin/user_manage',
  i: 'k-icon-yonghuguanli',
  title: '用户管理',
},
// }, {
//   href: '/admin/auto_approval',
//   i: 'k-icon-shenpi1',
//   title: '自动审批管理',
// }, {
{
  href: '/admin/operation',
  i: 'k-icon-xiaofeikecheng',
  title: '任务管理',
// }, {
//   href: '/admin/modify_user',
//   i: 'k-icon-jiaoseshouquan',
//   title: '密码修改',
}];

export interface IMenuItem {
  href: string;
  i: string;
  title: string;
}

export const userMap = new Map<string, IMenuItem>();
userMenu.forEach(m => {
  userMap.set(m.href, m);
});

export const adminMap = new Map<string, IMenuItem>();
adminMenu.forEach(m => {
  adminMap.set(m.href, m);
});

export const getActiveMenu = (mode: 'admin' | 'user', href: string) => {
  const map = mode === 'admin' ? adminMap : userMap;
  const defaultMenu = mode === 'admin' ? '/admin' : '/';
  const menuItem = map.get(href);
  return menuItem && menuItem.href || defaultMenu;
};
