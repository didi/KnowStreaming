import * as React from 'react';
import { Modal, Form, Row, Input, Select, InputNumber, notification, Switch } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { adminCreateTopic, modifyTopic } from 'lib/api';
import { ITopic, IValueLabel } from 'types/base-type';
import { operation } from 'store/operation';
import { broker } from 'store/broker';
import urlQuery from 'store/url-query';
import { observer } from 'mobx-react';
import { topic } from 'store/topic';
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

@observer
class Topic extends React.Component<any> {
  public state = {
    loading: false,
    targetType: false,
  };

  public getDisabled = () => !!modal.topicData;

  public handleTarget = (targetType: boolean) => {
    this.setState({ targetType });
  }

  public handleSubmit = () => {
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { principalList, retentionTime } = values;
      values.principalList = typeof principalList === 'string' ? principalList.split(',') : values.principalList;
      values.retentionTime = retentionTime * 3600000;
      this.setState({ loading: true });
      const fn = this.getDisabled() ? modifyTopic : adminCreateTopic;
      fn(values).then(data => {
        topic.getAdminTopics(urlQuery.clusterId);
        notification.success(this.getDisabled() ? { message: '修改Topic成功' } : { message: 'Topic创建成功' });
        modal.close();
      }, (err) => {
        this.setState({ loading: false });
      });
    });
  }
  public componentDidMount() {
    operation.initRegionOptions(urlQuery.clusterId);
    broker.initBrokerOptions(urlQuery.clusterId);
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const { loading } = this.state;
    const initialData = modal.topicData || {} as ITopic;
    const disabled = this.getDisabled();
    return (
      <Modal
        title={disabled ? '修改Topic' : 'Topic创建'}
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
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
                initialValue: +urlQuery.clusterId,
              })(
                <Select disabled={true} onChange={operation.initRegionOptions}>
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
                initialValue: disabled ? initialData.principalList || ' ' : getCookie('username'),
              })(
                <Input placeholder="多个负责人请用逗号隔开" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="保存时间">
              {getFieldDecorator('retentionTime', {
                rules: [{ required: true, message: '请输入保存时间' }],
                initialValue: disabled ? (initialData.retentionTime / 3600000).toFixed(0) : 24,
              })(
                <InputNumber min={0} />,
              )}
              <span className="ml-5">小时</span>
            </Form.Item>
          </Row>
          {!disabled ?
            <>
              <Row>
                <Form.Item label="Broker类型">
                  <Switch
                    checkedChildren="region"
                    unCheckedChildren="broker"
                    onChange={this.handleTarget}
                  />
                </Form.Item>
              </Row>
              <Row>
                {
                  this.state.targetType ? (
                    <Form.Item label="Target Region">
                      {getFieldDecorator('regionIdList', {
                        rules: [{ required: true, message: '请输入选择' }],
                      })(
                        <Select mode="multiple">
                          {
                            operation.RegionOptions.map(t => {
                              return <Select.Option value={t.value} key={t.value}>{t.label}</Select.Option>;
                            })
                          }
                        </Select>,
                      )}
                    </Form.Item>
                  ) : (
                      <Form.Item label="目标BrokerID">
                        {getFieldDecorator('brokerIdList', {
                          rules: [{ required: true, message: '请输入broker' }],
                        })(
                          <Select mode="multiple" optionLabelProp="title">
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
                    )
                }
              </Row>
            </> : null
          }
          <Row>
            <Form.Item label="分区数">
              {getFieldDecorator('partitionNum', {
                rules: [{ required: true, message: '请输入partition数' }],
                initialValue: topic.topicDetail && topic.topicDetail.partitionNum,
              })(
                <Input placeholder="请输入partition数" disabled={disabled} />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="副本数">
              {getFieldDecorator('replicaNum', {
                rules: [{ required: true, message: '请输入副本数' }],
                initialValue: topic.topicDetail && topic.topicDetail.replicaNum,
              })(
                <Input placeholder="请输入副本数" disabled={disabled} />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="自定义属性" >
              {getFieldDecorator('properties', {
                initialValue: initialData.properties,
              })(
                <Input.TextArea placeholder="请严格按照JSON格式来填写:{'key':'value'}" />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="业务说明" >
              {getFieldDecorator('description', {
                initialValue: initialData.description || disabled && ' ',
              })(
                <Input.TextArea placeholder={bussDesc} style={{ height: 100 }} />,
              )}
            </Form.Item>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'topic' })(Topic);
