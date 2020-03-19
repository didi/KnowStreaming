import * as React from 'react';
import { Modal, Form, Row, Input, Select, Radio, message } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { INewCluster } from 'types/base-type';
import { newCluster, modifyCluster } from 'lib/api';

const Option = Select.Option;

const topicFormItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 12,
  },
};

class Cluster extends React.Component<any> {
  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    const { clusterId } = modal.currentCluster;
    this.props.form.validateFieldsAndScroll((err: any, values: INewCluster) => {
      if (err) return;
      const commonFn = (fn: any) => {
        fn.then(() => {
          message.success(this.getTips());
          modal.close();
          cluster.getClusters();
        });
      };
      clusterId ?
      commonFn(modifyCluster(Object.assign(values, {clusterId}))) : commonFn(newCluster(values));
  });
}

  public getTips() {
    if (modal.currentCluster.clusterId) return '修改成功';
    return '添加成功';
  }

  public getTitle() {
    if (modal.currentCluster.clusterId) return '修改集群';
    return '添加集群';
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        title={this.getTitle()}
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={700}
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
      >
        <Form {...topicFormItemLayout} >
          <Row>
            <Form.Item
              label="集群名称"
            >
              {getFieldDecorator('clusterName', {
                rules: [{ required: true, message: '请输入集群名称' }],
                initialValue: modal.currentCluster.clusterName,
              })(
                <Input
                  placeholder="请输入集群名称"
                />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="zookeeper地址"
            >
              {getFieldDecorator('zookeeper', {
                rules: [{ required: true, message: '请输入 zookeeper 地址' }],
                initialValue: modal.currentCluster.zookeeper,
              })(
                <Input.TextArea
                  placeholder="请输入 zookeeper 地址"
                />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="kafka版本"
            >
              {getFieldDecorator('kafkaVersion', {
                rules: [{ required: true, message: '请选择 kafka 版本' }],
                initialValue: modal.currentCluster.kafkaVersion,
              })(
                <Select placeholder="请选择 kafka 版本">
                  {cluster.kafkaVersions.map((v) => {
                    return <Option key={v} value={v}>{v}</Option>;
                  })}
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="集群访问地址"
            >
              {getFieldDecorator('bootstrapServers', {
                rules: [{ required: true, message: '请输入集群访问地址' }],
                initialValue: modal.currentCluster.bootstrapServers,
              })(
                <Input.TextArea placeholder="请输入集群访问地址" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="SASL JAAS配置"
            >
              {getFieldDecorator('saslJaasConfig', {
                initialValue: modal.currentCluster.saslJaasConfig,
              })(
                <Input placeholder="请输入SASL JAAS配置" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="SASL机制"
            >
              {getFieldDecorator('saslMechanism', {
                initialValue: modal.currentCluster.saslMechanism,
              })(
                <Input placeholder="请输入SASL机制" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="安全协议"
            >
              {getFieldDecorator('securityProtocol', {
                initialValue: modal.currentCluster.securityProtocol,
              })(
                <Input placeholder="请输入安全协议" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="是否开启告警"
            >
              {getFieldDecorator('alarmFlag', {
                rules: [{ required: true, message: '请选择是否开启告警' }],
                initialValue: modal.currentCluster.alarmFlag === undefined ? 1 : modal.currentCluster.alarmFlag,
              })(
                <Radio.Group>
                  <Radio value={1}>是</Radio>
                  <Radio value={0}>否</Radio>
                </Radio.Group>,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'cluster' })(Cluster);
