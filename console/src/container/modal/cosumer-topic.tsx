import * as React from 'react';
import { modal } from 'store/modal';
import { consume } from 'store/consume';
import Modal from 'antd/es/modal';
import { observer } from 'mobx-react';
import Table from 'antd/es/table';

const columns = [
  {
    title: 'Topic 列表',
    dataIndex: 'topicName',
    key: 'topicName',
  },
];

@observer
export default class ConsumerTopic extends React.Component {
  public componentDidMount() {
    const { clusterId, consumerGroup, location } = modal.consumberGroup;
    consume.getConsumerTopic(clusterId, consumerGroup, location.toLowerCase());
  }
  public render() {
    return (
      <Modal
        title="消费的Topic列表"
        style={{ top: 200 }}
        visible={true}
        onCancel={modal.close}
        maskClosable={false}
        width={500}
        footer={null}
      >
        <Table
          rowKey="topicName"
          columns={columns}
          pagination={false}
          dataSource={consume.consumerTopic}
        />
      </Modal>
    );
  }
}
