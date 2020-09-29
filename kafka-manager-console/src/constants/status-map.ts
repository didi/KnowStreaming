import { IStatusMap, IStringMap, ILabelValue } from 'types/base-type';

export const optionMap = [
  'byteIn/byteOut',
  'bytesRejectedPerSec',
  'failFetchRequestPerSec',
  'failProduceRequestPerSec',
  'fetchConsumerRequestPerSec',
  'healthScore',
  'logFlushTime',
  'messagesInPerSec',
  'networkProcessorIdlPercent',
  'produceRequestPerSec',
  'requestHandlerIdlPercent',
  'requestQueueSize',
  'responseQueueSize',
  'totalTimeFetchConsumer99Th',
  'totalTimeProduce99Th',
];
export const copyValueMap = ['同步', '未同步'];

export const appStatusMap = {
  0: '待审批',
  1: '已通过',
  2: '被拒绝',
} as IStatusMap;

export const topicStatusMap = {
  0: '无权限',
  1: '可消费',
  2: '可发送',
  3: '可发送、消费',
  4: '可管理',
} as IStatusMap;

export const authStatusMap = {
  0: '无权限',
  1: '消费',
  2: '发送',
  3: '发送、消费',
  4: '管理',
} as IStatusMap;

export const orderStatusMap = {
  0: '待审批',
  1: '已通过',
  2: '已拒绝',
  3: '已取消',
} as IStatusMap;

export const clusterTypeMap = {
  0: '共享集群',
  1: '独享集群',
  2: '独立集群',
} as IStatusMap;

export const classStatusMap = {
  '-1': 'executing',
  '0': 'pending',
  '10': 'executing',
  '20': 'pending',
  '30': 'executing',
  '40': 'success',
  '41': 'success',
  '42': 'fail',
  '43': 'cancel',
  '44': 'executing',
  '45': 'pending',
} as IStatusMap;

export const orderApiTypeMap = {
  0: 'topics',
  1: 'apps',
  2: 'quotas',
  3: 'authorities',
  4: 'clusters',
} as IStatusMap;

export const offlineStatusMap = {
  '-1': '可下线',
  '0': '过期待通知',
  '1': '已通知待反馈',
} as IStatusMap;

export const orderApiMap = {
  0: '/normal/orders/topics',
  1: '/normal/orders/apps',
  2: '/normal/orders/quotas',
  3: '/normal/orders/authorities',
  4: '/normal/orders/clusters',
} as IStatusMap;

export const controlOptionMap = [{
  label: 'Bytes In/Bytes Out',
  value: 'byteIn/byteOut',
}, {
  label: 'Message In',
  value: 'messageIn',
}, {
  label: 'Topic Num',
  value: 'topicNum',
}, {
  label: 'Broker Num',
  value: 'brokerNum',
}] as ILabelValue[];

export const selectOptionMap = [{
  label: 'Bytes In/Bytes Out',
  value: 'byteIn/byteOut',
}, {
  label: 'Bytes Rejected',
  value: 'byteRejected',
}, {
  label: 'Message In/TotalProduceRequests',
  value: 'messageIn/totalProduceRequests',
}] as ILabelValue[];

export const selectBrokerMap = [{
  label: 'Bytes In/Bytes Out',
  value: 'byteIn/byteOut',
}, {
  label: 'Bytes Rejected',
  value: 'byteRejected',
}, {
  label: 'Message In',
  value: 'messageIn',
}] as ILabelValue[];

export const metricOptionMap = [
  {
    type: 'byteIn/byteOut',
    arr: ['bytesInPerSec', 'bytesOutPerSec'],
  },
  {
    type: 'messageIn/totalProduceRequests',
    arr: ['messagesInPerSec', 'totalProduceRequestsPerSec'],
  },
  {
    type: 'byteRejected',
    arr: ['bytesRejectedPerSec'],
  },
  {
    type: 'byteIn/byteOut/appByteIn/appByteOut',
    // tslint:disable-next-line:max-line-length
    // arr: ['bytesInPerSec', 'bytesOutPerSec', 'appIdBytesInPerSec', 'appIdBytesOutPerSec', 'consumeThrottled', 'produceThrottled'],
    arr: ['bytesInPerSec', 'bytesOutPerSec'],
  },
];

export const selectMonitorMap = [{
  label: '美国',
  value: '美国',
}, {
  label: '中国',
  value: '中国',
}, {
  label: '俄罗斯',
  value: '俄罗斯',
}] as ILabelValue[];

export const selectTakeMap = [{
  label: 'RequestTime99thPercentile',
  value: 'requestTime99thPercentile',
}, {
  label: 'RequestTime95thPercentile',
  value: 'requestTime95thPercentile',
}, {
  label: 'RequestTime75thPercentile',
  value: 'requestTime75thPercentile',
}, {
  label: 'RequestTime50thPercentile',
  value: 'requestTime50thPercentile',
}] as ILabelValue[];

export const columsDefault = {
  leaderPartitionList: 'leaderPartitions:',
  followerPartitionIdList: 'followerPartitions:',
  notUnderReplicatedPartitionIdList: 'notUnderReplicatedPartitions:',
} as IStringMap;

export const diskDefault = {
  leaderPartitions: 'leaderPartitions:',
  followerPartitions: 'followerPartitions:',
  notUnderReplicatedPartitions: 'notUnderReplicatedPartitions:',
} as IStringMap;

export const brokerMetrics = {
  bytesIn: 'Bytes In（MB/ 秒)',
  bytesOut: 'Bytes Out（MB/ 秒)',
  messagesIn: 'Messages In（条)',
  totalFetchRequests: 'Total Fetch Requests（QPS)',
  totalProduceRequests: 'Total Produce Requests（QPS)',
} as IStringMap;

export const roleMap = {
  0: '普通用户',
  1: '研发人员',
  2: '运维人员',
} as IStatusMap;

export const weekOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 0 },
];
