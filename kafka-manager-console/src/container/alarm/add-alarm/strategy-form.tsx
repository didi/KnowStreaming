import * as React from 'react';
import { Icon, InputNumber, Select, message, Form, Tooltip } from 'component/antd';
import { equalList, funcKeyMap, funcList } from 'constants/strategy';
import { IStringMap } from 'types/base-type';
import { IRequestParams } from 'types/alarm';
import { IFormSelect, IFormItem, FormItemType } from 'component/x-form';
import { searchProps } from 'constants/table';
import { alarm } from 'store/alarm';

interface IDynamicProps {
  form: any;
  formData?: any;
  maxLimit?: number;
}
interface ICRUDItem {
  id: string;
  func: string;
  eopt?: string;
  threshold?: number;
  period?: number;
  count?: number;
  day?: number;
}
const commonKeys = ['eopt', 'threshold', 'func'];

class DynamicSetStrategy extends React.Component<IDynamicProps> {
  public isDetailPage = window.location.pathname.includes('/alarm-detail'); // 判断是否为详情
  public crudList = [] as ICRUDItem[];
  public state = {
    shouldUpdate: false,
    monitorType: alarm.monitorType
  };

  public componentDidMount() {
    if (!this.crudList.length) {
      const id = `0_`;
      this.crudList.push({
        id,
        func: 'happen',
      });
    }
    this.updateRender();
  }

  public updateRender() {
    this.setState({
      shouldUpdate: !this.state.shouldUpdate,
    });
  }

  public resetForm() {
    const { resetFields } = this.props.form;
    resetFields();
  }

  public dealFormParams(monitorRule: IRequestParams) {
    const initialCrudList = [] as ICRUDItem[];

    if (monitorRule.strategyExpressionList) {
      const expressionList = monitorRule.strategyExpressionList;
      expressionList.map((row: any, index) => {
        const obj = {} as any;
        for (const key of commonKeys) {
          obj[key] = row[key];
        }
        const otherKeys = funcKeyMap[row.func] as string[];
        // 除去commonKeys中的key 其他值在提交时全塞到params中 故在编辑详情渲染时再拆回来
        const parmas = row.params ? row.params.split(',').map((row: string) => +row) : [];
        otherKeys.forEach((line: string, i: number) => {
          obj[line] = parmas[i] || 0;
        });
        obj.id = `${index}_`;

        initialCrudList.push(obj);
      });
    }
    return initialCrudList;
  }

  public updateFormValue(monitorRule: IRequestParams) {
    const { setFieldsValue } = this.props.form;
    const initialCrudList = this.dealFormParams(monitorRule);
    if (!initialCrudList.length) return;

    const filledKeys = ['period'].concat(commonKeys);
    const formKeyMap = {
      happen: ['count'].concat(filledKeys),
      ndiff: ['count'].concat(filledKeys),
      all: [].concat(filledKeys),
      pdiff: [].concat(filledKeys),
      sum: [].concat(filledKeys),
      c_avg_rate_abs: ['day'].concat(filledKeys),
    } as {
      [key: string]: string[],
    };
    const feildValue = {

    } as any;

    for (const item of initialCrudList) {
      for (const key of formKeyMap[item.func]) {
        feildValue[item.id + '-' + key] = (item as any)[key];
      }
    }
    setFieldsValue(feildValue);
    this.crudList = initialCrudList;
    this.updateRender();
  }

  public getFormValidateData() {
    let value = [] as IStringMap[];
    const { crudList } = this;

    this.props.form.validateFields((err: Error, values: any) => {
      if (!err) {
        let strategyList = [];
        for (const item of crudList) {
          const lineValue = {} as IStringMap;
          const paramsArray = [] as number[];

          // 不在commonKeys里的塞到params
          for (const key of Object.keys(values)) {
            if (key.indexOf(item.id) > -1) {
              const finalKey = key.substring(key.indexOf('-') + 1);
              if (commonKeys.indexOf(finalKey) < 0) { // 不在commonKeys里的塞到params 奇奇怪怪的接口
                paramsArray.push(finalKey === 'day' ? values[key] * 24 * 60 * 60 : values[key]); // 按接口单位天的时候需要换算成秒
              } else { // 在commonKeys里直接赋值
                lineValue[finalKey] = values[key];
              }
            }
          }

          if (lineValue.func === 'happen' && paramsArray.length > 1 && paramsArray[0] < paramsArray[1]) {
            strategyList = []; // 清空赋值
            return message.error('周期值应大于次数');
          }

          lineValue.params = paramsArray.join(',');
          strategyList.push(lineValue);
        }
        value = strategyList;
      }
    });

    return value;
  }

  public remove = (curr: string) => {
    const { crudList } = this;
    if (crudList.length <= 1) {
      return message.info('至少保留一项');
    }

    const index = crudList.findIndex(item => item.id === curr);

    crudList.splice(index, 1);
    this.updateRender();
  }

  public add = () => {
    const { maxLimit = 5 } = this.props;
    const { crudList } = this;
    if (crudList.length >= maxLimit) {
      return message.info('已达最大数量');
    }

    const id = `${crudList.length}_`;
    crudList.push({
      id,
      func: 'happen',
    });
    this.updateRender();
  }

  public onFuncTypeChange = (e: string, key: string) => {
    const { crudList } = this;
    const index = crudList.findIndex(row => row.id === key);

    if (index > -1) {
      crudList[index].func = e;
    }
    this.updateRender();
  }

  public getFormItem(item: IFormItem) {
    switch (item.type) {
      default:
      case FormItemType.input:
        return <InputNumber min={0} key={item.key} {...item.attrs} disabled={this.isDetailPage} />;
      case FormItemType.select:
        return (
          <Select
            key={item.key}
            {...item.attrs}
            disabled={this.isDetailPage}
            {...searchProps}
          >
            {(item as IFormSelect).options && (item as IFormSelect).options.map((v, index) => (
              <Select.Option
                key={v.value || v.key || index}
                value={v.value}
              >
                {v.label.length > 15 ? <Tooltip placement="bottomLeft" title={v.label}>
                  {v.label}
                </Tooltip> : v.label}
              </Select.Option>
            ))}
          </Select>
        );
    }
  }

  public getFuncItem(row: ICRUDItem) {
    const key = row.id;
    const funcType = row.func;
    let element = null;
    const common = (
      <>
        在最近
        {this.renderFormItem({ type: 'input', key: key + '-period', defaultValue: row.period } as IFormItem)}
        个周期内
      </>
    );
    const equalItem = {
      type: 'select',
      attrs: { className: 'small-size' },
      defaultValue: row.eopt || '=',
      options: equalList,
      key: key + '-eopt',
    } as IFormSelect;

    switch (funcType) {
      case 'happen':
      case 'ndiff':
        element = (
          <>
            {common}
            {this.renderFormItem({ type: 'input', key: key + '-count', defaultValue: row.count } as IFormItem)}
            次
            {this.renderFormItem(equalItem)}
            {this.renderFormItem({ type: 'input', key: key + '-threshold', defaultValue: row.threshold } as IFormItem)}
          </>
        );
        break;
      case 'all':
      case 'diff':
      case 'max':
      case 'min':
      case 'sum':
      case 'avg':
        element = (
          <>
            {common}
            {this.renderFormItem(equalItem)}
            {this.renderFormItem({ type: 'input', key: key + '-threshold', defaultValue: row.threshold } as IFormItem)}
          </>
        );
        break;
      case 'c_avg_rate_abs':
      case 'c_avg_rate':
        element = (
          <>
            {common}，平均值相对
            {this.renderFormItem({ type: 'input', key: key + '-day', defaultValue: row.day } as IFormItem)}
            天
            {this.renderFormItem(equalItem)}
            {this.renderFormItem({ type: 'input', key: key + '-threshold', defaultValue: row.threshold } as IFormItem)}
            %
          </>
        );
        break;
      case 'c_avg_abs':
      case 'c_avg':
        element = (
          <>
            {common}，平均值相对
            {this.renderFormItem({ type: 'input', key: key + '-day', defaultValue: row.day } as IFormItem)}
            天
            {this.renderFormItem(equalItem)}
            {this.renderFormItem({ type: 'input', key: key + '-threshold', defaultValue: row.threshold } as IFormItem)}
          </>
        );
        break;
      case 'pdiff':
        element = (
          <>
            {common}
            {this.renderFormItem(equalItem)}
            {this.renderFormItem({ type: 'input', key: key + '-threshold', defaultValue: row.threshold } as IFormItem)}
            %
          </>
        );
        break;
    }
    return element;
  }
  public unit(monitorType: string) {
    let element = null;
    switch (monitorType) {
      case 'online-kafka-topic-msgIn':
        element = "条/秒"
        break;
      case 'online-kafka-topic-bytesIn':
        element = "字节/秒"
        break;
      case 'online-kafka-topic-bytesRejected':
        element = "字节/秒"
        break;
      case 'online-kafka-topic-produce-throttled':
        element = "1表示被限流"
        break;
      case 'online-kafka-topic-fetch-throttled':
        element = "1表示被限流"
        break;
      case 'online-kafka-consumer-maxLag':
        element = "条"
        break;
      case 'online-kafka-consumer-lag':
        element = "条"
        break;
      case 'online-kafka-consumer-maxDelayTime':
        element = "秒"
        break;
    }
    return (
      <span>{element}</span>
    )
  }
  public renderFormList(row: ICRUDItem, monitorType: string) {
    const key = row.id;
    const funcType = row.func;

    return (
      <div key={key} className="form-list">
        {this.renderFormItem({
          type: 'select',
          defaultValue: funcType,
          attrs: {
            onChange: (e: string) => this.onFuncTypeChange(e, key),
          },
          options: funcList,
          key: key + '-func',
        } as IFormSelect)}
        {this.getFuncItem(row)}
        {row.func !== 'c_avg_rate_abs' && row.func !== 'pdiff' ? this.unit(monitorType) : null}
      </div>
    );
  }

  public renderFormItem(item: IFormItem) {
    const { getFieldDecorator } = this.props.form;
    const initialValue = item.defaultValue || '';
    const getFieldValue = {
      initialValue,
      rules: item.rules || [{ required: true, message: '请填写' }],
    };

    return (
      <Form.Item
        key={item.key}
      >
        {getFieldDecorator(item.key, getFieldValue)(
          this.getFormItem(item),
        )}
      </Form.Item>
    );
  }

  public render() {
    const { crudList } = this;
    const { maxLimit = 5 } = this.props;

    return (
      <Form>
        {crudList.map((row, index) => {
          return (
            <div key={`${index}-${this.state.monitorType}`}>
              {this.renderFormList(row, alarm.monitorType)}
              {
                crudList.length > 1 ? (
                  <Icon
                    className={this.isDetailPage ? 'is-show' : 'dynamic-button delete'}
                    type="minus-circle-o"
                    onClick={() => this.remove(row.id)}
                  />
                ) : null
              }
              {index === crudList.length - 1 && crudList.length < maxLimit ? (
                <Icon
                  className={this.isDetailPage ? 'is-show' : 'dynamic-button plus'}
                  type="plus-circle-o"
                  onClick={() => this.add()}
                />
              ) : null}
            </div>
          );
        })}
      </Form>
    );
  }
}

export const WrappedDynamicSetStrategy = Form.create({ name: 'dynamic_form_item' })(DynamicSetStrategy);
