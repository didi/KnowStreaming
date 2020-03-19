import * as React from 'react';
import { Modal, Form, Row, Col, Input, Select, Button, notification, Switch } from 'component/antd';
import { modal } from 'store/modal';
import { addAdminPartition } from 'lib/api';
import { broker } from 'store/broker';
import { observer } from 'mobx-react';
import moment from 'moment';
import { getCookie } from 'lib/utils';
import { order } from 'store/order';
import { IValueLabel } from 'types/base-type';
import './index.less';

const topicFormItemLayout = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 14,
  },
};

@observer
class PartitionApprove extends React.Component<any> {
  public state = {
    orderStatus: 1,
  };
  public componentDidMount() {
    const { clusterId } = modal.orderDetail;
    broker.initBrokerOptions(clusterId);
  }

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    if (modal.id === 'showPartitionDetail') { modal.close(); return false; }
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { orderStatus } = this.state;
      const { orderId } = modal.orderDetail;
      const { partitionNum: num } = values;
      values.partitionNum = +num;
      const params = Object.assign(values, { orderStatus, orderId });
      addAdminPartition(params).then(() => {
        notification.success({ message: '审批成功' });
        order.getAdminOrder();
        modal.close();
      });
    });
  }

  public handleReject = (e: React.MouseEvent<any, MouseEvent>) => {
    const event = e.currentTarget;
    this.setState({ orderStatus: 2 }, () => {
      event.nextElementSibling.click();
    });
  }

  public renderFooter = () => {
    return (
      <Form.Item>
        {
          modal.id !== 'showPartitionDetail' ?
            <>
              <Button type="primary" onClick={this.handleReject}>驳回</Button>
              <Button type="primary" onClick={this.handleSubmit}>通过</Button>
            </>
            : null
        }
      </Form.Item>
    );
  }

  public render() {
    const { orderDetail } = modal;
    const disabled = modal.id === 'showPartitionDetail';
    const { getFieldDecorator } = this.props.form;
    const { orderStatus } = this.state;
    return (
      <Modal
        title={disabled ? 'Topic扩容申请-工单详情' : 'Topic扩容工单审批'}
        style={{ top: 20 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width="900px"
        footer={this.renderFooter()}
        destroyOnClose={true}
      >
        <Form {...topicFormItemLayout}>
          <Row>
            <Col span={12}>
              <Form.Item label="工单ID" >
                <Input value={orderDetail.orderId} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="集群名称" >
                <Input value={orderDetail.clusterName} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="Topic名称" >
                <Input value={orderDetail.topicName} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="现有分区" >
                <Input value={orderDetail.partitionNum} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="预估峰值(MB/s)" >
                <Input value={orderDetail.predictBytesIn} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="申请人" >
                <Input value={orderDetail.applicant} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="实际峰值(MB/s)" >
                <Input value={orderDetail.realBytesIn} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="审批人" >
                <Input value={getCookie('username')} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="申请时间" >
                <Input value={moment(orderDetail.gmtCreate).format('YYYY-MM-DD HH:mm:ss')} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="备注信息" >
                <Input value={orderDetail.description} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="Region列表" >
                <Input value={orderDetail.regionNameList} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="RegionBroker列表" >
                <Input value={orderDetail.regionBrokerIdList} disabled={true} />
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item label="当前Broker列表" >
                <Input value={orderDetail.brokerIdList} disabled={true} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="新增分区数" >
                {getFieldDecorator('partitionNum', {
                  rules: [{ required: orderStatus === 1, message: '不能为空' }],
                })(<Input placeholder="请输入partition数" disabled={disabled} />)}
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Form.Item label="当前Broker列表" labelCol={{ span: 4 }} wrapperCol={{ span: 7 }}>
              {getFieldDecorator('brokerIdList', {
                rules: [{ required: orderStatus === 1, message: '不能为空' }],
                initialValue: orderDetail.brokerIdList || [],
              })(
                <Select mode="multiple" optionLabelProp="title" disabled={disabled} >
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
          </Row>
          <Row style={{ paddingTop: '20px', borderTop: '1px dashed rgba(0,0,0,.1)' }}>
            <Col span={12}>
              <Form.Item label="审批意见">
                {getFieldDecorator('approvalOpinions', {
                  rules: [{ required: orderStatus === 2, message: '审批意见不能为空' }],
                  initialValue: orderDetail.approvalOpinions || undefined,
                })(<Input.TextArea style={{ height: 50 }} disabled={disabled} />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'partitionApprove' })(PartitionApprove);
