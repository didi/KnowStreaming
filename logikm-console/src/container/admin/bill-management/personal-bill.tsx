import * as React from 'react';
import { Table, DatePicker } from 'antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { admin } from 'store/admin';
import { Moment } from 'moment';
import { observer } from 'mobx-react';
import { IStaffSummary } from 'types/base-type';
import { pagination } from 'constants/table';

import './index.less';
const { MonthPicker } = DatePicker;

import moment = require('moment');
import { timeFormat } from 'constants/strategy';

@observer
export class PersonalBill extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public handleTimeChange = (value: Moment) => {
    const timestamp = value.valueOf();
    admin.getStaffSummary(timestamp);
  }

  public selectTime() {
    return (
      <>
        <div className="zoning-otspots">
          <div>
            <span>选择月份：</span>
            <MonthPicker
              placeholder="Select month"
              defaultValue={moment()}
              onChange={this.handleTimeChange}
            />
          </div>
        </div>
      </>
    );
  }

  public getData<T extends IStaffSummary>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IStaffSummary) =>
      (item.username !== undefined && item.username !== null) && item.username.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public pendingTopic() {
    const columns = [
      {
        title: '月份',
        dataIndex: 'gmtMonth',
        width: '15%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => b.timestamp - a.timestamp,
        render: (text: string, record: IStaffSummary) => (
          <a href={`${this.urlPrefix}/admin/bill-individual`}> {text} </a>),
      },
      {
        title: '用户名',
        dataIndex: 'username',
        width: '20%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => a.username.charCodeAt(0) - b.username.charCodeAt(0),
      },
      {
        title: 'Topic数量',
        dataIndex: 'topicNum',
        width: '15%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => b.topicNum - a.topicNum,
      },
      {
        title: '时间',
        dataIndex: 'timestamp',
        width: '20%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => b.timestamp - a.timestamp,
        render: (t: number) => moment(t).format(timeFormat),
      },
      {
        title: 'Quota(M/S)',
        dataIndex: 'quota',
        width: '15%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => b.quota - a.quota,
        render: (t: number) => t === null ? '' : Number.isInteger(t) ? t : (t).toFixed(2),
      },
      {
        title: '金额',
        dataIndex: 'cost',
        width: '15%',
        sorter: (a: IStaffSummary, b: IStaffSummary) => b.cost - a.cost,
        render: (t: number) => t === null ? '' : Number.isInteger(t) ? t : (t).toFixed(2),
      },
    ];

    return (
      <>
        <ul className="bill-head">
          {this.renderSearch('名称：', '请输入用户名')}
          {this.selectTime()}
        </ul>
        <Table
          columns={columns}
          dataSource={this.getData(admin.staffSummary)}
          pagination={pagination}
          rowKey="key"
        />
      </>
    );
  }

  public componentDidMount() {
    const timestamp = +moment().format('x');
    admin.getStaffSummary(timestamp);
  }

  public render() {
    return(
      <>
        {admin.staffSummary ? this.pendingTopic() : null}
      </>
    );
  }
}
