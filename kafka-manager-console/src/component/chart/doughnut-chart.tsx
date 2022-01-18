import * as React from 'react';
import { Spin } from 'component/antd';
import * as echarts from 'echarts/core';
// 引入饼图
import { PieChart } from 'echarts/charts';

// 引入提示框和标题组件
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

// 注册必须的组件
echarts.use([
  PieChart,
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  GridComponent,
  CanvasRenderer,
]);
interface IPieProps {
  getChartData: any;
}

export class DoughnutChart extends React.Component<IPieProps> {
  public id: HTMLDivElement = null;
  public chart: echarts.ECharts;

  public state = {
    loading: true,
    isNoData: false,
  };

  public getChartData = () => {
    const { getChartData } = this.props;

    this.setState({ loading: true });
    const options = getChartData();
    if (!options || !options.series || !options.series.length) {
      this.setState({
        isNoData: true,
        loading: false,
      });
      return;
    }

    this.changeChartOptions(options);
  }

  public changeChartOptions(options: any) {
    this.chart.setOption(options, true);
    this.setState({ loading: false });
  }

  public componentDidMount() {
    this.chart = echarts.init(this.id);
    this.getChartData();
  }

  public render() {
    return (
      <>
        <Spin spinning={this.state.loading} className="chart-content">
          {this.state.isNoData ? <div className="nothing-style">暂无数据</div> : null}
          <div className="doughnut-chart" ref={(id) => this.id = id} />
        </Spin>
      </>
    );
  }
}
