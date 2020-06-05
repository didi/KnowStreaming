import * as React from 'react';
import './index.less';

import { Table, Tabs, ColumnProps, PaginationConfig } from 'component/antd';

import { modal } from 'store';
import { cluster } from 'store/cluster';
import { observer } from 'mobx-react';
import { IClusterData } from 'types/base-type';

const TabPane = Tabs.TabPane;

const detailUrl = '/admin/cluster_detail?clusterId=';

const collectionColumns: Array<ColumnProps<IClusterData>> = [
  {
    title: '集群ID',
    dataIndex: 'clusterId',
    key: 'clusterId',
    sorter: (a: IClusterData, b: IClusterData) => a.clusterId - b.clusterId,
  },
  {
    title: '集群名称',
    key: 'clusterName',
    sorter: (a: IClusterData, b: IClusterData) => a.clusterName.charCodeAt(0) - b.clusterName.charCodeAt(0),
    render: (text, record) => {
      const url = `${detailUrl}${record.clusterId}&clusterName=${record.clusterName}`;
      return <a href={encodeURI(url)}>{record.clusterName}</a>;
    },
  },
  {
    title: 'Topic 数',
    key: 'topicNum',
    sorter: (a: IClusterData, b: IClusterData) => a.topicNum - b.topicNum,
    render: (text, record) => {
      return <a href={`${detailUrl}${record.clusterId}#1`}>{record.topicNum}</a>;
    },
  },
  {
    title: 'Broker 数量',
    dataIndex: 'brokerNum',
    key: 'brokerNum',
    sorter: (a: IClusterData, b: IClusterData) => a.brokerNum - b.brokerNum,
    render: (text, record) => {
      return (
        <a
          href={
            `${detailUrl}${record.clusterId}&clusterName=${btoa(encodeURIComponent(record.clusterName))}#2`}
        >
          {record.brokerNum}
        </a>);
    },
  },
  {
    title: 'ConsumerGroup 数',
    key: 'consumerGroupNum',
    sorter: (a: IClusterData, b: IClusterData) => a.consumerGroupNum - b.consumerGroupNum,
    render: (text, record) => {
      return <a href={`${detailUrl}${record.clusterId}#3`}>{record.consumerGroupNum}</a>;
    },
  },
  {
    title: 'Region 数',
    key: 'regionNum',
    sorter: (a: IClusterData, b: IClusterData) => a.regionNum - b.regionNum,
    render: (text, record) => {
      return <a href={`${detailUrl}${record.clusterId}&#4`}>{record.regionNum}</a>;
    },
  },
  {
    title: 'ControllerID',
    key: 'controllerId',
    sorter: (a: IClusterData, b: IClusterData) => a.controllerId - b.controllerId,
    render: (text, record) => {
      return <a href={`${detailUrl}${record.clusterId}#5`}>{record.controllerId}</a>;
    },
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    render: (text, record) => {
      return (
        <span className="table-operation">
          <a onClick={modal.showModifyCluster.bind(null, record)}>修改</a>
        </span>
      );
    },
  },
];

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class AdminHome extends React.Component {
  public renderList() {
    return (
      <Table
        columns={collectionColumns}
        dataSource={cluster.data.slice(1)}
        pagination={pagination}
        rowKey="clusterId"
      />
    );
  }

  public componentDidMount() {
    cluster.getClusters();
    cluster.getKafkaVersions();
  }

  public render() {
    return (
      <>
        <ul className="table-operation-bar">
          <li className="new-topic" onClick={modal.showNewCluster}>
            <i className="k-icon-xinjian didi-theme"/>添加集群
          </li>
        </ul>
        <Tabs defaultActiveKey="1" type="card">
          <TabPane tab="集群列表" key="1">
            {this.renderList()}
          </TabPane>
        </Tabs>
      </>
    );
  }
}
