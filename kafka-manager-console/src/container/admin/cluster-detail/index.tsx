import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs, PageHeader } from 'antd';
import { IMetaData } from 'types/base-type';
import { ClusterOverview } from './cluster-overview';
import { ClusterTopic } from './cluster-topic';
import { ClusterBroker } from './cluster-broker';
import { ClusterConsumer } from './cluster-consumer';
import { ExclusiveCluster } from './exclusive-cluster';
import { LogicalCluster } from './logical-cluster';
import { ClusterController } from './cluster-controller';
import { CurrentLimiting } from './current-limiting';
import { handleTabKey } from 'lib/utils';
import { admin } from 'store/admin';
import { handlePageBack } from 'lib/utils';
import Url from 'lib/url-parser';

const { TabPane } = Tabs;

@observer
export class ClusterDetail extends React.Component {
  public clusterId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public componentDidMount() {
    admin.getBasicInfo(this.clusterId);
  }

  public render() {
    let content = {} as IMetaData;
    content = admin.basicInfo ? admin.basicInfo : content;
    return (
      <>
        <PageHeader
          className="detail topic-detail-header"
          onBack={() => handlePageBack('/admin')}
          title={`集群列表/${content.clusterName || ''}`}
        />
        <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
          <TabPane tab="集群概览" key="1">
            <ClusterOverview basicInfo={content} />
          </TabPane>
          <TabPane tab="Topic信息" key="2">
            <ClusterTopic tab={'Topic信息'} />
          </TabPane>
          <TabPane tab="Broker信息" key="3">
            <ClusterBroker tab={'Broker信息'} basicInfo={content} />
          </TabPane>
          <TabPane tab="消费组信息" key="4">
            <ClusterConsumer tab={'消费组信息'} />
          </TabPane>
          <TabPane tab="Region信息" key="5">
            <ExclusiveCluster tab={'Region信息'} basicInfo={content} />
          </TabPane>
          <TabPane tab="逻辑集群信息" key="6">
            <LogicalCluster tab={'逻辑集群信息'} basicInfo={content} />
          </TabPane>
          <TabPane tab="Controller信息" key="7">
            <ClusterController tab={'Controller变更历史'} />
          </TabPane>
          <TabPane tab="限流信息" key="8">
            <CurrentLimiting tab={'限流信息'} />
          </TabPane>
        </Tabs>
      </>
    );
  }
}
