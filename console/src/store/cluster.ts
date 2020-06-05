import { observable, action } from 'mobx';
import { getClusters, getKafkaVersion, getClusterMetricsHistory, getRebalanceStatus, getClustersBasic, getTopicMetriceInfo, getBrokerMetrics } from 'lib/api';
import { getClusterMetricOption } from 'lib/charts-config';
import { IClusterData, IClusterMetrics, IOptionType } from 'types/base-type';

import moment from 'moment';

class Cluster {
  @observable
  public data: IClusterData[] = [];

  @observable
  public active: number = null;

  @observable
  public kafkaVersions: string[] = [];

  @observable
  public startTime: moment.Moment;

  @observable
  public endTime: moment.Moment;

  @observable
  public clusterMetrics: IClusterMetrics[] = [];

  @observable
  public leaderStatus: string = '';

  @observable
  public type: IOptionType = 'byteIn/byteOut' ;

  @action.bound
  public setData(data: IClusterData[]) {
    data.unshift({
      clusterId: -1,
      clusterName: '所有集群',
    } as IClusterData);
    this.data = data;
    this.active = (this.data[0] || { clusterId: null }).clusterId;
  }

  @action.bound
  public changeCluster(data: number) {
    this.active = data;
  }

  @action.bound
  public setKafkaVersion(data: string[]) {
    this.kafkaVersions = data;
  }

  @action.bound
  public setChartsOpton(data: IClusterMetrics[]) {
    this.clusterMetrics = data;
    return this.changeType(this.type);
  }

  @action.bound
  public changeType(type: IOptionType) {
    cluster.type = type;
    return getClusterMetricOption(type, this.clusterMetrics);
  }

  @action.bound
  public changeStartTime(value: moment.Moment) {
    this.startTime = value;
  }

  @action.bound
  public changeEndTime(value: moment.Moment ) {
    this.endTime = value;
  }

  @action.bound
  public initTime() {
    this.startTime = moment().subtract(1, 'hour');
    this.endTime =  moment();
  }

  @action.bound
  public  setLeaderStatus(type: string) {
    this.leaderStatus = type;
  }

  public getClusters() {
    getClusters().then(this.setData);
  }

  public getClustersBasic() {
    getClustersBasic().then(this.setData);
  }

  public getKafkaVersions() {
    getKafkaVersion().then(this.setKafkaVersion);
  }

  public getClusterMetricsHistory(clusterId: number) {
    return getClusterMetricsHistory(clusterId,
      this.startTime.format('x'),
      this.endTime.format('x')).then(this.setChartsOpton);
  }

  public getMetriceInfo(clusterId: number, topicName: string) {
    return getTopicMetriceInfo(clusterId, topicName,
      this.startTime.format('x'),
      this.endTime.format('x')).then(this.setChartsOpton);
  }

  public getRebalance(clusterId: number) {
    return getRebalanceStatus(clusterId).then((type) => {
      this.setLeaderStatus(type);
      if (type === 'RUNNING') {
        window.setTimeout(() => {
          this.getRebalance(clusterId);
        }, 1000 * 2);
      }
    });
  }

  public getBrokerMetrics(clusterId: number, brokerId: number) {
    return getBrokerMetrics(clusterId, brokerId,
      this.startTime.format('x'),
      this.endTime.format('x')).then(this.setChartsOpton);
  }
}

export const cluster = new Cluster();
