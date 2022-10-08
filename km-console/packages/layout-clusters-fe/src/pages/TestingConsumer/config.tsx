import { FormItemType, IFormItem } from 'knowdesign/es/extend/x-form';
import moment from 'moment';
import React from 'react';
import { timeFormat } from '../../constants/common';
import { Select } from 'knowdesign';
import { ControlStatusMap } from '../CommonRoute';

const { Option } = Select;

export const cardList = [
  { label: 'Records Average', value: 1, key: 'string', unit: 'B' },
  { label: 'Fetch Requests', value: 1, key: 'string', unit: 'B' },
  { label: 'Fetch Size Average', value: 1, key: 'string', unit: 'B' },
  { label: 'Fetch Letency Average', value: 1, key: 'string', unit: 'B' },
];

export const filterList = [
  {
    label: 'none',
    value: 0,
  },
  {
    label: 'contains',
    value: 1,
  },
  {
    label: 'does not contains',
    value: 2,
  },
  {
    label: 'equals',
    value: 3,
  },
  {
    label: 'Above Size',
    value: 4,
  },
  {
    label: 'Under Size',
    value: 5,
  },
];

export const untilList = [
  {
    label: 'forever',
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

/**
 * LATEST(0, "最新位置开始消费"): now（latest）
 * EARLIEST(1, "最旧位置开始消费"): the beginning
 * PRECISE_TIMESTAMP(2, "指定时间开始消费"): last hour、today、yesterday、a specific date
 * PRECISE_OFFSET(3, "指定位置开始消费"),
 * CONSUMER_GROUP(4, "指定消费组进行消费"),
 * LATEST_MINUS_X_OFFSET(5, "近X条数据开始消费"),
 */

export const startFromMap = {
  'an offset': {
    type: 3,
  },
  'now（latest）': {
    type: 0,
  },
  today: {
    type: 2,
    getDate: () => new Date(new Date().setHours(0, 0, 0, 0)).getTime(),
  },
  'last hour': {
    type: 2,
    getDate: () => new Date().getTime() - 60 * 60 * 1000,
  },
  yesterday: {
    type: 2,
    getDate: () => new Date().getTime() - 24 * 60 * 60 * 1000,
  },
  'a specific date': {
    type: 2,
    getDate: (date: any) => new Date(date).getTime(),
  },
  'the beginning': {
    type: 1,
  },
  'a consumer group': {
    type: 4,
  },
  'latest x offsets': {
    type: 5,
  },
} as any;

const startFromOptions = [
  {
    value: 'Time Base',
    label: 'Time Base',
    children: [
      {
        value: 'now（latest）',
        label: 'now（latest）',
      },
      {
        value: 'last hour',
        label: 'last hour',
      },
      {
        value: 'today',
        label: 'today',
      },
      {
        value: 'yesterday',
        label: 'yesterday',
      },
      {
        value: 'the beginning',
        label: 'the beginning',
      },
      {
        value: 'a specific date',
        label: 'a specific date',
      },
    ],
  },
  {
    value: 'Offset based',
    label: 'Offset based',
    children: [
      {
        value: 'a consumer group',
        label: 'a consumer group',
      },
      {
        value: 'latest x offsets',
        label: 'latest x offsets',
      },
      {
        value: 'an offset',
        label: 'an offset',
      },
    ],
  },
];

export const getFormConfig = (topicMetaData: any, info = {} as any, partitionLists: any, consumerGroupList: any) => {
  return [
    {
      key: 'topic',
      label: 'Topic',
      type: FormItemType.select,
      attrs: {
        placeholder: '请选择 Topic',
        showSearch: true,
        filterOption: (input: string, option: any) => option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0,
      },
      rules: [{ required: true, message: '请选择Topic' }],
      options: topicMetaData,
    },
    {
      key: 'start',
      label: 'Start From',
      attrs: {
        placeholder: '请选择',
      },
      type: FormItemType.cascader,
      options: startFromOptions,
      rules: [
        {
          required: true,
          message: '请选择Start From',
        },
      ],
    },
    {
      key: 'startDate',
      label: 'Date',
      type: FormItemType.datePicker,
      invisible: !info?.needStartFromDate,
      attrs: {
        showTime: true,
      },
      rules: [
        {
          required: info?.needStartFromDate,
          message: '请选择Date',
        },
      ],
    },
    {
      key: 'consumerGroup',
      label: 'Consumer Group',
      type: FormItemType.select,
      options: consumerGroupList || [],
      invisible: !info?.needConsumerGroup,
      rules: [
        {
          required: info?.needConsumerGroup,
          message: '请选择Consumer Group',
        },
      ],
    },
    {
      key: 'partitionId',
      label: 'Partition',
      type: FormItemType.select,
      options: partitionLists || [],
      colSpan: 12,
      invisible: !info?.needPartitionList,
      rules: [
        {
          required: info?.needPartitionList,
          message: '请选择Partition',
        },
      ],
    },
    {
      key: 'offset',
      label: 'Offset',
      type: FormItemType.inputNumber,
      colSpan: 12,
      attrs: {
        min: 1,
        max: info?.needOffsetMax ? 99999 : Math.pow(2, 53) - 1,
      },
      invisible: !info?.needOffset,
      rules: [
        {
          required: info?.needOffset,
          message: '请输入Offset',
        },
      ],
    },
    {
      key: 'until',
      label: 'Until',
      type: FormItemType.select,
      options: untilList,
      rules: [
        {
          required: true,
          message: '请选择Until',
        },
      ],
    },
    {
      key: 'untilMsgNum',
      label: 'Message Number',
      type: FormItemType.inputNumber,
      attrs: {
        min: 1,
        max: 1000,
      },
      invisible: !info?.needMsgNum,
      rules: [
        {
          required: info?.needMsgNum,
          message: '请输入Message Number',
        },
      ],
    },
    {
      key: 'unitMsgSize',
      label: 'Message Size',
      type: FormItemType.inputNumber,
      attrs: {
        min: 1,
      },
      invisible: !info?.needMsgSize,
      rules: [
        {
          required: info?.needMsgSize,
          message: '请输入Message Size',
        },
      ],
    },
    {
      key: 'untilDate',
      label: 'Date',
      type: FormItemType.datePicker,
      invisible: !info?.needUntilDate,
      attrs: {
        showTime: true,
      },
      rules: [
        {
          required: info?.needUntilDate,
          message: '请选择Date',
        },
      ],
    },
    {
      key: 'filter',
      label: 'Filter',
      type: FormItemType.select,
      options: filterList,
      rules: [
        {
          required: true,
          message: '请选择Filter',
        },
      ],
    },
    {
      key: 'filterKey',
      label: 'Key',
      type: FormItemType.input,
      invisible: !info?.needFilterKeyValue,
      rules: [
        {
          required: info?.needFilterKeyValue,
          message: '请输入Key',
        },
      ],
    },
    {
      key: 'filterValue',
      label: 'Value',
      type: FormItemType.input,
      invisible: !info?.needFilterKeyValue,
      rules: [
        {
          required: info?.needFilterKeyValue,
          message: '请输入Value',
        },
      ],
    },
    {
      key: 'filterSize',
      label: 'Size',
      type: FormItemType.inputNumber,
      attrs: {
        min: 1,
      },
      invisible: !info?.needFilterSize,
      rules: [
        {
          required: info?.needFilterSize,
          message: '请输入Size',
        },
      ],
    },
  ] as unknown as IFormItem[];
};

export const getTableColumns = (props: any) => {
  const { isShowControl } = props;
  const columns = [
    {
      title: 'Partition',
      dataIndex: 'partitionId',
      width: 100,
    },
    {
      title: 'Offset',
      dataIndex: 'offset',
      width: 140,
    },
    {
      title: 'Timestamp',
      dataIndex: 'timestampUnitMs',
      width: 180,
      render: (text: number) => {
        return text ? moment(text).format(timeFormat) : '-';
      },
    },
    {
      title: 'Key',
      dataIndex: 'keydata',
      render: (text: string) => {
        return text || '-';
      },
      width: 180,
      needTooltip: true,
      lineClampTwo: true,
    },
    {
      title: 'Value',
      dataIndex: 'value',
      width: 180,
      needTooltip: true,
      lineClampTwo: true,
    },
  ];

  // eslint-disable-next-line no-constant-condition
  if (isShowControl && isShowControl(ControlStatusMap.TESTING_CONSUMER_HEADER)) {
    columns.push({
      title: 'Header',
      dataIndex: 'headerList',
      // render: (text: any) => (text ? text?.join('、') : '-'),
      render: (text: any) => {
        if (text && Array.isArray(text)) {
          const newText = text.map((item: any) => {
            try {
              return JSON.stringify(item);
            } catch (error) {
              return item;
            }
          });
          return newText?.join('、') || '-';
        }
        return text || '-';
      },
      width: 180,
      needTooltip: true,
      lineClampTwo: true,
    });
  }

  return columns;
};
