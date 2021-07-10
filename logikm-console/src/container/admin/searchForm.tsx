import * as React from 'react';
import { Select, Input, InputNumber, Form, Switch, Checkbox, DatePicker, Radio, Upload, Button, Icon, Tooltip } from 'component/antd';
// import './index.less';
const Search = Input.Search;
export interface IFormItem {
  key: string;
  label: string;
  type: string;
  value?: string;
  // 内部组件属性注入
  attrs?: any;
  // form属性注入
  formAttrs?: any;
  defaultValue?: string | number | any[];
  rules?: any[];
  invisible?: boolean;
  getvaluefromevent: Function;
}

interface SerachFormProps {
  formMap: IFormItem[];
  // formData: any;
  form: any;
  onSubmit: Function;
  isReset?: boolean;
  clearAll: Function;
  layout?: 'inline' | 'horizontal' | 'vertical';
}

export interface IFormSelect extends IFormItem {
  options: Array<{ key?: string | number, value: string | number, label: string }>;
}

class SearchForm extends React.Component<SerachFormProps>{
  public onSubmit = () => {
    // this.props.onSubmit()
    //
  }

  public renderFormItem(item: IFormItem) {
    switch (item.type) {
      default:
      case 'input':
        return <Input key={item.key} {...item.attrs} />;
      case 'select':
        return (
          <Select
            // size="small"
            key={item.key}
            {...item.attrs}
            invisibleValue={item.formAttrs.invisibleValue}
          >
            {(item as IFormSelect).options && (item as IFormSelect).options.map((v, index) => (

              <Select.Option
                key={v.value || v.key || index}
                value={v.value}
              >
                {v.label}
                {/* <Tooltip placement='left' title={v.value}>
                  {v.label}
                </Tooltip> */}
              </Select.Option>
            ))}
          </Select>
        );
    }
  }

  public theQueryClick = (value: any) => {
    this.props.onSubmit(value)
    this.props.clearAll()
    // this.props.form.resetFields()
  }
  public resetClick = () => {
    this.props.form.resetFields()
    this.props.clearAll()
    this.theQueryClick(this.props.form.getFieldsValue())
  }

  public render() {
    const { form, formMap, isReset } = this.props;
    const { getFieldDecorator, getFieldsValue } = form;
    return (
      <Form layout='inline' onSubmit={this.onSubmit}>
        {
          formMap.map(formItem => {
            // const { initialValue, valuePropName } = this.handleFormItem(formItem, formData);
            // const getFieldValue = {
            //   initialValue,
            //   rules: formItem.rules || [{ required: false, message: '' }],
            //   valuePropName,
            // };
            return (
              <Form.Item
                key={formItem.key}
                label={formItem.label}
                {...formItem.formAttrs}
              >
                {getFieldDecorator(formItem.key, {
                  initialValue: formItem.formAttrs?.initialvalue,
                  getValueFromEvent: formItem?.getvaluefromevent,
                })(
                  this.renderFormItem(formItem),
                )}
              </Form.Item>
            );
          })
        }
        <Form.Item>
          {
            isReset && <Button style={{ width: '80px', marginRight: '20px' }} type="primary" onClick={() => this.resetClick()}>重置</Button>
          }
          <Button style={{ width: '80px' }} type="primary" onClick={() => this.theQueryClick(getFieldsValue())}>查询</Button>
        </Form.Item>
      </Form>
    );
  }
}
export const SearchFormComponent = Form.create<SerachFormProps>({ name: 'search-form' })(SearchForm);