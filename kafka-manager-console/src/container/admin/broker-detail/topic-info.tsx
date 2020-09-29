import * as React from 'react';
import moment from 'moment';
import { observer } from 'mobx-react';
import { pagination, cellStyle } from 'constants/table';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IBrokersTopics } from 'types/base-type';
import Url from 'lib/url-parser';
import { Table, Tooltip } from 'component/antd';
import { admin } from 'store/admin';
import { region } from 'store/region';
import { timeFormat } from 'constants/strategy';

@observer
export class TopicInfo extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public clusterId: number;
  public brokerId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
  }

  public getData<T extends IBrokersTopics>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IBrokersTopics) =>
      (item.appName !== undefined && item.appName !== null) && item.appName.toLowerCase().includes(searchKey as string)
      || (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public renderTopicInfo() {
    const cloumns = [{
      title: 'Topic名称',
      key: 'topicName',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (t: string, r: IBrokersTopics) => {
        return (
          <Tooltip placement="bottomLeft" title={r.topicName} >
            <a
              // tslint:disable-next-line:max-line-length
              href={`${this.urlPrefix}/topic/topic-detail?clusterId=${this.clusterId}&topic=${r.topicName || ''}&isPhysicalClusterId=true&region=${region.currentRegion}`}
            >
              {r.topicName}
            </a>
          </Tooltip>
        );
      },
    }, {
      title: '分区数',
      dataIndex: 'partitionNum',
      key: 'partitionNum',
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => b.partitionNum - a.partitionNum,
    }, {
      title: '副本数',
      dataIndex: 'replicaNum',
      key: 'replicaNum',
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => b.replicaNum - a.replicaNum,
    }, {
      title: 'Bytes In(KB/s)',
      dataIndex: 'byteIn',
      key: 'byteIn',
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => b.byteIn - a.byteIn,
      render: (t: number) => t === null ? '' : (t / 1024).toFixed(2),
    }, {
      title: 'QPS',
      dataIndex: 'produceRequest',
      key: 'produceRequest',
      width: '10%',
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => b.produceRequest - a.produceRequest,
      render: (t: number) => t === null ? '' : t.toFixed(2),
    }, {
      title: '所属应用',
      dataIndex: 'appName',
      key: 'appName',
      width: '10%',
      render: (val: string, record: IBrokersTopics) => (
        <Tooltip placement="bottomLeft" title={record.appId} >
          {val}
        </Tooltip>
      ),
    }, {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      sorter: (a: IBrokersTopics, b: IBrokersTopics) => b.updateTime - a.updateTime,
      render: (t: number) => moment(t).format(timeFormat),
    }];

    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入Topic名称或者负责人')}
          </ul>
          <Table columns={cloumns} dataSource={this.getData(admin.brokersTopics)} rowKey="key" pagination={pagination} />
        </div>
      </>
    );
  }

  public componentDidMount() {
    admin.getBrokersTopics(this.clusterId, this.brokerId);
  }

  public render() {
    return (
      admin.brokersTopics ? <> {this.renderTopicInfo()} </>  : null
    );
  }
}
