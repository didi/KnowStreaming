export const urlPrefix = '/kafka';
import { ILeftMenu } from 'types/base-type';

export const topicMenu = [{
  href: `/topic`,
  i: 'k-icon-iconfontzhizuobiaozhun023110',
  title: '我的Topic',
}, {
  href: `/topic/topic-all`,
  i: 'k-icon-order1',
  title: '全部Topic',
}, {
  href: `/topic/app-list`,
  i: 'k-icon-gaojing',
  title: '应用管理',
}] as ILeftMenu[];

export const clusterMenu = [{
  href: `/cluster`,
  i: 'k-icon-jiqun',
  title: '我的集群',
}] as ILeftMenu[];

export const alarmMenu = [{
  href: `/alarm`,
  i: 'k-icon-jiqun',
  title: '监控告警',
}] as ILeftMenu[];

export const userMenu = [{
  href: `/user/my-order`,
  i: 'k-icon-order1',
  title: '我的申请',
  class: 'apply',
}, {
  href: `/user/my-approval`,
  i: 'k-icon-shenpi1',
  title: '我的审批',
  class: 'approval',
}, {
  href: `/user/bill`,
  i: 'k-icon-gaojing',
  title: '账单管理',
}] as ILeftMenu[];

export const adminMenu = [{
  href: `/admin`,
  i: 'k-icon-jiqun',
  title: '集群列表',
}, {
  href: `/admin/operation`,
  i: 'k-icon-xiaofeikecheng',
  title: '集群运维',
}, {
  href: `/admin/app`,
  i: 'k-icon-order1',
  title: '平台管理',
}, {
  href: `/admin/bill`,
  i: 'k-icon-renwuliebiao',
  title: '用户账单',
},{
  href: `/admin/operation-record`,
  i: 'k-icon-operationrecord',
  title: '操作记录',
}] as ILeftMenu[];

export const expertMenu = [{
  href: `/expert`,
  i: 'k-icon-jiqun',
  title: 'Topic分区热点',
}, {
  href: `/expert/topic-partition`,
  i: 'k-icon-order1',
  title: 'Topic分区不足',
}, {
  href: `/expert/topic-governance`,
  i: 'k-icon-order1',
  title: 'Topic资源治理',
}, {
  href: `/expert/diagnosis`,
  i: 'k-icon-xiaofeikecheng',
  title: '异常诊断',
}] as ILeftMenu[];
