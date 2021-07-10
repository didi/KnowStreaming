
import * as React from 'react';

import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Tooltip } from 'component/antd';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import Url from 'lib/url-parser';
import { IThrottles } from 'types/base-type';
import { cluster } from 'store/cluster';
import './index.less';

@observer
export class CurrentLimiting extends SearchAndFilterContainer {
  public clusterId: number;

  public state = {
    searchKey: '',
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public getData<T extends IThrottles>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IThrottles) =>
      (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string)
      || (item.appId !== undefined && item.appId !== null) && item.appId.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public renderController() {
    const clientType = Object.assign({
      title: '类型',
      dataIndex: 'throttleClientType',
      key: 'throttleClientType',
      filters: [{ text: 'fetch', value: 'FetchThrottleTime' }, { text: 'produce', value: 'ProduceThrottleTime' }],
      onFilter: (value: string, record: IThrottles) => record.throttleClientType === value,
      render: (t: string) => t,
    }, this.renderColumnsFilter('filterStatus'));
    const columns = [
      {
        title: 'Topic名称',
        key: 'topicName',
        dataIndex: 'topicName',
        sorter: (a: IThrottles, b: IThrottles) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
      },
      {
        title: '应用ID',
        dataIndex: 'appId',
        key: 'appId',
        sorter: (a: IThrottles, b: IThrottles) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
      },
      clientType,
      {
        title: 'Broker',
        dataIndex: 'brokerIdList',
        key: 'brokerIdList',
        render: (value: number[]) => {
          const num = value ? `[${value.join(',')}]` : '';
          return(
            <span>{num}</span>
          );
        },
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={this.getData(cluster.clustersThrottles)}
        pagination={pagination}
        rowKey="key"
      />
    );
  }

  public componentDidMount() {
    cluster.getClusterDetailThrottles(this.clusterId);
  }

  public render() {
    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入Topic名称或AppId')}
          </ul>
          {this.renderController()}
        </div>
      </>
    );
  }
}
