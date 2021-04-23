import { XFormComponent } from 'component/x-form';
import { xTypeFormMap } from './config';
import * as React from 'react';
import { IRequestParams, ITypeForm } from 'types/alarm';
import { app } from 'store/app';
import { observer } from 'mobx-react';
import { WrappedDynamicSetFilter } from './filter-form';

@observer
export class TypeForm extends React.Component {

  public $form: any = null;
  public filterForm: any = null;

  public getFormData() {
    const filterObj = this.filterForm.getFormValidateData();
    let typeValue = null as ITypeForm;
    this.$form.validateFields((error: Error, result: ITypeForm) => {
      if (error) {
        return;
      }
      typeValue = result;
    });
    const valueObj = {
      typeValue,
      filterObj,
    };
    return valueObj;
  }

  public resetFormData() {
    this.$form.resetFields();
    this.filterForm.resetForm();
  }

  public updateFormData(monitorRule: IRequestParams) {
    this.$form.setFieldsValue({
      app: monitorRule.appId,
      alarmName: monitorRule.name,
    });
    this.filterForm.initFormValue(monitorRule);
  }

  public render() {
    const formData = {};
    xTypeFormMap[1].options = app.data.map(item => ({
      label: item.name,
      value: item.appId,
    }));

    return (
      <>
        <div className="config-wrapper">
          <span className="span-tag">基本信息</span>
          <div className="alarm-x-form type-form">
            <XFormComponent
              ref={form => this.$form = form}
              formData={formData}
              formMap={xTypeFormMap}
              layout="inline"
            />
          </div>
        </div >
        <div className="config-wrapper">
          <span className="span-tag">选择指标</span>
          <div className="alarm-x-form type-form">
            <WrappedDynamicSetFilter wrappedComponentRef={(form: any) => this.filterForm = form} />
          </div>
        </div >
      </>
    );
  }

}
