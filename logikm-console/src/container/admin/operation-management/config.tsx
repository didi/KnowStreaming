
import * as React from 'react';
import { IReassignTasks } from 'types/base-type';
import { Popconfirm } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { startMigrationTask, modifyMigrationTask, cancelMigrationTask } from 'container/modal';
import moment = require('moment');
import { timeFormat } from 'constants/strategy';

export const migrationTaskColumns = (migrationUrl: string) => {
  const columns = [{
    title: '迁移任务名称',
    dataIndex: 'taskName',
    render: (text: string, item: IReassignTasks) =>
    <a href={`${urlPrefix}/${migrationUrl}?taskId=${item.taskId}`}>{text}</a>,
  },
  {
    title: '创建时间',
    dataIndex: 'gmtCreate',
    render: (t: number) => moment(t).format(timeFormat),
  },
  {
    title: '创建人',
    dataIndex: 'operator',
  },
  {
    title: 'Topic数量',
    dataIndex: 'totalTopicNum',
  },
  {
    title: '进度',
    dataIndex: 'completedTopicNum',
    render: (value: number, item: IReassignTasks) => <span>{item.completedTopicNum}/{item.totalTopicNum}</span>,
  },
  {
    title: '操作',
    dataIndex: 'action',
    render: (value: string, item: IReassignTasks) => (
      <>
        {item.status === 0 &&
          <Popconfirm
            title="确定开始？"
            onConfirm={() => startMigrationTask(item, 'start')}
            cancelText="取消"
            okText="确认"
          >
            <a style={{ marginRight: 16 }}>开始</a>
          </Popconfirm>}
        {[0, 1].indexOf(item.status) > -1 &&
          <a onClick={() => modifyMigrationTask(item, 'modify')} style={{ marginRight: 16 }}>编辑</a>}
        {item.status === 0 &&
          <Popconfirm
            title="确定取消？"
            cancelText="取消"
            okText="确认"
            onConfirm={() => cancelMigrationTask(item, 'cancel')}
          ><a>取消</a>
          </Popconfirm>}
      </>
    ),
  }];
  return columns;
};
