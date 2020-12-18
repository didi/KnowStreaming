import * as React from 'react';
import { Select, Tooltip, Divider, Spin } from 'component/antd';
import { ILabelValue } from 'types/base-type';
import { observer } from 'mobx-react';
import moment from 'moment';
import { ChartWithDatePicker } from 'component/chart';
import { timestore } from 'store/time';
import { indexUrl } from 'constants/strategy';
import { searchProps } from 'constants/table';

@observer
export class NetWorkFlow extends React.Component<any> {
  private $chart: any;

  public componentDidMount() {
    timestore.initTime();
  }

  public getChartData(startTime: moment.Moment, endTime: moment.Moment) {
    const { getApi } = this.props;
    timestore.changeStartTime(startTime);
    timestore.changeEndTime(endTime);
    return getApi();
  }

  public handleChange = (value: any) => {
    const { selectChange } = this.props;
    this.$chart.changeChartOptions(selectChange(value));
  }

  public renderSelect() {
    return (
      <>
        <span className="label">统计类型</span>
        <Select
          defaultValue={this.props.type}
          onChange={this.handleChange}
          style={{ width: 230 }}
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
      </>
    );
  }

  public render() {
    return (
      <div className="chart-box-0">
        <div className="chart-title">
          <span className="action-button">历史流量</span>
          <a href={indexUrl.indexUrl} target="_blank">指标说明</a>
        </div>
        <Divider />
        <ChartWithDatePicker
          customerNode={this.renderSelect()}
          getChartData={(startTime: moment.Moment, endTime: moment.Moment) => this.getChartData(startTime, endTime)}
          ref={chart => this.$chart = chart}
        />
      </div>
    );
  }
}

export const renderTrafficTable = (updateRealStatus: any, Element: React.ComponentClass) => {
  return (
    <div className="traffic-table">
      <div className="traffic-header">
        <span>
          <span className="action-button">实时流量</span>
          <a href={indexUrl.indexUrl} target="_blank">指标说明</a>
        </span>
        <span className="k-abs" onClick={updateRealStatus}>
          <i className="k-icon-shuaxin didi-theme mr-5" />
          <a>刷新</a>
        </span>
      </div>
      <Element />
    </div>
  );
};
