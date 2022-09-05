import { Button, XForm } from 'knowdesign';
import { IFormItem } from 'knowdesign/es/extend/x-form';
import * as React from 'react';
import './style/form.less';
import { useIntl } from 'react-intl';

const prefixTesting = 'config-form-panel-consume';
interface IProps {
  formConfig: IFormItem[];
  formData: any;
  formRef: any;
  onHandleValuesChange?: any;
  title: string;
  customContent?: React.ReactNode;
  customForm?: React.ReactNode;
  clearForm: any;
  submit: any;
  stop: any;
  running?: boolean;
}

const ConfigForm = (props: IProps): JSX.Element => {
  const { formConfig, formRef, formData, onHandleValuesChange, title, clearForm, submit, stop, running, customContent, customForm } = props;
  const intl = useIntl();
  const onSubmit = () => {
    running ? stop() : submit();
  };
  return (
    <>
      <div className={prefixTesting}>
        <div className={`${prefixTesting}-title`}>{title}</div>
        <div className={`${prefixTesting}-content`}>
          <div>{customContent}</div>
          {customForm ? (
            customForm
          ) : (
            <XForm
              onHandleValuesChange={onHandleValuesChange}
              formData={formData}
              formMap={formConfig}
              wrappedComponentRef={formRef}
              layout={'vertical'}
            />
          )}
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
