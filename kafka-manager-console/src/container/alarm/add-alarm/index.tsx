import * as React from 'react';
import './index.less';
import { WrappedDynamicSetStrategy } from './strategy-form';
import { Button, PageHeader, Spin, message } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { WrappedTimeForm } from './time-form';
import { ActionForm } from './action-form';
import { TypeForm } from './type-form';
import { handlePageBack } from 'lib/utils';
import { observer } from 'mobx-react';
import { alarm } from 'store/alarm';
import { app } from 'store/app';
import Url from 'lib/url-parser';
import { IStrategyExpression, IRequestParams } from 'types/alarm';
@observer
export class AddAlarm extends SearchAndFilterContainer {
  public isDetailPage = window.location.pathname.includes('/alarm-detail'); // 判断是否为详情
  public strategyForm: any = null;
  public actionForm: any = null;
  public timeForm: any = null;
  public typeForm: any = null;

  public id: number = null;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.id = Number(url.search.id);
  }

  public async componentDidMount() {
    alarm.getMonitorType();
    alarm.setLoading(true);
    app.getAppList();
    if (this.id || this.id === 0) {
      await alarm.getMonitorDetail(this.id);
      this.initMonitorDetailData();
    }
    alarm.setLoading(false);
  }

  public initMonitorDetailData() {
    if (alarm.monitorStrategyDetail.monitorRule) {
      if (!this.strategyForm || !this.actionForm || !this.typeForm || !this.timeForm) {
        return;
      }
      const monitorRule = alarm.monitorStrategyDetail.monitorRule || {} as IRequestParams;

      this.timeForm.updateFormData(monitorRule);
      this.typeForm.updateFormData(monitorRule);
      this.actionForm.updateFormData(monitorRule);
      this.strategyForm.updateFormValue(monitorRule);
    }
  }

  public handleSubmit = () => {
    if (!this.strategyForm || !this.actionForm || !this.typeForm || !this.timeForm) {
      return;
    }

    const params = this.generateRequestParams() as IRequestParams;
    if (!params) return;

    (this.id || this.id === 0) ?
      alarm.modifyMonitorStrategy({ id: this.id, ...params }) : alarm.addMonitorStategy(params);
  }

  public handleResetForm = (id?: number) => {
    if (id || id === 0) {
      alarm.getMonitorDetail(this.id);
      this.initMonitorDetailData();
    } else {
      if (!this.strategyForm || !this.actionForm || !this.typeForm || !this.timeForm) {
        return;
      }
      this.typeForm.resetFormData();
      this.timeForm.resetFormData();
      this.actionForm.resetFormData();

      this.strategyForm.resetForm();
    }
  }

  public generateRequestParams() {
    const actionValue = this.actionForm.getFormData();
    const timeValue = this.timeForm.getFormData();
    const typeValue = this.typeForm.getFormData().typeValue;
    let strategyList = this.strategyForm.getFormValidateData();
    const filterObj = this.typeForm.getFormData().filterObj;
    // tslint:disable-next-line:max-line-length
    if (!actionValue || !timeValue || !typeValue || !strategyList.length || !filterObj || !filterObj.filterList.length) {
      message.error('请正确填写必填项');
      return null;
    }

    if (filterObj.monitorType === 'online-kafka-topic-throttled') {
      filterObj.filterList.push({
        tkey: 'app',
        topt: '=',
        tval: [typeValue.app],
      });
    }
    this.id && filterObj.filterList.forEach((item: any) => {
      if (item.tkey === 'cluster') {
        item.tval = [item.clusterIdentification]
      }
    })
    strategyList = strategyList.map((row: IStrategyExpression) => {
      return {
        ...row,
        metric: filterObj.monitorType,
      };
    });
    return {
      appId: typeValue.app,
      name: typeValue.alarmName,
      periodDaysOfWeek: timeValue.weeks.join(','),
      periodHoursOfDay: timeValue.hours.join(','),
      priority: actionValue.level,
      strategyActionList: [{
        callback: actionValue.callback,
        notifyGroup: actionValue.acceptGroup,
        converge: actionValue.alarmPeriod + ',' + actionValue.alarmTimes,
        type: 'notify',
        sendRecovery: 1,
      }],
      strategyExpressionList: strategyList,
      strategyFilterList: filterObj.filterList,
    } as IRequestParams;
  }

  public renderAlarmStrategy() {
    return (
      <div className="config-wrapper">
        <span className="span-tag" data-set={alarm.monitorType}>报警策略</span>
        <div className="info-wrapper">
          <WrappedDynamicSetStrategy wrappedComponentRef={(form: any) => this.strategyForm = form} />
        </div>
      </div>
    );
  }

  public renderTimeForm() {
    return (
      <>
        <WrappedTimeForm wrappedComponentRef={(form: any) => this.timeForm = form} />
      </>
    );
  }

  public render() {
    return (
      <Spin spinning={alarm.loading}>
        <div className={this.isDetailPage ? '' : 'container_box'}>
          <PageHeader
            className={this.isDetailPage ? 'is-show' : 'btn-group'}
            onBack={() => handlePageBack('/alarm')}
            title={(this.id || this.id === 0) ? '编辑告警规则' : '新建告警规则'}
            extra={[
              <Button key="1" type="primary" onClick={() => this.handleSubmit()}>提交</Button>,
              <Button key="2" onClick={() => this.handleResetForm(this.id)}>重置</Button>,
            ]}
          />
          <TypeForm
            ref={(form) => this.typeForm = form}
          />
          {this.renderAlarmStrategy()}
          {this.renderTimeForm()}
          <ActionForm ref={(actionForm) => this.actionForm = actionForm} />
        </div>
      </Spin>
    );
  }
}
