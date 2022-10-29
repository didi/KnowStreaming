import { HealthStateEnum } from '@src/components/HealthState';
import { FormItemType, IFormItem } from 'knowdesign/es/extend/x-form';

export const bootstrapServersErrCodes = [10, 11, 12];
export const zkErrCodes = [20, 21];
export const jmxErrCodes = [30, 31];

export const statusFilters = [
  {
    label: 'Live',
    value: 1,
  },
  {
    label: 'Down',
    value: 0,
  },
];

export const sortFieldList = [
  {
    label: '接入时间',
    value: 'createTime',
  },
  {
    label: '健康状态',
    value: 'HealthState',
  },
  {
    label: 'Messages',
    value: 'LeaderMessages',
  },
  {
    label: 'MessageSize',
    value: 'TotalLogSize',
  },
  {
    label: 'BytesIn',
    value: 'BytesIn',
  },
  {
    label: 'BytesOut',
    value: 'BytesOut',
  },
  {
    label: 'Brokers',
    value: 'Brokers',
  },
];

export const sortTypes = [
  {
    label: '升序',
    value: 'asc',
  },
  {
    label: '降序',
    value: 'desc',
  },
];

export const linesMetric = ['LeaderMessages', 'TotalLogSize', 'BytesIn', 'BytesOut'];
export const pointsMetric = ['HealthScore', 'HealthCheckPassed', 'HealthCheckTotal', 'Brokers', 'Zookeepers', ...linesMetric].concat(
  process.env.BUSINESS_VERSION
    ? ['LoadReBalanceCpu', 'LoadReBalanceDisk', 'LoadReBalanceEnable', 'LoadReBalanceNwIn', 'LoadReBalanceNwOut']
    : []
);

export const metricNameMap = {
  LeaderMessages: 'Messages',
  TotalLogSize: 'LogSize',
} as {
  [key: string]: string;
};

export const sliderValueMap = {
  1: {
    code: HealthStateEnum.GOOD,
    key: 'goodCount',
    name: '好',
  },
  2: {
    code: HealthStateEnum.MEDIUM,
    key: 'mediumCount',
    name: '中',
  },
  3: {
    code: HealthStateEnum.POOR,
    key: 'poorCount',
    name: '差',
  },
  4: {
    code: HealthStateEnum.DOWN,
    key: 'deadCount',
    name: 'Down',
  },
  5: {
    code: HealthStateEnum.UNKNOWN,
    key: 'unknownCount',
    name: 'Unknown',
  },
};

export const healthSorceList = {
  0: '',
  1: '好',
  2: '中',
  3: '差',
  4: 'Down',
  5: 'Unknown',
};

export interface IMetricPoint {
  aggType: string;
  createTime: number;
  metricName: string;
  timeStamp: number;
  unit: string;
  updateTime: number;
  value: number;
  metricLines?: {
    name: string;
    data: [number | string, number | string];
  };
}

export const getFormConfig = () => {
  return [
    {
      key: 'name',
      label: '集群名称',
      type: FormItemType.input,
      rules: [{ required: true, message: '请输入集群名称' }],
    },
    {
      key: 'bootstrap',
      label: 'Bootstrap Servers',
      type: FormItemType.textArea,
      rules: [
        {
          required: true,
          message: '请输入Bootstrap Servers',
        },
      ],
    },
    {
      key: 'Zookeeper',
      label: 'Zookeeper',
      type: FormItemType.textArea,
      rules: [
        {
          required: true,
          message: '请输入Zookeeper',
        },
      ],
    },
    {
      key: 'config',
      label: '集群配置',
      type: FormItemType.textArea,
      rules: [
        {
          required: true,
          message: '请输入集群配置',
        },
      ],
    },
    {
      key: 'desc',
      label: '集群描述',
      type: FormItemType.textArea,
      rules: [
        {
          required: true,
          message: '请输入集群描述',
        },
      ],
    },
  ] as unknown as IFormItem[];
};
