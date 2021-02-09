
import { observable, action } from 'mobx';
import { getBrokersMetricsHistory } from 'lib/api';
import { IClusterMetrics } from 'types/base-type';

const STATUS = {
  PENDING: 'pending',
  REJECT: 'reject',
  FULLFILLED: 'fullfilled'
}
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
    this.setRequestId(STATUS.FULLFILLED);
    Promise.all(this.taskQueue).then(() => {
      this.setRequestId(null);
      this.taskQueue = [];
    })
    return data;
  }
  public taskQueue = [] as any[];

  public getBrokersMetricsList = async (startTime: string, endTime: string) => {
    if (this.requestId) {
      //逐条定时查询任务状态
      const p = new Promise((res, rej) => {
        const timer = window.setInterval(() => {
          if (this.requestId === STATUS.REJECT) {
            rej(this.brokersMetricsHistory);
            window.clearInterval(timer);
          } else if (this.requestId === STATUS.FULLFILLED) {
            res(this.brokersMetricsHistory);
            window.clearInterval(timer);
          }
        }, (this.taskQueue.length + 1) * 100);
      });
      this.taskQueue.push(p);
      return p;
    }

    this.setRequestId(STATUS.PENDING);
    return getBrokersMetricsHistory(this.currentClusterId, this.currentBrokerId, startTime, endTime)
      .then(this.setBrokersChartsData).catch(() => this.setRequestId(STATUS.REJECT));
  }

  public getBrokersChartsData = async (startTime: string, endTime: string, reload?: boolean) => {
    if (this.brokersMetricsHistory && !reload) {
      return new Promise(res => res(this.brokersMetricsHistory));
    }
    return this.getBrokersMetricsList(startTime, endTime);
  }
}

export const adminMonitor = new AdminMonitor();
