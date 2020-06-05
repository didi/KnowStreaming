import * as React from 'react';
import './index.less';
import { Table, Modal, notification, PaginationConfig, Button, Spin } from 'component/antd';
import { broker, IBroker, IBrokerNetworkInfo, IBrokerPartition } from 'store/broker';
import { observer } from 'mobx-react';
import { StatusGraghCom } from 'component/flow-table';
import urlQuery from 'store/url-query';
import moment from 'moment';
import { deleteBroker } from 'lib/api';
import { SearchAndFilter } from 'container/cluster-topic';

import './index.less';
import { modal } from 'store';
import { tableFilter } from 'lib/utils';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 5,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class BrokerStatus extends StatusGraghCom<IBrokerNetworkInfo> {
  public getData() {
    return broker.network;
  }
}

@observer
export class BrokerList extends SearchAndFilter {
  public state = {
    searchKey: '',
    searchId: '',
    filterRegionVisible: false,
    filterStatusVisible: false,
    filterVisible: false,
    filterRVisible: false,
  };

  public colPartition = (list: IBroker[]) => {
    const region = Object.assign({
      title: 'Region',
      dataIndex: 'regionName',
      key: 'regionName',
      filters: tableFilter<IBroker>(list, 'regionName'),
      onFilter: (value: string, record: IBroker) => record.regionName === value,
    }, this.renderColumnsFilter('filterRVisible'));

    const status = Object.assign({
      title: '已同步',
      dataIndex: 'underReplicatedPartitionCount',
      key: 'underReplicatedPartitionCount',
      filters: [{ text: '是', value: '1' }, { text: '否', value: '0' }],
      onFilter: (value: string, record: IBrokerPartition) => {
        // underReplicatedPartitionCount > 0 表示未同步完成
        const syncStatus = record.underReplicatedPartitionCount ? '0' : '1';
        return syncStatus === value;
      },
      render: (text: number) => (
        <>
          <span style={{ marginRight: 8 }}>{text ? '否' : '是'}</span>
        </>
      ),
    }, this.renderColumnsFilter('filterVisible'));

    return [{
      title: 'BrokerID',
      dataIndex: 'brokerId',
      key: 'brokerId',
      sorter: (a: IBrokerPartition, b: IBrokerPartition) => a.brokerId - b.brokerId,
    }, {
      title: '峰值(MB/s)',
      dataIndex: 'bytesInPerSec',
      key: 'bytesInPerSec',
      sorter: (a: IBrokerPartition, b: IBrokerPartition) => a.bytesInPerSec - b.bytesInPerSec,
      render: (t: number) => (t / (1024 * 1024)).toFixed(2),
    }, {
      title: '分区数量',
      dataIndex: 'partitionCount',
      key: 'partitionCount',
      sorter: (a: IBrokerPartition, b: IBrokerPartition) => a.partitionCount - b.partitionCount,
    }, {
      title: 'Leader数量',
      dataIndex: 'leaderCount',
      key: 'leaderCount',
      sorter: (a: IBrokerPartition, b: IBrokerPartition) => a.leaderCount - b.leaderCount,
    }, {
      title: '未同步副本数量',
      dataIndex: 'notUnderReplicatedPartitionCount',
      key: 'notUnderReplicatedPartitionCount',
      sorter: (a: IBrokerPartition, b: IBrokerPartition) =>
        a.notUnderReplicatedPartitionCount - b.notUnderReplicatedPartitionCount,
    },
      status,
      region,
    ];
  }

  public colList = (list: IBroker[]) => {
    const region = Object.assign({
      title: 'Region',
      dataIndex: 'regionName',
      key: 'regionName',
      filters: tableFilter<IBroker>(list, 'regionName'),
      onFilter: (value: string, record: IBroker) => record.regionName === value,
    }, this.renderColumnsFilter('filterRegionVisible'));

    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      filters: [{ text: '未使用', value: '未使用' }, { text: '使用中', value: '使用中' }],
      onFilter: (value: string, record: IBroker) => record.status === value,
      render: (t: number) => t ? '未使用' : '使用中',
    }, this.renderColumnsFilter('filterStatusVisible'));

    return [{
      title: 'BrokerID',
      dataIndex: 'brokerId',
      key: 'brokerId',
      sorter: (a: IBroker, b: IBroker) => a.brokerId - b.brokerId,
      render: (t: string, record: IBroker) => {
        return (
          <a
            href={`/admin/broker_detail?clusterId=${urlQuery.clusterId}&brokerId=${record.brokerId}`}
            target="_blank"
          >
            {t}
          </a>
        );
      },
    }, {
      title: '主机',
      dataIndex: 'host',
      key: 'host',
      sorter: (a: IBroker, b: IBroker) => a.host.charCodeAt(0) - b.host.charCodeAt(0),
    }, {
      title: 'Port',
      dataIndex: 'port',
      key: 'port',
      sorter: (a: IBroker, b: IBroker) => a.port - b.port,
    }, {
      title: 'JMX Port',
      dataIndex: 'jmxPort',
      key: 'jmxPort',
      sorter: (a: IBroker, b: IBroker) => a.jmxPort - b.jmxPort,
    }, {
      title: '启动时间',
      dataIndex: 'startTime',
      key: 'startTime',
      sorter: (a: IBroker, b: IBroker) => a.startTime - b.startTime,
      render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
    }, {
      title: '流入(KB/s)',
      dataIndex: 'byteIn',
      key: 'byteIn',
      sorter: (a: IBroker, b: IBroker) => b.byteIn - a.byteIn,
      render: (t: number) => (t / 1024).toFixed(2),
    }, {
      title: '流出(KB/s)',
      dataIndex: 'byteOut',
      key: 'byteOut',
      sorter: (a: IBroker, b: IBroker) => b.byteOut - a.byteOut,
      render: (t: number) => (t / 1024).toFixed(2),
    },
      region,
      status,
    {
      title: '操作',
      render: (text: string, record: IBroker) => {
        return (
          <>
            <span className="table-operation">
              <a
                href={`/admin/broker_detail?clusterId=${urlQuery.clusterId}&brokerId=${record.brokerId}`}
                target="_blank"
              >详情
              </a>
              <a
                onClick={!record.status ? () => { } : this.deleteBroker.bind(null, record)}
                style={!record.status ? { cursor: 'not-allowed', color: '#999' } : {}}
              >删除
              </a>
            </span>
          </>
        );
      },
    }];
  }

  public deleteBroker = ({ brokerId }: IBroker) => {
    Modal.confirm({
      title: `确认删除${brokerId}？`,
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        deleteBroker(urlQuery.clusterId, brokerId).then(() => {
          notification.success({ message: '删除成功' });
          broker.getBrokerList(urlQuery.clusterId);
        });
      },
    });
  }

  public componentDidMount() {
    broker.getBrokerList(urlQuery.clusterId);
    broker.getBrokerPartition(urlQuery.clusterId);
  }

  public render() {
    const dataList = this.state.searchKey !== '' ?
      broker.list.filter((d) => d.host.includes(this.state.searchKey) || d.brokerId === +this.state.searchKey)
      : broker.list;
    const dataPartitions = this.state.searchId !== '' ?
      broker.partitions.filter((d) => d.brokerId === +this.state.searchId) : broker.partitions;
    return (
      <Spin spinning={broker.loading}>
        <div className="k-row">
          <ul className="k-tab">
            <li>Broker概览</li>
            <li className="k-tab-button">
              <Button type="primary" onClick={modal.showLeaderRebalance}>Leader Rebalance</Button>
            </li>
            {this.renderSearch('请输入主机或BrokerId')}
          </ul>
          <div style={this.state.searchKey ? { minHeight: 370 } : null}>
            <Table
              columns={this.colList(dataList)}
              dataSource={dataList}
              rowKey="brokerId"
              pagination={pagination}
            />
          </div>
        </div>

        <div className="k-row" style={{ height: 400 }}>
          <ul className="k-tab">
            <li>Broker分区概览</li>
            <li className="k-tab-button">
              <Button type="primary" onClick={modal.showLeaderRebalance}>Leader Rebalance</Button>
            </li>
            {this.renderSearch('请输入BrokerId', 'searchId')}
          </ul>
          <Table
            columns={this.colPartition(dataPartitions)}
            dataSource={dataPartitions}
            rowKey="brokerId"
            pagination={pagination}
          />
        </div>
      </Spin>
    );
  }
}
