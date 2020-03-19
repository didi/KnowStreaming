import * as React from 'react';
import { Tabs } from 'component/antd';
import { BrokerList } from './base-info';
import { BrokerOverview } from './broker-overview';

const TabPane = Tabs.TabPane;

export class BrokerInfo extends React.Component {
  public render() {
    return (
      <Tabs defaultActiveKey="1" type="card">
        <TabPane tab="Broker状态概览" key="1">
          <BrokerList />
        </TabPane>
        {/* <TabPane tab="Broker状态总览" key="2">
          <BrokerOverview />
        </TabPane> */}
      </Tabs>
    );
  }
}
