import React from 'react';
import { Table, PaginationConfig } from 'component/antd';
import { observer } from 'mobx-react';
import { broker } from 'store/broker';
import urlQuery from 'store/url-query';
import moment from 'moment';
import { ITopic } from 'types/base-type';
import { SearchAndFilter } from 'container/cluster-topic';

const cloumns = [{
  title: 'Topic名称',
  key: 'topicName',
  width: 350,
  onCell: () => ({
    style: {
      maxWidth: 250,
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      cursor: 'pointer',
    },
  }),
  sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
  render: (t: string, r: ITopic) => {
    return (
      <a
        href={`/admin/topic_detail?clusterId=${urlQuery.clusterId}&topic=${r.topicName}`}
        target="_blank"
      >
        {r.topicName}
      </a>
    );
  },
}, {
  title: '分区数',
  dataIndex: 'partitionNum',
  key: 'partitionNum',
  sorter: (a: ITopic, b: ITopic) => b.partitionNum - a.partitionNum,
}, {
  title: '副本数',
  dataIndex: 'replicaNum',
  key: 'replicaNum',
  sorter: (a: ITopic, b: ITopic) => b.replicaNum - a.replicaNum,
}, {
  title: '流入(KB/s)',
  dataIndex: 'byteIn',
  key: 'byteIn',
  sorter: (a: ITopic, b: ITopic) => b.byteIn - a.byteIn,
  render: (t: number) => (t / 1024).toFixed(2),
}, {
  title: '流入(QPS)',
  dataIndex: 'produceRequest',
  key: 'produceRequest',
  sorter: (a: ITopic, b: ITopic) => b.produceRequest - a.produceRequest,
  render: (t: number) => t.toFixed(2),
}, {
  title: '负责人',
  dataIndex: 'principals',
  key: 'principals',
}, {
  title: '修改时间',
  dataIndex: 'updateTime',
  key: 'updateTime',
  sorter: (a: ITopic, b: ITopic) => a.updateTime - b.updateTime,
  render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
}, {
}];

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class BrokerTopicInfo extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterClusterVisible: false,
    filterStatusVisible: false,
  };

  public componentDidMount() {
    broker.getBrokerTopic(urlQuery.clusterId, urlQuery.brokerId);
  }
  public render() {
    const { searchKey } = this.state;
    const data: ITopic[] = broker.topics.filter((d) => d.topicName.includes(searchKey) ||
      (d.principals && d.principals.includes(searchKey)));
    return (
      <>
        <div style={{ height: 45 }}>
          <ul className="table-operation-bar">
            {this.renderSearch('请输入Topic名称或者负责人')}
          </ul>
        </div>
        <Table columns={cloumns} dataSource={data} rowKey="topicName" pagination={pagination} />
      </>
    );
  }
}
