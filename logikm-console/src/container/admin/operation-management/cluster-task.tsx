import * as React from 'react';

import { Table, Tabs, Button } from 'component/antd';
import { observer } from 'mobx-react';
import { ITaskManage, IEnumsMap, ITasksEnums } from 'types/base-type';
import { SearchAndFilterContainer } from 'container/search-filter';
import { tableFilter } from 'lib/utils';
import { pagination } from 'constants/table';
import { addMigrationTask } from 'container/modal';
import { admin } from 'store/admin';
import moment from 'moment';
import './index.less';
import { timeFormat } from 'constants/strategy';
import { region } from 'store';

@observer
export class ClusterTask extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterClusterVisible: false,
    filterStatusVisible: false,
    filterTaskVisible: false,
  };

  public renderColumns = (data: ITaskManage[]) => {
    const taskStatus = admin.configsTaskStatus ? admin.configsTaskStatus : [] as IEnumsMap[];
    const cluster = Object.assign({
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '15%',
      filters: tableFilter<any>(data, 'clusterName'),
      onFilter: (value: string, record: ITaskManage) => record.clusterName.indexOf(value) === 0,
    }, this.renderColumnsFilter('filterClusterVisible'));

    const status = Object.assign({
      title: '任务状态',
      dataIndex: 'status',
      key: 'status',
      width: '15%',
      filters: taskStatus.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: number, record: ITaskManage) => record.status === +value,
      render: (t: number) => {
        let messgae: string;
        taskStatus.map(ele => {
          if (ele.code === t) {
            messgae = ele.message;
          }
        });
        return (
          <span>{messgae}</span>
        );
      },
    }, this.renderColumnsFilter('filterStatusVisible'));

    const taskType = Object.assign({
      title: '任务类型',
      dataIndex: 'taskType',
      key: 'taskType',
      width: '15%',
      filters: admin.tasksEnums && admin.tasksEnums.map(ele => ({ text: ele.message, value: ele.name })),
      onFilter: (value: string, record: ITaskManage) => record.taskType === value,
      render: (text: string) => {
        const task = admin.tasksEnums && admin.tasksEnums.filter(ele => ele.name === text);
        return (<>{task && task[0].message}</>);
      },
    }, this.renderColumnsFilter('filterTaskVisible'));

    return [
      {
        title: '任务ID',
        dataIndex: 'taskId',
        key: 'taskId',
        width: '15%',
        sorter: (a: ITaskManage, b: ITaskManage) => b.taskId - a.taskId,
      },
      taskType,
      cluster,
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: '15%',
        sorter: (a: ITaskManage, b: ITaskManage) => b.createTime - a.createTime,
        render: (t: number) => moment(t).format(timeFormat),
      },
      {
        title: '操作人',
        dataIndex: 'operator',
        key: 'operator',
        width: '15%',
        sorter: (a: ITaskManage, b: ITaskManage) => a.operator.charCodeAt(0) - b.operator.charCodeAt(0),
      },
      status,
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: '10%',
        render: (text: string, record: ITaskManage) => {
          return (
            <span className="table-operation">
              <a href={`${this.urlPrefix}/admin/operation-detail?taskId=${record.taskId}&region=${region.currentRegion}`}>详情</a>
              <a href={`${this.urlPrefix}/admin/operation-detail?taskId=${record.taskId}&region=${region.currentRegion}#2`}>状态</a>
            </span>
          );
        },
      },
    ];
  }

  public getLabelValueData(data: any[]) {
    return data.map(item => {
      return {
        label: item,
        value: item,
      };
    });
  }

  public getPackages() {
    admin.packageList.map(item => {
      return {
        label: item,
        value: item,
      };
    });
  }

  public componentDidMount() {
    admin.getTaskManagement();
    admin.getMetaData(false);
    admin.getClusterTasksEnums();
    admin.getConfigsTaskStatus();
    admin.getConfigsKafkaRoles();
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入任务ID')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => addMigrationTask()}>新建集群任务</Button>
        </li>
      </ul>
    );
  }

  public render() {
    const { searchKey } = this.state;
    const taskManage: ITaskManage[] = admin.taskManagement && searchKey ?
      admin.taskManagement.filter((d: { taskId: number; }) => d.taskId === +searchKey) : admin.taskManagement;

    return (
      <>
        <div className="container">
          <div className="table-operation-panel">
            {this.renderOperationPanel()}
          </div>
          <div className="table-wrapper">
            <Table
              rowKey="taskId"
              columns={this.renderColumns(taskManage)}
              dataSource={taskManage}
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }
}
