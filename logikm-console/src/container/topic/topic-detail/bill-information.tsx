import * as React from 'react';
import Url from 'lib/url-parser';
import { DatePicker, notification, Icon } from 'component/antd';
import { BarChartComponet } from 'component/chart';
import { SearchAndFilterContainer } from 'container/search-filter';
import { observer } from 'mobx-react';
import { Moment } from 'moment';
import { topic } from 'store/topic';
import { timeMonth } from 'constants/strategy';
import './index.less';
const { RangePicker } = DatePicker;

import moment = require('moment');

@observer
export class BillInformation extends SearchAndFilterContainer {
  public clusterId: number;
  public topicName: string;

  public state = {
    mode: ['month', 'month'] as any,
    value: [moment(new Date()).subtract(6, 'months'), moment()] as any,
  };

  private startTime: number = moment(new Date()).subtract(6, 'months').valueOf();
  private endTime: number = moment().valueOf();
  private chart: any = null;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
  }

  public getData() {
    const { clusterId, topicName, startTime, endTime } = this;
    topic.setLoading(true);
    return topic.getBillInfo(clusterId, topicName, startTime, endTime);
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
              value={value}
              mode={mode}
              format={timeMonth}
              onChange={this.handlePanelChange}
              onPanelChange={this.handlePanelChange}
            />
          </span>
        </div>
      </>
    );
  }

  public disabledDateTime = (current: Moment) => {
    return current && current > moment().endOf('day');
  }

  public handleRefreshChart = () => {
    this.chart.handleRefreshChart();
  }

  public handlePanelChange = (value: any, mode: any) => {
    this.setState({
      value,
      mode: ['month', 'month'] as any,
    });

    this.startTime = value[0].valueOf();
    this.endTime = value[1].valueOf();

    if (this.startTime >= this.endTime) {
      return notification.error({ message: '开始时间不能大于或等于结束时间' });
    }
    this.getData();
    this.handleRefreshChart();
  }

  public renderChart() {
    return (
      <div className="chart-box">
        <BarChartComponet ref={(ref) => this.chart = ref} getChartData={this.getData.bind(this, null)} />
      </div>
    );
  }

  public render() {
    return (
      <>
        <div className="k-row" >
          <ul className="k-tab">
            <li>账单信息&nbsp;
            <a
                // tslint:disable-next-line:max-line-length
                href="https://github.com/didi/kafka-manager"
                target="_blank"
              >
                <Icon type="question-circle" />
              </a>
            </li>
            {this.renderDatePick()}
          </ul>
          {this.renderChart()}
        </div>
      </>
    );
  }
}
