import * as React from 'react';

import { Table, Tabs, PaginationConfig, Select } from 'component/antd';
import Url from 'lib/url-parser';
import { topic, IGroupInfo } from 'store/topic';
import { observer } from 'mobx-react';
import { TopicDetail } from 'container/topic-detail';

import './index.less';

const TabPane = Tabs.TabPane;

const parColumns = [
  {
    title: 'Partition ID',
    dataIndex: 'partitionId',
    key: 'partitionId',
    sorter: (a: IGroupInfo, b: IGroupInfo) => a.partitionId - b.partitionId,
  },
  {
    title: 'Consume ID',
    dataIndex: 'clientId',
    key: 'clientId',
    sorter: (a: IGroupInfo, b: IGroupInfo) => +a.clientId - +b.clientId,
  },
  {
    title: 'Consumer Offset',
    dataIndex: 'consumeOffset',
    key: 'consumeOffset',
    sorter: (a: IGroupInfo, b: IGroupInfo) => a.consumeOffset - b.consumeOffset,
  },
  {
    title: 'Partition Offset',
    dataIndex: 'partitionOffset',
    key: 'partitionOffset',
    sorter: (a: IGroupInfo, b: IGroupInfo) => a.partitionOffset - b.partitionOffset,
  },
  {
    title: 'Lag',
    dataIndex: 'lag',
    key: 'lag',
    sorter: (a: IGroupInfo, b: IGroupInfo) => a.lag - b.lag,
  },
];

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class Consumer extends TopicDetail {
  public clusterId: number;
  public topicName: string;
  public group: string;
  public location: string;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
    this.location = url.search.location;
    this.group = url.search.group;
    this.handleGroupChange(this.group + ',' + this.location);
  }

  public handleGroupChange = (value: string) => {
    const { topicName, clusterId } = this;
    topic.getGroupInfo(topicName, clusterId, value.split(',')[0], value.split(',')[1]);
  }

  public renderHeader = () => {
    return (
      <div className="group-title">
        consumerGroup:
        <div className="group-select">
          <Select defaultValue={this.group} onChange={this.handleGroupChange}>
            {topic.consumeInfo.map((d) => <Select.Option value={d.consumerGroup + ',' + d.location} key={d.consumerGroup}>
              {d.consumerGroup}</Select.Option>)}
          </Select>
        </div>
      </div>
    );
  }

  public renderConsumer() {
    const data = this.state.searchKey ?
      topic.groupInfo.filter((g) => g.partitionId === Number(this.state.searchKey)) : topic.groupInfo;
    return (
      <Table
        rowKey="partitionId"
        columns={parColumns}
        dataSource={data}
        pagination={pagination}
        title={this.renderHeader}
        className={location.pathname.includes('admin') ? 'consumer-container' : ''}
      />
    );
  }

  public renderTab() {
    return (
      <TabPane tab="消费详情" key="2">
        {this.renderConsumer()}
      </TabPane>
    );
  }
}
