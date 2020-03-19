import * as React from 'react';
import { Drawer, Form, Row, Button, Input, DatePicker, Col, Select, message, Alert } from 'component/antd';
import { drawer } from 'store/drawer';
import { topic } from 'store/topic';
import { consume } from 'store/consume';
import { observer } from 'mobx-react';

import './index.less';
import moment = require('moment');

@observer
class ResetOffset extends React.Component<any> {

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { timestamp } = values;
      consume.offsetPartition(Object.assign({ timestamp: +moment(timestamp).format('x') }, topic.currentGroup))
        .then(() => {
          message.success('重置时间成功');
          setTimeout(() => {
            location.reload();
          }, 200);
        });
    });
  }
  public submitPartiton = () => {
    consume.offsetPartition(topic.currentGroup, 1).then(() => {
      message.success('重置分区成功');
      setTimeout(() => {
        location.reload();
      }, 200);
    });
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Drawer
        title="重置消费偏移"
        width={520}
        closable={false}
        onClose={drawer.close}
        visible={true}
        destroyOnClose={true}
      >
        <Alert message="重置之前一定要停止消费任务！！！" type="warning" showIcon={true} />
        <div className="o-container">
          <Form labelAlign="left" onSubmit={this.handleSubmit} >
            <Row>
              <p style={{ fontSize: '14px', color: '#f38031' }}>重置到指定时间</p>
            </Row>
            <Row gutter={16}>
              <Col span={6}>
                <Form.Item label="请选择时间" >
                  {getFieldDecorator('timestamp', {
                    rules: [{ required: true, message: '请选择时间' }],
                    initialValue: moment(),
                  })(
                    <DatePicker showTime={true} style={{ width: '50%' }} />,
                  )}
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item className="timeButton">
                  <Button type="primary" htmlType="submit">
                    重置
                  </Button>
                </Form.Item>
              </Col>
            </Row>
            <Row className="headLine">
              <p style={{ fontSize: '14px' }}>重置到指定分区</p>
            </Row>
            <Row>
              <Form.Item>
                <Row>
                  <Col span={8}>partitionId</Col>
                  <Col span={16}>partitionOffset</Col>
                </Row>
                {consume.offsetList.map((ele, index) => {
                  return (
                    <Row key={index} gutter={16}>
                      <Col span={8}>
                        <Select placeholder="请选择" onChange={consume.selectChange.bind(consume, index)}>
                          {topic.groupInfo.map((r, k) => {
                            return <Select.Option key={k} value={r.partitionId}>{r.partitionId}</Select.Option>;
                          })}
                        </Select></Col>
                      <Col span={10}>
                        <Input placeholder="请输入partition offset" onChange={consume.inputChange.bind(consume, index)} />
                      </Col>
                      <Col span={6} className="b-list">
                        <Button type="dashed" icon="plus" onClick={consume.handleList.bind(null, null)} />
                        <Button
                          type="dashed"
                          icon="minus"
                          disabled={index === 0}
                          onClick={consume.handleList.bind(null, index)}
                        />
                      </Col>
                    </Row>
                  );
                })}
                <Row className="partionButton">
                  <Button type="primary" onClick={this.submitPartiton}>重置</Button>
                </Row>
              </Form.Item>
            </Row>
          </Form>
        </div>
      </Drawer>
    );
  }
}

export default Form.create({ name: 'topicSample' })(ResetOffset);
