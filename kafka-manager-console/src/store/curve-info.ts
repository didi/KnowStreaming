import { observable, action } from 'mobx';
import moment = require('moment');
import { EChartOption } from 'echarts/lib/echarts';
import { ICurve } from 'container/common-curve/config';
import { curveKeys, PERIOD_RADIO_MAP } from 'container/admin/data-curve/config';
import { timeFormat } from 'constants/strategy';

class CurveInfo {
  @observable
  public periodKey: string = 'oneHour';

  @observable
  public timeRange: [moment.Moment, moment.Moment] = PERIOD_RADIO_MAP.get(this.periodKey).dateRange;

  @observable
  public curveData: { [key: string]: EChartOption } = {};

  @observable
  public curveLoading: { [key: string]: boolean } = {};

  @observable
  public operators: string[] = [];

  @observable
  public currentOperator: string;

  @action.bound
  public setCurveData(key: curveKeys | string, data: EChartOption) {
    this.curveData[key] = data;
  }

  @action.bound
  public setCurveLoading(key: curveKeys | string, loading: boolean) {
    this.curveLoading[key] = loading;
  }

  @action.bound
  public setTimeRange(newRange: [moment.Moment, moment.Moment]) {
    this.timeRange = newRange;
  }

  @action.bound
  public setOperators(operators: string[]) {
    this.operators = operators;
  }

  @action.bound
  public setCurrentOperator(operator: string) {
    this.currentOperator = operator;
  }

  public getStartTime = () => {
    return this.timeRange[0].format(timeFormat);
  }

  public getEndTime = () => {
    return this.timeRange[1].format(timeFormat);
  }

  public getCommonCurveData = (
    options: ICurve,
    parser: (option: ICurve, data: any[]) => EChartOption,
    reload?: boolean) => {
    const { path } = options;
    this.setCurveData(path, null);
    this.setCurveLoading(path, true);
    return options.api(this.timeRange[0], this.timeRange[1], reload).then((data: any) => {
      this.setCurveData(path, parser(options, data));
    }).finally(() => {
      this.setCurveLoading(path, false);
    });
  }
}

export const curveInfo = new CurveInfo();
