import * as React from 'react';
import { Spin, notification } from 'component/antd';
import echarts, { EChartOption } from 'echarts/lib/echarts';

// 引入柱状图
import 'echarts/lib/chart/bar';

// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';

interface IChartProps {
  getChartData: any;
  customerNode?: React.ReactNode;
}

export class BarChartComponet extends React.Component<IChartProps> {
  public id: HTMLDivElement = null;
  public chart: echarts.ECharts;

  public state = {
    loading: false,
    noData: false,
  };

  public componentDidMount() {
    this.chart = echarts.init(this.id);
    this.getChartData();
    window.addEventListener('resize', this.resize);
  }

  public componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
  }

  public resize = () => {
    this.chart.resize();
  }

  public isHasData = (data: EChartOption) => {
    const noData = !(data.series && data.series.length);
    this.setState({ noData });
    return !noData;
  }

  public getChartData = () => {
    const { getChartData } = this.props;
    if (!getChartData) {
      return notification.error({ message: '图表信息有误' });
    }

    this.setState({ loading: true });
    const chartOptions = getChartData();

    if ((typeof chartOptions.then) === 'function') {
      return chartOptions.then((data: EChartOption) => {
        this.setState({ loading: false });

        if (this.isHasData(data)) {
          this.changeChartOptions(data);
        }
      });
    }

    if (this.isHasData(chartOptions)) {
      this.changeChartOptions(chartOptions);
      this.setState({ loading: false });
    }
  }

  public changeChartOptions(options: any) {
    this.chart.setOption(options, true);
  }

  public handleRefreshChart() {
    this.getChartData();
  }

  public render() {
    return (
      <>
        <Spin spinning={this.state.loading} className="chart-content">
          {this.state.noData ? <div className="nothing-style">暂无数据</div> : null}
          <div className={this.state.noData ? 'chart-no-data' : 'chart'} ref={(id) => this.id = id} />
        </Spin>
      </>
    );
  }
}
