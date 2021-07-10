import * as React from 'react';
import { Table, Button } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { observer } from 'mobx-react';
import { app } from 'store/app';
import { getAlarmColumns } from './add-alarm/config';
import { IMonitorStrategies } from 'types/base-type';
import { pagination } from 'constants/table';
import { urlPrefix } from 'constants/left-menu';
import { alarm } from 'store/alarm';
import 'styles/table-filter.less';
import { Link } from 'react-router-dom';

@observer
export class AlarmList extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public getData<T extends IMonitorStrategies>(origin: T[]) {
    let data: T[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (app.active !== '-1' || searchKey !== '') {
      data = origin.filter(d =>
        ((d.name !== undefined && d.name !== null) && d.name.toLowerCase().includes(searchKey as string)
          || ((d.operator !== undefined && d.operator !== null) && d.operator.toLowerCase().includes(searchKey as string)))
        && (app.active === '-1' || d.appId === (app.active + '')),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public renderTableList(data: IMonitorStrategies[]) {
    return (
      <Table
        rowKey="key"
        columns={getAlarmColumns(urlPrefix)}
        dataSource={data}
        pagination={pagination}
      />
    );
  }

  public renderTable() {
    return this.renderTableList(this.getData(alarm.monitorStrategies));
  }

  public renderOperationPanel() {
    return (
      <>
        {this.renderApp('应用：')}
        {this.renderSearch('名称：', '请输入告警规则或者操作人')}
        <li className="right-btn-1">
          <Button type="primary">
            <Link to={`/alarm/add`}>新增规则</Link>
          </Button>
        </li>
      </>
    );
  }

  public componentDidMount() {
    if (!alarm.monitorStrategies.length) {
      alarm.getMonitorStrategies();
    }
    if (!app.data.length) {
      app.getAppList();
    }
  }

  public render() {
    return (
      <div className="container">
        <div className="table-operation-panel">
          <ul>
            {this.renderOperationPanel()}
          </ul>
        </div>
        <div className="table-wrapper">
          {this.renderTable()}
        </div>
      </div>
    );
  }
}
