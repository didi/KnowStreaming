export interface INewCluster {
  alarmFlag: number;
  bootstrapServers: string;
  clusterId: number;
  clusterName: string;
  kafkaVersion: string;
  saslJaasConfig: string;
  saslMechanism: string;
  securityProtocol: string;
  zookeeper: string;
}

export interface IClusterData {
  bootstrapServers: string;
  brokerNum: number;
  clusterId: number;
  clusterName: string;
  controllerId: number;
  consumerGroupNum: number;
  gmtCreate: string;
  gmtModify: string;
  kafkaVersion: string;
  regionNum: number;
  status: number;
  topicNum: number;
  zookeeper: string;
  alarmFlag: number;
  saslJaasConfig: string;
  saslMechanism: string;
  securityProtocol: string;
}

export interface ISeriesOption {
  name: string;
  type: string;
  data: number[];
}

export interface IClusterMetrics {
  gmtCreate: number;
  bytesInPerSec: number;
  bytesOutPerSec: number;
  bytesRejectedPerSec: number;
  messagesInPerSec: number;
}

export interface IBrokerMetrics {
  requestHandlerIdlPercent: number;
  networkProcessorIdlPercent: number;
  requestQueueSize: number;
  responseQueueSize: number;
  logFlushTime: number;
  failFetchRequest: number;
  failProduceRequest: number;
  totalTimeProduceMean: number;
  totalTimeProduce99Th: number;
  totalTimeFetchConsumerMean: number;
  totalTimeFetchConsumer99Th: number;
  gmtCreate: number;
}

export interface IValueLabel {
  value: string;
  label: string;
}

export type IOptionType =  'byteIn/byteOut' | 'byteRejected' | 'messageIn' | 'messageIn/totalProduceRequests';

export interface ITaskDetail {
  clusterId: number;
  operator: number;
  throttle: number;
  topicName: string;
  brokerIdList: number[];
}

interface IBase {
  clusterId: number;
  clusterName: string;
  topicName: string;
  principals: string;
  description: string;
  brokerIdList: string[];
  partitionNum: number;
}

export interface IBaseOrder extends IBase {
  peakBytesInPerSec: number;
  orderId: number;
  gmtModify: number;
  gmtCreate: number;
  orderStatus: number;
  approver: string;
  approvalOpinions: string;
  applicant: string;
  predictBytesIn: number;
  realBytesIn: number;
  regionBrokerIdList: any[];
  regionNameList: string[];
  statusStr?: string;
  replicaNum?: number;
  retentionTime?: number;
  peakBytesIn?: number;
  regions?: string;
  brokers?: string;
}

export interface ITopic extends IBase {
  brokerNum: number;
  byteIn: number;
  byteOut: number;
  favorite: boolean;
  messageIn: number;
  produceRequest: number;
  replicaNum: number;
  updateTime: number;
  principalList: string[];
  properties: string;
  regionIdList: number[];
  retentionTime: number;
  peakBytesIn: number;
  key?: number;
}

export interface IFiler {
  text: string;
  value: string;
}

export interface IDilation {
  brokerIdList: number[];
  clusterId: number;
  partitionNum: number;
  regionIdList: number[];
  topicName: string;
}

export interface IAlarmBase {
  alarmName: string;
  id?: number;
  strategyExpressionList: IExpressionList[];
  strategyFilterList: IStrategyFilter[];
  strategyActionList: IActionList[];
  principalList: string[];
  status: number;
}

export interface IExpressionList {
  metric: string;
  opt: string;
  threshold: number;
  duration: number;
}
export interface IStrategyFilter {
  key: string | string[];
  value: string;
}

export interface IActionList {
  actionWay: string;
  actionTag: string;
}
export interface ITaskBase {
  taskId: number;
  clusterId?: number;
  topicName?: string;
  status?: number;
  throttle?: number;
  partitionIdList?: string[];
  brokerIdList?: number[];
  description?: string;
  action: string;
}

export interface IRebalance {
  brokerId: number;
  clusterId: number;
  dimension: number;
}

export interface IOrderTopic {
  orderId: number;
  orderStatus: number;
  retentionTime: number;
  regionIdList: number[];
  brokerIdList: number[];
  approvalOpinions: string;
  partitionNum: number;
  replicaNum: number;
}

export interface IUser {
  password: string;
  role: string;
  username: string;
  oldPassword: string;
}

export interface ISample {
  maxMsgNum: number;
  offset: number;
  partitionId: number;
  clusterId: number;
  topicName: string;
}

export interface IDeleteTopic {
  clusterId: number;
  topicNameList: string[];
}

export interface IOffset {
  clusterId: number;
  consumerGroup: string;
  location: string;
  offsetList?: [];
  topicName: string;
}
