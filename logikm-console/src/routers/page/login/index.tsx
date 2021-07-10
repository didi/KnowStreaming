import * as React from 'react';

import { Form, Row, Input, Button, notification, Icon } from 'component/antd';
import { userLogin } from 'lib/api';
import { setCookie } from 'lib/utils';
import './index.less';
import bgImgUrl from '../../../assets/image/login-bg.png';
import kafkaImgUrl from '../../../assets/image/kafka-manager.png';

class Login extends React.Component<any> {
  public state = {
    show: '',
  };

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      userLogin(values).then((data: any) => {
        setCookie([{ key: 'username', value: data.username, time: 1 }, { key: 'role', value: data.role, time: 1 }]);
        notification.success({ message: '登录成功' });
        this.props.history.push('/');
      });
    });
  }

  public handleReset = () => {
    this.props.form.resetFields();
  }

  public handleKeyPress = (e: any) => {
    if (e.nativeEvent.keyCode === 13) {
      this.handleSubmit(e);
    }
  }

  public inputOnBlur = () => {
    this.setState({ show: 'show' });
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <div className="l-container" style={{ background: `url(${bgImgUrl})`, backgroundSize: '100% 100%'}}>
        <img className="top-title" src={kafkaImgUrl} />
        <div className="f-container">
          <Form onSubmit={this.handleSubmit} labelAlign="left" >
            <Form.Item className="item-style">
              {getFieldDecorator('username')(
                <Input
                  placeholder="请输入用户名"
                  onChange={this.inputOnBlur}
                />,
              )}
            </Form.Item>
            <Form.Item className="item-style">
              {getFieldDecorator('password')(
                <Input.Password
                  placeholder="请输入密码"
                />,
              )}
            </Form.Item>
            <Form.Item className="b-item btn-style">
              <Button htmlType="submit" onKeyPress={this.handleKeyPress}>登录</Button>
            </Form.Item>
          </Form>
        </div>
      </div>
    );
  }
}

export default Form.create({ name: 'login' })(Login);
