import { Button, Col, Form, Row } from 'knowdesign';
import { FormItemType, handleFormItem, IFormItem, renderFormItem } from 'knowdesign/es/extend/x-form';
import * as React from 'react';
import './style/form.less';
import EditTable from './EditTable';
import { useIntl } from 'react-intl';

const prefixTesting = 'config-form-panel';
interface IProps {
  formConfig: IFormItem[];
  formData: any;
  form: any;
  customFormRef: any;
  onHandleValuesChange?: any;
  title: string;
  customContent?: React.ReactNode;
  customForm?: React.ReactNode;
  clearForm: any;
  submit: any;
  stop: any;
  activeKey: string;
  running?: boolean;
}

export const renderFormContent = ({ formMap, formData, layout, formLayout, formItemColSpan = 24 }: any) => {
  return (
    <Row gutter={10}>
      {formMap.map((formItem: IFormItem) => {
        const { initialValue = undefined, valuePropName } = handleFormItem(formItem, formData);
        if (formItem.type === FormItemType.text)
          return (
            <Col style={{ display: formItem.invisible ? 'none' : '' }} key={formItem.key} span={formItem.colSpan || formItemColSpan}>
              {layout === 'vertical' ? (
                <>
                  <span className="dcloud-form-item-label" style={{ padding: '0 0 7px', display: 'block' }}>
                    {formItem.label}
                  </span>
                  {(formItem as any).customFormItem ? (
                    <span style={{ fontSize: '14px', padding: '0 0 16px', display: 'block' }}>{(formItem as any).customFormItem}</span>
                  ) : null}
                </>
              ) : (
                <Row style={{ padding: '6px 0 10px' }}>
                  <Col span={formLayout?.labelCol.span || 4} style={{ textAlign: 'right' }}>
                    <span className="dcloud-form-item-label" style={{ padding: '0 10px 0 0', display: 'inline-block' }}>
                      {formItem.label}:
                    </span>
                  </Col>
                  <Col span={formLayout?.wrapperCol.span || 20}>
                    <span>{(formItem as any).customFormItem}</span>
                  </Col>
                </Row>
              )}
            </Col>
          );
        return (
          <Col style={{ display: formItem.invisible ? 'none' : '' }} key={formItem.key} span={formItem.colSpan || formItemColSpan}>
            <Form.Item
              name={formItem.key}
              key={formItem.key}
              label={formItem.label}
              rules={formItem.rules || [{ required: false, message: '' }]}
              initialValue={initialValue}
              valuePropName={valuePropName}
              {...formItem.formAttrs}
            >
              {renderFormItem(formItem)}
            </Form.Item>
          </Col>
        );
      })}
    </Row>
  );
};

const ConfigForm = (props: IProps): JSX.Element => {
  const {
    formConfig,
    activeKey,
    customFormRef,
    form,
    formData,
    onHandleValuesChange,
    title,
    clearForm,
    submit,
    stop,
    running,
    customContent,
  } = props;
  const intl = useIntl();

  const onSubmit = () => {
    running ? stop() : submit();
  };

  return (
    <>
      <div className={prefixTesting}>
        <div className={`${prefixTesting}-title`}>{title}</div>
        <div>{customContent}</div>
        <div className={`${prefixTesting}-content`}>
          <div style={{ display: activeKey === 'Header' ? '' : 'none' }}>
            <EditTable ref={customFormRef} />
          </div>
          <Form form={form} layout={'vertical'} onValuesChange={onHandleValuesChange}>
            {renderFormContent({ formMap: formConfig, formData, layout: 'vertical' })}
          </Form>
        </div>

        <div className={`${prefixTesting}-footer-btn`}>
          <Button loading={running} onClick={clearForm}>
            {intl.formatMessage({ id: 'test.client.clear' })}
          </Button>
          <Button onClick={onSubmit} type="primary">
            {intl.formatMessage({ id: running ? 'test.client.stop' : 'test.client.run' })}
          </Button>
        </div>
      </div>
    </>
  );
};

export default ConfigForm;
