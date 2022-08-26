import { FormItemType, IFormItem } from 'knowdesign/lib/extend/x-form';

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
    label: '健康分',
    value: 'HealthScore',
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

export const healthSorceList = {
  0: 0,
  10: '',
  20: 20,
  30: '',
  40: 40,
  50: '',
  60: 60,
  70: '',
  80: 80,
  90: '',
  100: 100,
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
