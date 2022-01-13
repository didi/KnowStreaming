import React from 'react';
import * as echarts from 'echarts/core';
import './index.less';

// 引入柱状图
import { PieChart, LineChart } from 'echarts/charts';

// 引入提示框和标题组件
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent,
  DatasetComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

// 注册必须的组件
echarts.use([
  PieChart,
  LineChart,
  ToolboxComponent,
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  CanvasRenderer,
]);
export interface IEchartsProps {
  width?: number;
  height?: number;
  options?: any;
}

export const hasData = (options: any) => {
  if (options && options.series && options.series.length) return true;
  return false;
};

export default class LineCharts extends React.Component<IEchartsProps> {
  public id = null as HTMLDivElement;

  public myChart = null as echarts.ECharts;

  public componentDidMount() {
    const { options } = this.props;
    this.myChart = echarts.init(this.id);
    this.myChart.setOption(options, true);
    window.addEventListener('resize', this.resize);
  }

  public componentWillUnmount() {
    window.removeEventListener('resize', this.resize);
  }

  public componentDidUpdate() {
    this.refresh();
  }

  public refresh = () => {
    const { options } = this.props;
    this.myChart.setOption(options, true);
  }

  public resize = () => {
    this.myChart.resize();
  }

  public render() {
    const { height, width } = this.props;
    return <div ref={id => this.id = id} style={{ width: `${width}px`, height: `${height}px` }} />;
  }
}
