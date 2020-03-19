import React from 'react';
import { Table, PaginationConfig } from 'component/antd';
import { observer } from 'mobx-react';
import { broker, IPartitions } from 'store/broker';
import { columsDefault } from './constant';
import urlQuery from 'store/url-query';
import './index.less';

const columns = [{
  title: 'Topic',
  dataIndex: 'topicName',
  key: 'topicName',
}, {
  title: 'Leader',
  dataIndex: 'leaderPartitionList',
  onCell: () => ({
    style: {
      maxWidth: 250,
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      cursor: 'pointer',
    },
  }),
  render: (value: number[]) => {
    return value.map(i => <span key={i} className="p-params">{i}</span>);
  },
}, {
  title: '副本',
  dataIndex: 'followerPartitionIdList',
  onCell: () => ({
    style: {
      maxWidth: 250,
      overflow: 'hidden',
      whiteSpace: 'nowrap',
      textOverflow: 'ellipsis',
      cursor: 'pointer',
    },
  }),
  render: (value: number[]) => {
    return value.map(i => <span key={i} className="p-params">{i}</span>);
  },
}, {
  title: '未同步副本',
  dataIndex: 'notUnderReplicatedPartitionIdList',
  render: (value: number[]) => {
    return value.map(i => <span key={i} className="p-params-unFinished">{i}</span>);
  },
}, {
  title: '状态',
  dataIndex: 'underReplicated',
  render: (value: boolean) => {
    return value ? '已同步' : '未同步';
  },
}, {
  title: '操作',
  render: (record: IPartitions) => {
    return (<a onClick={broker.handleOpen.bind(broker, record.topicName)}>查看详情</a>);
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
export class BrokerPartition extends React.Component {

  public componentDidMount() {
    broker.getPartitions(urlQuery.clusterId, urlQuery.brokerId);
  }

  public getDescription = (value: any, record: IPartitions) => {
    return Object.keys(value).map((key: keyof IPartitions) => {
      return (
        <p key={key}><span>{value[key]}</span>{(record[key] as []).join(',')}
          （共{(record[key] as []).length}个)</p>);
    });
  }

  public getMoreDetail = (record: IPartitions) => {
    return (
      <div className="p-description">
        <p><span>Topic: </span>{record.topicName}</p>
        <p><span>isUnderReplicated:</span>{record.underReplicated ? '已同步' : '未同步'}</p>
        {this.getDescription(columsDefault, record)}
      </div>
    );
  }

  public render() {
    return (
      <Table
        columns={columns}
        expandIconAsCell={false}
        expandIconColumnIndex={-1}
        expandedRowRender={this.getMoreDetail}
        dataSource={broker.topicPartitionsInfo}
        expandedRowKeys={broker.openKeys}
        rowKey="topicName"
        pagination={pagination}
      />
    );
  }
}
