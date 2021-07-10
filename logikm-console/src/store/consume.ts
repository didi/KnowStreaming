import { observable, action } from 'mobx';
import { getConsumeInfo, resetOffset, getConsumeGroup } from 'lib/api';
import { IOffset } from 'types/base-type';

export interface IConsumeInfo {
  location: string;
  consumerGroup: string;
  clusterId?: number;
  key?: string;
}
export interface IConsumeDetail {
  clientId: string;
  clusterId: number;
  consumeOffset: number;
  consumerGroup: string;
  lag: number;
  location: string;
  partitionId: number;
  partitionOffset: number;
  topicName: string;
}

export interface IOffsetlist {
  offset: number;
  partitionId: number;
}

class Consume {
  @observable
  public data: IConsumeInfo[] = [];

  @observable
  public consumeGroupDetail: IConsumeDetail[] = [];

  @observable
  public offsetList: IOffsetlist[] = [{ offset: null, partitionId: null }];

  @observable
  public consumerTopic: any[] = [];

  @action.bound
  public setData(data: IConsumeInfo[]) {
    this.data = data.map(i => {
      i.location = i.location.toLowerCase();
      return i;
    });
  }

  @action.bound
  public selectChange(index: number, value: number) {
    this.offsetList[index].partitionId = value;
  }

  @action.bound
  public inputChange(index: number, event: any) {
    this.offsetList[index].offset = event.target.value;
  }

  @action.bound
  public handleList(index?: number) {
    index ? this.offsetList.splice(index, 1) : this.offsetList.push({ offset: null, partitionId: null });
    this.offsetList = this.offsetList.slice(0);
  }

  @action.bound
  public offsetPartition(params: IOffset, type?: number) {
    if (type) return resetOffset(Object.assign(params, { offsetList: this.offsetList }));
    return resetOffset(params);
  }

  @action.bound
  public setConsumerTopic(topic: string[]) {
    this.consumerTopic = topic.map(i => ({ topicName: i }));
  }

  public getConsumeInfo(clusterId: number) {
    getConsumeInfo(clusterId).then(this.setData);
  }

  public getConsumerTopic( clusterId: number, consumerGroup: string, location: string ) {
    getConsumeGroup(clusterId, consumerGroup, location).then(this.setConsumerTopic);
  }
}

export const consume = new Consume();
