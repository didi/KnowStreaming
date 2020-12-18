import * as React from 'react';
import { Table, Modal, Tooltip, Icon, message, notification } from 'component/antd';
import { app } from 'store/app';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import { users } from 'store/users';
import { cellStyle } from 'constants/table';
import { cluster } from 'store/cluster';

@observer
export class OfflineClusterModal extends React.Component {

  public componentDidMount() {
    cluster.getClusterMetaTopics(modal.params);
  }

  public handleCancel = () => {
    modal.close();
    cluster.setClusterTopicsMeta([]);
  }

  public handleSubmit = () => {
    if (cluster.clusterMetaTopics.length) {
      return message.warning('存在Topic信息，无法申请下线！');
    }
    const offlineParams = {
      type: 14,
      applicant: users.currentUser.username,
      description: '',
      extensions: JSON.stringify({clusterId: modal.params}),
    };
    cluster.applyClusterOffline(offlineParams).then(data => {
      notification.success({ message: '申请下线成功' });
    });
    modal.close();
  }

  public getColumns = () => {
    const offlineColumns = [
      {
        title: 'Topic列表',
        dataIndex: 'topicName',
        key: 'topicName',
        onCell: () => ({
          style: {
            maxWidth: 250,
            ...cellStyle,
          },
        }),
        render: (t: string) => {
          return (
            <Tooltip placement="bottomLeft" title={t} >{t}</Tooltip>
          );
        },
      },
    ];
    return offlineColumns;
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
              <Tooltip placement="right" title={'如若有topic列表，则表示资源正处于使用中，禁止下线操作。如需下线，烦请下线topic列表所有topic。'} >
                <Icon className="question-icon" type="question-circle" />
              </Tooltip>
            </span>
          }
          maskClosable={false}
          onCancel={this.handleCancel}
          onOk={this.handleSubmit}
          okText="确认"
          cancelText="取消"
          okButtonProps={{ disabled: cluster.filterLoading || !!cluster.clusterMetaTopics.length }}
          width={700}
        >
          <Table
            rowKey="key"
            loading={cluster.filterLoading}
            dataSource={cluster.clusterMetaTopics}
            columns={this.getColumns()}
            scroll={{ x: 300, y: 320 }}
            pagination={false}
            bordered={true}
          />
        </Modal>
      </>
    );
  }
}
