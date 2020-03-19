import * as React from 'react';
import { Modal, Form, Row, Col, Input, Select, Button, notification, Collapse, Switch } from 'component/antd';
import { modal } from 'store/modal';
import { addTopicApprove } from 'lib/api';
import { region } from 'store/region';
import './index.less';
import { broker } from 'store/broker';
import moment from 'moment';
import { observer } from 'mobx-react';
import { order } from 'store/order';
import { IValueLabel } from 'types/base-type';

const topicFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 16,
  },
};

@observer
class OrderApprove extends React.Component<any> {
  public state = {
    broker: false,
    orderStatus: 1,
  };

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    if (modal.id === 'showOrderDetail') { modal.close(); return false; }
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { orderStatus } = this.state;
      const { orderId } = modal.orderDetail;
      const params = Object.assign(values, { orderStatus, orderId });
      addTopicApprove(params).then(() => {
        notification.success({ message: '审批成功' });
        order.getAdminOrder();
        modal.close();
      });
    });
  }

  public handleReject = (e: React.MouseEvent<any, MouseEvent>) => {
    const event = e.currentTarget;
    this.setState({orderStatus: 2}, () => {
      event.nextElementSibling.click();
    });
  }

  public changeBroker = (broker: boolean) => {
    this.setState({ broker });
  }

  public componentDidMount = () => {
    region.getRegions(modal.orderDetail.clusterId);
    broker.initBrokerOptions(modal.orderDetail.clusterId);
  }

  public renderFooter = () => {
    return (
      <Form.Item>
        {
          modal.id !== 'showOrderDetail' ?
            <>
              <Button type="primary" onClick={this.handleReject}>驳回</Button>
              <Button type="primary" onClick={this.handleSubmit}>同意</Button>
            </>
            : null
        }
      </Form.Item>
    );
  }

  public render() {
    const { orderDetail } = modal;
    const disabled = modal.id === 'showOrderDetail';
    const { getFieldDecorator } = this.props.form;
    const { orderStatus } = this.state;
    return (
      <Modal
        title={disabled ? 'Topic申请工单详情' : 'Topic申请工单审批'}
        style={{ top: 20 }}
        visible={true}
        maskClosable={false}
        width="1000px"
        destroyOnClose={true}
        footer={this.renderFooter()}
        onCancel={modal.close}
      >
        <Collapse className="order-detail" bordered={false} destroyInactivePanel={true} defaultActiveKey={['1', '2']}>
          <Collapse.Panel header="工单信息" key="1">
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
                  <Form.Item label="Topic 名称" >
                    <Input value={orderDetail.topicName} disabled={true} />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="流量上限" >
                    <Input value={orderDetail.peakBytesIn} disabled={true} addonAfter="MB/s" />
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item label="申请人" >
                    <Input value={orderDetail.applicant} disabled={true} />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="负责人" >
                    <Input value={orderDetail.principals} disabled={true} />
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item label="工单处理状态" >
                    <Input value={orderDetail.statusStr} disabled={true} />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="工单申请时间" >
                    <Input value={moment(orderDetail.gmtCreate).format('YYYY-MM-DD HH:mm:ss')} disabled={true} />
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item label="业务意义" >
                    <Input.TextArea value={orderDetail.description} disabled={true} />
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Collapse.Panel>
          <Collapse.Panel header="Topic配置" key="2">
            <Form {...topicFormItemLayout} onSubmit={this.handleSubmit}>
              <Row>
                <Col span={12}>
                  <Form.Item label="Broker类型">
                    <Switch
                      disabled={disabled}
                      onChange={this.changeBroker}
                      checkedChildren="region"
                      unCheckedChildren="broker"
                    />
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  {
                    this.state.broker ?
                      <Form.Item label="region">
                        {getFieldDecorator('regionIdList', {
                          rules: [{ required: orderStatus === 1, message: '不能为空' }],
                        })(
                          <Select disabled={disabled} mode="multiple">
                            {
                              region.data.map(r => {
                                return <Select.Option key={r.regionId} value={r.regionId}>
                                  {r.regionName}
                                </Select.Option>;
                              })
                            }
                          </Select>,
                        )}
                      </Form.Item>
                      :
                      <Form.Item label="BrokerID">
                        {getFieldDecorator('brokerIdList', {
                          rules: [{ required: orderStatus === 1, message: '不能为空' }],
                          initialValue: (orderDetail.brokers && orderDetail.brokers.split(',')) ||
                            (orderDetail.regions && orderDetail.regions.split(',')) || [],
                        })(
                          <Select mode="multiple" optionLabelProp="title" disabled={disabled}>
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
                  }
                </Col>
                <Col span={12}>
                  <Form.Item label="副本数" >
                    {getFieldDecorator('replicaNum', {
                     rules: [{ required: orderStatus === 1, message: '不能为空' }],
                      initialValue: orderDetail.replicaNum || '',
                    })(<Input placeholder="请输入副本数" disabled={disabled} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item label="数据保留时间" >
                    {getFieldDecorator('retentionTime', {
                      rules: [{ required: orderStatus === 1, message: '不能为空' }],
                      initialValue: orderDetail && orderDetail.retentionTime / 3600000 || '',
                    })(<Input addonAfter="小时" placeholder="请输入数据保留时间" disabled={disabled} />)}
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="分区数" >
                    {getFieldDecorator('partitionNum', {
                      rules: [{ required: orderStatus === 1, message: '不能为空' }],
                      initialValue: orderDetail.partitionNum,
                    })(<Input placeholder="请输入partition数" disabled={disabled} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item label="审批意见" >
                    {getFieldDecorator('approvalOpinions', {
                      rules: [{ required: orderStatus === 2, message: '审批意见不能为空' }],
                      initialValue: orderDetail.approvalOpinions || undefined,
                    })(<Input.TextArea style={{ height: 80 }} disabled={disabled} />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Collapse.Panel>
        </Collapse>
      </Modal>
    );
  }
}

export default Form.create({ name: 'orderApprove' })(OrderApprove);
