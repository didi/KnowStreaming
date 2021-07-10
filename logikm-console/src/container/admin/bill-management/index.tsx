import * as React from 'react';
import { handleTabKey } from 'lib/utils';
import { PersonalBill } from './personal-bill';
import { Tabs } from 'antd';

const { TabPane } = Tabs;

export class BillManagement extends React.Component {
  public render() {
    return(
      <>
        <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="个人账单" key="1">
          <PersonalBill />
        </TabPane>
      </Tabs>
      </>
    );
  }
}
