import React from 'react';
import echarts, { EChartOption } from 'echarts/lib/echarts';
import 'echarts/lib/chart/pie';
import 'echarts/lib/chart/line';
import 'echarts/lib/component/legend';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/axis';
import './index.less';

export interface IEchartsProps {
  width?: number;
  height?: number;
  options?: EChartOption;
}

export const hasData = (options: EChartOption) => {
  if (options && options.series && options.series.length) return true;
  return false;
};

export default class LineChart extends React.Component<IEchartsProps> {
  public id = null as HTMLDivElement;

  public myChart = null as echarts.ECharts;

  public componentDidMount() {
    const { options } = this.props;
    this.myChart = echarts.init(this.id);
    this.myChart.setOption(options);
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
    this.myChart.setOption(options);
  }

  public resize = () => {
    this.myChart.resize();
  }

  public render() {
    const { height, width } = this.props;
    return <div ref={id => this.id = id} style={{width: `${width}px`, height: `${height}px`}} />;
  }
}
