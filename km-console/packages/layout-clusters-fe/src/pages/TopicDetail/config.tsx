import React from 'react';
import moment from 'moment';
import { timeFormat } from '../../constants/common';
import { message, Tooltip, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import ContentWithCopy from '@src/components/CopyContent';
const aclOperationType: any = {
  0: 'UNKNOWN',
  1: 'ANY',
  2: 'ALL',
  3: 'READ',
  4: 'WRITE',
  5: 'CREATE',
  6: 'DELETE',
  7: 'ALTER',
  8: 'DESCRIBE',
  9: 'CLUSTER_ACTION',
  10: 'DESCRIBE_CONFIGS',
  11: 'ALTER_CONFIGS',
  12: 'IDEMPOTENT_WRITE',
};

const aclPermissionType: any = {
  0: 'UNKNOWN',
  1: 'ANY',
  2: 'DENY',
  3: 'ALLOW',
};

const resourcePatternType: any = {
  0: 'UNKNOWN',
  1: 'ANY',
  2: 'MATCH',
  3: 'LITERAL',
  4: 'PREFIXED',
};

export const getTopicACLsColmns = () => {
  const columns = [
    {
      title: 'Principle',
      dataIndex: 'kafkaUser',
      key: 'kafkaUser',
    },
    {
      title: 'Operations',
      dataIndex: 'aclOperation',
      key: 'aclOperation',
      render: (t: number) => {
        return t || t === 0 ? aclOperationType[t] : '-';
      },
    },
    {
      title: 'Permission Type',
      dataIndex: 'aclPermissionType',
      key: 'aclPermissionType',
      render: (t: number) => {
        return t || t === 0 ? aclPermissionType[t] : '-';
      },
    },
    {
      title: 'Pattern Type',
      dataIndex: 'resourcePatternType',
      key: 'resourcePatternType',
      render: (t: number) => {
        return t || t === 0 ? resourcePatternType[t] : '-';
      },
    },
  ];
  return columns;
};

export const getTopicMessagesColmns = () => {
  const columns = [
    {
      title: 'Partition',
      dataIndex: 'partitionId',
      key: 'partitionId',
    },
    {
      title: 'Offset',
      dataIndex: 'offset',
      key: 'offset',
      sorter: true,
      render: (t: number) => (+t || +t === 0 ? t.toLocaleString() : '-'), // TODO: 千分位展示
    },
    {
      title: 'Timestamp',
      dataIndex: 'timestampUnitMs',
      key: 'timestampUnitMs',
      sorter: true,
      render: (t: number) => (t ? moment(t).format(timeFormat) + '.' + moment(t).millisecond() : '-'),
    },
    {
      title: 'Key',
      dataIndex: 'key',
      key: 'key',
      needTooltip: true,
      lineClampOne: true,
    },
    {
      title: 'Value',
      dataIndex: 'value',
      key: 'value',
      width: 280,
      render: (t: string) => {
        return t ? <ContentWithCopy content={t} /> : '-';
      },
    },
    {
      title: 'Header',
      dataIndex: 'headerList',
      key: 'headerList',
      needTooltip: true,
      lineClampTwo: true,
      width: 280,
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
    },
  ];
  return columns;
};

export const getTopicConfigurationColmns = (arg: any) => {
  const columns: any = [
    // {
    //   title: '',
    //   dataIndex: 'readOnly',
    //   key: 'readOnly',
    //   align: 'right',
    //   // eslint-disable-next-line react/display-name
    //   render: (t: string, r: any) => {
    //     return t ? (
    //       <Tooltip title="该配置无法修改" visible={r.name === arg?.readOnlyRecord?.name && arg?.readOnlyVisible}>
    //         <IconFont style={{ color: '#556EE6', fontSize: '16px' }} type="icon-suoding" />
    //       </Tooltip>
    //     ) : null;
    //   },
    //   width: 56,
    //   classsName: 'xxxxxxxx',
    // },
    {
      title: '配置名',
      dataIndex: 'name',
      key: 'name',
      width: 250,
      // eslint-disable-next-line react/display-name
      // render: (t: string, r: any) => {
      //   return r.readOnly ? <span>只读 {t}</span> : <span>{t}</span>;
      // },
    },
    {
      title: '描述',
      dataIndex: 'documentation',
      key: 'documentation',
      width: 300,
      lineClampTwo: true,
      needTooltip: true,
    },
    {
      title: '配置值',
      dataIndex: 'value',
      key: 'value',
      width: 300,
      lineClampTwo: true,
    },
  ];

  if (arg.allowEdit) {
    columns.push({
      title: '操作',
      dataIndex: 'option',
      key: 'option',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        return !r.readOnly ? <a onClick={() => arg.setEditOp(r)}>编辑</a> : '-';
      },
    });
  }

  return columns;
};
