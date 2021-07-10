import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs } from 'antd';
import { handleTabKey } from 'lib/utils';
import { ClusterTask } from './cluster-task';
import { MigrationTask } from './migration-task';
import { VersionManagement } from '../version-management';
import { users } from 'store/users';
import { expert } from 'store/expert';

const { TabPane } = Tabs;

@observer
export class OperationManagement extends React.Component {
  public tabs = [{
    title: '迁移任务',
    component: <MigrationTask />,
  }, {
    title: '集群任务',
    component: <ClusterTask />,
  }, {
    title: '版本管理',
    component: <VersionManagement />,
  }];

  public render() {
    let tabs = [].concat(this.tabs);
    if (users.currentUser.role !== 2) {
      tabs = tabs.splice(2);
    }
    return (
      <>
        <Tabs activeKey={location.hash.substr(1) || '0'} type="card" onChange={handleTabKey}>
          {
            tabs.map((item, index) => {
              return (
                <TabPane tab={item.title} key={'' + index}>
                  {item.component}
                </TabPane>);
            })
          }
        </Tabs>
      </>
    );
  }
}
