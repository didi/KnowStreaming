import { Tabs } from 'knowdesign';
import React, { useState } from 'react';
import TypicalListCard from '../../components/TypicalListCard';
import UserTabContent from './UserTabContent';
import RoleTabContent from './RoleTabContent';
import './index.less';

const { TabPane } = Tabs;

const UserManage = () => {
  const [curTabKey, setCurTabKey] = useState<string>(window.history.state?.tab || 'user');

  const onTabChange = (key) => {
    setCurTabKey(key);
    window.history.replaceState({ tab: key }, '');
  };

  return (
    <TypicalListCard title="用户管理">
      <Tabs defaultActiveKey={curTabKey} onChange={onTabChange}>
        <TabPane tab="人员管理" key="user">
          <UserTabContent curTabKey={curTabKey} />
        </TabPane>
        <TabPane tab="角色管理" key="role">
          <RoleTabContent curTabKey={curTabKey} />
        </TabPane>
      </Tabs>
    </TypicalListCard>
  );
};

export default UserManage;
