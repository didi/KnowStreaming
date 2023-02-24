import moment from 'moment';
import React from 'react';
import { timeFormat } from '../../constants/common';
import TagsWithHide from '../../components/TagsWithHide/index';
import { Form, InputNumber, Tooltip } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { Link } from 'react-router-dom';
import { systemKey } from '../../constants/menu';

export const dimensionMap = {
  // '-1': {
  //   label: 'Unknown',
  //   href: ``,
  // },
  // 0: {
  //   label: 'Cluster',
  //   href: ``,
  // },
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
  4: {
    label: 'Zookeeper',
    href: '/zookeeper',
  },
  5: {
    label: 'Connect',
    href: '/connect',
  },
  6: {
    label: 'Connector',
    href: '/connect/connectors',
  },
  7: {
    label: 'MirrorMaker',
    href: '/replication',
  },
} as any;

const toLowerCase = (name = '') => {
  const [first, ...rest] = name.split('');
  return first.toUpperCase() + rest.join('').toLowerCase();
};

const CONFIG_ITEM_DETAIL_DESC = {
  Controller: () => {
    return '集群 Controller 数等于 1';
  },
  RequestQueueSize: (valueGroup: any) => {
    return `Broker-RequestQueueSize 小于 ${valueGroup?.value}`;
  },
  NoLeader: (valueGroup: any) => {
    return `Topic 无 Leader 数小于 ${valueGroup?.value}`;
  },
  NetworkProcessorAvgIdlePercent: (valueGroup: any) => {
    return `Broker-NetworkProcessorAvgIdlePercent 的 idle 大于 ${valueGroup?.value * 100}%`;
  },
  UnderReplicaTooLong: (valueGroup: any) => {
    return `Topic 小于 ${parseFloat(((valueGroup?.detectedTimes / valueGroup?.latestMinutes) * 100).toFixed(2))}% 周期处于未同步状态`;
  },
  'Group Re-Balance': (valueGroup: any) => {
    return `Consumer Group 小于 ${parseFloat(
      ((valueGroup?.detectedTimes / valueGroup?.latestMinutes) * 100).toFixed(2)
    )}% 周期处于 Re-balance 状态`;
  },
  BrainSplit: () => {
    return `Zookeeper 未脑裂`;
  },
  OutstandingRequests: (valueGroup: any) => {
    return `Zookeeper 请求堆积数小于 ${valueGroup?.ratio * 100}% 总容量`;
  },
  WatchCount: (valueGroup: any) => {
    return `Zookeeper 订阅数小于 ${valueGroup?.ratio * 100}% 总容量`;
  },
  AliveConnections: (valueGroup: any) => {
    return `Zookeeper 连接数小于 ${valueGroup?.ratio * 100}% 总容量`;
  },
  ApproximateDataSize: (valueGroup: any) => {
    return `Zookeeper 数据大小小于 ${valueGroup?.ratio * 100}% 总容量`;
  },
  SentRate: (valueGroup: any) => {
    return `Zookeeper 首发包数小于 ${valueGroup?.ratio * 100}% 总容量`;
  },
  TaskStartupFailurePercentage: (valueGroup: any) => {
    return `任务启动失败概率 小于 ${valueGroup?.value * 100}%`;
  },
  ConnectorFailedTaskCount: (valueGroup: any) => {
    return `失败状态的任务数量 小于 ${valueGroup?.value}`;
  },
  ConnectorUnassignedTaskCount: (valueGroup: any) => {
    return `未被分配的任务数量 小于 ${valueGroup?.value}`;
  },
  MirrorMakerFailedTaskCount: (valueGroup: any) => {
    return `失败状态的任务数量 小于 ${valueGroup?.value}`;
  },
  MirrorMakerUnassignedTaskCount: (valueGroup: any) => {
    return `未被分配的任务数量 小于 ${valueGroup?.value}`;
  },
  ReplicationLatencyMsMax: (valueGroup: any) => {
    return `消息复制最大延迟时间 小于 ${valueGroup?.value}`;
  },
  'TotalRecord-errors': (valueGroup: any) => {
    return `消息处理错误的次数 增量小于 ${valueGroup?.value}`;
  },
};

export const getConfigItemDetailDesc = (item: keyof typeof CONFIG_ITEM_DETAIL_DESC, valueGroup: any) => {
  return CONFIG_ITEM_DETAIL_DESC[item]?.(valueGroup);
};

const getFormItem = (params: { configItem: string; type?: string; percent?: boolean; attrs?: any; validator?: any }) => {
  const { validator, configItem, percent, type = 'value', attrs = { min: 0 } } = params;
  return (
    <Form.Item
      name={`${type}_${configItem}`}
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
          max={1}
          style={{ width: 86 }}
          formatter={(value) => `${value * 100}%`}
          parser={(value: any) => parseFloat(value.replace('%', '')) / 100}
        />
      ) : (
        <InputNumber style={{ width: 86 }} size="small" {...attrs} />
      )}
    </Form.Item>
  );
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
    render: (text: number, record: any) => {
      return dimensionMap[text] ? (
        <Link to={`/${systemKey}/${clusterId}${dimensionMap[text]?.href}`}>{record?.dimensionDisplayName}</Link>
      ) : (
        record?.dimensionDisplayName
      );
    },
  },
  {
    title: '检查项',
    dataIndex: 'checkConfig',
    render(config: any, record: any) {
      let valueGroup = {};
      try {
        valueGroup = JSON.parse(config.value);
      } catch (e) {
        //
      }
      return getConfigItemDetailDesc(record.configItem, valueGroup) || record.configDesc || '-';
    },
  },
  // {
  //   title: '权重',
  //   dataIndex: 'weightPercent',
  //   width: 80,
  //   render(value: number) {
  //     return `${value}%`;
  //   },
  // },
  // {
  //   title: '得分',
  //   dataIndex: 'score',
  //   width: 60,
  // },
  {
    title: '检查时间',
    width: 190,
    dataIndex: 'updateTime',
    render: (text: string) => {
      return text ? moment(text).format(timeFormat) : '-';
    },
  },
  {
    title: '检查结果',
    dataIndex: 'passed',
    width: 280,
    // eslint-disable-next-line react/display-name
    render: (passed: boolean, record: any) => {
      if (record?.updateTime) {
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
      } else {
        return '-';
      }
    },
  },
];

export const getHealthySettingColumn = (form: any, data: any, clusterId: string) =>
  [
    {
      title: '检查模块',
      dataIndex: 'dimensionCode',
      width: 140,
      // eslint-disable-next-line react/display-name
      render: (text: number, record: any) => {
        return dimensionMap[text] ? (
          <Link to={`/${systemKey}/${clusterId}${dimensionMap[text]?.href}`}>{record?.dimensionDisplayName}</Link>
        ) : (
          record?.dimensionDisplayName
        );
      },
    },
    {
      title: '检查项',
      dataIndex: 'configItem',
      width: 230,
      needTooltip: true,
    },
    {
      title: '检查项描述',
      dataIndex: 'configDesc',
      width: 310,
      needToolTip: true,
    },
    // {
    //   title: '权重',
    //   dataIndex: 'weight',
    //   // width: 180,
    //   // eslint-disable-next-line react/display-name
    //   render: (text: number, record: any) => {
    //     return (
    //       <>
    //         <Form.Item
    //           name={`weight_${record.configItemName}`}
    //           label=""
    //           rules={[
    //             {
    //               required: true,
    //               validator: async (rule: any, value: string) => {
    //                 const otherWeightCongigName: string[] = [];
    //                 let totalPercent = 0;
    //                 data.map((item: any) => {
    //                   if (item.configItemName !== record.configItemName) {
    //                     otherWeightCongigName.push(`weight_${item.configItemName}`);
    //                     totalPercent += form.getFieldValue(`weight_${item.configItemName}`) ?? 0;
    //                   }
    //                 });
    //                 if (!value) {
    //                   return Promise.reject('请输入权重');
    //                 }
    //                 if (+value < 0) {
    //                   return Promise.reject('最小为0');
    //                 }
    //                 if (+value + totalPercent !== 100) {
    //                   return Promise.reject('总和应为100%');
    //                 }
    //                 form.setFields(otherWeightCongigName.map((i) => ({ name: i, errors: [] })));
    //                 return Promise.resolve('');
    //               },
    //             },
    //           ]}
    //         >
    //           <InputNumber
    //             size="small"
    //             min={0}
    //             max={100}
    //             formatter={(value) => `${value}%`}
    //             parser={(value: any) => value.replace('%', '')}
    //           />
    //         </Form.Item>
    //       </>
    //     );
    //   },
    // },
    {
      title: '检查规则',
      // width: 350,
      dataIndex: 'passed',
      // eslint-disable-next-line react/display-name
      render: (text: any, record: any) => {
        const configItem = record.configItem;

        switch (configItem) {
          case 'Controller': {
            return <div className="table-form-item">≠ 1 则不通过</div>;
          }
          case 'BrainSplit': {
            return <div className="table-form-item">脑裂则不通过</div>;
          }
          case 'RequestQueueSize':
          case 'NoLeader': {
            return (
              <div className="table-form-item">
                <span className="left-text">≥</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'SentRate':
          case 'WatchCount':
          case 'AliveConnections':
          case 'ApproximateDataSize':
          case 'OutstandingRequests': {
            return (
              <div className="table-form-item">
                <span className="left-text">总容量指标</span>
                {getFormItem({ configItem, type: 'amount' })}
                <span className="left-text">, ≥</span>
                {getFormItem({ configItem, type: 'ratio', percent: true })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'NetworkProcessorAvgIdlePercent': {
            return (
              <div className="table-form-item">
                <span className="left-text">≤</span>
                {getFormItem({ configItem, percent: true })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'UnderReplicaTooLong':
          case 'Group Re-Balance': {
            return (
              <div className="table-form-item">
                {getFormItem({ type: 'latestMinutes', configItem, attrs: { min: 1, max: 10080 } })}
                <span className="right-text left-text">周期内，≥</span>
                {getFormItem({
                  type: 'detectedTimes',
                  configItem,
                  attrs: { min: 1, max: 10080 },
                  validator: async (rule: any, value: string) => {
                    const latestMinutesValue = form.getFieldValue(`latestMinutes_${configItem}`);

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
          case 'TaskStartupFailurePercentage': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, percent: true })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'ConnectorFailedTaskCount': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'ConnectorUnassignedTaskCount': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'MirrorMakerFailedTaskCount': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'MirrorMakerUnassignedTaskCount': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'ReplicationLatencyMsMax': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          case 'TotalRecord-errors': {
            return (
              <div className="table-form-item">
                <span className="left-text">{'>'}</span>
                {getFormItem({ configItem, attrs: { min: 0, max: 99998 } })}
                <span className="right-text">则不通过</span>
              </div>
            );
          }
          default: {
            return <></>;
          }
        }
      },
    },
  ] as any;
