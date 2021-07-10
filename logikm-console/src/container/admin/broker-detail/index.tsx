import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs, PageHeader } from 'antd';
import { handleTabKey } from 'lib/utils';
import { IMetaData } from 'types/base-type';
import Url from 'lib/url-parser';
import { BaseInfo } from './base-info';
import { MonitorInfo } from './monitor-info';
import { TopicInfo } from './topic-info';
import { DiskInfo } from './disk-info';
import { PartitionInfo } from './partition-info';
import { TopicAnalysis } from './topic-analysis';
import { handlePageBack } from 'lib/utils';
import { admin } from 'store/admin';

const { TabPane } = Tabs;

@observer
export class BrokerDetail extends React.Component {
  public clusterId: number;
  public brokerId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
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
          onBack={() => handlePageBack(`/admin/cluster-detail?clusterId=${this.clusterId}#3`)}
          title={`集群列表/${content.clusterName || ''}/${this.brokerId || ''}`}
      />
      <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="基本信息" key="1">
          <BaseInfo/>
        </TabPane>
        <TabPane tab="监控信息" key="2">
          <MonitorInfo />
        </TabPane>
        <TabPane tab="Topic信息" key="3">
          <TopicInfo tab={'Topic信息'} />
        </TabPane>
        <TabPane tab="磁盘信息" key="4">
          <DiskInfo tab={'磁盘信息'} />
        </TabPane>
        <TabPane tab="partition信息" key="5">
          <PartitionInfo tab={'partition信息'} />
        </TabPane>
        <TabPane tab="Topic分析" key="6">
          <TopicAnalysis />
        </TabPane>
      </Tabs>
      </>
    );
  }
}
