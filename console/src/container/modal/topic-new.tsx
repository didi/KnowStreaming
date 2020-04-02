import * as React from 'react';
import { Modal, Form, Row, Input, Select, InputNumber, notification } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { createTopic } from 'lib/api';
import { ITopic } from 'types/base-type';
import { operation } from 'store/operation';
import urlQuery from 'store/url-query';
import { getCookie } from 'lib/utils';

const topicFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 11,
  },
};

const bussDesc = `概要描述Topic的数据源, Topic数据的生产者/消费者, Topic的申请原因及备注信息等。`;

class Topic extends React.Component<any> {
  public state = {
    loading: false,
  };

  public getDisabled = () => !!modal.topicData;

  public handleSubmit = () => {
    if (this.getDisabled()) return modal.close();
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { principalList, retentionTime } = values;
      values.principalList = principalList.split(',');
      values.retentionTime = retentionTime;
      this.setState({ loading: true });
      createTopic(values).then(data => {
        notification.success({ message: '申请Topic成功' });
        window.setTimeout(() => location.assign('/user/my_order'), 500);
        modal.close();
      }, (err) => {
        this.setState({ loading: false });
      });
    });
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const disabled = this.getDisabled();
    const { loading } = this.state;
    const initialData = modal.topicData || {} as ITopic;
    return (
      <Modal
        title={disabled ? 'Topic申请详情' : 'Topic申请'}
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close.bind(null, null)}
        maskClosable={false}
        width="680px"
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
        confirmLoading={loading}
      >
        <Form {...topicFormItemLayout}>
          <Row>
            <Form.Item label="集群名称">
              {getFieldDecorator('clusterId', {
                rules: [{ required: true, message: '请选择集群' }],
                initialValue: +urlQuery.clusterId || initialData.clusterId || (cluster.data[1] && cluster.data[1].clusterId),
              })(
                <Select disabled={disabled} onChange={operation.initRegionOptions}>
                  {
                    cluster.data.slice(1).map(c => {
                      return <Select.Option value={c.clusterId} key={c.clusterId}>{c.clusterName}</Select.Option>;
                    })
                  }
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="Topic名称">
              {getFieldDecorator('topicName', {
                rules: [{ required: true, message: '请输入Topic 名称' },
                { pattern: /^[a-zA-Z0-9_-]{1,64}$/, message: '格式不正确' }],
                initialValue: initialData.topicName,
              })(
                <Input placeholder="支持字母、数字、下划线、中划线，6-64个字符" disabled={disabled} />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="负责人">
              {getFieldDecorator('principalList', {
                rules: [{ required: true, message: '请输入负责人' }],
                initialValue: disabled ? initialData.principals || ' ' : getCookie('username'),
              })(
                <Input disabled={disabled} placeholder="多个负责人请用逗号隔开" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="保存时间">
              {getFieldDecorator('retentionTime', {
                rules: [{ required: true, message: '请输入保存时间' }],
                initialValue: (initialData.retentionTime / 3600000) || 24,
              })(
                <InputNumber disabled={disabled} min={0} />,
              )}
              <span className="ml-5">小时</span>
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="流量上限">
              {getFieldDecorator('peakBytesIn', {
                rules: [{ required: true, message: '请输入限流值' }],
                initialValue: initialData.peakBytesIn || 1,
              })(
                <InputNumber min={0} disabled={disabled} />,
              )}
              <span className="ml-5">MB/s</span>
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="业务说明" >
              {getFieldDecorator('description', {
                rules: [{ required: true, message: '请输入业务说明' }],
                initialValue: initialData.description || '',
              })(
                <Input.TextArea disabled={disabled} placeholder={bussDesc} style={{ height: 100 }} />,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'topic' })(Topic);
