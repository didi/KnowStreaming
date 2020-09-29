import * as React from 'react';
import { Table, Button } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import moment from 'moment';
import { alarm } from 'store/alarm';
import Url from 'lib/url-parser';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IMonitorAlerts } from 'types/base-type';
import './index.less';
import { observer } from 'mobx-react';
import { timeFormat } from 'constants/strategy';

@observer
export class AlarmHistory extends SearchAndFilterContainer {
  public id: number = null;
  public startTime: any = moment().subtract(3, 'day').format('x');
  public endTime: any = moment().endOf('day').format('x');

  public state = {
    filterStatus: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.id = Number(url.search.id);
  }

  public historyCreateTime(value?: number) {
    this.startTime = value ? moment().subtract(7, 'day').format('x') : moment().subtract(3, 'day').format('x');
    this.endTime = moment().format('x');
    alarm.getMonitorAlerts(this.id, this.startTime, this.endTime);
  }

  public historySelect() {
    return(
      <>
      <div className="alarm-history-day">
        <Button onClick={() => this.historyCreateTime()}>近三天</Button>
        <Button onClick={() => this.historyCreateTime(7)}>近一周</Button>
      </div>
      </>
    );
  }

  public historyTable() {
    const monitorAlerts: IMonitorAlerts[] = alarm.monitorAlerts ? alarm.monitorAlerts : [];
    const alertStatus = Object.assign({
      title: '状态',
      dataIndex: 'alertStatus',
      key: 'alertStatus',
      filters: [{ text: '故障', value: '0' }, { text: '已恢复', value: '1' }],
      onFilter: (value: string, record: IMonitorAlerts) => record.alertStatus === Number(value),
      render: (t: number) => t === 0 ? '故障' : '已恢复',
    }, this.renderColumnsFilter('filterStatus'));

    const columns = [
      {
        title: '监控名称',
        dataIndex: 'monitorName',
        key: 'monitorName',
        render: (text: string, record: IMonitorAlerts) => (
          <a href={`${urlPrefix}/alarm/history-detail?alertId=${record.alertId}`}> {text} </a>),
      },
      {
        title: '开始时间',
        dataIndex: 'startTime',
        key: 'startTime',
        render: (time: number) => moment(time).format(timeFormat),
      },
      {
        title: '结束时间',
        dataIndex: 'endTime',
        key: 'endTime',
        render: (time: number) => moment(time).format(timeFormat),
      },
      alertStatus,
      {
        title: '监控级别',
        dataIndex: 'monitorPriority',
        key: 'monitorPriority',
      },
    ];
    return (
      <>
        <Table rowKey="key" dataSource={monitorAlerts} columns={columns} loading={alarm.loading}/>
      </>
    );
  }

  public componentDidMount() {
    alarm.getMonitorAlerts(this.id, this.startTime, this.endTime);
  }

  public render() {
    return(
      <>
      {this.historySelect()}
      {this.historyTable()}
      </>
    );
  }
}
