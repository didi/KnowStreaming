import { getAlarmTime, getAlarmWeek } from './config';
import * as React from 'react';
import { IRequestParams, IAlarmTime } from 'types/alarm';
import { Checkbox, TimePicker, Form } from 'component/antd';
import { weekOptions } from 'constants/status-map';

import moment = require('moment');

interface ITimeProps {
  form?: any;
  formData?: any;
}

export class TimeForm extends React.Component<ITimeProps> {
  public isDetailPage = window.location.pathname.includes('/alarm-detail'); // 判断是否为详情
  public $form: any = null;
  public weeks: number[] = [0, 1, 2, 3, 4, 5, 6, 7];
  public startTime: number = 0;
  public endTime: number = 23;

  public getFormData() {
    let value = null as IAlarmTime;
    this.props.form.validateFields((error: Error, result: any) => {
      if (error) {
        return;
      }
      const start = Number(moment(result.startTime).format('HH'));
      const end = Number(moment(result.endTime).format('HH'));
      const timeArr = getAlarmTime().defaultTime;
      const hours = timeArr.slice(start, end + 1);
      value = {
        weeks: result.weeks,
        hours,
      };
    });
    return value;
  }

  public resetFormData() {
    const { defaultTime } = getAlarmTime();
    const { defWeek } = getAlarmWeek();
    this.props.form.setFieldsValue({
      hours: defaultTime,
      weeks: defWeek,
      startTime: moment(0, 'HH'),
      endTime: moment(23, 'HH'),
    });
  }

  public updateFormData = (monitorRule: IRequestParams) => {
    const selectHours = monitorRule.periodHoursOfDay.split(',').map(item => +item);
    const selectWeek = monitorRule.periodDaysOfWeek.split(',').map(item => +item);

    this.props.form.setFieldsValue({
      // hours: selectHours,
      weeks: selectWeek,
      startTime: moment(selectHours[0], 'HH'),
      endTime: moment(selectHours[selectHours.length - 1], 'HH'),
    });
    this.startTime = selectHours[0];
    this.endTime = selectHours[selectHours.length - 1];
  }
  public onStartChange = (time: any, timeString: string) => {
    this.startTime = Number(timeString);
  }
  public disabledHours = () => {
    const hours = [] as number[];
    for (let i = 0; i < this.startTime; i++) {
        hours.push(i);
    }
    return hours;
  }

  public render() {
    // const formData = {};
      // {/* <div className="alarm-x-form">
      //   <XFormComponent
      //     ref={form => this.$form = form}
      //     formData={formData}
      //     formMap={xTimeFormMap}
      //     formLayout={formLayout}
      //   />
      // </div> */}
    const { getFieldDecorator } = this.props.form;
    const format = 'HH';
    return (
      <div className="config-wrapper">
        <span className="span-tag">生效时间</span>
        <div className="alarm-time-form">
          <Form name="basic" >
            <b>在每</b>
            <Form.Item label="" key={1} className="form-item">
              {getFieldDecorator('weeks', {
                initialValue: this.weeks,
                rules: [{ required: true, message: '请选择周期' }],
              })(
              <Checkbox.Group
                options={weekOptions}
                disabled={this.isDetailPage}
              />)}
            </Form.Item>
            <b>的</b>
            <Form.Item label="" key={2} className="form-item">
              {getFieldDecorator('startTime', {
                initialValue: moment(this.startTime, format),
                rules: [{ required: true, message: '请选择开始时间' }],
              })(
              <TimePicker
                key={1}
                format={format}
                style={{width: 60}}
                onChange={this.onStartChange}
                disabled={this.isDetailPage}
              />)}
            </Form.Item>
            <b>~</b>
            <Form.Item label="" key={3} className="form-item">
              {getFieldDecorator('endTime', {
                initialValue: moment(this.endTime, format),
                rules: [{ required: true, message: '请选择结束时间' }],
              })(
              <TimePicker
                key={2}
                format={format}
                disabledHours={this.disabledHours}
                style={{width: 60}}
                disabled={this.isDetailPage}
              />)}
            </Form.Item>
          </Form>
        </div>
      </div>
    );
  }
}

export const WrappedTimeForm = Form.create({ name: 'dynamic_time_form' })(TimeForm);
