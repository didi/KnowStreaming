import React from 'react';
import { Utils, Tooltip } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
export const getConfigurationColmns = (arg: any) => {
  const columns: any = [
    {
      title: '',
      dataIndex: 'readOnly',
      key: 'readOnly',
      align: 'right',
      // eslint-disable-next-line react/display-name
      render: (t: string, r: any) => {
        return t ? (
          <Tooltip title="该配置无法修改" visible={r.name === arg?.readOnlyRecord?.name && arg?.readOnlyVisible}>
            <IconFont style={{ color: '#556EE6', fontSize: '16px' }} type="icon-suoding" />
          </Tooltip>
        ) : null;
      },
      width: 56,
      className: 'table-suoding',
    },
    {
      title: '配置名',
      dataIndex: 'name',
      key: 'name',
      width: 250,
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
      width: 250,
      lineClampTwo: true,
    },
    {
      title: '状态',
      dataIndex: 'state',
      key: 'state',
      // eslint-disable-next-line react/display-name
      render: (t: string, r: any) => {
        return r.differentiated ? (
          <div className="differ"></div>
        ) : r.exclusive ? (
          <div className="unique"></div>
        ) : (
          <div className="normal"></div>
        );
      },
    },
  ];

  if (arg.allowEdit) {
    columns.push({
      title: '操作',
      dataIndex: 'options',
      key: 'options',
      // eslint-disable-next-line react/display-name
      render: (_t: any, r: any) => {
        return !r.readOnly ? <a onClick={() => arg.setEditOp(r)}>编辑</a> : '-';
      },
    });
  }

  return columns;
};

export const getDataLogsColmns = () => {
  const columns = [
    {
      title: 'Folder',
      dataIndex: 'dir',
      key: 'dir',
    },
    {
      title: 'Topic',
      dataIndex: 'topicName',
      key: 'topicName',
    },
    {
      title: 'Partition',
      dataIndex: 'partitionId',
      key: 'partitionId',
    },
    {
      title: 'Offset Lag',
      dataIndex: 'offsetLag',
      key: 'offsetLag',
    },
    {
      title: 'Size（MB）',
      dataIndex: 'logSizeUnitB',
      key: 'logSizeUnitB',
      render: (t: number, r: any) => {
        return t || t === 0 ? Utils.transBToMB(t) : '-';
      },
    },
  ];
  return columns;
};
