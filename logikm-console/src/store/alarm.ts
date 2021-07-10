import { observable, action } from 'mobx';
import { message } from 'component/antd';
import { IMonitorStrategies, IMonitorAlerts, IMonitorSilences, IMonitorGroups, IAlertsDetail, IMonitorAlert, IMonitorMetric, IMonitorType } from 'types/base-type';
import { getMonitorStrategies, deteleMonitorStrategies, getMonitorAlerts, getAlertsDetail, createSilences, getMonitorSilences, modifyMask, getSilencesDetail, deleteSilences, getMonitorType, addMonitorStrategy, getMonitorDetail, modifyMonitorStrategy, getMonitorNotifyGroups } from 'lib/api';
import { getMonitorMetricOption } from 'lib/line-charts-config';
import { IRequestParams, IMonitorStrategyDetail, IMonitorMetricType, IMetricType } from 'types/alarm';
import { urlPrefix } from 'constants/left-menu';

class Alarm {

  @observable
  public loading: boolean = false;

  @observable
  public monitorStrategies: IMonitorStrategies[] = [];

  @observable
  public monitorAlerts: IMonitorAlerts[] = [];

  @observable
  public alertsDetail: IAlertsDetail;

  @observable
  public monitorAlert: IMonitorAlert;

  @observable
  public monitorMetric: IMonitorMetric;

  @observable
  public monitorSilences: IMonitorSilences[] = [];

  @observable
  public silencesDetail: IMonitorSilences;

  @observable
  public monitorGroups: IMonitorGroups[] = [];

  @observable
  public monitorType: string = '';

  @observable
  public currentTopic: string = '';

  @observable
  public monitorTypeList: IMetricType[] = [];

  @observable
  public monitorStrategyDetail: IMonitorStrategyDetail = {} as IMonitorStrategyDetail;

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public changeMonitorStrategyType(type: string) {
    this.monitorType = type;
  }

  @action.bound
  public changeTopic(topic: string) {
    this.currentTopic = topic;
  }

  @action.bound
  public setMonitorStrategies(data: IMonitorStrategies[]) {
    this.monitorStrategies = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setMonitorAlerts(data: IMonitorAlerts[]) {
    this.monitorAlerts = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.setLoading(false);
  }

  @action.bound
  public setAlertsDetail(data: IAlertsDetail) {
    this.alertsDetail = data;
    this.monitorAlert = data.monitorAlert;
    this.monitorMetric = data.monitorMetric;
  }

  @action.bound
  public setMonitorSilences(data: IMonitorSilences[]) {
    this.monitorSilences = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }
  @action.bound
  public setMonitorType(data: IMonitorMetricType) {
    this.monitorTypeList = data.metricNames || [];
    // this.monitorType = this.monitorTypeList[0].metricName;
    this.monitorType = '';
  }

  @action.bound
  public setMonitorStrategyDetail(data: IMonitorStrategyDetail) {
    this.monitorStrategyDetail = data || {} as IMonitorStrategyDetail;
  }

  @action.bound
  public setSilencesDetail(data: IMonitorSilences) {
    this.silencesDetail = data;
  }

  @action.bound
  public setMonitorGroups(data: IMonitorGroups[]) {
    this.monitorGroups = data;
  }

  public getMetircHistoryChartOptions = () => {
    return getMonitorMetricOption(this.monitorMetric.metric, this.monitorMetric.values);
  }

  public getMonitorStrategies() {
    getMonitorStrategies().then(this.setMonitorStrategies);
  }

  public deteleMonitorStrategies(monitorId: number) {
    return deteleMonitorStrategies(monitorId).then(() => this.getMonitorStrategies());
  }

  public getMonitorAlerts(monitorId: number, startTime: number, endTime: number) {
    this.setLoading(true);
    getMonitorAlerts(monitorId, startTime, endTime).then(this.setMonitorAlerts);
  }

  public getAlertsDetail(alertId: number) {
    return getAlertsDetail(alertId).then(this.setAlertsDetail);
  }

  public createSilences(params: IMonitorSilences, monitorId: number) {
    return createSilences(params).then(() => {
      this.getMonitorSilences(monitorId);
    });
  }

  public getMonitorSilences(monitorId: number) {
    getMonitorSilences(monitorId).then(this.setMonitorSilences);
  }

  public modifyMask(params: IMonitorSilences, monitorId: number) {
    return modifyMask(params).then(() => {
      this.getMonitorSilences(monitorId);
    });
  }

  public getSilencesDetail(silenceId: number) {
    return getSilencesDetail(silenceId).then(this.setSilencesDetail);
  }

  public deleteSilences(monitorId: number, silenceId: number) {
    return deleteSilences(monitorId, silenceId).then(() => {
      this.getMonitorSilences(monitorId);
    });
  }

  public getMonitorType() {
    return getMonitorType().then(this.setMonitorType);
  }

  public addMonitorStategy(params: IRequestParams) {
    this.setLoading(true);
    return addMonitorStrategy(params).then(() => {
      message.success('操作成功');
      window.location.href = `${urlPrefix}/alarm`;
    }).finally(() => this.setLoading(false));
  }

  public async getMonitorDetail(id: number) {
    return getMonitorDetail(id).then(this.setMonitorStrategyDetail);
  }

  public modifyMonitorStrategy(params: IRequestParams) {
    return modifyMonitorStrategy(params).then(() => {
      message.success('操作成功');
      window.location.href = `${urlPrefix}/alarm`;
    }).finally(() => this.setLoading(false));
  }

  public getMonitorGroups() {
    return getMonitorNotifyGroups(); // this.setMonitorGroups
  }
}
export const alarm = new Alarm();
