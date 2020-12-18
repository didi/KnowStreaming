import { observable, action } from 'mobx';
import { IAuthorities, IQuotaQuery, IOrderParams, IQuotaModelItem, ILimitsItem } from 'types/base-type';
import { deferTopic, applyOrder, getMytopics, getExpiredTopics, updateTopic, getBaseInfo, getTopicBroker, getAllTopic, getAuthorities, getQuotaQuery, getTopicBasicInfo, getRealTimeTraffic, getRealConsume, getConnectionInfo, getConsumerGroups, getConsumeDetails, getPartitionsInfo, getBrokerInfo, getAppsIdInfo, getBillInfo, getTopicMetriceInfo, getTopicMetriceTake, getTopicBusiness } from 'lib/api';
import { getClusterMetricOption, getClusterMetricTake } from 'lib/line-charts-config';
import { getBillBarOption } from 'lib/bar-pie-config';
import { message } from 'component/antd';

import { ITopic, IBaseInfoItem, IClusterMetrics, ITakeMetric, IOptionType, ITakeType, IBill } from 'types/base-type';

import moment from 'moment';

export interface ITopicBroker {
  alive: boolean;
  brokerId: number;
  host: string;
  leaderPartitionIdList: number[];
  partitionIdList: number[];
  partitionNum: number;
  clusterId: number;
}

export interface ITopicBaseInfo {
  appId: string;
  appName: string;
  bootstrapServers: string;
  clusterId: number;
  createTime: string;
  description: string;
  modifyTime: number;
  partitionNum: number;
  principals: string;
  regionNames: string;
  replicaNum: number;
  retentionTime: number;
  score: number;
  topicCodeC: string;
  physicalClusterId: number;
  percentile: string;
  regionNameList: any;
}

export interface IRealTimeTraffic {
  byteIn: number[];
  byteOut: number[];
  byteRejected: number[];
  failedFetchRequest: number[];
  failedProduceRequest: number[];
  messageIn: number[];
  totalFetchRequest: number[];
  totalProduceRequest: number[];
  [key: string]: number[];
}

export interface IRealTimeConsume {
  [key: string]: IRealConsumeDetail;
}

export interface IRealConsumeDetail {
  metricsName?: string;
  localTimeMs: number;
  remoteTimeMs: number;
  requestQueueTimeMs: number;
  responseQueueTimeMs: number;
  responseSendTimeMs: number;
  throttleTimeMs: number;
  totalTimeMs: number;
}

export interface IConnectionInfo {
  appId: string;
  clientType: string;
  clientVersion: string;
  clusterId: number;
  hostname: string;
  ip: string;
  topicName: string;
  key?: number;
}

export interface IConsumerGroups {
  appIds: string;
  clusterId: number;
  consumerGroup: string;
  location: string;
  key?: number;
}

export interface IConsumeDetails {
  clientId: string;
  clusterId: number;
  consumeOffset: number;
  consumerGroup: string;
  lag: number;
  location: string;
  partitionId: number;
  partitionOffset: number;
  topicName: string;
  consumerId: number;
  key?: number;
}

export interface IPartitionsInfo {
  endOffset: number;
  isrBrokerIdList: number[];
  leaderBrokerId: number;
  leaderEpoch: number;
  location: string;
  logSize: number;
  msgNum: number;
  partitionId: number;
  replicaBrokerIdList: number[];
  beginningOffset: number;
  underReplicated: boolean;
  preferredBrokerId: number;
  key?: number;
}

export interface IBrokerInfo {
  brokerId: number;
  host: string;
  leaderPartitionIdList: number[];
  partitionIdList: number[];
  partitionNum: number;
  key?: number;
}

export interface IAppsIdInfo {
  appId: string;
  appName: string;
  appPrincipals: string;
  clusterId: number;
  consumerQuota: number;
  fetchThrottled: boolean;
  produceQuota: number;
  produceThrottled: boolean;
  topicName: string;
  key?: number;
  access?: number;
  clusterName?: string;
  isPhysicalClusterId?: boolean;
}

class Topic {

  @observable
  public loading: boolean = false;

  @observable
  public expiredLoading: boolean = false;

  @observable
  public consumeLoading: boolean = false;

  @observable
  public realLoading: boolean = false;

  @observable
  public allTopicData: ITopic[] = [];

  @observable
  public authorities: IAuthorities = null;

  @observable
  public quotaQueryData: IQuotaQuery[] = [];

  @observable
  public mytopicData: ITopic[] = [];

  @observable
  public baseInfoData: IBaseInfoItem[] = [];

  @observable
  public expireData: ITopic[] = [];

  @observable
  public baseInfo: ITopicBaseInfo = null;

  @observable
  public realTraffic: IRealTimeTraffic = null;

  @observable
  public realConsume: IRealTimeConsume = null;

  @observable
  public connectionInfo: IConnectionInfo[] = [];

  @observable
  public consumerGroups: IConsumerGroups[] = [];

  @observable
  public filterGroups: IConsumerGroups[] = [];

  @observable
  public consumeDetails: IConsumeDetails[] = [];

  @observable
  public partitionsInfo: IPartitionsInfo[] = [];

  @observable
  public brokerInfo: IBrokerInfo[] = [];

  @observable
  public appsIdInfo: IAppsIdInfo[] = [];

  @observable
  public appInfo: IAppsIdInfo[] = [];

  @observable
  public billInfo: IBill[] = [];

  @observable
  public topicBrokers: ITopicBroker[] = [];

  @observable
  public clusterMetrics: IClusterMetrics[] = [];

  @observable
  public takeMetrics: ITakeMetric[] = [];

  @observable
  public type: IOptionType = 'byteIn/byteOut';

  @observable
  public takeType: ITakeType = 'requestTime99thPercentile';

  @observable
  public startTime: moment.Moment;

  @observable
  public endTime: moment.Moment;

  @observable
  public appId: string = '';

  @observable
  public connectLoading: boolean = true;

  @observable
  public topicBusiness: ITopic = null;

  @observable
  public showConsumeDetail: boolean;

  @action.bound
  public setConsumeDetail(value: boolean) {
    this.showConsumeDetail = value;
  }

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setExpireLoading(value: boolean) {
    this.expiredLoading = value;
  }

  @action.bound
  public setConsumeLoading(value: boolean) {
    this.consumeLoading = value;
  }

  @action.bound
  public setRealLoading(value: boolean) {
    this.realLoading = value;
  }

  @action.bound
  public setConnectLoading(value: boolean) {
    this.connectLoading = value;
  }

  @action.bound
  public setAllTopicData(data: ITopic[] = []) {
    this.allTopicData = data.map((i, index) => {
      i.key = index;
      return i;
    });
    this.setLoading(false);
  }

  @action.bound
  public setAuthorities(data: IAuthorities) {
    this.authorities = data;
    return data;
  }

  @action.bound
  public setQuotaQuery(data: IQuotaQuery[]) {
    return this.quotaQueryData = data;
  }

  @action.bound
  public setTopicData(data: ITopic[]) {
    this.mytopicData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.setLoading(false);
  }

  @action.bound
  public setBaseInfoData(data: IBaseInfoItem[]) {
    this.baseInfoData = data;
  }

  @action.bound
  public setExpireData(data: ITopic[]) {
    this.expireData = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    this.setExpireLoading(false);
  }

  @action.bound
  public setTopicBaseInfo(data: ITopicBaseInfo) {
    this.baseInfo = data;
  }

  @action.bound
  public setRealTimeTraffic(data: IRealTimeTraffic) {
    this.realTraffic = data;
    this.setRealLoading(false);
  }

  @action.bound
  public setRealConsume(data: IRealTimeConsume) {
    this.realConsume = data;
    this.setConsumeLoading(false);
  }

  @action.bound
  public setConnectionInfo(data: IConnectionInfo[]) {
    this.setConnectLoading(false);
    this.connectionInfo = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    return this.connectionInfo;
  }

  @action.bound
  public setConsumerGroups(data: IConsumerGroups[]) {
    const consumer = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
    const res = new Map();
    this.consumerGroups = consumer.filter((ele: any) => !res.has(ele.consumerGroup) && res.set(ele.consumerGroup, 1));
    this.filterGroups = consumer.filter((ele: any) => !res.has(ele.location) && res.set(ele.location, 1));
  }

  @action.bound
  public setConsumeDetails(data: IConsumeDetails[]) {
    this.consumeDetails = data ? data.map((item, index) => {
      item.key = index;
      return item;
    }) : [];
  }

  @action.bound
  public setPartitionsInfo(data: IPartitionsInfo[]) {
    this.partitionsInfo = data.map((d, index) => {
      d.key = index;
      return d;
    });
  }

  @action.bound
  public setBrokerInfo(data: IBrokerInfo[]) {
    this.brokerInfo = data.map((d, index) => {
      d.key = index;
      return d;
    });
  }

  @action.bound
  public setAppsIdInfo(data: IAppsIdInfo[]) {
    this.appsIdInfo = data.map((d, index) => {
      d.key = index;
      return d;
    });
    this.appInfo = [{
      appId: '',
      appName: '请选择',
    }].concat(this.appsIdInfo) as IAppsIdInfo[];
    this.setLoading(false);
  }

  @action.bound
  public setBillInfo(data: IBill[]) {
    this.billInfo = data ? data.map((item, index) => ({
      ...item,
      cost: +item.cost.toFixed(2),
      key: index,
    })) : [];

    return getBillBarOption(this.billInfo);
  }

  @action.bound
  public setChartsOpton(data: IClusterMetrics[]) {
    this.clusterMetrics = data;
    return this.changeType(this.type);
  }

  @action.bound
  public changeType(type: IOptionType) {
    this.type = this.appId && type === 'byteIn/byteOut' ? 'byteIn/byteOut/appByteIn/appByteOut' : type;
    return getClusterMetricOption(this.type, this.clusterMetrics);
  }

  @action.bound
  public setChartsTake(data: ITakeMetric[]) {
    this.takeMetrics = data;
    return this.changeTakeType(this.takeType);
  }

  @action.bound
  public changeTakeType(takeType: ITakeType) {
    this.takeType = takeType;
    return getClusterMetricTake(takeType, this.takeMetrics);
  }

  @action.bound
  public changeStartTime(value: moment.Moment) {
    this.startTime = value;
  }

  @action.bound
  public changeEndTime(value: moment.Moment) {
    this.endTime = value;
  }

  @action.bound
  public initTime() {
    this.startTime = moment().subtract(1, 'hour');
    this.endTime = moment();
  }

  @action.bound
  public setTopicBrokers(tb: ITopicBroker[]) {
    this.topicBrokers = tb;
  }

  @action.bound
  public setTopicBusiness(data: ITopic) {
    this.topicBusiness = data;
  }

  public getTopic() {
    this.setLoading(true);
    getMytopics().then(this.setTopicData);
  }

  public getExpired() {
    this.setExpireLoading(true);
    getExpiredTopics().then(this.setExpireData);
  }

  public getbaseNews() {
    getBaseInfo().then(this.setBaseInfoData);
  }

  public getTopicBroker(clusterId: number, topicName: string) {
    return getTopicBroker(clusterId, topicName).then(this.setTopicBrokers);
  }

  public getAllTopic() {
    this.setLoading(true);
    getAllTopic().then(this.setAllTopicData);
  }

  public getAuthorities(appId: string, clusterId: number, topicName: string) {
    return getAuthorities(appId, clusterId, topicName).then(this.setAuthorities);
  }

  public getQuotaQuery(appId: string, clusterId: number, topicName: string) {
    return getQuotaQuery(appId, clusterId, topicName).then(this.setQuotaQuery);
  }

  public getTopicBasicInfo(clusterId: number, topicName: string) {
    return getTopicBasicInfo(clusterId, topicName).then(this.setTopicBaseInfo);
  }

  public getRealTimeTraffic(clusterId: number, topicName: string) {
    this.setRealLoading(true);
    return getRealTimeTraffic(clusterId, topicName).then(this.setRealTimeTraffic);
  }

  public getRealConsume(clusterId: number, topicName: string, percentile: string) {
    this.setConsumeLoading(true);
    return getRealConsume(clusterId, topicName, percentile).then(this.setRealConsume);
  }

  public getConnectionInfo(clusterId: number, topicName: string, appId?: string) {
    this.setConnectLoading(true);
    return getConnectionInfo(clusterId, topicName, appId)
      .then(this.setConnectionInfo)
      .catch(() => {
        this.setConnectLoading(false);
        message.error('加载请求失败！');
      });
  }

  public getConsumerGroups(clusterId: number, topicName: string) {
    return getConsumerGroups(clusterId, topicName).then(this.setConsumerGroups);
  }

  public getConsumeDetails(clusterId: number, topicName: string, consumerGroup: string, location: string) {
    return getConsumeDetails(clusterId, topicName, consumerGroup, location).then(this.setConsumeDetails);
  }

  public getPartitionsInfo(clusterId: number, topicName: string) {
    return getPartitionsInfo(clusterId, topicName).then(this.setPartitionsInfo);
  }

  public getBrokerInfo(clusterId: number, topicName: string) {
    return getBrokerInfo(clusterId, topicName).then(this.setBrokerInfo);
  }

  public getAppsIdInfo(clusterId: number, topicName: string) {
    this.setLoading(true);
    return getAppsIdInfo(clusterId, topicName).then(this.setAppsIdInfo);
  }

  public getBillInfo(clusterId: number, topicName: string, startTime: number, endTime: number) {
    this.setLoading(false);
    return getBillInfo(clusterId, topicName, startTime, endTime).then(this.setBillInfo);
  }

  public getMetriceInfo(clusterId: number, topicName: string) {
    return getTopicMetriceInfo({
      clusterId, topicName,
      startTime: this.startTime.format('x'),
      endTime: this.endTime.format('x'),
      appId: this.appId,
    }).then(this.setChartsOpton);
  }

  public getMetriceTake(clusterId: number, topicName: string) {
    return getTopicMetriceTake({
      clusterId, topicName,
      startTime: this.startTime.format('x'),
      endTime: this.endTime.format('x'),
    }).then(this.setChartsTake);
  }

  public applyQuota(params: IOrderParams) {
    return applyOrder(params);
  }

  public applyTopic(params: IOrderParams) {
    return applyOrder(params);
  }

  public updateTopic(params: IQuotaModelItem) {
    return updateTopic(params);
  }

  public deferTopic(params: ILimitsItem) {
    return deferTopic(params);
  }

  public applyTopicOnline(params: IOrderParams) {
    return applyOrder(params);
  }

  public getTopicBusiness(clusterId: number, topicName: string) {
    return getTopicBusiness(clusterId, topicName).then(this.setTopicBusiness);
  }
}

export const topic = new Topic();
