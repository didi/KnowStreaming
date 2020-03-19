import * as React from 'react';
import { Modal, Form, Row, Input, Select, notification } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { addPartitionApprove } from 'lib/api';
import { IBaseOrder } from 'types/base-type';
import { topic } from 'store/topic';
import { observer } from 'mobx-react';
import { order } from 'store/order';

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
    if (!!modal.topicDetail) modal.close();
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      if (modal.topicDetail) values.clusterId = modal.topicDetail.clusterId || values.clusterId;
      addPartitionApprove(values).then(() => {
        notification.success({ message: '申请成功' });
        order.getOrder();
        modal.close();
      });
    });
  }

  public filterSelection = (input: string, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

  public initSelection = (value: number) => {
    topic.getTopicList(value);
    cluster.getClusters();
  }

  public componentDidMount() {
    const value = !!modal.topicDetail ? modal.topicDetail.clusterId : cluster.data[1].clusterId;
    this.initSelection(value);
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const initialData = modal.topicDetail || {} as IBaseOrder;
    const disabled = !!modal.topicDetail;
    const isDetail = location.pathname.includes('topic_detai');
    return (
      <Modal
        title={isDetail ? '扩容申请' : disabled ? 'Topic 扩容申请详情' : 'Topic 扩容申请'}
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
        <Form {...topicFormItemLayout} className="t-expand" >
          <Row>
            <Form.Item label="集群名称">
              {getFieldDecorator('clusterId', {
                rules: [{ required: true, message: '请选择集群' }],
                initialValue: +initialData.clusterId || +cluster.data[1].clusterId,
              })(
                <Select onChange={this.initSelection} disabled={disabled}>
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
                <Select disabled={disabled} filterOption={this.filterSelection} showSearch={true}>
                  {
                    topic.topicNameList.map(t => {
                      return <Select.Option value={t} key={t}>{t}</Select.Option>;
                    })
                  }
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Form.Item
            label="预估峰值流量"
          >
            {getFieldDecorator('predictBytesIn', {
              rules: [{ required: true, message: '请输入预估峰值流量' }],
              initialValue: initialData.predictBytesIn,
            })(
              <Input disabled={!isDetail && disabled} />,
            )}
            <span className="ml-5">MB/s</span>
          </Form.Item>
          {
            !isDetail && disabled ? (
              <>
                <Form.Item label="审批人">
                  <Input disabled={disabled} value={initialData.approver} />
                </Form.Item>
                <Form.Item label="审批人意见">
                  <Input disabled={disabled} value={initialData.approvalOpinions} />
                </Form.Item>
              </>) : null
          }
          <Row>
            <Form.Item
              label="备注说明"
            >
              {getFieldDecorator('description', {
                rules: [{ required: true, message: '请输入申请原因' }],
                initialValue: initialData.description,
              })(
                <Input.TextArea disabled={!isDetail && disabled} style={{ height: 100 }} />,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'topic' })(Topic);
