import { observable, action } from 'mobx';
import { getTopicOrder, getPartitionOrder, getAdminTopicOrder, getAdminPartitionOrder } from 'lib/api';
import { IBaseOrder } from 'types/base-type';

const statusMap = ['待审批', '通过', '拒绝', '撤销'];
export const tableStatusFilter = statusMap.map(i => ({text: i, value: i}));

export interface IPartitionOrder extends IBaseOrder {
  peakAvgBytesInPerSec: number;
}

class Order {
  @observable
  public topicOrder: IBaseOrder[] = [];

  @observable
  public partitionOrder: IPartitionOrder[] = [];

  @observable
  public adminTopicOrder: IBaseOrder[] = [];

  @observable
  public adminPartitionOrder: IPartitionOrder[] = [];

  @observable
  public pendingTopic: number = 0;

  @observable
  public pendingOrder: number = 0;

  @action
  public mapData(data: any, type?: 'pendingTopic' | 'pendingOrder') {
    this[type] = 0;
    return data.map((d: any) => {
      if (!d.orderStatus) this[type] += 1;
      d.statusStr = statusMap[d.orderStatus];
      d.key = d.orderId;
      return d;
    });
  }

  @action.bound
  public setTopicOrder(data: IBaseOrder[]) {
    this.topicOrder = this.mapData(data);
  }

  @action.bound
  public setPartitionOrder(data: IPartitionOrder[]) {
    this.partitionOrder = this.mapData(data);
  }

   @action.bound
  public setAdiminTopicOrder(data: IBaseOrder[]) {
    this.adminTopicOrder = this.mapData(data, 'pendingTopic');
  }

  @action.bound
  public setAdminPartitionOrder(data: IPartitionOrder[]) {
    this.adminPartitionOrder = this.mapData(data, 'pendingOrder');
  }

  public getOrder() {
    getTopicOrder().then(this.setTopicOrder);
    getPartitionOrder().then(this.setPartitionOrder);
  }

  public getAdminOrder() {
    getAdminTopicOrder().then(this.setAdiminTopicOrder);
    getAdminPartitionOrder().then(this.setAdminPartitionOrder);
  }
}

export const order = new Order();
