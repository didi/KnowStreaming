import * as React from 'react';

import { Form, Row, Input, notification, Button } from 'component/antd';
import { modifyUser } from 'lib/api';
import { getCookie } from 'lib/utils';

import './index.less';

class ModifyUser extends React.Component<any>  {
  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.props.form.validateFields((err: Error, values: any) => {
      const username = getCookie('username');
      const role = getCookie('role');
      const { oldPassword, password } = values;
      if (err) return;
      modifyUser({ role, username, oldPassword, password }).then(() => {
        notification.success({ message: '修改成功' });
        this.handleReset();
      });
    });
  }

  public comparePassword = (rule: any, value: any, callback: any) => {
    const { form } = this.props;
    if (value && value !== form.getFieldValue('password')) {
      callback('两次密码不相同');
    } else {
      callback();
    }
  }

  public handleReset = () => {
    this.props.form.resetFields();
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <div className="fw-container">
        <Form onSubmit={this.handleSubmit} style={{ width: '80%' }} >
          <Row>
            <Form.Item >
              {getFieldDecorator('oldPassword', {
                rules: [{ required: true, message: '请输入原密码' }],
              })(
                <Input placeholder="请输入原密码" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item>
              {getFieldDecorator('password', {
                rules: [{ required: true, message: '请输入新密码' }],
              })(
                <Input.Password placeholder="请输入新密码" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item>
              {getFieldDecorator('confirm password', {
                rules: [{ required: true, message: '请重新输入新密码' }, {
                  validator: this.comparePassword,
                }],
              })(
                <Input.Password placeholder="请重新输入新密码" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item className="style-button">
              <Button type="primary" htmlType="submit">确认</Button>
              <Button type="primary" onClick={this.handleReset}>取消</Button>
            </Form.Item>
          </Row>
        </Form>
      </div>
    );
  }
}

export default Form.create({ name: 'modifyUser' })(ModifyUser);
