
import { observable, action } from 'mobx';
import { getBrokersMetricsHistory } from 'lib/api';
import { IClusterMetrics } from 'types/base-type';

class AdminMonitor {
  @observable
  public currentClusterId = null as number;

  @observable
  public currentBrokerId = null as number;

  @observable
  public brokersMetricsHistory: IClusterMetrics[];

  public requestId = null as any;

  @action.bound
  public setCurrentClusterId(clusterId: number) {
    this.currentClusterId = clusterId;
  }

  @action.bound
  public setCurrentBrokerId(brokeId: number) {
    this.currentBrokerId = brokeId;
  }

  @action.bound
  public setRequestId(requestId: any) {
    this.requestId = requestId;
  }

  @action.bound
  public setBrokersChartsData(data: IClusterMetrics[]) {
    this.brokersMetricsHistory = data;
    this.setRequestId(null);
    return data;
  }

  public getBrokersMetricsList = async (startTime: string, endTime: string) => {
    if (this.requestId && this.requestId !== 'error') {
      return new Promise((res, rej) => {
        window.setTimeout(() => {
          if (this.requestId === 'error') {
            rej();
          } else {
            res(this.brokersMetricsHistory);
          }
        }, 800); // TODO: 该实现方式待优化
      });
    }

    this.setRequestId('requesting');
    return getBrokersMetricsHistory(this.currentClusterId, this.currentBrokerId, startTime, endTime)
      .then(this.setBrokersChartsData).catch(() => this.setRequestId('error'));
  }

  public getBrokersChartsData = async (startTime: string, endTime: string, reload?: boolean) => {
    if (this.brokersMetricsHistory && !reload) {
      return new Promise(res => res(this.brokersMetricsHistory));
    }

    return this.getBrokersMetricsList(startTime, endTime);
  }
}

export const adminMonitor = new AdminMonitor();
