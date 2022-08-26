import { systemKey } from '../constants/menu';
/**
 * 用于左侧菜单与顶部路由导航中文展示，key值与各页面路径对应，比如dashboard页，路由：/cluster/dashbord，key值：menu.cluster.dashborad
 */
export default {
  [`menu.${systemKey}.setting`]: '配置管理',
  [`menu.${systemKey}.user`]: '用户管理',
  [`menu.${systemKey}.operationLog`]: '审计日志',

  'sider.footer.hide': '收起',
  'sider.footer.expand': '展开',
};
