import * as React from 'react';
import { Drawer, Form, Row, Button, Input, InputNumber, notification } from 'component/antd';
import { drawer } from 'store/drawer';

import './index.less';
import { topic } from 'store/topic';

const topicFormItemLayout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 14,
  },
};

class TopicSample extends React.Component<any> {
  public state = {
    loading: false,
  };

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { offset, partitionId } = values;
      const bothExist = [offset, partitionId].filter(item => item === undefined || item === null).length;
      if (bothExist === 1) {
        notification.error({ message: '分区号和偏移量必须同时存在' });
        return false;
      }
      this.setState({ loading: true });
      topic.addSample(Object.assign(drawer.topicData, values)).then(() => this.setState({ loading: false }));
    });
  }

  public cleanData = () => {
    topic.sampleData = null;
    drawer.close();
  }
  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Drawer
        title="Topic 采样"
        width={620}
        closable={false}
        onClose={this.cleanData}
        visible={true}
        destroyOnClose={true}
      >
        <div className="o-container">
          <Form {...topicFormItemLayout} labelAlign="right" onSubmit={this.handleSubmit} >
            <Row>
              <Form.Item label="最大采样条数" >
                {getFieldDecorator('maxMsgNum', {
                  rules: [{ required: true, message: '请输入最大采样条数' }],
                  initialValue: 1,
                })(<Input />)}
              </Form.Item>
            </Row>
            <Row>
              <Form.Item label="最大采样时间">
                {getFieldDecorator('timeout', {
                  rules: [{ required: true, message: '请输入最大采样时间' }],
                  initialValue: 3000,
                })(<Input />)}
              </Form.Item>
            </Row>
            <Row>
              <Form.Item label="分区号">
                {getFieldDecorator('partitionId')(<InputNumber min={0} />)}
              </Form.Item>
            </Row>
            <Row>
              <Form.Item label="偏移量">
                {getFieldDecorator('offset')(<InputNumber min={0} />)}
              </Form.Item>
            </Row>
            <Row>
              <Form.Item className="b-item">
                <Button type="primary" htmlType="submit" loading={this.state.loading}>确认</Button>
                <Button type="primary" onClick={this.cleanData}>取消</Button>
              </Form.Item>
            </Row>
          </Form>
          {
            topic.sampleData ?
              topic.sampleData.map((i: any, index: number) => {
                return <Input.TextArea
                  value={i.value}
                  style={{ height: 120, width: 500, marginTop: 10 }}
                  key={index}
                />;
              }) : null
          }
        </div>
      </Drawer>
    );
  }
}

export default Form.create({ name: 'topicSample' })(TopicSample);
