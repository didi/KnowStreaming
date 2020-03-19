import * as React from 'react';
import { Modal, Form, Row, Input, Select, notification } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { operation } from 'store/operation';
import { topic, IAdminExpand } from 'store/topic';
import { observer } from 'mobx-react';
import { broker } from 'store/broker';
import { topicDilatation } from 'lib/api';
import urlQuery from 'store/url-query';
import { IValueLabel } from 'types/base-type';

const topicFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 11,
  },
};
@observer
class Topic extends React.Component<any> {

  public handleSubmit = () => {
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      values.partitionNum = +values.partitionNum;
      topicDilatation(values).then(() => {
        topic.getAdminTopics(urlQuery.clusterId);
        notification.success({ message: '扩容成功' });
        modal.close();
      });
    });
  }

  public componentDidMount() {
    const { clusterId, topicName } = modal.topicDetail;
    operation.initRegionOptions(clusterId);
    broker.initBrokerOptions(clusterId);
    cluster.getClusters();
    topic.getTopicMetaData(clusterId, topicName);
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const initialData = topic.topicDetail || {} as IAdminExpand;
    return (
      <Modal
        title="扩分区"
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width="680px"
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
      >
        <Form {...topicFormItemLayout}>
          <Row>
            <Form.Item label="集群名称">
              {getFieldDecorator('clusterId', {
                rules: [{ required: true, message: '请选择集群' }],
                initialValue: +initialData.clusterId || +cluster.data[1].clusterId,
              })(
                <Select disabled={true}>
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
                rules: [{ required: true, message: '请选择Topic' }],
                initialValue: initialData.topicName,
              })(
                <Input disabled={true} />,
              )}
            </Form.Item>
          </Row>
          <Form.Item label="所处Broker列表">
            <Input value={initialData.brokerIdList && initialData.brokerIdList.join(',')} disabled={true} />
          </Form.Item>
          <Form.Item
            label="已有分区数"
          >
            <Input value={initialData.partitionNum} disabled={true} />
          </Form.Item>
          <Form.Item
            label="副本数"
          >
            <Input value={initialData.replicaNum} disabled={true} />
          </Form.Item>
          <Form.Item
            label="新扩Broker列表"
          >
            {getFieldDecorator('brokerIdList', {
              initialValue: initialData.brokerIdList || [],
              rules: [{
                required: true,
                validator: (_: any, value: string, callback: (wrong?: string) => void) => {
                  if (initialData.replicaNum > value.length) {
                    callback('Broker数需要大于或等于副本数');
                  }
                  callback();
                },
              }],
            })(
              <Select mode="multiple" optionLabelProp="title" placeholder="请选择broker">
                {
                  broker.BrokerOptions.map((t: IValueLabel) => {
                    return <Select.Option value={t.value} title={t.value + ''} key={t.value} >
                      <span aria-label={t.value}> {t.label} </span>
                    </Select.Option>;
                  })
                }
              </Select>,
            )}
          </Form.Item>
          <Form.Item
            label="新增分区数"
          >
            {getFieldDecorator('partitionNum', {
              rules: [{ required: true, message: '请输入新增分区数' }],
            })(
              <Input placeholder="请输入新增分区数" />,
            )}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'topic' })(Topic);
