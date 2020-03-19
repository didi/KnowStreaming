import * as React from 'react';
import { Tabs } from 'component/antd';
import { BrokerBaseDetail } from './base-detail';
import { TopicAnalysis } from './topic-analysis';
import { BrokerTopicInfo } from './topic-info';
import { BrokerPartition } from './broker-partition';
import { BrokerMetrics } from './broker-index';
import { handleTabKey } from 'lib/utils';

const TabPane = Tabs.TabPane;

export class BrokerDetail extends React.Component {
  public render() {
    return (
      <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="状态信息" key="1">
          <BrokerBaseDetail />
        </TabPane>
        <TabPane tab="Topic信息" key="2">
          <BrokerTopicInfo />
        </TabPane>
        <TabPane tab="Partition信息" key="3">
          <BrokerPartition />
        </TabPane>
        {/* <TabPane tab="状态图" key="4">
          <NetWorkFlow clusterId={urlQuery.clusterId} />
        </TabPane> */}
        <TabPane tab="Topic分析" key="5">
          <TopicAnalysis />
        </TabPane>
        <TabPane tab="Broker关键指标" key="6">
          <BrokerMetrics />
        </TabPane>
      </Tabs>
    );
  }
}
