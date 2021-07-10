import { XFormComponent } from 'component/x-form';
import { xActionFormMap } from './config';
import * as React from 'react';
import { IRequestParams, IStrategyAction, IConfigForm } from 'types/alarm';

export class ActionForm extends React.Component {
  public $form: any = null;

  public getFormData() {
    let configValue = null as IConfigForm;
    this.$form.validateFields((error: Error, result: IConfigForm) => {
      if (error) {
        return;
      }
      configValue = result;
    });
    return configValue;
  }

  public resetFormData() {
    this.$form.resetFields();
  }

  public updateFormData(monitorRule: IRequestParams) {
    const strategyAction = monitorRule.strategyActionList[0] || {} as IStrategyAction;
    this.$form.setFieldsValue({
      level: monitorRule.priority,
      alarmPeriod: strategyAction.converge.split(',')[0],
      alarmTimes: strategyAction.converge.split(',')[1],
      acceptGroup: strategyAction.notifyGroup,
      callback: strategyAction.callback,
    });
  }

  public render() {
    const formData = {};
    const formLayout = {
      labelCol: { span: 3 },
      wrapperCol: { span: 12 },
    };

    return (
      <div className="config-wrapper">
        <span className="span-tag">配置发送信息</span>
        <div className="alarm-x-form action-form">
          <XFormComponent
            ref={form => this.$form = form}
            formData={formData}
            formMap={xActionFormMap}
            formLayout={formLayout}
          />
        </div>
      </div>
    );
  }

}
