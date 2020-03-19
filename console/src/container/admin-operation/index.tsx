import * as React from 'react';

import { Table, Tabs, Modal, notification } from 'component/antd';
import { PaginationConfig } from 'antd/es/table/interface';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { operation, ITask, taskMap } from 'store/operation';
import { cluster } from 'store/cluster';
import moment from 'moment';
import { modifyTask } from 'lib/api';
import { SearchAndFilter } from 'container/cluster-topic';
import { tableFilter } from 'lib/utils';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class AdminOperation extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterClusterVisible: false,
    filterStatusVisible: false,
  };

  public renderColumns = (data: ITask[]) => {
    const cluster = Object.assign({
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterId',
      filters: tableFilter<ITask>(data, 'clusterName'),
      onFilter: (value: string, record: ITask) => record.clusterName.indexOf(value) === 0,
    }, this.renderColumnsFilter('filterClusterVisible'));

    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      filters: taskMap.map((ele, index) => ({ text: ele, value: index + '' })),
      onFilter: (value: string, record: ITask) => record.status === +value,
      render: (t: number) => <span className={t === 2 || t === 1 ? 'success' : t === 3 ? 'fail' : ''}>{taskMap[t]}</span>,
    }, this.renderColumnsFilter('filterStatusVisible'));

    return [
      {
        title: '任务id',
        dataIndex: 'taskId',
        key: 'taskId',
        sorter: (a: ITask, b: ITask) => a.taskId - b.taskId,
      },
      cluster,
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        sorter: (a: ITask, b: ITask) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      },
      {
        title: '创建人',
        dataIndex: 'operator',
        key: 'operator',
        sorter: (a: ITask, b: ITask) => a.operator.charCodeAt(0) - b.operator.charCodeAt(0),
      },
      {
        title: '创建时间',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        sorter: (a: ITask, b: ITask) => a.gmtCreate - b.gmtCreate,
        render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
      },
      status,
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: 200,
        render: (text: string, record: ITask) => {
          const status: number = record.status;
          return (
            <span className="table-operation">
              <a onClick={modal.showTask.bind(null, record, 'detail')}>详情</a>
              {+status === 0 || +status === 1 ? <a onClick={modal.showTask.bind(null, record)}>修改</a> : null}
              {!status ? <a onClick={this.handleAction.bind(null, record.taskId, 'start')}>执行</a> : null}
              {!status ? <a onClick={this.handleAction.bind(null, record.taskId, 'cancel')}>撤销</a> : null}
            </span>
          );
        },
      },
    ];
  }

  public handleAction(taskId: number, type: string) {
    Modal.confirm({
      title: `确认${type === 'start' ? '执行Topic迁移任务' : '撤销任务' + taskId}`,
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        modifyTask({ action: type, taskId }).then(() => {
          notification.success({ message: `${type === 'start' ? '执行' : '撤销'}任务成功` });
          operation.getTask();
        });
      },
    });
  }

  public componentDidMount() {
    operation.getTask();
    cluster.getClusters();
  }

  public render() {
    const { searchKey } = this.state;
    const data: ITask[] = operation.tasks && searchKey ? operation.tasks.filter((d) => d.taskId === +searchKey) : operation.tasks;
    return (
      <>
        <ul className="table-operation-bar">
          <li className="new-topic" onClick={modal.showTask.bind(null, null)}>
            <i className="k-icon-xinjian didi-theme" />新建迁移任务
          </li>
          {this.renderSearch('请输入关键字')}
        </ul>
        <Tabs type="card">
          <Tabs.TabPane tab="迁移任务" key="0">
            <Table
              rowKey="taskId"
              columns={this.renderColumns(data)}
              dataSource={data}
              pagination={pagination}
            />
          </Tabs.TabPane>
        </Tabs>
      </>
    );
  }
}
