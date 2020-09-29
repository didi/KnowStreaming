import * as React from 'react';
import { createMonitorSilences } from 'container/modal';
import { IMonitorAlert, IMonitorMetric } from 'types/base-type';
import { Divider, Table, Button, PageHeader, Spin, Tooltip } from 'component/antd';
import { alarm } from 'store/alarm';
import { observer } from 'mobx-react';
import { handlePageBack } from 'lib/utils';
import LineChart, { hasData } from 'component/chart/line-chart';
import { EChartOption } from 'echarts';
import { timeFormat } from 'constants/strategy';
import Url from 'lib/url-parser';
import moment = require('moment');
import './index.less';

@observer
export class HistoryDetail extends React.Component {
  public alertId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.alertId = Number(url.search.alertId);
  }

  public componentDidMount() {
    alarm.getAlertsDetail(this.alertId);
  }

  public getChartOption = () => {
    return alarm.getMetircHistoryChartOptions();
  }

  public renderNoData = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="noData">暂无数据</div>;
  }

  public renderLoading = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="loading"><Spin /></div>;
  }

  public renderEchart = (options: EChartOption, loading = false) => {
    const data = hasData(options);
    if (loading) return this.renderLoading(400);
    if (!data) return this.renderNoData(400);
    return (
      <div className="chart">
        <LineChart height={400} options={options} key="chart" />
      </div>);
  }

  public renderHistoricalTraffic(metric: IMonitorMetric) {
    const option = this.getChartOption() as EChartOption;

    return (
      <>
        <div className="history-left">
          <div className="chart-box-0">
            <div className="chart-title metric-head">
              <span>{metric.metric}</span>
            </div>
            <Divider />
            {this.renderEchart(option)}
          </div>
        </div>
      </>
    );
  }

  public renderAlarmEventDetails(alert: IMonitorAlert) {
    const pointsColumns = [
      {
        title: 'timestamp',
        dataIndex: 'timestamp',
        key: 'timestamp',
        render: (t: number) => moment(t * 1000).format(timeFormat),
      },
      {
        title: 'value',
        dataIndex: 'value',
        key: 'value',
      }];
    return (
      <>
        <div className="history-right">
          <div className="history-right-header">
            <h2>报警事件详情</h2>
            <Button onClick={() => { createMonitorSilences(alert.monitorId, alert.monitorName); }}>快速屏蔽</Button>
          </div>
          <Divider className="history-right-divider" />
          <ul>
            <li><b>监控名称：</b>{alert.monitorName}</li>
            <li><b>告警状态：</b>{alert.alertStatus === 0 ? '故障' : '已恢复'}</li>
            <li><b>告警组：</b>{alert.groups ? alert.groups.join('、') : null}</li>
            <li><b>告警指标：</b>{alert.metric}</li>
            <li><b>告警开始时间：</b>{moment(alert.startTime).format(timeFormat)}</li>
            <li><b>告警结束时间：</b>{moment(alert.endTime).format(timeFormat)}</li>
            <li><b>监控级别：</b>{alert.monitorPriority}级告警</li>
            <li><b>触发值：</b>{alert.value}</li>
            <li>
              <b>表达式：</b>
              <Tooltip placement="bottomLeft" title={alert.info} >
                  {alert.info}
              </Tooltip>
            </li>
          </ul>
          <h4>现场值：</h4>
          <Table
            rowKey="timestamp"
            dataSource={alert.points}
            columns={pointsColumns}
            showHeader={false}
            pagination={false}
            bordered={true}
            scroll={{ y: 260 }}
          />
        </div>
      </>
    );
  }

  public render() {
    return (
      <>
        {alarm.alertsDetail &&
          <>
            <PageHeader
              className="detail topic-detail-header"
              onBack={() => handlePageBack('/alarm')}
              title={`${alarm.monitorAlert.monitorName || ''}`}
            />
            <div className="alarm-history">
              {this.renderHistoricalTraffic(alarm.monitorMetric)}
              {this.renderAlarmEventDetails(alarm.monitorAlert)}
            </div>
          </>}
      </>
    );
  }
}
