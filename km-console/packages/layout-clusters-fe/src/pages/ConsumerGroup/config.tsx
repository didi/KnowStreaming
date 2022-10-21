/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import React from 'react';
import { AppContainer } from 'knowdesign';
import TagsWithHide from '@src/components/TagsWithHide';
import { ClustersPermissionMap } from '../CommonConfig';

export const runningStatusEnum: any = {
  1: 'Doing',
  2: 'Prepare',
  3: 'Success',
  4: 'Failed',
  5: 'Canceled',
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
};

export const getGroupColumns = (arg?: any) => {
  const columns = [
    {
      title: 'ConsumerGroup',
      dataIndex: 'name',
      key: 'name',
      lineClampTwo: true,
      render: (v: any, r: any) => {
        return (
          <a
            onClick={() => {
              window.location.hash = `groupName=${v || ''}`;
            }}
          >
            {v}
          </a>
        );
      },
      width: 200,
    },
    {
      title: '消费的Topic',
      dataIndex: 'topicNameList',
      key: 'topicNameList',
      width: 200,
      render(t: any, r: any) {
        return t && t.length > 0 ? <TagsWithHide placement="bottom" list={t} expandTagContent={(num: any) => `共有${num}个`} /> : '-';
      },
    },
    {
      title: 'Status',
      dataIndex: 'state',
      key: 'state',
      width: 200,
    },
    {
      title: 'Member数',
      dataIndex: 'memberCount',
      key: 'memberCount',
      width: 200,
      render: (t: number) => (t ? t.toLocaleString() : '-'),
    },
  ];
  return columns;
};

export const getGtoupTopicColumns = (arg?: any) => {
  const [global] = AppContainer.useGlobalValue();
  const columns: any = [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      needTooltip: true,
      lineClampOne: true,
      width: 150,
    },
    {
      title: 'Status',
      dataIndex: 'state',
      key: 'state',
      width: 150,
    },
    {
      title: 'Max Lag',
      dataIndex: 'maxLag',
      key: 'maxLag',
      width: 150,
      render: (t: number) => (t ? t.toLocaleString() : '-'),
    },
    {
      title: 'Member数',
      dataIndex: 'memberCount',
      key: 'memberCount',
      width: 150,
      render: (t: number) => (t ? t.toLocaleString() : '-'),
    },
  ];
  if (global.hasPermission && global.hasPermission(ClustersPermissionMap.CONSUMERS_RESET_OFFSET)) {
    columns.push({
      title: '操作',
      dataIndex: 'desc',
      key: 'desc',
      width: 150,
      render: (value: any, record: any) => {
        return (
          <div>
            <a onClick={() => arg.resetOffset(record)}>重置Offset</a>
          </div>
        );
      },
    });
  }
  return columns;
};
