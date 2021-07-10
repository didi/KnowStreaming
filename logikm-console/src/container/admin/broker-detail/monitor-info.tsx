import * as React from 'react';
import { observer } from 'mobx-react';
import Url from 'lib/url-parser';
import { adminMonitor } from 'store/admin-monitor';
import moment from 'moment';
import './index.less';
import { ExpandCard } from 'component/expand-card';
import { DataCurveFilter } from '../data-curve';
import { allCurves, ICurveType } from '../data-curve/config';
import { CommonCurve } from 'container/common-curve';

@observer
export class MonitorInfo extends React.Component {
  public clusterId: number;
  public brokerId: number;
  public $chart: any;
  public chart11: any;

  public state = {
    startTime: moment().subtract(1, 'hour'),
    endTime: moment(),
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
    adminMonitor.setCurrentBrokerId(this.brokerId);
    adminMonitor.setCurrentClusterId(this.clusterId);
  }

  public handleRef(chart: any, index: number) {
    this.$chart[index] = chart;
  }

  public getCurves = (curveType: ICurveType) => {
    return curveType.curves.map(o => {
      return <CommonCurve key={o.path} options={o} parser={curveType.parser} />;
    });
  }

  public renderChart() {
    return (
      <div className="curve-wrapper">
        <DataCurveFilter />
        {allCurves.map(c => {
          return <ExpandCard key={c.type} title={c.title} charts={this.getCurves(c)} />;
        })}
      </div>
    );
  }

  public componentDidMount() {
    adminMonitor.getBrokersMetricsList(moment().subtract(1, 'hour').format('x'), moment().format('x'));
  }

  public render() {
    return (
      <>
        {adminMonitor.brokersMetricsHistory ? this.renderChart() : null}
      </>
    );
  }
}
