import { IAppItem, IStringMap } from './base-type';

export interface IStrategyAction {
  callback: string;
  converge: string;
  notifyGroup: string[];
  type?: string;
  sendRecovery?: number;
}

export interface IStrategyExpression {
  eopt: string;
  func: string;
  metric: string;
  params: string;
  threshold: number;
}
export interface IStrategyFilter {
  tkey: string;
  topt: string;
  tval: string[];
  clusterIdentification?: string;
}
export interface IRequestParams {
  appId: string;
  id?: number;
  name: string;
  periodDaysOfWeek: string;
  periodHoursOfDay: string;
  priority: number;
  strategyActionList: IStrategyAction[];
  strategyExpressionList: IStrategyExpression[];
  strategyFilterList: IStrategyFilter[];
}
export interface IConfigForm {
  level: number;
  acceptGroup: string[];
  alarmPeriod: number;
  alarmTimes: number;
  alarmName: string;
  callback: string;
}

export interface ITypeForm {
  app: string;
  type: string;
}

export interface IMonitorStrategyDetail {
  appSummary: IAppItem;
  createTime: number;
  id: number;
  modifyTime: number;
  monitorRule: IRequestParams;
  name: string;
  operator: string;
}

export interface IFilterValue {
  cluster: number;
  clusterName?: string;
  topic: string;
  location?: string;
  consumerGroup?: string;
}

export interface IAlarmTime {
  hours: number[];
  weeks: number[];
}

export interface IMetricType {
  metricName: string;
}
export interface IMonitorMetricType {
  metricNames: IMetricType[];
}
