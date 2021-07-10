import * as React from 'react';
import './index.less';
import { topic, ITopicBroker, IBrokerInfo } from 'store/topic';
import { Table, Tooltip } from 'component/antd';
import Url from 'lib/url-parser';
import { SearchAndFilterContainer } from 'container/search-filter';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';

@observer
export class BrokersInformation extends SearchAndFilterContainer {
  public clusterId: number;
  public topicName: string;
  public from: string;
  public isPhysical: boolean;

  public state = {
    searchKey: '',
  };

  public brokerColumns = [{
    title: 'BrokerID',
    key: 'brokerId',
    dataIndex: 'brokerId',
    width: '10%',
    sorter: (a: ITopicBroker, b: ITopicBroker) => b.brokerId - a.brokerId,
    render: (t: string, record: ITopicBroker) => {
      return ( // alive true==可点击，false==不可点击
        <>
        {record.alive ?
          <a href={`${this.urlPrefix}/admin/broker-detail?clusterId=${record.clusterId}&brokerId=${t}`}>{t}</a>
          : <a style={{ cursor: 'not-allowed', color: '#999' }}>{t}</a>}
        </>
      );
    },
  }, {
    title: 'Host',
    key: 'host',
    dataIndex: 'host',
    width: '20%',
    render: (text: string) => <Tooltip placement="bottomLeft" title={text}>{text}</Tooltip>,
  }, {
    title: 'Leader个数',
    key: 'leaderPartitionIdListLength',
    dataIndex: 'leaderPartitionIdList',
    width: '10%',
    sorter: (a: ITopicBroker, b: ITopicBroker) => b.leaderPartitionIdList.length - a.leaderPartitionIdList.length,
    render: (t: []) => t.length,
  }, {
    title: '分区LeaderID',
    key: 'leaderPartitionIdList',
    dataIndex: 'leaderPartitionIdList',
    width: '25%',
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
      return (
        <Tooltip placement="bottomLeft" title={t.join('、')}>
          {t.map(i => <span key={i} className="p-params">{i}</span>)}
        </Tooltip>
      );
    },
  }, {
    title: '分区个数',
    key: 'partitionNum',
    dataIndex: 'partitionNum',
    width: '10%',
    sorter: (a: ITopicBroker, b: ITopicBroker) => b.partitionNum - a.partitionNum,
  }, {
    title: '分区ID',
    key: 'partitionIdList',
    dataIndex: 'partitionIdList',
    width: '25%',
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
      return (
        <Tooltip placement="bottomLeft" title={t.join('、')}>
          {t.map(i => <span key={i} className="p-params">{i}</span>)}
        </Tooltip>
        );
    },
  }];

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
    this.from = decodeURIComponent(url.search.from);
    this.isPhysical = this.from.includes('expert');
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

  public getData<T extends IBrokerInfo>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IBrokerInfo) =>
      (item.host !== undefined && item.host !== null) && item.host.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public renderMore() {
    const { searchKey } = this.state;
    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>Broker信息</li>
            {this.renderSearch('', '请输入Host')}
          </ul>
          <div style={searchKey ? { minHeight: 370 } : null}>
            <Table
              columns={this.brokerColumns}
              dataSource={this.getData(topic.brokerInfo)}
              rowKey="key"
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }

  public componentDidMount() {
    topic.getBrokerInfo(this.clusterId, this.topicName);
  }

  public render() {
    return (
      <>
        {this.renderMore()}
      </>
    );
  }
}
