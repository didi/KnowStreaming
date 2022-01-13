import * as React from 'react';
import { Spin, notification } from 'component/antd';
import * as echarts from 'echarts/core';

// 引入柱状图
import { BarChart } from 'echarts/charts';

// 引入提示框和标题组件
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { EChartsOption } from 'echarts';

// 注册必须的组件
echarts.use([
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  BarChart,
  GridComponent,
  CanvasRenderer,
]);

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

  public isHasData = (data: any) => {
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
      return chartOptions.then((data: EChartsOption) => {
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
