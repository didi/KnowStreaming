import * as React from 'react';
import { Modal, Form, Row, Input, Select, notification, Col, Radio } from 'component/antd';
import { modal } from 'store/modal';
import { alarm } from 'store/alarm';
import { observer } from 'mobx-react';
import { cluster } from 'store/cluster';
import { addAlarm, modifyAlarm } from 'lib/api';
import { topic } from 'store/topic';
import { broker } from 'store/broker';
import { getRandomPassword, getCookie } from 'lib/utils';
import { IAlarmBase } from 'types/base-type';

const Option = Select.Option;

const topicFormItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
};

@observer
class Alarm extends React.Component<any> {
  public data: any = null;
  public state = {
    loading: false,
    type: 'Lag',
  };

  public handleSubmit = () => {
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      this.setState({ loading: true });
      const { metric, opt, threshold, duration, alarmName, principalList: principal, actionTag, status } = values;
      const principalList = typeof (principal) === 'object' ? principal : principal.split(',');
      const strategyActionList = [{ actionTag, actionWay: 'KAFKA' }];
      const strategyFilterList = Array.from(['topicName', 'consumerGroup', 'brokerId', 'clusterId'], (item) => {
        if ((this.state.type === 'Lag' || !+getCookie('role')) && item === 'brokerId') return;
        if (this.state.type !== 'Lag' && item === 'consumerGroup' || JSON.stringify(values[item]) === '[]') return;
        return {
          key: item,
          value: values[item],
        };
      }).filter(i => i);
      const params: IAlarmBase = {
        status, strategyActionList, strategyFilterList, alarmName, principalList,
        strategyExpressionList: [{ metric, opt, threshold: +threshold, duration: +duration }],
      };
      const notiMessage = alarm.curData ? { message: '修改成功' } : { message: '添加告警成功' };
      const fn = alarm.curData ? modifyAlarm : addAlarm;
      fn(alarm.curData ? Object.assign({ id: alarm.curData.id }, params) : params).then(() => {
        notification.success(notiMessage);
        alarm.getAlarm();
        modal.close();
      }, (err) => {
        this.setState({ loading: false });
      });
    });
  }

  public initSelection = (value: number) => {
    topic.getTopicList(value);
    broker.initBrokerOptions(value);
  }

  public componentDidMount = () => {
    cluster.getClusters();
    if (this.isModify()) {
      this.setState({ type: alarm.curData.strategyExpressionList[0].metric }, () => {
        this.data = this.getFilterList();
      });
    }
  }

  public onChange = (type: any) => {
    this.setState({ type });
  }

  public filterSelection = (input: string, option: any) => {
    return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }

  public getFilterList = () => {
    const filterList = new Map();
    alarm.curData.strategyFilterList.forEach(item => {
      filterList.set(item.key, item.value);
    });
    this.initSelection(+filterList.get('clusterId'));
    return filterList;
  }

  public getActionTag = () => {
    this.props.form.setFieldsValue({
      actionTag: 'KAFKA_' + getRandomPassword(),
    });
  }

  public isModify = () => !!alarm.curData;

  public render() {
    const { getFieldDecorator } = this.props.form;
    const { loading, type } = this.state;
    const isModify = this.isModify();
    const initialData = alarm.curData;
    const { data } = this;
    const role = +getCookie('role');
    return (
      <Modal
        title="告警配置"
        style={{ top: 30 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={700}
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
        confirmLoading={loading}
      >
        <Form {...topicFormItemLayout} >
          <Form.Item label="告警名称">
            {getFieldDecorator('alarmName', {
              rules: [{ required: true, message: '告警规则名称' }],
              initialValue: isModify ? initialData.alarmName : '',
            })(
              <Input placeholder="请输入告警配置名称" />,
            )}
          </Form.Item>
          <Form.Item label="负责人">
            {getFieldDecorator('principalList', {
              rules: [{ required: true, message: '请输入负责人' }],
              initialValue: isModify ? initialData.principalList : getCookie('username'),
            })(
              <Input placeholder="多个负责人请用逗号隔开" />,
            )}
          </Form.Item>
          <Row>
            <Col span={6}>
              <p style={{ textAlign: 'right', color: 'rgba(0,0,0,.85)', paddingTop: '8px' }}>告警规则： </p>
            </Col>
            <Col span={14} className="ruleArea">
              <Row>
                <Col span={12}>
                  <Form.Item wrapperCol={{ span: 22 }}>
                    {getFieldDecorator('metric', {
                      rules: [{ required: true, message: '请选择 Metric' }],
                      initialValue: isModify ? initialData.strategyExpressionList[0].metric : '',
                    })(
                      <Select placeholder="请选择 Metric" onChange={this.onChange}>
                        {
                          alarm.alarmConstant.metricTypeList.map((ele, index) => {
                            return <Option key={index} value={Object.keys(ele)[0]}> {Object.keys(ele)[0]}</Option>;
                          })
                        }
                      </Select>,
                    )}
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item wrapperCol={{ span: 22 }}>
                    {getFieldDecorator('opt', {
                      rules: [{ required: true, message: '请选择 Condition' }],
                      initialValue: isModify ? initialData.strategyExpressionList[0].opt : '',
                    })(
                      <Select placeholder="请选择 Condition">
                        {
                          alarm.alarmConstant.conditionTypeList.map((ele, index) => {
                            return <Option key={index} value={Object.keys(ele)[0]}> {Object.keys(ele)[0]} </Option>;
                          })
                        }
                      </Select>,
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <Form.Item wrapperCol={{ span: 22 }}>
                    {getFieldDecorator('threshold', {
                      rules: [{ required: true, message: '请输入 value' }],
                      initialValue: isModify ? initialData.strategyExpressionList[0].threshold : '',
                    })(
                      <Input addonBefore="metricValue" />,
                    )}
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item wrapperCol={{ span: 22 }}>
                    {getFieldDecorator('duration', {
                      rules: [{ required: true, message: '请输入持续时间' }],
                      initialValue: isModify ? initialData.strategyExpressionList[0].duration : '',
                    })(
                      <Input addonBefore="duration" />,
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Col>
          </Row>
          <Row>
            <Col span={6}>
              <p style={{ textAlign: 'right', color: 'rgba(0,0,0,.85)', paddingTop: '8px' }}>过滤规则： </p>
            </Col>
            <Col span={14} className="ruleArea">
              <span className="label">集群名称</span>
              <Form.Item wrapperCol={{ span: 25 }} className="t-input">
                {getFieldDecorator('clusterId', {
                  rules: [{ required: true, message: '请选择集群' }],
                  initialValue: data && +data.get('clusterId') || '',
                })(
                  <Select onChange={this.initSelection} placeholder="请选择集群" >
                    {
                      cluster.data.slice(1).map(c => {
                        return <Select.Option value={c.clusterId} key={c.clusterId}>{c.clusterName}</Select.Option>;
                      })
                    }
                  </Select>,
                )}
              </Form.Item>
              <Row>
                <span className="label">Topic名称</span>
                <Form.Item wrapperCol={{ span: 25 }} className="t-input">
                  {getFieldDecorator('topicName', {
                    rules: [{ required: type === 'Lag' || !role, message: '请选择Topic' }],
                    initialValue: data && data.get('topicName') || '',
                  })(
                    <Select
                      placeholder="请选择Topic"
                      allowClear={true}
                      filterOption={this.filterSelection}
                      showSearch={true}
                    >
                      {
                        topic.topicNameList.map(t => {
                          return <Select.Option value={t} key={t}>{t}</Select.Option>;
                        })
                      }
                    </Select>,
                  )}
                </Form.Item>
              </Row>
              {type !== 'Lag' ? role ? (
                <Row>
                  <span className="label">BrokeId</span>
                  <Form.Item wrapperCol={{ span: 25 }} className="t-input">
                    {getFieldDecorator('brokerId', {
                      initialValue: data && data.get('brokerId') || [],
                    })(
                      <Select optionLabelProp="title" placeholder="请选择Broker" allowClear={true}>
                        {
                          broker.BrokerOptions.map(t => {
                            return <Select.Option value={t.value} title={t.value + ''} key={t.value} >
                              <span aria-label={t.value}> {t.label} </span>
                            </Select.Option>;
                          })
                        }
                      </Select>,
                    )}
                  </Form.Item>
                </Row>
              ) : null : (
                  <>
                    <span className="label">消费组</span>
                    <Form.Item wrapperCol={{ span: 25 }} className="t-input">
                      {getFieldDecorator('consumerGroup', {
                        rules: [{ required: type === 'Lag', message: '请输入消费组' }],
                        initialValue: data && data.get('consumerGroup') || '',
                      })(
                        <Input placeholder="请输入消费组" />,
                      )}
                    </Form.Item>
                  </>
                )}
            </Col>
          </Row>
          <Form.Item label="TagName">
            {getFieldDecorator('actionTag', {
              rules: [{ required: true, message: '请输入tag' }],
              initialValue: isModify ? initialData.strategyActionList[0].actionTag : '',
            })(
              <Input placeholder="请输入tag" addonAfter={<span style={{ cursor: 'pointer' }} onClick={this.getActionTag}>自动生成</span>} />,
            )}
          </Form.Item>
          <Form.Item label="开启告警">
            {getFieldDecorator('status', {
              initialValue: isModify ? initialData.status : 1,
            })(
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>,
            )}
          </Form.Item>
        </Form>
      </Modal >
    );
  }
}

export default Form.create({ name: 'alarm' })(Alarm);
