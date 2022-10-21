import React from 'react';
import { Form, Button, Input, Row, InputNumber, Utils } from 'knowdesign';
import message from '@src/components/Message';
import { FormMap } from './config';
import Api from '../../api';
import { useHistory } from 'react-router-dom';
import { systemCipherKey } from '@src/constants/common';

export enum FormItemType {
  input = 'input',
  inputPassword = 'inputPassword',
  inputNumber = 'inputNumber',
  custom = 'custom',
}

export interface IFormItem {
  key: string;
  type: FormItemType;
  attrs?: any;
  rules?: any[];
  invisible?: boolean;
  customFormItem?: any;
}

export const renderFormItem = (item: IFormItem) => {
  switch (item.type) {
    default:
    case FormItemType.input:
      return <Input key={item.key} {...item.attrs} />;
    case FormItemType.inputPassword:
      return <Input.Password key={item.key} {...item.attrs} />;
    case FormItemType.inputNumber:
      return <InputNumber key={item.key} {...item.attrs} />;
    case FormItemType.custom:
      return (item as IFormItem).customFormItem;
  }
};

export const LoginForm: React.FC<any> = (props) => {
  const [form] = Form.useForm();
  const history = useHistory();

  const handleSubmit = async ({ userName, pw }: { userName: string; pw: string }) => {
    Utils.post(Api.login, {
      userName,
      pw: Utils.encryptAES(pw, systemCipherKey),
    }).then((res) => {
      message.success('登录成功');
      localStorage.setItem('userInfo', JSON.stringify(res));
      const redirectPath = window.location.search.slice('?redirect='.length) || '/';
      history.replace(redirectPath);
    });
  };

  return (
    <>
      <Form name="normal_login" form={form} className="login-form" onFinish={handleSubmit} layout={'vertical'}>
        {FormMap.map((formItem) => {
          return (
            <Row key={formItem.key}>
              <Form.Item key={formItem.key} name={formItem.key} label={formItem.label} rules={formItem.rules} style={{ width: '100%' }}>
                {renderFormItem(formItem)}
              </Form.Item>
            </Row>
          );
        })}
        <Form.Item key={'submit'}>
          <Row>
            <Button className="submit-btn" type="primary" htmlType="submit">
              登录
            </Button>
          </Row>
        </Form.Item>
      </Form>
    </>
  );
};
