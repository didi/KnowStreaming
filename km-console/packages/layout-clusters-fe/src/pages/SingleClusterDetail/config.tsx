import moment from 'moment';
import React from 'react';
import { timeFormat } from '../../constants/common';
import TagsWithHide from '../../components/TagsWithHide/index';
import { Form, IconFont, InputNumber, Tooltip } from 'knowdesign';
import { Link } from 'react-router-dom';
import { systemKey } from '../../constants/menu';

const statusTxtEmojiMap = {
  success: {
    emoji: '👍',
    txt: '优异',
  },
  normal: {
    emoji: '😊',
    txt: '正常',
  },
  exception: {
    emoji: '👻',
    txt: '异常',
  },
};

export const dimensionMap = {
  '-1': {
    label: 'Unknown',
    href: ``,
  },
  0: {
    label: 'Cluster',
    href: ``,
  },
  1: {
    label: 'Broker',
    href: `/broker`,
  },
  2: {
    label: 'Topic',
    href: `/topic`,
  },
  3: {
    label: 'ConsumerGroup',
    href: `/consumers`,
  },
} as any;

export const getHealthState = (value: number, down: number) => {
  if (value === undefined) return '-';
  const progressStatus = +down <= 0 ? 'exception' : value >= 90 ? 'success' : 'normal';
  return (
    <span>
      {statusTxtEmojiMap[progressStatus].emoji}&nbsp;集群状态{statusTxtEmojiMap[progressStatus].txt}
    </span>
  );
};

export const getHealthText = (value: number, down: number) => {
  return +down <= 0 ? 'Down' : value ? value.toFixed(0) : '-';
};

export const getHealthProcessColor = (value: number, down: number) => {
  return +down <= 0 ? '#FF7066' : +value < 90 ? '#556EE6' : '#00C0A2';
};

export const getHealthClassName = (value: number, down: number) => {
  return +down <= 0 ? 'down' : value === undefined ? 'no-info' : +value < 90 ? 'less-90' : '';
};

export const renderToolTipValue = (value: string, num: number) => {
  return (
    <>
      {value?.length > num ? (
        <>
          <Tooltip placement="topLeft" title={value}>
            {value ?? '-'}
          </Tooltip>
        </>
      ) : (
        value ?? '-'
      )}
    </>
  );
};

export const getDetailColumn = (clusterId: number) => [
  {
    title: '检查模块',
    dataIndex: 'dimension',
    // eslint-disable-next-line react/display-name
    render: (text: number) => {
      if (text === 0 || text === -1) return dimensionMap[text]?.label;
      return <Link to={`/${systemKey}/${clusterId}${dimensionMap[text]?.href}`}>{dimensionMap[text]?.label}</Link>;
    },
  },
  {
    title: '检查项',
    dataIndex: 'checkConfig',
    render(config: any, record: any) {
      const valueGroup = JSON.parse(config.value);
      if (record.configItem === 'Controller') {
        return '集群 Controller 数等于 1';
      } else if (record.configItem === 'RequestQueueSize') {
        return `Broker-RequestQueueSize 小于 ${valueGroup.value}`;
      } else if (record.configItem === 'NoLeader') {
        return `Topic 无 Leader 数小于 ${valueGroup.value}`;
      } else if (record.configItem === 'NetworkProcessorAvgIdlePercent') {
        return `Broker-NetworkProcessorAvgIdlePercent 的 idle 大于 ${valueGroup.value}%`;
      } else if (record.configItem === 'UnderReplicaTooLong') {
        return `Topic 小于 ${parseFloat(((valueGroup.detectedTimes / valueGroup.latestMinutes) * 100).toFixed(2))}% 周期处于未同步状态`;
      } else if (record.configItem === 'Group Re-Balance') {
        return `Consumer Group 小于 ${parseFloat(
          ((valueGroup.detectedTimes / valueGroup.latestMinutes) * 100).toFixed(2)
        )}% 周期处于 Re-balance 状态`;
      }

      return <></>;
    },
  },
  {
    title: '权重',
    dataIndex: 'weightPercent',
    width: 80,
    render(value: number) {
      return `${value}%`;
    },
  },
  {
    title: '得分',
    dataIndex: 'score',
    width: 60,
  },
  {
    title: '检查时间',
    width: 190,
    dataIndex: 'updateTime',
    render: (text: string) => {
      return moment(text).format(timeFormat);
    },
  },
  {
    title: '检查结果',
    dataIndex: 'passed',
    width: 280,
    // eslint-disable-next-line react/display-name
    render: (passed: boolean, record: any) => {
      if (passed) {
        return (
          <>
            <IconFont type="icon-zhengchang" />
            <span style={{ marginLeft: 4 }}>通过</span>
          </>
        );
      }
      return (
        <div style={{ display: 'flex', alignItems: 'center', width: '240px' }}>
          <IconFont type="icon-yichang" />
          <div style={{ marginLeft: 4, marginRight: 6, flexShrink: 0 }}>未通过</div>
          <TagsWithHide list={record.notPassedResNameList || []} expandTagContent="更多" />
        </div>
      );
    },
  },
];

export const getHealthySettingColumn = (form: any, data: any, clusterId: string) =>
  [
    {
      title: '检查模块',
      dataIndex: 'dimensionCode',
      // eslint-disable-next-line react/display-name
      render: (text: number) => {
        if (text === 0 || text === -1) return dimensionMap[text]?.label;
        return <Link to={`/${systemKey}/${clusterId}${dimensionMap[text]?.href}`}>{dimensionMap[text]?.label}</Link>;
      },
    },
    {
      title: '检查项',
      dataIndex: 'configItem',
      width: 200,
      needTooltip: true,
    },
    {
      title: '检查项描述',
      dataIndex: 'configDesc',
      width: 240,
      needToolTip: true,
    },
    {
      title: '权重',
      dataIndex: 'weight',
      // width: 180,
      // eslint-disable-next-line react/display-name
      render: (text: number, record: any) => {
        return (
          <>
            <Form.Item
              name={`weight_${record.configItemName}`}
              label=""
              rules={[
                {
                  required: true,
                  validator: async (rule: any, value: string) => {
                    const otherWeightCongigName: string[] = [];
                    let totalPercent = 0;
                    data.map((item: any) => {
                      if (item.configItemName !== record.configItemName) {
                        otherWeightCongigName.push(`weight_${item.configItemName}`);
                        totalPercent += form.getFieldValue(`weight_${item.configItemName}`) ?? 0;
                      }
                    });
                    if (!value) {
                      return Promise.reject('请输入权重');
                    }
                    if (+value < 0) {
                      return Promise.reject('最小为0');
                    }
                    if (+value + totalPercent !== 100) {
                      return Promise.reject('总和应为100%');
                    }
                    form.setFields(otherWeightCongigName.map((i) => ({ name: i, errors: [] })));
                    return Promise.resolve('');
                  },
                },
              ]}
            >
              <InputNumber
                size="small"
                min={0}
                max={100}
                formatter={(value) => `${value}%`}
                parser={(value: any) => value.replace('%', '')}
              />
            </Form.Item>
          </>
        );
      },
    },
    {
      title: '检查规则',
      // width: 350,
      dataIndex: 'passed',
      // eslint-disable-next-line react/display-name
      render: (text: any, record: any) => {
        const getFormItem = (params: { type?: string; percent?: boolean; attrs?: any; validator?: any }) => {
          const { validator, percent, type = 'value', attrs = { min: 0 } } = params;
          return (
            <Form.Item
              name={`${type}_${record.configItemName}`}
              label=""
              rules={
                validator
                  ? [
                      {
                        required: true,
                        validator: validator,
                      },
                    ]
                  : [
                      {
                        required: true,
                        message: '请输入',
                      },
                    ]
              }
            >
              {percent ? (
                <InputNumber
                  size="small"
                  min={0}
                  max={100}
                  style={{ width: 86 }}
                  formatter={(value) => `${value}%`}
                  parser={(value: any) => value.replace('%', '')}
                />
              ) : (
                <InputNumber style={{ width: 86 }} size="small" {...attrs} />
              )}
            </Form.Item>
          );
        };

        if (record.configItemName === 'Controller') {
          return <div className="table-form-item">≠ 1 则不通过</div>;
        }
        if (record.configItemName === 'RequestQueueSize' || record.configItemName === 'NoLeader') {
          return (
            <div className="table-form-item">
              <span className="left-text">≥</span>
              {getFormItem({ attrs: { min: 0, max: 99998 } })}
              <span className="right-text">则不通过</span>
            </div>
          );
        }
        if (record.configItemName === 'NetworkProcessorAvgIdlePercent') {
          return (
            <div className="table-form-item">
              <span className="left-text">≤</span>
              {getFormItem({ percent: true })}
              <span className="right-text">则不通过</span>
            </div>
          );
        }
        if (record.configItemName === 'UnderReplicaTooLong' || record.configItemName === 'ReBalance') {
          return (
            <div className="table-form-item">
              {getFormItem({ type: 'latestMinutes', attrs: { min: 1, max: 10080 } })}
              <span className="right-text left-text">周期内，≥</span>
              {getFormItem({
                type: 'detectedTimes',
                attrs: { min: 1, max: 10080 },
                validator: async (rule: any, value: string) => {
                  const latestMinutesValue = form.getFieldValue(`latestMinutes_${record.configItemName}`);

                  if (!value) {
                    return Promise.reject('请输入');
                  }
                  if (+value < 1) {
                    return Promise.reject('最小为1');
                  }
                  if (+value > +latestMinutesValue) {
                    return Promise.reject('值不能大于周期');
                  }
                  return Promise.resolve('');
                },
              })}
              <span className="right-text">则不通过</span>
            </div>
          );
        }

        return <></>;
      },
    },
  ] as any;
