import * as React from 'react';
import { Select, Spin, Tooltip } from 'component/antd';
import { topic, IAppsIdInfo } from 'store/topic';
import { ILabelValue } from 'types/base-type';
import { observer } from 'mobx-react';
import { selectOptionMap } from 'constants/status-map';
import { ChartWithDatePicker } from 'component/chart';
import { searchProps } from 'constants/table';
import moment from 'moment';

@observer
export class NetWorkFlow extends React.Component<any> {

  public state = {
    loading: false,
  };
  private $chart: any;

  public getChartData = (startTime: moment.Moment, endTime: moment.Moment) => {
    const { getApi } = this.props;
    topic.changeStartTime(startTime);
    topic.changeEndTime(endTime);
    return getApi();
  }

  public handleChange = (value: any) => {
    const { selectChange } = this.props;
    this.$chart.changeChartOptions(selectChange(value));
  }

  public handleAppChange = (value: string) => {
    const { clusterId, topicName } = this.props;
    topic.appId = value;
    this.setState({ loading: true });
    topic.getMetriceInfo(clusterId, topicName).then(data => {
      this.$chart.changeChartOptions(data);
      this.setState({ loading: false });
    });
  }

  public renderSelect() {
    const isTrue = this.props.selectArr === selectOptionMap;
    return (
      <>
        <li>
          <span className="label">统计类型</span>
          <Select
            defaultValue={this.props.type}
            style={{ width: 230 }}
            onChange={this.handleChange}
            {...searchProps}
          >
            {this.props.selectArr.map((item: ILabelValue) => (
              <Select.Option key={item.value} value={item.value}>
                {item.label.length > 16 ?
                  <Tooltip placement="bottomLeft" title={item.label}>{item.label}</Tooltip>
                  : item.label}
              </Select.Option>
            ))}
          </Select>
        </li>
        <li className={!isTrue ? 'is-show' : ''}>
          <span className="label">应用</span>
          <Select
            placeholder="请选择应用"
            style={{ width: 180 }}
            onChange={this.handleAppChange}
            {...searchProps}
          >
            {topic.appInfo.map((item: IAppsIdInfo) => (
              <Select.Option key={item.appId} value={item.appId}>
                {item.appName && item.appName.length > 16 ?
                  <Tooltip placement="bottomLeft" title={item.appName}> {item.appName} </Tooltip>
                  : item.appName || ''}
              </Select.Option>
            ))}
          </Select>
        </li>
      </>
    );
  }

  public componentDidMount() {
    topic.initTime();
  }

  public render() {
    return (
      <Spin spinning={this.state.loading} className="chart-content">
        <ChartWithDatePicker
          customerNode={this.renderSelect()}
          getChartData={this.getChartData}
          ref={chart => this.$chart = chart}
        />
      </Spin>
    );
  }
}
