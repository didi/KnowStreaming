import * as React from 'react';
import { Table, Tabs, DatePicker, notification, Icon } from 'component/antd';
import { pagination } from 'constants/table';
import moment, { Moment } from 'moment';
import { BarChartComponet } from 'component/chart';
import { observer } from 'mobx-react';
import { getBillListColumns } from '../user-center/config';
import { urlPrefix } from 'constants/left-menu';
import { timeMonthStr } from 'constants/strategy';
import { users } from 'store/users';
import { admin } from 'store/admin';
import * as XLSX from 'xlsx';

const { TabPane } = Tabs;
const { RangePicker } = DatePicker;

@observer
export class IndividualBill extends React.Component {
  public username: string;

  public state = {
    mode: ['month', 'month'] as any,
    value: [moment(new Date()).subtract(6, 'months'), moment()] as any,
  };

  private startTime: number = moment(new Date()).subtract(6, 'months').valueOf();
  private endTime: number = moment().valueOf();
  private chart: any = null;

  public getData() {
    const { startTime, endTime } = this;
    return admin.getBillStaffList(this.username, startTime, endTime);
  }

  public handleDownLoad() {
    const tableData = admin.billStaff.map(item => {
      return {
        // tslint:disable
        '月份': item.gmtMonth,
        'Topic数量': item.topicNum,
        'quota数量': item.quota,
        '金额': item.cost,
      };
    });
    const data = [].concat(tableData);
    const wb = XLSX.utils.book_new();
    // json转sheet
    const ws = XLSX.utils.json_to_sheet(data, {
      header: ['月份', 'Topic数量', 'quota数量', '金额'],
    });
    // XLSX.utils.
    XLSX.utils.book_append_sheet(wb, ws, 'bill');
    // 输出
    XLSX.writeFile(wb, 'bill' + '.xlsx');
  }

  public disabledDateTime = (current: Moment) => {
    return current && current > moment().endOf('day');
  }

  public handleChartSearch = (date: moment.Moment[]) => {
    this.setState({
      value: date,
      mode: ['month', 'month'] as any,
    });

    this.startTime = date[0].valueOf();
    this.endTime = date[1].valueOf();

    if (this.startTime >= this.endTime) {
      return notification.error({ message: '开始时间不能大于或等于结束时间' });
    }
    this.getData();
    this.handleRefreshChart();
  }

  public handleRefreshChart = () => {
    this.chart.handleRefreshChart();
  }

  public renderTableList() {
    const adminUrl = `${urlPrefix}/admin/bill-detail`
    return (
      <Table
        rowKey="key"
        columns={getBillListColumns(adminUrl)}
        dataSource={admin.billStaff}
        pagination={pagination}
      />
    );
  }

  public renderChart() {
    return (
      <div className="chart-box">
        <BarChartComponet ref={(ref) => this.chart = ref} getChartData={this.getData.bind(this, null)} />
      </div>
    );
  }

  public renderDatePick() {
    const { value, mode } = this.state;

    return (
      <>
        <div className="op-panel">
          <span>
            <RangePicker
              ranges={{
                近半年: [moment(new Date()).subtract(6, 'months'), moment()],
                近一年: [moment().startOf('year'), moment().endOf('year')],
              }}
              defaultValue={[moment(new Date()).subtract(6, 'months'), moment()]}
              value={value}
              mode={mode}
              format={timeMonthStr}
              onChange={this.handleChartSearch}
              onPanelChange={this.handleChartSearch}
            />
          </span>
          <span>
            <Icon type="download" onClick={this.handleDownLoad.bind(this, null)} />
          </span>
        </div>
      </>
    );
  }

  public render() {
    this.username = users.currentUser.username;
    return (
      <>
        <div className="container">
          <Tabs defaultActiveKey="1" type="card">
            <TabPane
              tab={<>
                <span>账单趋势</span>&nbsp;
                <a
                  // tslint:disable-next-line:max-line-length
                  href="https://github.com/didi/kafka-manager"
                  target="_blank"
                >
                  <Icon type="question-circle" />
                </a>
              </>}
              key="1"
            >
              {this.renderDatePick()}
              {this.username ? this.renderChart() : null}
            </TabPane>
          </Tabs>
          <div className="table-content">
            {this.renderTableList()}
          </div>
        </div>
      </>
    );
  }
}
