import { EChartOption } from 'echarts';
import { observer } from 'mobx-react';
import React from 'react';
import { curveInfo } from 'store/curve-info';
import { fullScreen } from 'store/full-screen';
import { getHight, EXPAND_GRID_HEIGHT, ICurve } from './config';
import './index.less';
import { Spin, Icon } from 'component/antd';
import LineChart, { hasData } from 'component/chart/line-chart';

export interface ICommonCurveProps {
  options: ICurve;
  parser?: (option: ICurve, data: any[]) => EChartOption;
}

@observer
export class CommonCurve extends React.Component<ICommonCurveProps> {

  public componentDidMount() {
    this.refresh(false);
  }

  public componentWillUnmount() {
    const { path } = this.props.options;
    curveInfo.setCurveData(path, null);
  }

  public refresh = (refresh?: boolean) => {
    curveInfo.getCommonCurveData(this.props.options, this.props.parser, refresh);
  }

  public expand = () => {
    const curveOption = this.getCurveData();
    const options = Object.assign({}, curveOption, {
      grid: {
        ...curveOption.grid,
        height: EXPAND_GRID_HEIGHT,
      },
    });
    const loading = this.getLoading();
    fullScreen.show(this.renderCurve(options, loading, true));
  }

  public renderOpBtns = (options: EChartOption, expand = false) => {
    const data = hasData(options);
    return (
      <div className="charts-op" key="op">
        {!expand ? <Icon type="reload" onClick={() => this.refresh(true)} key="refresh" /> : null}
        {data ? this.renderExpand(expand) : null}
      </div>
    );
  }

  public renderExpand = (expand = false) => {
    if (expand) return <Icon type="close" onClick={fullScreen.close} key="close" />;
    return <Icon type="fullscreen" className="ml-17" onClick={this.expand} key="full-screen" />;
  }

  public renderTitle = () => {
    const { title } = this.props.options;
    return (
      <div className="charts-title" key="title">{title}</div>
    );
  }

  public getCurveData = () => {
    const { path } = this.props.options;
    return curveInfo.curveData[path];
  }

  public getLoading = () => {
    const { path } = this.props.options;
    return curveInfo.curveLoading[path] || false;
  }

  public renderOthers = () => null as JSX.Element;

  public renderNoData = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="noData">暂无数据</div>;
  }

  public renderLoading = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="loading"><Spin /></div>;
  }

  public renderEchart = (options: EChartOption, loading = false) => {
    const height = getHight(options);
    const data = hasData(options);

    if (loading) return this.renderLoading(height);
    if (!data) return this.renderNoData(height);
    return <LineChart height={height} options={options} key="chart" />;
  }

  public renderCurve = (options: EChartOption, loading: boolean, expand = false) => {
    const data = hasData(options);
    return (
      <div className="common-chart-wrapper" >
        {this.renderTitle()}
        {this.renderEchart(options, loading)}
        {this.renderOpBtns(options, expand)}
        {data ? this.renderOthers() : null}
      </div>
    );
  }

  public render() {
    const options = this.getCurveData();
    const loading = this.getLoading();
    return (
      <>
        {this.renderCurve(options, loading)}
      </>
    );
  }
}
