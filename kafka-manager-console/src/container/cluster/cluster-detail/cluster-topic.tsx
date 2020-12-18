import * as React from 'react';
import Url from 'lib/url-parser';
import { cluster } from 'store/cluster';
import { Table, Tooltip } from 'antd';
import { pagination, cellStyle } from 'constants/table';
import { observer } from 'mobx-react';
import { IClusterTopics } from 'types/base-type';
import { SearchAndFilterContainer } from 'container/search-filter';
import { urlPrefix } from 'constants/left-menu';
import { transMSecondToHour } from 'lib/utils';
import { region } from 'store/region';
import './index.less';

import moment = require('moment');
import { timeFormat } from 'constants/strategy';

@observer
export class ClusterTopic extends SearchAndFilterContainer {
  public clusterId: number;
  public clusterTopicsFrom: IClusterTopics;

  public state = {
    searchKey: '',
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public getData<T extends IClusterTopics>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IClusterTopics) =>
      (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string)
      || (item.appName !== undefined && item.appName !== null) && item.appName.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public clusterTopicList() {
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        width: '20%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, record: IClusterTopics) => {
          return (
            <Tooltip placement="bottomLeft" title={record.topicName} >
              <a
                // tslint:disable-next-line:max-line-length
                href={`${urlPrefix}/topic/topic-detail?clusterId=${record.logicalClusterId}&topic=${record.topicName}&region=${region.currentRegion}`}
              >
                {text}
              </a>
            </Tooltip>);
        },
      },
      {
        title: 'QPS',
        dataIndex: 'produceRequest',
        key: 'produceRequest',
        width: '10%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.produceRequest - a.produceRequest,
        render: (t: number) => t === null ? '' : t.toFixed(2),
      },
      {
        title: 'Bytes In(KB/s)',
        dataIndex: 'byteIn',
        key: 'byteIn',
        width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.byteIn - a.byteIn,
        render: (val: number) => val === null ? '' : (val / 1024).toFixed(2),
      },
      {
        title: '所属应用',
        dataIndex: 'appName',
        key: 'appName',
        width: '15%',
        render: (val: string, record: IClusterTopics) => (
          <Tooltip placement="bottomLeft" title={record.appId} >
            {val}
          </Tooltip>
        ),
      },
      {
        title: '保存时间（h）',
        dataIndex: 'retentionTime',
        key: 'retentionTime',
        width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.retentionTime - a.retentionTime,
        render: (time: any) =>  transMSecondToHour(time),
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        key: 'updateTime',
        width: '20%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.updateTime - a.updateTime,
        render: (t: number) => moment(t).format(timeFormat),
      },
      {
        title: 'Topic说明',
        dataIndex: 'description',
        key: 'description',
        width: '30%',
        onCell: () => ({
          style: {
            maxWidth: 200,
            ...cellStyle,
          },
        }),
        render: (val: string) => (
          <Tooltip placement="topLeft" title={val} >
            {val}
          </Tooltip>
        ),
      },
    ];
    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入Topic名称或所属应用')}
          </ul>
          <Table
            loading={cluster.loading}
            rowKey="topicName"
            dataSource={this.getData(cluster.clusterTopics)}
            columns={columns}
            pagination={pagination}
          />
        </div>
      </>
    );
  }

  public componentDidMount() {
    cluster.getClusterDetailTopics(this.clusterId);
  }

  public render() {
    return (
      cluster.clusterTopics ? <> {this.clusterTopicList()} </> : null
    );
  }
}
