import React from 'react';
import { observer } from 'mobx-react';
import { broker } from 'store/broker';
import urlQuery from 'store/url-query';
import echarts from 'echarts/lib/echarts';
import { getBrokerMetricOption } from 'lib/charts-config';
import { MetricChartList as list } from './constant';
import { Spin, DatePicker, notification, Button } from 'component/antd';
import './index.less';

// 引入柱状图
import 'echarts/lib/chart/line';
// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import moment from 'moment';

@observer
export class BrokerMetrics extends React.Component {
  public chartId: HTMLDivElement[] = [];
  public charts: echarts.ECharts[] = [];

  public state = {
    loading: true,
  };

  public renderCharts(startTime: moment.Moment, endTime: moment.Moment) {
    broker.getBrokerKeyMetrics(urlQuery.clusterId, urlQuery.brokerId,
      startTime.format('x'),
      endTime.format('x')).then(data => {
        getBrokerMetricOption(list, data).forEach((ele: object, index) => {
          if (ele) {
            this.charts[index] = echarts.init(this.chartId[index]);
            this.charts[index].setOption(ele);
            this.setState({ loading: false });
          }
          this.charts[index] = null;
        });
      });
  }

  public componentDidMount() {
    this.renderCharts(broker.startTime, broker.endTime);
  }

  public handleSearch = () => {
    const { startTime, endTime } = broker;
    if (startTime >= endTime) {
      notification.error({ message: '开始时间不能大于或等于结束时间' });
      return false;
    }
    this.setState({ loading: true });
    this.renderCharts(startTime, endTime);
  }

  public render() {
    const charts = list.map((item, index) => {
      if (!item.value) return <div className="emptyChart" key={index} ref={(id) => this.chartId.push(id)} />;
      return (
        <div key={index}>
          <p>{item.label}</p>
          <Spin spinning={this.state.loading}>
            <div style={{ height: 300, padding: '0px 10px' }} ref={(id) => this.chartId.push(id)} />
          </Spin>
        </div>
      );
    });
    return (
      <>
        <div className="status-graph">
          <ul className="k-toolbar topic-line-tool">
            <li>
              <span className="label">开始时间</span>
              <DatePicker showTime={true} value={broker.startTime} onChange={broker.changeStartTime} />
            </li>
            <li>
              <span className="label" >结束时间</span>
              <DatePicker showTime={true} value={broker.endTime} onChange={broker.changeEndTime} />
            </li>
            <li><Button type="primary" size="small" onClick={this.handleSearch}>查询</Button></li>
          </ul>
        </div>
        <div className="charts-container" >
          {charts}
        </div>
      </>
    );
  }
}
