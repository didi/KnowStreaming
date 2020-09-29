import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs } from 'antd';
import { handleTabKey } from 'lib/utils';
import { AdminAppList } from './admin-app-list';
import { UserManagement } from './user-management';
import { ConfigureManagement } from './configure-management';

const { TabPane } = Tabs;

@observer
export class PlatformManagement extends React.Component {
  public render() {
    return (
      <>
      <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="应用管理" key="1">
          <AdminAppList />
        </TabPane>
        <TabPane tab="用户管理" key="2">
          <UserManagement />
        </TabPane>
        <TabPane tab="配置管理" key="3">
          <ConfigureManagement />
        </TabPane>
      </Tabs>
      </>
    );
  }
}
