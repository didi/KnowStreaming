import * as React from 'react';
import './index.less';
import { NetWorkFlow, StatusGragh, Group, Base } from './com';
import { Table, Tabs, Button, PaginationConfig } from 'component/antd';
import Url from 'lib/url-parser';
import { topic, ITopicPartition, ITopicBroker } from 'store/topic';
import { observer } from 'mobx-react';
import { modal } from 'store';
import { drawer } from 'store/drawer';
import { handleTabKey } from 'lib/utils';
import { getCookie } from 'lib/utils';
import { SearchAndFilter } from 'container/cluster-topic';
import { broker } from 'store/broker';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class TopicDetail extends SearchAndFilter {
  public clusterId: number;
  public topicName: string;
  public role: string;

  public brokerColumns = [{
    title: 'BrokerID',
    key: 'brokerId',
    dataIndex: 'brokerId',
    sorter: (a: ITopicBroker, b: ITopicBroker) => a.brokerId - b.brokerId,
    render: (t: string) => {
      return (
        <a href={`/admin/broker_detail?clusterId=${this.clusterId}&brokerId=${t}`}>
          {t}
        </a>
      );
    },
  }, {
    title: 'Host',
    key: 'host',
    dataIndex: 'host',
  }, {
    title: 'Leader个数',
    key: 'leaderPartitionIdListLength',
    dataIndex: 'leaderPartitionIdList',
    render: (t: []) => {
      return t.length;
    },
  }, {
    title: '分区LeaderID',
    key: 'leaderPartitionIdList',
    dataIndex: 'leaderPartitionIdList',
    onCell: () => ({
      style: {
        maxWidth: 180,
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
      },
    }),
    render: (t: []) => {
      return t.map(i => <span key={i} className="p-params">{i}</span>);
    },
  }, {
    title: '分区个数',
    key: 'partitionNum',
    dataIndex: 'partitionNum',
  }, {
    title: '分区ID',
    key: 'partitionIdList',
    dataIndex: 'partitionIdList',
    onCell: () => ({
      style: {
        maxWidth: 180,
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        cursor: 'pointer',
      },
    }),
    render: (t: []) => {
      return t.map(i => <span key={i} className="p-params">{i}</span>);
    },
  }, {
    title: '操作',
    render: (record: ITopicBroker) => {
      return (<a onClick={broker.handleOpen.bind(broker, record.brokerId)}>查看详情</a>);
    },
  }];

  public state = {
    searchKey: '',
    partitionKey: '',
    brokerKey: '',
    consumerKey: '',
    filterVisible: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
    this.role = getCookie('role');
  }

  public renderColumns = () => {
    const underReplicated = Object.assign({
      title: '已同步',
      key: 'underReplicated',
      dataIndex: 'underReplicated',
      filters: [{ text: '是', value: '0' }, { text: '否', value: '1' }],
      onFilter: (value: string, record: ITopicPartition) => +record.underReplicated === +value,
      render: (t: any) => <span className={t ? 'fail' : 'success'}>{t ? '否' : '是'}</span>,
    }, this.renderColumnsFilter('filterVisible'));

    return [{
      title: '分区号',
      key: 'partitionId',
      dataIndex: 'partitionId',
      sorter: (a: ITopicPartition, b: ITopicPartition) => a.partitionId - b.partitionId,
    }, {
      title: '偏移量',
      key: 'offset',
      dataIndex: 'offset',
    }, {
      title: 'LeaderBrokerID',
      key: 'leaderBrokerId',
      dataIndex: 'leaderBrokerId',
    }, {
      title: '副本BrokerID',
      key: 'replicaBrokerIdList',
      dataIndex: 'replicaBrokerIdList',
      render: (t: []) => {
        return t.map(i => <span key={i} className="p-params">{i}</span>);
      },
    }, {
      title: 'ISR',
      key: 'isrBrokerIdList',
      dataIndex: 'isrBrokerIdList',
      render: (t: []) => {
        return t.map(i => <span key={i} className="p-params">{i}</span>);
      },
    }, {
      title: '首选Leader副本',
      key: 'preferredBrokerId',
      dataIndex: 'preferredBrokerId',
    },
      underReplicated];
  }

  public componentDidMount() {
    const { topicName, clusterId } = this;
    topic.getTopicBasicInfo(topicName, clusterId);
    topic.getTopicStatusInfo(topicName, clusterId);
    topic.getTopicConsumeInfo(clusterId, topicName);
    topic.getTopicBroker(clusterId, topicName);
    topic.getTopicPartition(clusterId, topicName);
  }

  public updateStatus = () => {
    topic.getTopicStatusInfo(this.topicName, this.clusterId);
  }

  public getMoreDetail = (record: ITopicBroker) => {
    return (
      <div className="p-description">
        <p><span>BrokerID: </span>{record.brokerId}</p>
        <p><span>Host:</span>{record.host}</p>
        <p><span>LeaderID: </span>{record.leaderPartitionIdList.join(',')}（共{record.leaderPartitionIdList.length}个)</p>
        <p><span>分区ID:</span>{record.partitionIdList.join(',')}（共{record.partitionIdList.length}个)</p>
      </div>
    );
  }

  public renderMore() {
    const data = this.state.brokerKey ?
      topic.topicBrokers.filter((d) => d.host.includes(this.state.brokerKey)) : topic.topicBrokers;
    return (
      <>
        <div className="k-row mb-24">
          <ul className="k-tab">
            <li>Broker信息</li>
            {this.renderSearch('请输入Host', 'brokerKey')}
          </ul>
          <div style={this.state.brokerKey ? { minHeight: 370 } : null}>
            <Table
              columns={this.brokerColumns}
              dataSource={data}
              rowKey="brokerId"
              pagination={pagination}
              expandedRowRender={this.getMoreDetail}
              expandIconAsCell={false}
              expandIconColumnIndex={-1}
              expandedRowKeys={broker.openKeys}
            />
          </div>
        </div>
      </>
    );
  }

  public renderOperation() {
    const { topicName, clusterId } = this;
    return (
      <div className="t-button">
        {location.hash.substr(1) === '2' ? <Button onClick={drawer.showResetOffset}>重置offset</Button> :
          <>
            <Button onClick={modal.showExpandTopic.bind(null, { topicName, clusterId })}>扩容申请</Button>
            <Button onClick={drawer.showTopicSample.bind(null, { topicName, clusterId })}>采样</Button>
          </>}
      </div>
    );
  }

  public renderFlow() {
    return (
      <>
        <div className="k-row mb-24">
          <p className="k-title">历史流量</p>
          <NetWorkFlow clusterId={this.clusterId} topicName={this.topicName} />
        </div>
        <div className="k-row right-flow mb-24">
          <p className="k-title">实时流量</p>
          <span className="k-abs" onClick={this.updateStatus}>
            <i className="k-icon-shuaxin didi-theme" />刷新
          </span>
          <StatusGragh />
        </div>
      </>
    );
  }

  public renderMessage() {
    const data = this.state.partitionKey ?
      topic.topicPartitions.filter((d) => d.partitionId + '' === this.state.partitionKey) : topic.topicPartitions;
    const consumerData = this.state.consumerKey ?
      topic.consumeInfo.filter((d) => d.consumerGroup.includes(this.state.consumerKey)) : topic.consumeInfo;
    return (
      <>
        <div className="k-row mb-24">
          <div className="k-top-row" style={{ width: '42%', float: 'left' }}>
            <p className="k-title">基本信息</p>
            <Base />
          </div>
          <div className="k-top-row" style={{ width: '58%', float: 'right' }}>
            <ul className="k-tab">
              <li>消费组信息</li>
              {this.renderSearch('请输入消费组名称', 'consumerKey')}
            </ul>
            <Group data={consumerData} />
          </div>
        </div>
        {+this.role ? this.renderMore() : null}
        <div className="k-row" >
          <ul className="k-tab">
            <li>分区信息</li>
            {this.renderSearch('请输入分区号', 'partitionKey')}
          </ul>
          <div style={this.state.partitionKey ? { minHeight: 700 } : null}>
            <Table
              columns={this.renderColumns()}
              table-Layout="fixed"
              dataSource={data}
              rowKey="partitionId"
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }

  public renderTab() { };

  public render() {
    return (
      <>
        <div className="nav">
          <p>{this.topicName}</p>
        </div>
        <Tabs
          activeKey={location.hash.substr(1) || '1'}
          type="card"
          onChange={handleTabKey}
          tabBarExtraContent={this.renderOperation()}
        >
          <Tabs.TabPane tab="Topic 信息" key="1">
            {this.renderMessage()}
          </Tabs.TabPane>
          <Tabs.TabPane tab="Topic 流量" key="0">
            {this.renderFlow()}
          </Tabs.TabPane>
          {this.renderTab()}
        </Tabs>
      </>
    );
  }
}
