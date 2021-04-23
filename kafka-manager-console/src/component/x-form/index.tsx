import * as React from 'react';
import { Select, Input, InputNumber, Form, Switch, Checkbox, DatePicker, Radio, Upload, Button, Icon, Tooltip } from 'component/antd';
import Monacoeditor from 'component/editor/monacoEditor';
import { searchProps } from 'constants/table';
import { version } from 'store/version';
import './index.less';

const TextArea = Input.TextArea;
const { RangePicker } = DatePicker;

export enum FormItemType {
  input = 'input',
  inputPassword = 'input_password',
  inputNumber = 'input_number',
  textArea = 'text_area',
  select = 'select',
  _switch = '_switch',
  custom = 'custom',
  checkBox = 'check_box',
  datePicker = 'date_picker',
  rangePicker = 'range_picker',
  radioGroup = 'radio_group',
  upload = 'upload',
  monacoEditor = 'monaco_editor',
}

export interface IFormItem {
  key: string;
  label: string;
  type: FormItemType;
  value?: string;
  // 内部组件属性注入
  attrs?: any;
  // form属性注入
  formAttrs?: any;
  defaultValue?: string | number | any[];
  rules?: any[];
  invisible?: boolean;
  renderExtraElement?: () => JSX.Element;
}

export interface IFormSelect extends IFormItem {
  options: Array<{ key?: string | number, value: string | number, label: string, text?: string }>;
}

interface IFormCustom extends IFormItem {
  customFormItem: React.Component;
}

interface IXFormProps {
  formMap: IFormItem[];
  formData: any;
  form: any;
  formLayout?: any;
  layout?: 'inline' | 'horizontal' | 'vertical';
}

class XForm extends React.Component<IXFormProps> {

  private defaultFormLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 },
  };

  public onUploadFileChange = (e: any) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e && e.fileList;
  }

  public handleFormItem(formItem: any, formData: any) {
    let initialValue = formData[formItem.key] === 0 ? 0 : (formData[formItem.key] || formItem.defaultValue || '');
    let valuePropName = 'value';

    if (formItem.type === FormItemType.datePicker) {
      initialValue = initialValue || null;
    }

    // if (formItem.type === FormItemType.checkBox) {
    //   initialValue = formItem.defaultValue ? [formItem.defaultValue] : [];
    // }

    if (formItem.type === FormItemType._switch) {
      initialValue = false;
    }

    // if (formItem.type === FormItemType.select && formItem.attrs
    //   && ['tags'].includes(formItem.attrs.mode)) {
    //   initialValue = formItem.defaultValue ? [formItem.defaultValue] : [];
    // }

    if (formItem.type === FormItemType._switch) {
      valuePropName = 'checked';
    }

    if (formItem.type === FormItemType.upload) {
      valuePropName = 'fileList';
    }

    return { initialValue, valuePropName };
  }

  public render() {
    const { form, formData, formMap, formLayout, layout } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form layout={layout || 'horizontal'} onSubmit={() => ({})}>
        {formMap.map(formItem => {
          const { initialValue, valuePropName } = this.handleFormItem(formItem, formData);
          const getFieldValue = {
            initialValue,
            rules: formItem.rules || [{ required: false, message: '' }],
            valuePropName,
          };
          if (formItem.type === FormItemType.upload) {
            Object.assign(getFieldValue, {
              getValueFromEvent: this.onUploadFileChange,
            });
          }
          return (
            !formItem.invisible &&
            <Form.Item
              key={formItem.key}
              label={formItem.label}
              {...(formLayout || (layout === 'inline' ? {} : this.defaultFormLayout))}
              {...formItem.formAttrs}
            >
              {getFieldDecorator(formItem.key, getFieldValue)(
                this.renderFormItem(formItem),
              )}
              {formItem.renderExtraElement ? formItem.renderExtraElement() : null}
              {/* 添加保存时间提示文案 */}
              {formItem.attrs?.prompttype ? <span style={{ color: "#cccccc", fontSize: '12px', lineHeight: '20px', display: 'block' }}>{formItem.attrs.prompttype}</span> : null}
            </Form.Item>
          );
        })}
      </Form>
    );
  }

  public renderFormItem(item: IFormItem) {
    switch (item.type) {
      default:
      case FormItemType.input:
        return <Input key={item.key} {...item.attrs} />;
      case FormItemType.inputPassword:
        return <Input.Password key={item.key} {...item.attrs} />;
      case FormItemType.inputNumber:
        return <InputNumber {...item.attrs} />;
      case FormItemType.textArea:
        return <TextArea rows={5} {...item.attrs} />;
      case FormItemType.monacoEditor:
        // tslint:disable-next-line: jsx-wrap-multiline
        return <Monacoeditor {...item.attrs} />;
      case FormItemType.select:
        return (
          <Select
            key={item.key}
            {...item.attrs}
            {...searchProps}
          >
            {(item as IFormSelect).options && (item as IFormSelect).options.map((v, index) => (
              <Select.Option
                key={v.value || v.key || index}
                value={v.value}
              >
                {v.label.length > 35 ? <Tooltip placement="bottomLeft" title={v.text || v.label}>
                  {v.label}
                </Tooltip> : v.label}
              </Select.Option>
            ))}
          </Select>
        );
      case FormItemType._switch:
        return <Switch {...item.attrs} />;
      case FormItemType.custom:
        return (item as IFormCustom).customFormItem;
      case FormItemType.checkBox:
        return <Checkbox.Group options={(item as IFormSelect).options} />;
      case FormItemType.datePicker:
        return <DatePicker key={item.key} {...item.attrs} />;
      case FormItemType.rangePicker:
        return <RangePicker key={item.key} {...item.attrs} />;
      case FormItemType.radioGroup:
        return (
          <Radio.Group key={item.key} {...item.attrs}>
            {(item as IFormSelect).options.map((v, index) => (
              <Radio.Button key={v.value || v.key || index} value={v.value}>{v.label}</Radio.Button>
            ))}
          </Radio.Group>);
      case FormItemType.upload:
        return (
          <Upload beforeUpload={(file: any) => false} {...item.attrs}>
            <Button><Icon type="upload" />上传</Button>{version.fileSuffix && <span style={{ color: '#fb3939', padding: '0 0 0 10px' }}>{`请上传${version.fileSuffix}文件`}</span>}
          </Upload>
        );
    }
  }
}

export const XFormComponent = Form.create<IXFormProps>()(XForm);
