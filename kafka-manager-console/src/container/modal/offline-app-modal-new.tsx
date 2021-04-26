import * as React from 'react';
import { Table, Modal, Tooltip, Icon, message, notification, Alert, Button } from 'component/antd';
import { app } from 'store/app';
import { getApplyOnlineColumns } from 'container/topic/config';
import { observer } from 'mobx-react';
import { modal } from 'store/modal';
import { users } from 'store/users';
import { urlPrefix } from 'constants/left-menu';
import { region } from 'store';

@observer
export class ConnectAppNewList extends React.Component {

  public componentDidMount() {
    app.getAppsConnections(modal.params);
  }

  public handleCancel = () => {
    app.setAppsConnections([]);
    modal.close();
  }

  public handleSubmit = () => {
    const connectionList = app.appsConnections;
    if (connectionList && connectionList.length) {
      return message.warning('存在连接信息，无法申请下线！');
    }
    const offlineParams = {
      type: 11,
      applicant: users.currentUser.username,
      description: '',
      extensions: JSON.stringify({ appId: modal.params }),
    };
    app.applyAppOffline(offlineParams).then((data: any) => {
      notification.success({ message: '申请下线成功' });
      window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
    });
    modal.close();
  }

  public render() {
    const connectionList = app.appsConnections;
    return (
      <>
        <Modal
          visible={true}
          className="stream-debug-modal"
          title="提示"
          maskClosable={false}
          onCancel={this.handleCancel}
          // onOk={this.handleSubmit}
          // okText="确认"
          // cancelText="取消"
          okButtonProps={{ disabled: app.connectLoading || !!app.appsConnections.length }}
          footer={connectionList && connectionList.length ?
            <Button type="primary" onClick={this.handleCancel}>确定</Button>
            :
            <>
              <Button onClick={this.handleCancel}>取消</Button>
              <Button type="primary" onClick={this.handleSubmit}>确定</Button>
            </>
          }
          width={500}
        >
          <div style={{ textAlign: 'center', fontWeight: "bolder" }}>
            {
              connectionList && connectionList.length
                ?
                <span>该应用已与Topic关联，请先解除应用与Topic之间的关系。<a href={`${urlPrefix}/topic/app-detail?appId=${modal.params}`}>点击查看</a></span>
                :
                <span>应用下线后，AppID、密钥将会失效，请谨慎操作！</span>
            }
          </div>
        </Modal>
      </>
    );
  }
}
