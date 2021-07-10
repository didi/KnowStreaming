import * as React from 'react';
import { IReassignTasks, IEnumsMap } from 'types/base-type';
import { Table, Button } from 'component/antd';
import { expert } from 'store/expert';
import { pagination } from 'constants/table';
import { observer } from 'mobx-react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { createMigrationTasks } from 'container/modal';
import { admin } from 'store/admin';
import { migrationTaskColumns } from './config';
import './index.less';

@observer
export class MigrationTask extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterStatusVisible: false,
  };

  public getColumns = () => {
    const columns = migrationTaskColumns(window.location.href.includes('/expert') ? 'expert/hotspot-detail' : 'admin/migration-detail');
    const taskStatus = admin.configsTaskStatus as IEnumsMap[];
    const status = Object.assign({
      title: '任务状态',
      dataIndex: 'status',
      key: 'status',
      filters: taskStatus.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: number, record: IReassignTasks) => record.status === +value,
      render: (t: number) => {
        let message = '';
        taskStatus.forEach((ele: any) => {
          if (ele.code === t) {
            message = ele.message;
          }
        });
        let statusName = '';
        if (t === 100 || t === 101) {
          statusName = 'success';
        } else if (t === 40 || t === 99 || t === 102 || t === 103 || t === 104 || t === 105 || t === 106) {
          statusName = 'fail';
        }
        return <span className={statusName}>{message}</span>;
      },
    }, this.renderColumnsFilter('filterStatusVisible'));
    const col = columns.splice(4, 0, status);
    return columns;
  }

  public getMigrationTask() {
    return (
      <>
        <Table
          columns={this.getColumns()}
          dataSource={expert.reassignTasks}
          pagination={pagination}
        />
      </>
    );
  }

  public getData(data: IReassignTasks[]) {
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    const reassignTasks: IReassignTasks[] = data.filter(d =>
      (d.taskName !== undefined && d.taskName !== null) && d.taskName.toLowerCase().includes(searchKey as string));
    return reassignTasks;
  }

  public componentDidMount() {
    expert.getReassignTasks();
    if (!expert.metaData.length) {
      expert.getMetaData(false);
    }
    admin.getConfigsTaskStatus();
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入任务名称')}
        {location.pathname.includes('expert') ? null : <li className="right-btn-1">
          <Button type="primary" onClick={() => createMigrationTasks()}>新建迁移任务</Button>
        </li>}
      </ul>
    );
  }

  public render() {
    if (!admin.configsTaskStatus) {
      return null;
    }
    return (
      <>
        <div className="container">
          <div className="table-operation-panel">
            {this.renderOperationPanel()}
          </div>
          <div className="table-wrapper">
            <Table
              columns={this.getColumns()}
              dataSource={this.getData(expert.reassignTasks)}
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }
}
