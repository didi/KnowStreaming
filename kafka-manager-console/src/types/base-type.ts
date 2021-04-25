export interface IStatusMap {
  [key: number]: string;
}

export interface IStringMap {
  [key: string]: string;
}

export interface ILeftMenu {
  href: string;
  i: string;
  title: string;
  hide?: boolean;
  class?: string;
}

export interface IBtn {
  clickFunc?: any;
  label: string | JSX.Element;
  className?: string;
  aHref?: string;
  show?: boolean;
}

export interface IClusterData {
  clusterIdentification: any;
  clusterId: number;
  mode: number;
  clusterName: string;
  clusterVersion: string;
  gmtCreate: number;
  gmtModify: number;
  topicNum: number;
  description?: string;
  label?: string;
  value?: number;
}

export interface ISeriesOption {
  name: string;
  type: string;
  data?: number[];
  markPoint?: any;
  symbol?: string;
  lineStyle?: object;
  id?: string;
  smooth?: boolean;
  encode?: object;
  showSymbol?: boolean;
}

export interface IBrokerMetrics {
  produceRequestPerSec?: number;
  requestHandlerIdlPercent?: number;
  networkProcessorIdlPercent?: number;
  requestQueueSize?: number;
  responseQueueSize?: number;
  logFlushTime?: number;
  failFetchRequest?: number;
  failProduceRequest?: number;
  totalTimeProduce99Th?: number;
  totalTimeFetchConsumerMean?: number;
  totalTimeFetchConsumer99Th?: number;
  gmtCreate?: number;
  totalTimeProduceMean?: number;
}

export interface IClusterMetrics extends IBrokerMetrics {
  bytesInPerSec: number;
  bytesOutPerSec: number;
  bytesRejectedPerSec: number;
  failFetchRequestPerSec?: number;
  failProduceRequestPerSec?: number;
  fetchConsumerRequestPerSec?: number;
  messagesInPerSec?: number;
  healthScore?: number;
  gmtCreate?: number;
  appId?: string;
  appIdBytesInPerSec?: number;
  appIdBytesOutPerSec?: number;
  appIdMessagesInPerSec?: number;
  clusterId?: number;
  consumeThrottled?: boolean;
  produceThrottled?: boolean;
  topicName?: string;
  totalProduceRequestsPerSec?: number;
  id?: number;
  brokerNum?: number;
  topicNum?: number;
  [key: string]: any;
}

export interface ITakeMetric {
  clusterId: number;
  fetchRequestTime50thPercentile: number;
  fetchRequestTime75thPercentile: number;
  fetchRequestTime95thPercentile: number;
  fetchRequestTime99thPercentile: number;
  fetchRequestTimeMean: number;
  gmtCreate: number;
  produceRequestTime50thPercentile: number;
  produceRequestTime75thPercentile: number;
  produceRequestTime95thPercentile: number;
  produceRequestTime99thPercentile: number;
  produceRequestTimeMean: number;
  topicName: string;
}

export interface ILabelValue {
  value: string | number;
  label?: string;
  name?: string;
  key?: number;
}

// tslint:disable-next-line:max-line-length
export type IOptionType =
  | 'byteIn/byteOut'
  | 'byteRejected'
  | 'messageIn'
  | 'topicNum'
  | 'brokerNum'
  | 'messageIn/totalProduceRequests'
  | 'byteIn/byteOut/appByteIn/appByteOut';
// tslint:disable-next-line:max-line-length
export type ITakeType =
  | 'requestTime99thPercentile'
  | 'requestTime95thPercentile'
  | 'requestTime75thPercentile'
  | 'requestTime50thPercentile';

export type IMonitorType = '中国' | '美国' | '俄罗斯' | '指标';

export interface IStaff {
  username: string;
  chineseName: string;
  department: string;
  label?: string;
  value?: string;
  role: string | number;
}

interface IBase {
  appId: string;
  appName: string;
  appPrincipals: string;
  clusterId: number;
  clusterName: string;
  topicName: string;
  principals: string;
  description: string;
}

export interface ITopic extends IBase {
  access: number;
  key?: number;
  bytesIn?: number;
  bytesOut?: number;
  connectionList?: IConnectionInfo[];
  isPhysicalClusterId?: boolean;
  needAuth?: boolean;
}

export interface IAuthorities {
  access: number;
  appId: string;
  topicName: string;
}

export interface IQuotaQuery {
  appId: string;
  clusterId: number;
  consumeQuota: number;
  produceQuota: number;
  topicName: string;
  access: number;
}

export interface IFilter {
  text: string;
  value: string;
}

export interface IUser {
  password?: string;
  role: number;
  username: string;
  oldPassword: string;
  roleName?: string;
  chineseName?: string;
  department?: string;
  key?: number;
  confirmPassword?: string
}

export interface IOffset {
  clusterId: number;
  consumerGroup: string;
  location: string;
  offsetList?: [];
  topicName: string;
  timestamp: number;
  key?: number;
}

export interface IPageRouteItem {
  path: string;
  exact: boolean;
  component: any;
}

export interface IAppItemBase {
  appId: string;
  name: string;
  description: string;
  principals: string;
  status: number;
  password: string;
}

export interface IAppItem extends IAppItemBase {
  appliction?: string;
  principalList?: string[];
  idc?: string;
  connectionList?: IConnectionInfo[];
}

export interface IAppQuota {
  appId: string;
  access: number;
  appName: string;
  appPrincipals: string;
  consumerQuota: number;
  produceQuota: number;
  label?: string;
  value?: string;
  key?: number;
}

export interface IOrderParams {
  description?: string;
  type: number;
  applicant?: string;
  extensions?: string;
}

export interface IBaseInfoItem {
  appID: string;
  bootstrapServers: string;
  createTime: number;
  description: string;
  modifyTime: number;
  partitionNum: number;
  principals: string;
  replicaNum: number;
  retentionTime: number;
  topicCodeC: string;
}

export interface IQuotaModelItem {
  appId?: string;
  clusterId: number;
  topicName: string;
  description: string;
  consumeQuota?: number;
  produceQuota?: number;
}

export interface ILimitsItem {
  access?: string;
  appId?: string;
  clusterId?: number;
  topicName?: string;
  peakBytesInPerSec?: number;
  description?: string;
  retainDays?: number;
  name?: string;
  password?: string;
  principals?: string;
  status?: number;
}

export interface IRadioItem {
  code: number;
  message: string;
  type?: number;
  label?: string;
  value?: number;
}

export interface IConfigInfo {
  code: number;
  message: string;
  type?: number;
}
export interface IApprovalOrder {
  detail: string;
  id: number;
  opinion: string;
  status: number;
}

export interface IBrokerMetadata {
  brokerId: number;
  host: string;
  logicClusterId?: number;
  key?: number;
}

export interface IBatchApproval {
  opinion: string;
  orderIdList: number[];
  status: number;
}

export interface IBatchApprovalData {
  id: number;
  result: IBatchDataResult;
}

export interface IBatchDataResult {
  code: number;
  data: object;
  message: string;
  tips: string;
}

export interface IBatchData {
  id: number;
  code: number;
  message: string;
}

export interface IDetailData {
  brokeId: number;
  partitionNum: number;
  key?: number;
}

export interface IHotTopics {
  clusterId: number;
  clusterName: string;
  detailList: IDetailData[];
  regionId: number;
  regionName: string;
  topicName: string;
  retentionTime: number;
  key?: number;
}

export interface IReassignTasks {
  clusterName: string;
  completedTopicNum: number;
  finishTime: number;
  gmtCreate: number;
  maxThrottle: number;
  minThrottle: number;
  operator: string;
  beginTime: number;
  status: number;
  taskId: number;
  taskName: string;
  throttle: number;
  totalTopicNum: number;
  key?: number;
}
export interface ITopicMetadata {
  partitionIdList: number[];
  topicName: string;
  key?: number;
}

export interface IDetailVO {
  destReplicaIdList: number[];
  partitionId: number;
  status: number;
  presentReplicaIdList?: number[];
  topicName?: string;
  key?: number;
}

export interface IReassign {
  clusterId: number;
  completedPartitionNum: number;
  reassignList: IDetailVO[];
  status: number;
  topicName: string;
  totalPartitionNum: number;
  clusterName?: string;
  subTaskId: number;
  maxThrottle: number;
  minThrottle: number;
  realThrottle: number;
  key?: number;
}

export interface ITasksDetail {
  clusterId?: number;
  clusterName?: string;
  finishTime: number;
  endTime: number;
  gmtCreate: string;
  maxThrottle: number;
  minThrottle: number;
  operator: string;
  beginTime: number;
  status: number;
  taskId: number;
  taskName: string;
  throttle: number;
  description: string;
  completedTopicNum: number;
  totalTopicNum: number;
  key?: number;
}

export interface IPartition {
  brokerIdList: number[];
  bytesInPerPartition: number;
  clusterId: number;
  clusterName: string;
  maxAvgBytesInList: number[];
  presentPartitionNum: number;
  suggestedPartitionNum: number;
  topicName: string;
  [key: number]: number;
  key?: number;
}
export interface IResource {
  clusterId: number;
  clusterName: string;
  expiredDay: number;
  fetchConnectionNum: number;
  produceConnectionNum: number;
  status: number;
  topicName: string;
  appId: string;
  appName: string;
  principals: string;
  key?: number;
}

export interface IAnomalyFlow {
  bytesIn: number;
  bytesInIncr: number;
  clusterId: number;
  clusterName: string;
  iops: number;
  iopsIncr: number;
  topicName: string;
  key?: number;
}

export interface IKafkaFiles {
  fileName: string;
  fileMd5: string;
  fileType: number;
}

export interface IMetaData {
  bootstrapServers: string;
  brokerNum: number;
  clusterId: number;
  clusterName: string;
  consumerGroupNum: number;
  controllerId: number;
  gmtCreate: number;
  gmtModify: number;
  idc: string;
  kafkaVersion: string;
  mode: number;
  regionNum: number;
  securityProperties: string;
  status: number;
  topicNum: number;
  zookeeper: string;
  key?: number;
}

export interface IConfigure {
  configDescription: string;
  configKey: string;
  configValue: string;
  gmtCreate: number;
  gmtModify: number;
  id: number;
  key?: number;
}

export interface IConfigGateway {
  id: number;
  key?: number;
  modifyTime: number;
  name: string;
  value: string;
  version: string;
  type: string;
  description: string;
}

export interface IEepand {
  brokerIdList: number[];
  clusterId: number;
  partitionNum: number;
  regionId?: number | string;
  topicName: string;
}

export interface ISample {
  maxMsgNum: number;
  offset: number;
  partitionId: number;
  timeout: number;
  truncate: boolean;
  [key: string]: number | string | boolean;
}

export interface IMigration {
  throttle: number;
  beginTime: number;
  originalRetentionTime: number;
  reassignRetentionTime: number;
  clusterId: number;
  description: string;
  maxThrottle: number;
  minThrottle: number;
  reassignList: any[];
  brokerIdList: number[];
  partitionIdList: number[];
  regionId: number;
  topicName: string;
  key?: string;
}

export interface IExecute {
  action: string;
  beginTime: number;
  taskId?: number;
  throttle?: number;
  maxThrottle?: number;
  minThrottle?: number;
  subTaskId?: number;
}

export interface IUtils {
  clusterId: number;
  force: boolean;
  topicName: string;
}

export interface IXFormWrapper {
  type?: string;
  title: string | JSX.Element;
  onSubmit: (result: any) => any;
  onCancel?: () => any;
  onChange?: (result: any) => any;
  visible: boolean;
  onChangeVisible?: (visible: boolean) => any;
  formMap?: any[];
  formData?: any;
  width?: number;
  formLayout?: any;
  okText?: string;
  cancelText?: string;
  customRenderElement?: React.ReactNode;
  noform?: boolean;
  nofooter?: boolean;
  isWaitting?: boolean;
  onSubmitFaild?: (err: any, ref: any, formData: any, formMap: any) => any;
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
  gmtHandle: number;
  id: number;
  status: number;
  access: number;
  name: string;
  type: number;
  gmtTime?: number;
  title?: string;
}

export interface IClusterReal {
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

export interface IBasicInfo {
  clusterIdentification: any;
  bootstrapServers: string;
  clusterId: number;
  mode: number;
  clusterName: string;
  clusterNameCn: string;
  clusterVersion: string;
  gmtCreate: number;
  gmtModify: number;
  physicalClusterId: number;
  topicNum: number;
}

export interface IClusterTopics {
  regionNameList: any;
  appId: string;
  appName: string;
  byteIn: number;
  byteOut: number;
  clusterId: number;
  description: string;
  partitionNum: number;
  produceRequest: number;
  replicaNum: number;
  topicName: string;
  updateTime: number;
  retentionTime: number;
  properties: any;
  clusterName: string;
  logicalClusterId: number;
  key?: number;
}

export interface IBrokerData {
  brokerId: number;
  byteIn: number;
  byteOut: number;
  host: string;
  jmxPort: number;
  port: number;
  regionName: string;
  startTime: number;
  status: number;
  peakFlowStatus: number;
  underReplicated: boolean;
  key?: number;
}

export interface IController {
  brokerId: number;
  host: string;
  timestamp?: number;
  version?: number;
  startTime?: number;
  status?: number;
  key?: number;
}

export interface IRegister {
  bootstrapServers: string;
  clusterId: number;
  clusterName: string;
  idc: string;
  kafkaVersion: string;
  mode: string;
  securityProperties: string;
  zookeeper: string;
}

export interface ITopicMetriceParams {
  clusterId: number;
  topicName: string;
  startTime: string;
  endTime: string;
  appId?: string;
}

export interface IBill {
  cost: number;
  gmtMonth: string;
  quota: number;
  topicNum: number;
  timestamp: number;
}

export interface IBillDetail {
  cost: number;
  topicName: string;
  quota: number;
  clusterName: string;
  clusterId: number;
}

export interface IApplicant {
  department: string;
  name: string;
  ldap: string;
}

interface IDetail {
  name?: string;
  appName?: string;
  appId?: string;
  appPrincipals?: string;
  principals?: string;
  logicalClusterId?: number;
  logicalClusterName?: string;
  physicalClusterId?: number;
  physicalClusterName?: string;
  topicName?: string;
  access?: number;
  consumeQuota?: number;
  produceQuota?: number;
  maxAvgBytesInList?: number[];
  password?: string;
  connectionDTOList?: number[];
  connectionList?: IConnectionInfo[];
  bytesIn?: number;
  idc?: string;
  mode?: number;
  peakBytesIn?: number;
  topicNameList?: number[];
  oldProduceQuota?: number;
  oldConsumeQuota?: number;
  regionIdList?: number[];
  regionBrokerIdList?: number[];
  regionNameList?: string[];
  topicBrokerIdList?: number[];
  partitionNum?: number;
  presentPartitionNum?: number;
  needIncrPartitionNum?: number;
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

export interface IOrderInfo {
  id: number;
  type: number;
  approverList: IUser[];
  applicant: IUser;
  gmtCreate: string;
  gmtHandle: string;
  opinion: string;
  status: number;
  detail: IDetail;
  description?: string;
  approvers?: string;
  currentStep?: 0 | 1 | 2;
}
export interface IEditTopic {
  appId: string;
  clusterId: number;
  description: string;
  properties: string;
  retentionTime: number;
  topicName: string;
}

export interface IExpand {
  brokerIdList: number[];
  clusterId: number;
  partitionNum: number;
  regionId: number;
  topicName: string;
}

export interface IDeleteTopic {
  clusterId: number;
  topicName: string;
  unForce: boolean;
}

export interface IBrokersBasicInfo {
  host: string;
  jmxPort: number;
  leaderCount: number;
  partitionCount: number;
  port: number;
  startTime: number;
  topicNum: number;
  key?: number;
}

export interface IBrokersStatus {
  clusterId: number;
  brokerReplicaStatusList: number[];
  brokerBytesInStatusList: number[];
}

export interface IBrokersTopics {
  appId: string;
  appName: string;
  byteIn: number;
  clusterId: number;
  description: string;
  partitionNum: number;
  produceRequest: number;
  replicaNum: number;
  topicName: string;
  updateTime: number;
  logicalClusterId?: number;
  key?: number;
}

export interface IBrokersPartitions {
  followerPartitionIdList: number[];
  leaderPartitionList: number[];
  notUnderReplicatedPartitionIdList: number[];
  topicName: string;
  underReplicated: boolean;
  key?: number;
}
export interface IBrokerHistory {
  bytesIn?: number;
  bytesOut?: number;
  messagesIn?: number;
  totalFetchRequests?: number;
  totalProduceRequests?: number;
}

export interface IBrokersAnalysis extends IBrokerHistory {
  topicAnalysisVOList: [];
  baseTime?: number;
  brokerId?: number;
}

export interface IAnalysisTopicVO {
  bytesIn: string;
  bytesInRate: string;
  bytesOut: string;
  bytesOutRate: string;
  messagesIn: string;
  messagesInRate: string;
  topicName: string;
  totalFetchRequests: string;
  totalFetchRequestsRate: string;
  totalProduceRequests: string;
  totalProduceRequestsRate: string;
  key?: number;
}

export interface IBrokersMetadata {
  brokerId: number;
  host: string;
  key?: number;
}

export interface IBrokersRegions {
  capacity: number;
  realUsed: number;
  estimateUsed: number;
  brokerIdList: number[];
  clusterId: number;
  clusterName: string;
  description: string;
  gmtModify: number;
  id: number;
  mode: string;
  name: string;
  principals?: string;
  status: number;
  appId: number;
  appName?: string;
  key?: number;
}

export interface INewRegions {
  brokerIdList?: any;
  clusterId: number;
  description: string;
  id?: number;
  mode: number;
  name: string;
  principals?: string;
  status: number;
  clusterName?: string;
  appId: number;
}

export interface IRebalance {
  brokerId: number;
  clusterId: number;
  dimension: number;
  regionId: number;
  topicName: string;
}

export interface IThrottles {
  appId: string;
  brokerIdList: number[];
  throttleClientType: string;
  topicName: string;
  key?: number;
}

export interface ILogicalCluster {
  appId: string;
  description: string;
  gmtCreate: number;
  gmtModify: number;
  logicalClusterId: number;
  logicalClusterName: string;
  mode: number;
  physicalClusterId: number;
  regionIdList: number[];
  name?: string;
  key?: number;
}

export interface INewLogical {
  appId: string;
  clusterId: number;
  description: string;
  id: number;
  mode: number;
  name: string;
  logicalClusterName?: string;
  logicalClusterNameCn?: string;
  regionIdList: number[];
  logicalClusterIdentification?: string
}

export interface IPartitionsLocation {
  brokerId: number;
  clusterId: number;
  diskName: string;
  followerPartitions: number[];
  leaderPartitions: number[];
  notUnderReplicatedPartitions: number[];
  topicName: string;
  underReplicated: boolean;
  key?: number;
}

export interface ITaskManage {
  clusterId: number;
  clusterName: string;
  gmtCreate: number;
  createTime: number;
  operator: string;
  status: number;
  taskId: number;
  taskType: string;
  key?: number;
}

export interface ITaskType {
  taskType: ITasksEnums[];
}

export interface ITasksEnums {
  name: string;
  beanName: string;
  message: string;
}

export interface IEnumsMap {
  code: number;
  message: string;
}

export interface IStaffSummary {
  cost: number;
  gmtMonth: string;
  quota: number;
  timestamp: number;
  topicNum: number;
  username: string;
  key?: number;
}

export interface INewBulidEnums {
  clusterId: number;
  taskType: string;
  kafkaPackageName: string;
  kafkaPackageMd5: string;
  serverPropertiesName: string;
  serverPropertiesMd5: string;
  upgradeSequenceList?: any[];
  ignoreList?: any[];
  hostList?: string[];
  kafkafileNameMd5?: string;
  serverfileNameMd5?: string;
}

export interface ITrigger {
  action: string;
  hostname: string;
  taskId: number;
}

export interface ITasksMetaData {
  clusterId: number;
  clusterName: string;
  gmtCreate: number;
  hostList: string[];
  kafkaPackageMd5: string;
  kafkaPackageName: string;
  operator: string;
  pauseHostList: string[];
  serverPropertiesMd5: string;
  serverPropertiesName: string;
  taskId: number;
  taskName: string;
  serverPropertiesFileId: number;
}

export interface ITaskStatusDetails {
  status: number;
  subTaskStatusList: ISubtasksStatus[];
  sumCount: number;
  taskId: number;
  failedCount: number;
  successCount: number;
  runningCount: number;
  waitingCount: number;
  rollback: boolean;
}

export interface IKafkaRoles {
  role: string;
}

export interface ISubtasksStatus {
  groupId: number;
  hostname: string;
  status: number;
  kafkaRoles: string;
}

export interface IUploadFile {
  id: number;
  clusterId?: number;
  clusterName?: string;
  description: string;
  fileMd5: string;
  fileName?: string;
  fileType?: number;
  gmtModify?: string;
  operator?: string;
  storageName?: string;
  file?: any;
  uploadFile?: any;
  configType?: string;
  key?: number;
}
/*
 ** 监控报警
 */
export interface IMonitorStrategies {
  appId: string;
  appName: string;
  createTime: number;
  id: number;
  name: string;
  operator: string;
  principals: string;
  key?: number;
}

export interface IMonitorAlerts {
  alertId: number;
  alertStatus: number;
  endTime: number;
  monitorId: number;
  monitorName: string;
  monitorPriority: number;
  startTime: number;
  key?: number;
}

export interface IMonitorSilences {
  description: string;
  endTime: number;
  monitorId: number;
  monitorName: string;
  monitorPriority?: number;
  silenceId: number;
  startTime: number;
  id?: number;
  key?: number;
}

export interface IMonitorGroups {
  comment: string;
  id: number;
  name: string;
  key?: number;
  label?: string;
  value?: number;
}

export interface IAlertsDetail {
  monitorAlert: IMonitorAlert;
  monitorMetric: IMonitorMetric;
}

export interface IMonitorAlert {
  alertId: number;
  alertStatus: number;
  endTime: number;
  groups: string[];
  info: string;
  metric: string;
  monitorId: number;
  monitorName: string;
  monitorPriority: number;
  points: IMetricPoint[];
  startTime: number;
  value: number;
}

export interface IMonitorMetric {
  comparison: number;
  delta: number;
  metric: string;
  origin: boolean;
  step: number;
  values: IMetricPoint[];
}

export interface IMetricPoint {
  timestamp: number;
  value: number;
}
