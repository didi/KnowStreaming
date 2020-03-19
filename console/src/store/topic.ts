import { observable, action } from 'mobx';
import { getTopic, getTopicBasicInfo, getTopicConsumeInfo, getTopicStatusInfo, getGroupInfo, getConsumeGroup, addSample, getTopicBroker, getTopicPartition, getTopicNameById, getTopicMetaData } from 'lib/api';
import { IFlowInfo } from 'component/flow-table';
import urlQuery from './url-query';
import { ITopic, IOffset, ISample } from 'types/base-type';

export interface ITopicBaseInfo {
  brokerNum: number;
  createTime: string;
  description: string;
  modifyTime: string;
  partitionNum: number;
  principals: string;
  regionNames: string;
  replicaNum: number;
  retentionTime: number;
  topicName: string;
}

export interface IConsumeInfo {
  location: string;
  consumerGroup: string;
  clusterId?: number;
  key?: string;
}

export interface ITopicStatusInfo extends IFlowInfo {
  totalProduceRequest: number[];
}

export interface IGroupInfo {
  clientId: string;
  clusterId: number;
  consumeOffset: number;
  consumerGroup: string;
  lag: number;
  location: string;
  partitionId: number;
  partitionOffset: number;
  topicName: string;
  key?: string;
}

export interface ITopicBroker {
  brokerId: number;
  host: string;
  leaderPartitionIdList: number[];
  partitionIdList: number[];
  partitionNum: number;
}

export interface ITopicPartition {
  isrBrokerIdList: number[];
  leaderBrokerId: number;
  leaderEpoch: number;
  offset: number;
  partitionId: number;
  preferredBrokerId: number;
  replicaBrokerIdList: number[];
  underReplicated: boolean;
}

export interface IAdminExpand {
  brokerIdList: number[];
  clusterId: number;
  partitionNum: number;
  replicaNum: number;
  topicName: string;
}

class Topic {
  @observable
  public data: ITopic[] = [];

  @observable
  public favData: ITopic[] = [];

  @observable
  public comsumeTopics: string[] = [];

  @observable
  public baseInfo: ITopicBaseInfo = null;

  @observable
  public consumeInfo: IConsumeInfo[] = [];

  @observable
  public groupInfo: IGroupInfo[] = [];

  @observable
  public statusInfo: ITopicStatusInfo = null;

  @observable
  public sampleData: ISample[] = null;

  @observable
  public topicBrokers: ITopicBroker[] = [];

  @observable
  public topicPartitions: ITopicPartition[] = [];

  @observable
  public topicNameList: string[] = [];

  @observable
  public topicDetail: IAdminExpand = null;

  public currentTopicName = '';
  public currentClusterId: number = null;
  public currentGroup: IOffset = null;

  @action.bound
  public setData(data: ITopic[]) {
    this.data = data.map((i, index) => {
      i.key = index;
      return i;
    });
  }

  @action.bound
  public setFavData(data: ITopic[]) {
    this.favData = data.map((i, index) => {
      i.key = index;
      return i;
    });
  }

  @action.bound
  public setTopicBaseInfo(data: ITopicBaseInfo) {
    this.baseInfo = data;
  }

  @action.bound
  public setTopicConsumeInfo(data: IConsumeInfo[]) {
    this.consumeInfo = data.map(d => {
      d.key = d.consumerGroup;
      return d;
    });
  }

  @action.bound
  public setTopicStatusInfo(data: ITopicStatusInfo) {
    this.statusInfo = data;
  }

  @action.bound
  public setGroupInfo(data: IGroupInfo[]) {
    this.groupInfo = data.map(d => { d.key = d.clientId; return d; });
  }

  @action.bound
  public setComsumeTopics(topics: string[]) {
    this.comsumeTopics = topics;
  }

  @action.bound
  public setSampleData(data: ISample[]) {
    this.sampleData = data;
  }

  @action.bound
  public setTopicBrokers(tb: ITopicBroker[]) {
    this.topicBrokers = tb;
  }

  @action.bound
  public setTopicPartitions(tp: ITopicPartition[]) {
    this.topicPartitions = tp;
  }

  @action.bound
  public setTopicNameList(data: string[]) {
    this.topicNameList = data;
  }

  @action.bound
  public setTopicDetail(data: IAdminExpand) {
    this.topicDetail = data;
  }

  public setCurrent({ topicName, clusterId }: ITopic) {
    this.currentTopicName = topicName;
    this.currentClusterId = clusterId;
  }

  public getTopics() {
    getTopic(null, false).then(this.setData);
    getTopic(null, true).then(this.setFavData);
  }

  public getAdminTopics(clusterId: number) {
    getTopic(clusterId).then(this.setData);
  }

  public getTopicBasicInfo(topic: string, clusterId: number) {
    getTopicBasicInfo(topic, clusterId).then(this.setTopicBaseInfo);
  }

  public getTopicConsumeInfo(clusterId: number, topicName: string) {
    getTopicConsumeInfo(clusterId, topicName).then(this.setTopicConsumeInfo);
  }

  public getTopicStatusInfo(topic: string, clusterId: number) {
    this.currentTopicName = topic;
    this.currentClusterId = clusterId;
    getTopicStatusInfo(topic, clusterId).then(this.setTopicStatusInfo);
  }

  public getGroupInfo(topicName: string, clusterId: number, consumerGroup: string, location: string) {
    this.currentGroup = { topicName, clusterId, consumerGroup, location: location.toLowerCase() };
    getGroupInfo(topicName, clusterId, consumerGroup, location).then(this.setGroupInfo);
  }

  public getConsumeGroup(group: string, location: string) {
    return getConsumeGroup(urlQuery.clusterId, group, location).then(this.setComsumeTopics);
  }

  public addSample(params: ISample) {
    return addSample(params).then(this.setSampleData);
  }

  public getTopicBroker(clusterId: number, topicName: string) {
    return getTopicBroker(clusterId, topicName).then(this.setTopicBrokers);
  }

  public getTopicPartition(clusterId: number, topicName: string) {
    return getTopicPartition(clusterId, topicName).then(this.setTopicPartitions);
  }

  public getTopicList = (value: number) => {
    getTopicNameById(value).then(this.setTopicNameList);
  }

  public getTopicMetaData = (clusterId: number, topicName: string) => {
    getTopicMetaData(clusterId, topicName).then(this.setTopicDetail);
  }
}

export const topic = new Topic();
