import { observable, action } from 'mobx';
import { alarm, IAlarm } from './alarm';
import { IRegionData } from './region';
import { operation, ITask } from './operation';
import { IClusterData, IBaseOrder, ITopic } from 'types/base-type';
import { getAdminPartitionOrder, getAdminTopicDetail } from 'lib/api';
import { IUserDetail } from './users';
import { topic, IConsumeInfo } from './topic';

class Modal {
  @observable
  public id: string = null;

  @observable
  public orderDetail: IBaseOrder = {} as IBaseOrder;

  @observable
  public topicData: ITopic = null;

  public topicDetail: IBaseOrder = null;
  public regionData: IRegionData = null;
  public currentCluster: IClusterData = {} as IClusterData;
  public userDetail: IUserDetail = null;
  public consumberGroup: IConsumeInfo = null;

  @action.bound
  public showNewTopic(r: ITopic) {
    this.topicData = r;
    this.id = 'showNewTopic';
  }

  @action.bound
  public showNewCluster() {
    this.id = 'showNewCluster';
  }

  @action.bound
  public showModifyCluster(cluster: IClusterData) {
    this.id = 'showModifyCluster';
    this.currentCluster = cluster;
  }

  @action.bound
  public setTopic(data: ITopic) {
    this.topicData = data;
  }

  @action.bound
  public showAdimTopic(r: ITopic) {
    this.id = 'showAdimTopic';
    this.topicData = r;
    if (r) {
      getAdminTopicDetail(r.clusterId, r.topicName).then(this.setTopic);
      topic.getTopicMetaData(r.clusterId, r.topicName);
    }
  }

  @action.bound
  public showAlarm(r: IAlarm) {
    alarm.setCurData(r);
    this.id = 'showAlarm';
  }

  @action.bound
  public showRegion(r: IRegionData) {
    this.id = 'showRegion';
    this.regionData = r;
  }

  @action.bound
  public showExpandTopic(data: IBaseOrder) {
    this.topicDetail = data;
    this.id = 'showExpandTopic';
  }

  @action.bound
  public showExpandAdmin(data: IBaseOrder) {
    this.topicDetail = data;
    this.id = 'showExpandAdmin';
  }

  @action.bound
  public showResetOffset() {
    this.id = 'showResetOffset';
  }

  @action.bound
  public showLeaderRebalance() {
    this.id = 'showLeaderRebalance';
  }

  @action.bound
  public showTask(value: ITask, type?: string) {
    this.id = type === 'detail' ? 'showTaskDetail' : 'showTask';
    value ? operation.getTaskDetail(value.taskId) : operation.setTaskDetail(value);
  }

  @action.bound
  public showOrderApprove(value: any, type: string) {
    this.id = type === 'showOrderApprove' ? 'showOrderApprove' : 'showOrderDetail';
    this.orderDetail = value;
  }

  @action.bound
  public setDetail(data: any) {
    if (data[0]) this.orderDetail = data[0];
  }

  @action.bound
  public showPartition(value: any, type: string) {
    this.orderDetail = value;
    this.id = type === 'showPartition' ? 'showPartition' : 'showPartitionDetail';
    getAdminPartitionOrder(value.orderId).then(this.setDetail);
  }

  @action.bound
  public showNewUser(data: IUserDetail) {
    this.userDetail = data;
    this.id = 'showNewUser';
  }

  @action.bound
  public shoeTopicConfig(data: IBaseOrder) {
    this.topicDetail = data;
    this.id = 'shoeTopicConfig';
  }

  @action.bound
  public showAlarmModify(r: IAlarm) {
    alarm.setCurData(r);
    this.id = 'showAlarmModify';
  }

  @action.bound
  public showConsumerTopic(r: IConsumeInfo) {
    this.consumberGroup = r;
    this.id = 'showConsumerTopic';
  }

  @action.bound
  public close() {
    this.id = null;
    this.currentCluster = {} as IClusterData;
  }
}

export const modal = new Modal();
