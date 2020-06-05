import * as React from 'react';
import { Modal, Form, Row, Input, Select, notification, InputNumber, Switch, Icon, Tooltip, Col } from 'component/antd';
import { modal } from 'store/modal';
import { cluster } from 'store/cluster';
import { executeTask, modifyTask } from 'lib/api';
import { operation, ITask } from 'store/operation';
import { observer } from 'mobx-react';
import { IValueLabel } from 'types/base-type';
import { topic } from 'store/topic';
import { broker } from 'store/broker';

const topicFormItemLayout = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 11,
  },
};

@observer
class Task extends React.Component<any> {
  public handleSubmit = () => {
    this.props.form.validateFields((err: Error, values: any) => {
      if (err) return;
      const { throttle, partitionIdList } = values;
      values.throttle = throttle * 1024 * 1024;
      values.partitionIdList = typeof partitionIdList === 'string' ? values.partitionIdList.split(',') : [];
      if (this.getDisabled()) {
        const { taskId } = operation.taskDetail;
        modifyTask({ throttle: values.throttle, taskId, action: 'modify' }).then(() => {
          notification.success({ message: '修改成功' });
          operation.getTask();
          modal.close();
        });
      } else {
        executeTask(values).then(() => {
          notification.success({ message: '迁移任务创建成功' });
          operation.getTask();
          modal.close();
        });
      }
    });
  }

  public getDisabled = () => !!operation.taskDetail;

  public initSelection = (value: number) => {
    topic.getTopicList(value);
    broker.initBrokerOptions(value);
  }

  public filterSelection = (input: string, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

  public test = (): any => {
    if (modal.id === 'showTaskDetail') return null;
  }
  public render() {
    const disabled = this.getDisabled();
    const { getFieldDecorator } = this.props.form;
    const initialData = operation.taskDetail || {} as ITask;
    const isModify = modal.id === 'showTaskDetail';
    return (
      <Modal
        title="Topic 迁移"
        style={{ top: 70 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width="680px"
        destroyOnClose={true}
        okText="确定"
        cancelText="取消"
        onOk={this.handleSubmit}
        footer={this.test()}
      >
        <Form {...topicFormItemLayout} >
          <Row>
            <Form.Item label="集群名称">
              {getFieldDecorator('clusterId', {
                rules: [{ required: true, message: '请选择集群' }],
                initialValue: initialData.clusterId,
              })(
                <Select onChange={this.initSelection} disabled={disabled} placeholder="请选择集群">
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
                <Select filterOption={this.filterSelection} showSearch={true} disabled={disabled} placeholder="请选择Topic">
                  {
                    topic.topicNameList.map(t => {
                      return <Select.Option value={t} key={t}>{t}</Select.Option>;
                    })
                  }
                </Select>,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item label="分区ID列表">
              {getFieldDecorator('partitionIdList', {
                initialValue: initialData.partitionIdList || [],
              })(
                <Input disabled={isModify} />,
              )}
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label={<span>
                流量上限&nbsp;
                     <Tooltip title="仅需填写迁移流量、副本同步流量自动涵盖">
                  <Icon type="question-circle-o" />
                </Tooltip>
              </span>}
            >
              {getFieldDecorator('throttle', {
                rules: [{ required: true, message: '请输入限流值' }],
                initialValue: +(initialData.throttle / (1024 * 1024)).toFixed(2) || 1,
              })(
                <InputNumber min={0} disabled={isModify} />,
              )}
              <span className="ml-5">MB/s</span>
            </Form.Item>
          </Row>
          {
            isModify ? (
              <Row>
                <Col span={7} style={{ textAlign: 'right', color: 'rgba(0, 0, 0, 0.85)', margin: '5px 5px 0 0' }}>
                  分区列表&nbsp;
                    <Tooltip title={<span>无色：待迁移<br />半绿：迁移中<br />全绿：迁移成功<br />红色：迁移失败</span>}>
                    <Icon type="question-circle-o" />
                  </Tooltip>：
                  </Col>
                <Col span={14}>
                  <ul className="region_style">
                    {initialData.regionList && initialData.regionList.map((i, index) => {
                      return <li
                        key={index}
                        className={!initialData.migrationStatus[i[0]] ? '' :
                          initialData.migrationStatus[i[0]] === 2 ? 'success' :
                            initialData.migrationStatus[i[0]] === 3 ? 'fail' : 'pending'}
                      >分区{i[0]}: <span>{i[1].join(',')}</span>
                      </li>;
                    })}
                  </ul>
                </Col>
              </Row>
            ) : null
          }
          {
            !disabled ? (
              <>
                <Row>
                  <Form.Item label="目标Broker列表">
                    {getFieldDecorator('brokerIdList', {
                      rules: [{ required: true, message: '请输入broker' }],
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
                </Row>
                <Row>
                  <Form.Item label="说明">
                    {getFieldDecorator('description', {
                      rules: [{ required: true, message: '请输入迁移说明' }],
                    })(
                      <Input.TextArea style={{ height: 100 }} placeholder="请输入迁移说明" />,
                    )}
                  </Form.Item>
                </Row>
              </>
            ) : null
          }
        </Form>
      </Modal>
    );
  }
}

export default Form.create({ name: 'task' })(Task);
