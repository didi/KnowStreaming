import * as React from 'react';
import { Modal, Form, Input, Button, Table } from 'component/antd';
import { modal } from 'store/modal';
import Url from 'lib/url-parser';
import { cluster } from 'store/cluster';
import { addRebalance } from 'lib/api';
import { observer } from 'mobx-react';

const topicFormItemLayout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 16,
  },
};

const columns = [
  {
    title: '集群',
    dataIndex: 'clusterName',
    key: 'clusterName',
  },
  {
    title: 'Broker ID',
    dataIndex: 'brokerId',
    key: 'brokerId',
  },
  {
    title: 'Status',
    dataIndex: 'status',
    key: 'status',
  },
];

@observer
class LeaderRebalance extends React.Component<any> {
  public clusterName: string;
  public clusterId: number;
  public brokerId: number;

  public state = {
    loading: false,
  };
  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterName = decodeURI(atob(url.search.clusterName));
    this.clusterId = Number(url.search.clusterId);
  }

  public handleSubmit = (e: React.MouseEvent<any, MouseEvent>) => {
    e.preventDefault();
    this.setState({ loading: true });
    this.props.form.validateFieldsAndScroll((err: any, values: any) => {
      this.brokerId = Number(values.brokerId);
      addRebalance({ brokerId: this.brokerId, clusterId: this.clusterId, dimension: 0 }).then(() => {
        cluster.getRebalance(this.clusterId).then(() => {
          if (cluster.leaderStatus && cluster.leaderStatus !== 'RUNNING') {
            this.setState({ loading: false });
          }
        });
      });
    });
  }

  public render() {
    const { getFieldDecorator } = this.props.form;
    const { brokerId, clusterName } = this;
    return (
      <Modal
        title="Leader Rebalance"
        style={{ top: 70 }}
        visible={true}
        footer={null}
        onCancel={modal.close}
        width={500}
        destroyOnClose={true}
      >
        <Form onSubmit={this.handleSubmit}  {...topicFormItemLayout}>
          <Form.Item label="集群">
            <Input value={this.clusterName} disabled={true} />
          </Form.Item>
          <Form.Item label="brokerId">
            {getFieldDecorator('brokerId', {
              rules: [{ required: true, message: 'brokerId 不能为空' }],
            })(<Input placeholder="请输入 brokerId" />)}
          </Form.Item>
          <Form.Item label="">
            <Button style={{ left: 380 }} type="primary" htmlType="submit" loading={this.state.loading}>执行
            </Button>
          </Form.Item>
        </Form>
        {
          cluster.leaderStatus ? <Table
            rowKey="clusterName"
            columns={columns}
            pagination={false}
            dataSource={[{ clusterName, brokerId, status: cluster.leaderStatus }]}
          /> : ''
        }
      </Modal>
    );
  }
}

export default Form.create({ name: 'LeaderRebalance' })(LeaderRebalance);
