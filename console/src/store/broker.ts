import { observable, action } from 'mobx';
import { getBrokerBaseInfo, getBrokerList, getBrokerNetwork, getBrokerPartition, getOneBrokerNetwork, getBrokerTopic, getPartitions, getBrokerKeyMetrics, getBrokerTopicAnalyzer, getBrokerNameList } from 'lib/api';
import { IFlowInfo } from 'component/flow-table';
import { ITopic, IValueLabel } from 'types/base-type';
import moment from 'moment';

export interface IBrokerBaseInfo {
  host: string;
  jmxPort: number;
  leaderCount: number;
  partitionCount: number;
  port: number;
  startTime: number | string;
  topicNum: number;
}

export interface IBroker {
  brokerId: number;
  byteIn: number;
  byteOut: number;
  host: string;
  jmxPort: number;
  port: number;
  startTime: number;
  status: string;
  regionName: string;
}

export interface IBrokerNetworkInfo extends IFlowInfo {
  produceRequest: number[];
  fetchConsumerRequest: number[];
}

export interface IBrokerPartition extends IBroker {
  leaderCount: number;
  partitionCount: number;
  notUnderReplicatedPartitionCount: number;
  regionName: string;
  bytesInPerSec: number;
}

export interface IPartitions {
  followerPartitionIdList: number[];
  leaderPartitionList: number[];
  topicName: string;
  underReplicated: boolean;
  underReplicatedPartitionsIdList: number[];
}

export interface ITopicStatus {
  BytesOutPerSec_rate: number;
  BytesInPerSec_rate: number;
}

export interface IBrokerMetrics {
  bytesIn?: number;
  bytesOut?: number;
  messagesIn?: number;
  totalFetchRequests?: number;
  totalProduceRequests?: number;
}

export interface IAnalyzerData extends IBrokerMetrics {
  topicAnalysisVOList: [];
  baseTime?: number;
  brokerId?: number;
}

export type IOverviewKey = 'partitionCount' | 'leaderCount' | 'notUnderReplicatedPartitionCount';

interface IBrokerOption {
  host: string;
  brokerId: string;
}

class Broker {
  @observable
  public brokerBaseInfo: IBrokerBaseInfo = {} as IBrokerBaseInfo;

  @observable
  public list: IBroker[] = [];

  @observable
  public network: IBrokerNetworkInfo = null;

  @observable
  public oneNetwork: IBrokerNetworkInfo = null;

  @observable
  public partitions: IBrokerPartition[] = [];

  @observable
  public topics: ITopic[] = [];

  @observable
  public topicPartitionsInfo: [] = [];

  @observable
  public openKeys: string[] = [];

  @observable
  public realPartitions: IBrokerPartition[] = [];

  @observable
  public analyzerData: IAnalyzerData = {topicAnalysisVOList: []};

  @observable
  public viewType: IOverviewKey = 'partitionCount';

  @observable
  public regionOption: any = ['all'];

  @observable
  public endTime = moment();

  @observable
  public startTime = moment().subtract(1, 'hour');

  @observable
  public BrokerOptions: IValueLabel[] = [{ value: null, label: '请选择Broker' }];

  @action.bound
  public setBrokerBaseInfo(data: IBrokerBaseInfo) {
    data.startTime = moment(data.startTime).format('YYYY-MM-DD HH:mm:ss'),
    this.brokerBaseInfo = data;
  }

  @action.bound
  public setBrokerList(list: IBroker[]) {
    this.list = list;
  }

  @action.bound
  public setBrokerNetWork(network: IBrokerNetworkInfo) {
    if (!network) return false;
    this.network = network;
  }

  @action.bound
  public setBrokerPartition(pars: IBrokerPartition[]) {
    const res = new Map();
    this.partitions = pars.map(i => {
      i.status = i.notUnderReplicatedPartitionCount ? '是' : '否';
      return i;
    });
    this.realPartitions = pars;
    this.regionOption = pars.filter((a) => !res.has(a.regionName) && res.set(a.regionName, 1));
  }

  @action.bound
  public setOneBrokerNetwork(network: IBrokerNetworkInfo) {
    this.oneNetwork = network;
  }

  @action.bound
  public setBrokerTopic(topics: ITopic[]) {
    this.topics = topics;
  }

  @action.bound
  public setPartitionsInfo(pI: []) {
    this.topicPartitionsInfo = pI;
  }

  @action.bound
  public handleOpen(key: string) {
    if (this.openKeys.includes(key)) {
      this.openKeys = this.openKeys.filter(k => k !== key);
    } else {
      this.openKeys.push(key);
      this.openKeys = this.openKeys.slice(0);
    }
  }

  @action.bound
  public handleOverview(type: IOverviewKey) {
    this.viewType = type;
  }

  @action.bound
  public filterSquare(type: string) {
    this.realPartitions = this.partitions;
    if (type !== 'all') {
      this.realPartitions = this.partitions.filter(i => i.regionName === type);
    }
  }

  @action.bound
  public setBrokerTopicAnalyzer(data: IAnalyzerData) {
    for (const item of Object.keys(data)) {
      if (item === 'bytesIn' || item === 'bytesOut') data[item] = +(data[item] / (1024 * 1024)).toFixed(2);
    }
    this.analyzerData = data;
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
  public setBrokerOptions(data: IBrokerOption[]) {
    this.BrokerOptions = data.map(i => ({
      label: `BrokerID: ${i.brokerId}, Host: ${i.host}`,
      value: i.brokerId,
    }));
  }

  public getBrokerBaseInfo(clusterId: number, brokerId: number) {
    getBrokerBaseInfo(clusterId, brokerId).then(this.setBrokerBaseInfo);
  }

  public getBrokerList(clusterId: number) {
    getBrokerList(clusterId).then(this.setBrokerList);
  }

  public getBrokerNetwork(clusterId: number) {
    getBrokerNetwork(clusterId).then(this.setBrokerNetWork);
  }

  public getBrokerPartition(clusterId: number) {
    getBrokerPartition(clusterId).then(this.setBrokerPartition);
  }

  public getOneBrokerNetwork(clusterId: number, brokerId: number) {
    getOneBrokerNetwork(clusterId, brokerId).then(this.setOneBrokerNetwork);
  }

  public getBrokerTopic(clusterId: number, brokerId: number) {
    getBrokerTopic(clusterId, brokerId).then(this.setBrokerTopic);
  }

  public getPartitions(clusterId: number, brokerId: number) {
    getPartitions(clusterId, brokerId).then(this.setPartitionsInfo);
  }

  public getBrokerKeyMetrics(clusterId: number, brokerId: number, startTime: string, endTime: string) {
    return getBrokerKeyMetrics(clusterId, brokerId, startTime, endTime);
  }

  public getBrokerTopicAnalyzer(clusterId: number, brokerId: number) {
    getBrokerTopicAnalyzer(clusterId, brokerId).then(this.setBrokerTopicAnalyzer);
  }

  public initBrokerOptions = (clusterId: number) => {
    getBrokerNameList(clusterId).then(this.setBrokerOptions);
  }
}

export const broker = new Broker();
