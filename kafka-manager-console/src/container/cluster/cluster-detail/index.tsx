import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs, PageHeader } from 'antd';
import { ClusterOverview } from './cluster-overview';
import { ClusterTopic } from './cluster-topic';
import { ClusterBroker } from './cluster-broker';
import { CurrentLimiting } from './current-limiting';
import { IBasicInfo } from 'types/base-type';
import { handleTabKey } from 'lib/utils';
import { cluster } from 'store/cluster';
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
    cluster.getClusterBasicInfo(this.clusterId);
  }

  public render() {
    let content = {} as IBasicInfo;
    content = cluster.basicInfo ? cluster.basicInfo : content;
    return (
      <>
        <PageHeader
          className="detail topic-detail-header"
          onBack={() => handlePageBack('/cluster')}
          title={`集群列表/${content.clusterName || ''}`}
        />
        <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
          <TabPane tab="集群概览" key="1">
            <ClusterOverview basicInfo={content}/>
          </TabPane>
          <TabPane tab="Topic信息" key="2">
            <ClusterTopic tab={'Topic信息'} />
          </TabPane>
          <TabPane tab="Broker信息" key="3">
            <ClusterBroker tab={'Broker信息'} />
          </TabPane>
          <TabPane tab="限流信息" key="7">
            <CurrentLimiting tab={'限流信息'} />
          </TabPane>
        </Tabs>
      </>
    );
  }
}
