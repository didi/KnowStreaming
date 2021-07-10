import * as React from 'react';
import './index.less';
import { observer } from 'mobx-react';
import { topic, IPartitionsInfo } from 'store/topic';
import { Table, Tooltip } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import Url from 'lib/url-parser';
import { transBToMB } from 'lib/utils';
import { pagination } from 'constants/table';

@observer
export class PartitionInformation extends SearchAndFilterContainer {
  public clusterId: number;
  public topicName: string;

  public state = {
    brokerKey: '',
    searchKey: '',
  };
  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
  }

  public renderColumns = () => {
    const underReplicated = Object.assign({
      title: '是否同步',
      key: 'underReplicated',
      dataIndex: 'underReplicated',
      filters: [{ text: '是', value: 'true' }, { text: '否', value: 'false' }],
      onFilter: (value: string, record: IPartitionsInfo) => record.underReplicated + '' === value,
      render: (t: any) => <span className={t ? 'success' : 'fail'}>{t ? '是' : '否'}</span>,
    }, this.renderColumnsFilter('filterVisible'));
    return [{
      title: '分区ID',
      key: 'partitionId',
      dataIndex: 'partitionId',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.partitionId - a.partitionId,
    }, {
      title: 'beginningOffset',
      key: 'beginningOffset',
      dataIndex: 'beginningOffset',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.beginningOffset - a.beginningOffset,
    }, {
      title: 'endOffset',
      key: 'endOffset',
      dataIndex: 'endOffset',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.endOffset - a.endOffset,
    }, {
      title: 'msgNum',
      key: 'msgNum',
      dataIndex: 'msgNum',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.msgNum - a.msgNum,
    }, {
      title: 'Leader Broker',
      key: 'leaderBrokerId',
      dataIndex: 'leaderBrokerId',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.leaderBrokerId - a.leaderBrokerId,
    }, {
      title: 'LogSize（MB）',
      key: 'logSize',
      dataIndex: 'logSize',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.logSize - a.logSize,
      render: (t: number) => transBToMB(t),
    }, {
      title: '优选副本',
      key: 'preferredBrokerId',
      dataIndex: 'preferredBrokerId',
      sorter: (a: IPartitionsInfo, b: IPartitionsInfo) => b.preferredBrokerId - a.preferredBrokerId,
    }, {
      title: 'AR',
      key: 'replicaBrokerIdList',
      dataIndex: 'replicaBrokerIdList',
      render: (t: []) => {
        return (
          <Tooltip placement="bottomLeft" title={t.join('、')}>
            {t ? t.map(i => <span key={i} className="p-params">{i}</span>) : null}
          </Tooltip>
        );
      },
    }, {
      title: 'ISR',
      key: 'isrBrokerIdList',
      dataIndex: 'isrBrokerIdList',
      render: (t: []) => {
        return (
          <Tooltip placement="bottomLeft" title={t.join('、')}>
            {t ? t.map(i => <span key={i} className="p-params">{i}</span>) : null}
          </Tooltip>
        );
      },
    },
      underReplicated];
  }

  public renderPartitionsInfo() {
    const { searchKey } = this.state;
    const data = searchKey ?
      topic.partitionsInfo.filter((d) => d.partitionId + '' === searchKey) : topic.partitionsInfo;
    return (
      <>
        <div className="k-row" >
          <ul className="k-tab">
            <li>分区信息</li>
            {this.renderSearch('', '请输入分区号')}
          </ul>
          <div style={searchKey ? { minHeight: 700 } : null}>
            <Table
              columns={this.renderColumns()}
              table-Layout="fixed"
              dataSource={data}
              rowKey="key"
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }

  public componentDidMount() {
    topic.getPartitionsInfo(this.clusterId, this.topicName);
  }

  public render() {
    return (
      <>{this.renderPartitionsInfo()}</>
    );
  }
}
