import * as React from 'react';
import { Table, Modal, Tooltip, Icon, message, notification } from 'component/antd';
import { app } from 'store/app';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import { users } from 'store/users';
import { urlPrefix } from 'constants/left-menu';
import { region } from 'store';
import { topic, IConnectionInfo } from 'store/topic';
import urlQuery from 'store/url-query';
import { cellStyle } from 'constants/table';
import { XFormComponent } from 'component/x-form';
import { ILimitsItem } from 'types/base-type';

const formLayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 16 },
};

@observer
export class CancelTopicPermission extends React.Component {
  private $formRef: any;

  public componentDidMount() {
    topic.getConnectionInfo(modal.params.clusterId, modal.params.topicName, urlQuery.appId);
  }

  public handleCancel = () => {
    topic.setConnectionInfo([]);
    modal.close();
  }

  public handleSubmit = () => {
    this.$formRef.validateFields((error: Error, value: ILimitsItem) => {
      if (error) {
        return;
      }
      if (topic.connectionInfo && topic.connectionInfo.length) {
        return message.warning('存在连接信息，无法取消权限！');
      }
      const appId = urlQuery.appId;
      if (value.access.length) {
        value.access = value.access.length === 2 ? '3' : value.access[0];
      }
      const extensions = {
        appId,
        clusterId: modal.params.clusterId,
        topicName: modal.params.topicName,
        access: Number(value.access),
      };
      app.cancelProdPermission({
        type: 13,
        description: '',
        applicant: users.currentUser.username,
        extensions: JSON.stringify(extensions),
      }).then((data: any) => {
        notification.success({ message: '取消权限操作成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      });
      modal.close();
    });
  }

  public getColumns = () => {
    const onlineColumns = [
      {
        title: 'AppID',
        dataIndex: 'appId',
        key: 'appId',
        width: '20%',
        sorter: (a: IConnectionInfo, b: IConnectionInfo) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
      },
      {
        title: '主机名',
        dataIndex: 'hostname',
        key: 'hostname',
        width: '40%',
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
      {
        title: '客户端版本',
        dataIndex: 'clientVersion',
        key: 'clientVersion',
        width: '20%',
      },
      {
        title: '客户端类型',
        dataIndex: 'clientType',
        key: 'clientType',
        width: '20%',
        render: (t: string) => <span>{t === 'consumer' ? '消费' : '生产'}</span>,
      },
    ] as any;
    return onlineColumns;
  }

  public render() {
    const access: number = modal.params.access;
    const consume = access === 0 || access === 1;
    const send = access === 0 || access === 2;
    const accessStatus = access === 0 ? [] : (access === 1 || access === 2) ? [access + ''] : ['1', '2'];
    const formMap = [
      {
        key: 'access',
        label: '权限',
        type: 'check_box',
        defaultValue: accessStatus,
        options: [{
          label: '取消消费权限',
          value: '1',
          disabled: send,
        }, {
          label: '取消发送权限',
          value: '2',
          disabled: consume,
        }],
        rules: [{ required: access !== 0, message: '请选择' }],
      },
    ] as any;

    return (
      <>
        <Modal
          visible={true}
          className="stream-debug-modal"
          title={
            <span>
              取消权限申请
              {topic.connectionInfo.length ?
                <Tooltip placement="right" title={'如若有连接信息，则表示资源正处于使用中，禁止下线操作。如需下线，烦请关闭连接信息中的Kafka发送/消费客户端后再进行下线。'} >
                  <Icon className="question-icon" type="question-circle" />
                </Tooltip> : null}
            </span>
          }
          maskClosable={false}
          onCancel={this.handleCancel}
          onOk={this.handleSubmit}
          okText="确定"
          cancelText="取消"
          okButtonProps={{ disabled: topic.loading || !!topic.connectionInfo.length }}
          width={700}
        >
          <>
          <Table
            title={() => '连接信息'}
            loading={topic.loading}
            className="custom-content"
            rowKey="key"
            scroll={{ x: 450, y: 260 }}
            dataSource={topic.connectionInfo}
            columns={this.getColumns()}
            pagination={false}
            bordered={true}
          />
            <div className="topic-x-form-box">
                <XFormComponent
                  ref={form => this.$formRef = form}
                  formData={{}}
                  formMap={formMap}
                  formLayout={formLayout}
                />
                <div>
                  注意： 如需取消权限，请把对应选项打勾！
                </div>
            </div>
          </>
        </Modal>
      </>
    );
  }
}
