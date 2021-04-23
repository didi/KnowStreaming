import React from 'react';
import './index.less';
import { Radio, DatePicker, RadioChangeEvent, RangePickerValue, Button, Icon } from 'component/antd';
import { curveInfo } from 'store/curve-info';
import { curveKeys, CURVE_KEY_MAP, PERIOD_RADIO_MAP, PERIOD_RADIO } from './config';
import moment = require('moment');
import { observer } from 'mobx-react';
import { timeStampStr } from 'constants/strategy';
import { adminMonitor } from 'store/admin-monitor';

@observer
export class DataCurveFilter extends React.Component {
  public handleRangeChange = (dates: RangePickerValue, dateStrings: [string, string]) => {
    curveInfo.setTimeRange(dates as [moment.Moment, moment.Moment]);
    this.refreshAll();
  }

  public radioChange = (e: RadioChangeEvent) => {
    const { value } = e.target;
    curveInfo.setTimeRange(PERIOD_RADIO_MAP.get(value).dateRange);
    this.refreshAll();
  }

  public refreshAll = () => {
    adminMonitor.setRequestId(null);
    Object.keys(curveKeys).forEach((c: curveKeys) => {
      const { typeInfo, curveInfo: option } = CURVE_KEY_MAP.get(c);
      const { parser } = typeInfo;
      curveInfo.getCommonCurveData(option, parser, true);
    });
  }

  public render() {
    return (
      <>
        <Radio.Group onChange={this.radioChange} defaultValue={curveInfo.periodKey}>
          {PERIOD_RADIO.map(p => <Radio.Button key={p.key} value={p.key}>{p.label}</Radio.Button>)}
        </Radio.Group>
        <DatePicker.RangePicker
          format={timeStampStr}
          onChange={this.handleRangeChange}
          className="ml-10"
          value={curveInfo.timeRange}
        />
        <div className="right-btn">
          <Button onClick={this.refreshAll}><Icon className="dsui-icon-shuaxin1 mr-4" type="reload" />刷新</Button>
        </div>
      </>
    );
  }
}
