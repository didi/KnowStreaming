import * as React from 'react';

import urlQuery from 'store/url-query';
import { NetWorkFlow } from 'container/topic-detail/com';
import { Tabs } from 'component/antd';
import { broker } from 'store/broker';
import { BrokerStatus } from 'container/broker-info/base-info';
import { BrokerList } from 'container/broker-info/base-info';
import { AdminConsume } from 'container/admin-consume';
import { AdminRegion } from 'container/admin-region';
import { AdminController } from 'container/admin-controller';
import AdminTopic from 'container/admin-topic/index';
import { handleTabKey } from 'lib/utils';

export class ClusterDetail extends React.Component {

  public updateStatus = () => {
    broker.getBrokerNetwork(urlQuery.clusterId);
  }
  public componentDidMount() {
    this.updateStatus();
  }
  public render() {
    return (
      <>
        <Tabs type="card" activeKey={location.hash.substr(1) || '0'} onChange={handleTabKey}>
          <Tabs.TabPane tab="集群流量" key="0">
            <div className="k-row right-flow">
              <p className="k-title">历史流量</p>
              <NetWorkFlow clusterId={urlQuery.clusterId} />
            </div>
            <div className="k-row right-flow" style={{ marginTop: '50px' }}>
              <p className="k-title">实时流量</p>
              <span className="k-abs" onClick={this.updateStatus}>
                <i className="k-icon-shuaxin didi-theme" />刷新
              </span>
              <BrokerStatus />
            </div>
          </Tabs.TabPane>
          <Tabs.TabPane tab="Topic管理" key="1">
            <AdminTopic />
          </Tabs.TabPane>
          <Tabs.TabPane tab="Broker状态概览" key="2">
            <BrokerList />
          </Tabs.TabPane>
          <Tabs.TabPane tab="ConsumerGroup列表" key="3">
            <AdminConsume />
          </Tabs.TabPane>
          <Tabs.TabPane tab="Region管理" key="4">
            <AdminRegion />
          </Tabs.TabPane>
          <Tabs.TabPane tab="Controller变更历史" key="5">
            <AdminController />
          </Tabs.TabPane>
        </Tabs>

      </>
    );
  }
}
