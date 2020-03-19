import * as React from 'react';
import { Modal, Form, Row, Input, Select, DatePicker, Col, Button } from 'component/antd';
import { modal } from 'store/modal';

const Option = Select.Option;

const topicFormItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
};

class ResetOffset extends React.Component<any> {
  public handleSubmit = () => {
    debugger
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        title="重置"
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={800}
        destroyOnClose={true}
      >
        <Form {...topicFormItemLayout} >
          <Row>
            <Col span={20}>
              <Form.Item
                label="重置到指定时间"
              >
                <DatePicker showTime={true} style={{ width: '100%' }}/>
              </Form.Item>
            </Col>
            {/* <Col span={3} offset={1}>
              <Form.Item>
                <Button type="primary" size="small">确定重置</Button>
              </Form.Item>
            </Col> */}
          </Row>
          <Row>
            <Col span={20}>
              <Form.Item
                label="重置指定分区"
                style={{ marginBottom: 0 }}
              >
                <Row>
                  <Col span={8}><Select placeholder="请选择分区 id"/></Col>
                  <Col span={16}><Input placeholder="分区 offset"/></Col>
                </Row>
                <Row>
                  <Col span={8}><Select placeholder="请选择分区 id"/></Col>
                  <Col span={16}><Input placeholder="分区 offset"/></Col>
                </Row>
              </Form.Item>
              <Form.Item
                wrapperCol={{
                  offset: 8,
                }}
              >
                <Button size="small" style={{ width: '100%' }}>新增</Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'topic' })(ResetOffset);
