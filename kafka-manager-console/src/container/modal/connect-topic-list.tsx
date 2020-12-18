import * as React from 'react';
import { Table, Modal, Tooltip, Icon, message, notification, Alert } from 'component/antd';
import { getApplyOnlineColumns } from 'container/topic/config';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import { users } from 'store/users';
import { urlPrefix } from 'constants/left-menu';
import { region } from 'store';
import { topic } from 'store/topic';

@observer
export class ConnectTopicList extends React.Component {

  public componentDidMount() {
    topic.getConnectionInfo(modal.params.clusterId, modal.params.topicName, modal.params.appId);
  }

  public handleCancel = () => {
    topic.setConnectionInfo([]);
    modal.close();
  }

  public handleSubmit = () => {
    const connectionList = topic.connectionInfo;
    if (connectionList && connectionList.length) {
      return message.warning('存在连接信息，无法申请下线！');
    }
    const online = {
      clusterId: modal.params.clusterId,
      topicName: modal.params.topicName,
    };
    const offlineParams = {
      type: 10,
      applicant: users.currentUser.username,
      description: '',
      extensions: JSON.stringify(online),
    };
    topic.applyTopicOnline(offlineParams).then((data: any) => {
      notification.success({ message: '申请下线成功' });
      window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
    });
    modal.close();
  }

  public render() {
    return (
      <>
        <Modal
          visible={true}
          className="stream-debug-modal"
          title={
            <span>
              申请下线
              <Tooltip placement="right" title={' 如若有连接信息，则表示资源正处于使用中，禁止下线操作。如需下线，烦请关闭连接信息中的Kafka发送/消费客户端后再进行下线。 '} >
                <Icon className="question-icon" type="question-circle" />
              </Tooltip>
            </span>
          }
          maskClosable={false}
          onCancel={this.handleCancel}
          onOk={this.handleSubmit}
          okText="确认"
          cancelText="取消"
          okButtonProps={{ disabled: topic.connectLoading || !!topic.connectionInfo.length }}
          width={700}
        >
        <Table
          rowKey="key"
          title={() => '连接信息'}
          loading={topic.connectLoading}
          scroll={{ x: 450, y: 260 }}
          dataSource={topic.connectionInfo}
          columns={getApplyOnlineColumns()}
          pagination={false}
          bordered={true}
        />
        <Alert message="如若有连接信息，则表示资源正处于使用中，禁止下线操作。如需下线，烦请关闭连接信息中的Kafka发送/消费客户端后再进行下线。" type="error" showIcon />
        </Modal>
      </>
    );
  }
}
