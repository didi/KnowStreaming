import { QuestionCircleOutlined } from '@ant-design/icons';
import { Switch, Tooltip } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { FormItemType, IFormItem } from 'knowdesign/es/extend/x-form';
import moment from 'moment';
import React from 'react';
import { timeFormat, getRandomStr } from '@src/constants/common';
import { ControlStatusMap } from '../CommonRoute';

export const filterList = [
  {
    label: 'none',
    value: 'none',
  },
  {
    label: 'contains',
    value: 'contains',
  },
  {
    label: 'does not contains',
    value: 'does not contains',
  },
  {
    label: 'equals',
    value: 'equals',
  },
  {
    label: 'Above Size',
    value: 'aboveSize',
  },
  {
    label: 'Under Size',
    value: 'underSize',
  },
];

export const untilList = [
  {
    label: 'Forever',
    value: 'forever',
  },
  {
    label: 'number of messages',
    value: 'number of messages',
  },
  {
    label: 'number of messages per partition',
    value: 'number of messages per partition',
  },
  {
    label: 'max size',
    value: 'max size',
  },
  {
    label: 'max size per partition',
    value: 'max size per partition',
  },
  {
    label: 'timestamp',
    value: 'timestamp',
  },
];

export const tabsConfig = [
  { name: 'Data', control: '' },
  { name: 'Flow', control: '' },
  { name: 'Header', control: ControlStatusMap.TESTING_PRODUCER_HEADER },
  { name: 'Options', control: '' },
];

const getComplexLabel = (key: string, label: string, form: any, onSwitchChange?: any, needSwitch = false) => (
  <div className="complex-label">
    <span>
      <span>{label}</span>
      <Tooltip title="暂支持string格式">
        <QuestionCircleOutlined size={12} style={{ marginLeft: 2 }} />
      </Tooltip>
      :
      {needSwitch ? (
        <span>
          <Switch onClick={onSwitchChange} size="small" defaultChecked className="switch" />
        </span>
      ) : null}
    </span>
    <span>
      <Tooltip title={'生成随机内容'}>
        <IconFont
          type="icon-shengchengdaima"
          className="random-icon"
          onClick={() => {
            const randomStr = getRandomStr(key === 'key' ? 30 : 128);
            form && form.setFieldsValue({ [key]: randomStr });
          }}
        />
      </Tooltip>
    </span>
  </div>
);

export const getFormConfig = (params: any) => {
  const { topicMetaData, activeKey: type, configInfo: info, form, onKeySwitchChange, isKeyOn, isShowControl } = params;
  const formConfig = [
    {
      key: 'topicName',
      label: 'Topic',
      type: FormItemType.select,
      invisible: type !== 'Data',
      rules: [{ required: true, message: '请选择Topic' }],
      options: topicMetaData,
      attrs: {
        showSearch: true,
      },
    },
    {
      key: 'key',
      label: getComplexLabel('key', 'Key', form, onKeySwitchChange, true),
      type: FormItemType.textArea,
      invisible: type !== 'Data',
      rules: [
        {
          required: false,
          message: '请输入Key',
        },
      ],
      attrs: {
        disabled: !isKeyOn,
        rows: 5,
        placeholder: '暂支持string类型',
      },
    },
    {
      key: 'value',
      label: getComplexLabel('value', 'Value', form),
      type: FormItemType.textArea,
      invisible: type !== 'Data',
      rules: [
        {
          required: true,
          message: '请输入Value',
        },
      ],
      attrs: {
        rows: 5,
        placeholder: '暂支持string类型',
      },
    },
    {
      key: 'chunks',
      label: '单次发送消息数',
      type: FormItemType.inputNumber,
      invisible: type !== 'Flow',
      defaultValue: 1,
      rules: [{ required: true, message: '请输入' }],
      attrs: {
        min: 0,
        max: 1000,
        style: { width: 232 },
      },
    },
    {
      key: 'producerMode',
      label: '生产模式',
      type: FormItemType.radioGroup,
      invisible: type !== 'Flow',
      defaultValue: 'manual',
      options: [
        {
          label: '手动',
          value: 'manual',
        },
        {
          label: '周期',
          value: 'timed',
        },
      ],
      rules: [
        {
          required: true,
          message: '请选择生产模式',
        },
      ],
    },
    // {
    //   key: 'timeOptions',
    //   label: 'Timer options',
    //   type: FormItemType.text,
    //   invisible: type !== 'Flow' || !info?.needTimeOption,
    //   customFormItem: null,
    //   rules: [
    //     {
    //       required: false,
    //       message: '请选择Producer Mode',
    //     },
    //   ],
    // },
    {
      key: 'elapsed',
      label: '运行总时间（min）',
      type: FormItemType.inputNumber,
      invisible: type !== 'Flow' || !info?.needTimeOption,
      rules: [
        {
          required: info?.needTimeOption,
          message: '请输入',
        },
      ],
      attrs: {
        min: 0,
        max: 300,
        style: { width: '100%' },
      },
    },
    {
      key: 'interval',
      label: '时间间隔（ms）',
      type: FormItemType.inputNumber,
      invisible: type !== 'Flow' || !info?.needTimeOption,
      rules: [
        {
          required: info?.needTimeOption,
          message: '请输入',
        },
      ],
      attrs: {
        min: 300, // 300ms间隔保证请求不太频繁
        style: { width: '100%' },
      },
    },
    // {
    //   key: 'jitter',
    //   label: 'Max jitter（ms）',
    //   type: FormItemType.inputNumber,
    //   invisible: type !== 'Flow' || !info?.needTimeOption,
    //   rules: [
    //     {
    //       required: info?.needTimeOption,
    //       message: '请输入Max jitter',
    //     },
    //   ],
    //   attrs: {
    //     max: info.maxJitter || 0,
    //     size: 'small',
    //     style: { width: 216 },
    //   },
    //   formAttrs: {
    //     className: 'inner-item',
    //   },
    // },
    // {
    //   key: 'lifecycle',
    //   label: (
    //     <>
    //       Lifecycle options
    //       <Tooltip title="Shutdown the producer automatically">
    //         <QuestionCircleOutlined style={{ marginLeft: 8 }} />
    //       </Tooltip>
    //     </>
    //   ),
    //   type: FormItemType.text,
    //   invisible: type !== 'Flow' || !info?.needTimeOption,
    //   customFormItem: null,
    //   rules: [
    //     {
    //       required: false,
    //       message: '',
    //     },
    //   ],
    // },
    // {
    //   key: 'messageProduced',
    //   label: 'Number of message produced',
    //   type: FormItemType.inputNumber,
    //   invisible: type !== 'Flow' || !info?.needTimeOption,
    //   rules: [
    //     {
    //       required: info?.needTimeOption,
    //       message: '请输入Number of message produced',
    //     },
    //   ],
    //   attrs: {
    //     min: 0,
    //     size: 'small',
    //     style: { width: 216 },
    //   },
    //   formAttrs: {
    //     className: 'inner-item',
    //   },
    // },
    {
      key: 'frocePartition',
      label: 'Froce Partition',
      invisible: type !== 'Options',
      type: FormItemType.select,
      attrs: {
        mode: 'multiple',
        maxTagCount: 'responsive',
        allowClear: true,
      },
      options: info.partitionIdList || [],
      rules: [{ required: false, message: '请选择' }],
    },
    {
      key: 'compressionType',
      label: 'Compression Type',
      type: FormItemType.radioGroup,
      defaultValue: 'none',
      invisible: type !== 'Options',
      options: (() => {
        const options = [
          {
            label: 'none',
            value: 'none',
          },
          {
            label: 'gzip',
            value: 'gzip',
          },
          {
            label: 'snappy',
            value: 'snappy',
          },
          {
            label: 'lz4',
            value: 'lz4',
          },
        ];

        if (isShowControl && isShowControl(ControlStatusMap.TESTING_PRODUCER_COMPRESSION_TYPE_ZSTD)) {
          options.push({
            label: 'zstd',
            value: 'zstd',
          });
        }

        return options;
      })(),
      rules: [
        {
          required: false,
          message: '请选择Compression Type',
        },
      ],
    },
    {
      key: 'acks',
      label: 'Acks',
      type: FormItemType.radioGroup,
      defaultValue: '0',
      invisible: type !== 'Options',
      options: [
        {
          label: 'none',
          value: '0',
        },
        {
          label: 'leader',
          value: '1',
        },
        {
          label: 'all',
          value: 'all',
        },
      ],
      rules: [
        {
          required: false,
          message: '请选择Acks',
        },
      ],
    },
  ] as unknown as IFormItem[];

  return formConfig;
};

export const getTableColumns = () => {
  return [
    {
      title: 'Partition',
      dataIndex: 'partitionId',
    },
    {
      title: 'offset',
      dataIndex: 'offset',
    },
    {
      title: 'Timestamp',
      dataIndex: 'timestampUnitMs',
      render: (text: number) => {
        return moment(text).format(timeFormat);
      },
    },
    {
      title: 'time',
      dataIndex: 'costTimeUnitMs',
      width: 100,
    },
  ];
};
