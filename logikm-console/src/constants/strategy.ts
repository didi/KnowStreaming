export const timeOptions = [
  { label: '周日', value: '0' },
  { label: '周一', value: '1' },
  { label: '周二', value: '2' },
  { label: '周三', value: '3' },
  { label: '周四', value: '4' },
  { label: '周五', value: '5' },
  { label: '周六', value: '6' },
];

export const equalList = [
  { label: '大于', value: '>' },
  { label: '小于', value: '<' },
  { label: '等于', value: '=' },
  { label: '大于等于', value: '>=' },
  { label: '小于等于', value: '<=' },
  { label: '不等于', value: '!=' },
];

export const timeList = [
  { label: '天', value: 'day' },
  { label: '小时', value: 'hour' },
];

export const funcList = [
  { label: '周期发生-happen', value: 'happen' },
  { label: '连续发生-all', value: 'all' },
  { label: '同比变化率-c_avg_rate_abs', value: 'c_avg_rate_abs' },
  { label: '突增突降值-diff', value: 'diff' },
  { label: '突增突降率-pdiff', value: 'pdiff' },
  { label: '求和-sum', value: 'sum' },
];

interface IStringArray {
  [key: string]: string [];
}

export const funcKeyMap = {
  happen: ['period', 'count'],
  ndiff: ['period', 'count'],
  c_avg_rate_abs: ['period', 'day'],
  all: ['period'],
  diff: ['period'],
  pdiff: ['period'],
  sum: ['period'],
} as IStringArray;

export const filterList = [
  { label: '集群', value: 'clusterName' },
  { label: 'Topic', value: 'topic' },
  { label: 'Location', value: 'loaction' },
  { label: '消费组', value: 'consumerGroup' },
];

export const filterKeys = ['clusterName', 'topic', 'location', 'consumerGroup'];

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const timeMinute = 'YYYY-MM-DD HH:mm';

export const timeMonth = 'YYYY-MM';

export const timeStampStr = 'YYYY/MM/DD HH:mm:ss';

export const timeMonthStr = 'YYYY/MM';

// tslint:disable-next-line:max-line-length

export const indexUrl ={
  indexUrl:'https://github.com/didi/Logi-KafkaManager/blob/master/docs/user_guide/kafka_metrics_desc.md', // 指标说明
  cagUrl:'https://github.com/didi/Logi-KafkaManager/blob/master/docs/user_guide/add_cluster/add_cluster.md', // 集群接入指南 Cluster access Guide
} 

export const expandRemarks = `请填写不少于5字的申请原因！以便工作人员判断审核`;

export const quotaRemarks = `请填写不少于5字的申请原因！以便工作人员判断审核\n\n如需申请分区（分区标准为3MB/s一个），请填写“分区数：n”`;

