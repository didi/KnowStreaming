import * as React from 'react';
import { Modal, Form, Row, Input, notification, Select } from 'component/antd';
import { modal } from 'store/modal';
import { users } from 'store/users';
import { addUser, modifyUser } from 'lib/api';
import { getRandomPassword } from 'lib/utils';
import { IUserDetail } from 'store/users';

import './index.less';

const topicFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 11,
  },
};

class NewUser extends React.Component<any> {

  public handleSubmit = () => {
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      this.getIsModify() ?
        modifyUser(values).then(() => {
          notification.success({ message: '修改成功' });
          modal.close();
          users.getUsers();
        }) :
        addUser(values).then(() => {
          notification.success({ message: '创建用户成功' });
          modal.close();
          users.getUsers();
        });
    });
  }

  public getPassword = () => {
    this.props.form.setFieldsValue({
      password: getRandomPassword(6),
    });
  }

  public getIsModify = () => !!modal.userDetail;

  public render() {
    const { getFieldDecorator } = this.props.form;
    const initailData = modal.userDetail || {} as IUserDetail;
    const isModify = this.getIsModify();
    return (
      <Modal
        title={isModify ? '修改用户信息' : '添加用户'}
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width="680px"
        destroyOnClose={true}
        okText="提交"
        cancelText="取消"
        onOk={this.handleSubmit}
      >
        <Form {...topicFormItemLayout} >
          <Row>
            <Form.Item label="用户名" >
              {getFieldDecorator('username', {
                rules: [{ required: true, message: '请输入用户名' }],
                initialValue: initailData.username,
              })(
                <Input placeholder="请输入用户名" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="密码">
              {getFieldDecorator('password', {
                rules: [
                  { required: !isModify, message: '请输入密码' },
                  { pattern: /^[a-zA-Z0-9]{6,10}$/, message: '请输入6-10位密码' }],
                nitialValue: initailData.password,
              })(
                <Input
                  addonAfter={<span style={{ cursor: 'pointer' }} onClick={this.getPassword}>随机生成密码</span>}
                  placeholder="请输入密码"
                />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="角色">
              {getFieldDecorator('role', {
                rules: [{ required: true, message: '请选择角色' }],
                initialValue: initailData.role,
              })(
                <Select>
                  {users.roleMap.map((r, k) => {
                    return <Select.Option key={k} value={k}>{r}</Select.Option>;
                  })}
                </Select>,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'newUser' })(NewUser);
