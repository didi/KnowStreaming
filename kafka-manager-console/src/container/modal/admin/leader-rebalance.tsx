import * as React from 'react';
import { Table, notification, Button, Modal, Input, Form, Select, message, Tooltip, Icon, Spin } from 'component/antd';
import { IBrokersMetadata, IRebalance } from 'types/base-type';
import { admin } from 'store/admin';
import { implementRegions, rebalanceStatus } from 'lib/api';
import { searchProps } from 'constants/table';

interface IXFormProps {
  form: any;
  changeVisible?: (visible: boolean) => any;
  visible?: boolean;
  clusterId?: number;
  clusterName?: string;
}

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 },
};

class LeaderRebalanceModal extends React.Component<IXFormProps> {
  public brokerId: number;
  public host: string;
  public metadata = [] as IBrokersMetadata[];
  public timer = null as any;

  public state = {
    imVisible: false,
    status: '',
    isExecutionBtn: false
  };

  public handleRebalanceCancel() {
    this.props.changeVisible(false);
    this.props.form.resetFields();
    this.setState({ imVisible: false });
    clearInterval(this.timer);
  }

  public onMetaChange(value: number) {
    this.brokerId = value;
    this.metadata.forEach((element: IBrokersMetadata) => {
      if (element.brokerId === value) {
        this.host = element.host;
      }
    });
  }

  public handleSubmit = (e: any) => {
    e.preventDefault();
    this.props.form.validateFields((err: any, values: any) => {
      values.brokerId && this.setState({ isExecutionBtn: true })
      if (!err) {
        let params = {} as IRebalance;
        params = {
          clusterId: this.props.clusterId,
          brokerId: values.brokerId,
          dimension: 2,
          regionId: 0,
          topicName: '',
        };
        implementRegions(params).then(data => {
          // message.success('获取成功');
          this.getStatus();
          this.setState({
            imVisible: true,
            isExecutionBtn: false
          });
        }).catch((err) => {
          message.error('获取失败')
          this.setState({
            imVisible: true,
            isExecutionBtn: false
          });
        })
      }
    });
  }

  // 定时器
  public iTimer = () => {
    this.timer = setInterval(() => {
      this.getStatus();
    }, 3000);
  }

  public getStatus() {
    rebalanceStatus(this.props.clusterId).then((data: any) => {
      // message.success('状态更新成功');
      if (data.code === 30) { // code -1 未知 101 成功 30 运行中
        setTimeout(this.iTimer, 0);
      } else {
        this.setState({ status: data.message });
        clearInterval(this.timer);
      }
    });
  }

  // 组件清除时清除定时器
  public componentWillUnmount() {
    clearInterval(this.timer);
  }
  // 执行加载图标
  public antIcon = <Icon type="loading" style={{ fontSize: 12, color: '#cccccc' }} spin />

  public render() {
    const { visible } = this.props;
    const reblanceData = [
      {
        clusterName: this.props.clusterName,
        host: this.host,
        status: this.state.status,
      },
    ];
    const columns = [
      {
        title: '集群名称',
        dataIndex: 'clusterName',
        key: 'clusterName',
      },
      {
        title: 'BrokerHost',
        dataIndex: 'host',
        key: 'host',
      },
      {
        title: 'Status',
        dataIndex: 'status',
        key: 'status',
      },
    ];
    this.metadata = admin.brokersMetadata ? admin.brokersMetadata : this.metadata;
    const { getFieldDecorator } = this.props.form;
    return (
      <>
        <Modal
          visible={visible}
          title="Leader Rebalance"
          onCancel={() => this.handleRebalanceCancel()}
          maskClosable={false}
          footer={null}
        >
          <Form {...layout} name="basic" onSubmit={this.handleSubmit} >
            <Form.Item label="集群名称" >
              {getFieldDecorator('clusterName', {
                initialValue: this.props.clusterName,
                rules: [{ required: true, message: '请输入集群名称' }],
              })(<Input disabled={true} />)}
            </Form.Item>
            <Form.Item label="Broker" >
              {getFieldDecorator('brokerId', {
                rules: [{ required: true, message: '请输入Broker' }],
              })(
                <Select
                  onChange={(value: number) => this.onMetaChange(value)}
                  {...searchProps}
                >
                  {this.metadata.map((v, index) => (
                    <Select.Option
                      key={v.brokerId || v.key || index}
                      value={v.brokerId}
                    >
                      {v.host.length > 16 ?
                        <Tooltip placement="bottomLeft" title={v.host}> {v.host} </Tooltip>
                        : v.host}
                    </Select.Option>
                  ))}
                </Select>)}
            </Form.Item>
            <Form.Item label="" >
              {getFieldDecorator('submit')(
                <Button
                  htmlType="submit"
                  type="primary"
                  className="implement-button"
                  disabled={this.state.isExecutionBtn}
                >
                  {this.state.isExecutionBtn ? (<span>执行中<Spin indicator={this.antIcon} size="small" /></span>) : '执行'}
                </Button>,
              )}
            </Form.Item>
            {
              this.state.imVisible && <Table
                rowKey="clusterName"
                bordered={true}
                dataSource={reblanceData}
                columns={columns}
                pagination={false}
              />
            }
          </Form>
        </Modal>
      </>
    );
  }
}

export const LeaderRebalanceWrapper = Form.create<IXFormProps>()(LeaderRebalanceModal);
