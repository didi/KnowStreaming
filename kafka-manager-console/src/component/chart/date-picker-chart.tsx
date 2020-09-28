import * as React from 'react';
import { DatePicker, notification, Spin } from 'component/antd';
import moment, { Moment } from 'moment';
import { timeStampStr } from 'constants/strategy';
import { disabledDate } from 'lib/utils';
import echarts from 'echarts';

// 引入柱状图和折线图
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/line';

// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import './index.less';

const { RangePicker } = DatePicker;

interface IChartProps {
  getChartData: (startTime: moment.Moment, endTime: moment.Moment) => any;
  customerNode?: React.ReactNode;
}

export class ChartWithDatePicker extends React.Component<IChartProps> {
  public state = {
    startTime: moment().subtract(1, 'hour'),
    endTime: moment(),
    loading: false,
    noData: false,
  };

  public id: HTMLDivElement = null;
  public chart: echarts.ECharts;

  public getData = () => {
    const { startTime, endTime } = this.state;
    const { getChartData } = this.props;
    this.setState({ loading: true });
    getChartData(startTime, endTime).then((data: any) => {
      this.setState({ loading: false });
      this.changeChartOptions(data);
    });
  }

  public componentDidMount() {
    this.chart = echarts.init(this.id);
    this.getData();
    window.addEventListener('resize', this.resize);
  }

  public componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
  }

  public resize = () => {
    this.chart.resize();
  }

  public changeChartOptions(options: any) {
    const noData = options.series.length ? false : true;
    this.setState({ noData });
    this.chart.setOption(options, true);
  }

  public handleTimeChange = (dates: Moment[]) => {
    this.setState({
      startTime: dates[0],
      endTime: dates[1],
    });
    const { getChartData } = this.props;
    this.setState({ loading: true });
    getChartData(dates[0], dates[1]).then((data: any) => {
      this.setState({ loading: false });
      this.changeChartOptions(data);
    });
  }

  public render() {
    const { customerNode } = this.props;
    return (
      <div className="status-box" style={{minWidth: '930px'}}>
        <div className="status-graph">
          <div className="k-toolbar">
            {customerNode}
          </div>
          <ul className="k-toolbar">
            <li>
              <RangePicker
                ranges={{
                  今日: [moment().startOf('date'), moment()],
                  近一天: [moment().subtract(1, 'day'), moment()],
                  近一周: [moment().subtract(7, 'day'), moment()],
                }}
                disabledDate={disabledDate}
                defaultValue={[moment().subtract(1, 'hour'), moment()]}
                format={timeStampStr}
                onChange={this.handleTimeChange}
              />
            </li>
          </ul>
        </div>
        <Spin spinning={this.state.loading} className="chart-content">
          {this.state.noData ? <div className="nothing-style">暂无数据</div> : null}
          <div className={this.state.noData ? 'chart-no-data' : 'chart'} ref={(id) => this.id = id} />
        </Spin>
      </div>
    );
  }
}
