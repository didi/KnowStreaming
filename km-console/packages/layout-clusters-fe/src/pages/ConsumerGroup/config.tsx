/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import React from 'react';
import { AppContainer, Button, Popconfirm } from 'knowdesign';
import TagsWithHide from '@src/components/TagsWithHide';
import { ClustersPermissionMap } from '../CommonConfig';
import Delete from './Delete';

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
  const [global] = AppContainer.useGlobalValue();
  const columns: any = [
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
  if (global.hasPermission && global.hasPermission(ClustersPermissionMap.GROUP_DELETE)) {
    columns.push({
      title: '操作',
      dataIndex: 'options',
      key: 'options',
      width: 200,
      filterTitle: true,
      fixed: 'right',
      render: (_t: any, r: any) => {
        return (
          <div>
            <Delete record={r} onConfirm={arg?.deleteTesk}></Delete>
          </div>
        );
      },
    });
  }
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
  if (global.hasPermission) {
    columns.push({
      title: '操作',
      dataIndex: 'desc',
      key: 'desc',
      width: 200,
      render: (value: any, record: any) => {
        return (
          <div>
            {global.hasPermission(ClustersPermissionMap.CONSUMERS_RESET_OFFSET) ? (
              <a onClick={() => arg.resetOffset(record)}>重置Offset</a>
            ) : (
              <></>
            )}
            {global.hasPermission(ClustersPermissionMap.GROUP_TOPIC_DELETE) ? (
              <Popconfirm
                placement="top"
                title={`是否要删除当前Topic？`}
                onConfirm={() => arg.deleteOffset(record)}
                okText="是"
                cancelText="否"
              >
                <Button type="link">删除</Button>
              </Popconfirm>
            ) : (
              <></>
            )}
          </div>
        );
      },
    });
  }
  return columns;
};
