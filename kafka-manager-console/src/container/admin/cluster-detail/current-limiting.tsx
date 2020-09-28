
import * as React from 'react';

import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Tooltip } from 'component/antd';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import Url from 'lib/url-parser';
import { IThrottles } from 'types/base-type';
import { admin } from 'store/admin';
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

  public renderController() {
    const clientType = Object.assign({
      title: '类型',
      dataIndex: 'throttleClientType',
      key: 'throttleClientType',
      width: '15%',
      filters: [{ text: 'fetch', value: 'Fetch' }, { text: 'produce', value: 'Produce' }],
      onFilter: (value: string, record: IThrottles) => record.throttleClientType === value,
      render: (t: string) => t,
    }, this.renderColumnsFilter('filterStatus'));
    const columns = [
      {
        title: 'Topic名称',
        key: 'topicName',
        dataIndex: 'topicName',
        width: '50%',
        sorter: (a: IThrottles, b: IThrottles) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
      },
      {
        title: '应用ID',
        dataIndex: 'appId',
        key: 'appId',
        width: '15%',
        sorter: (a: IThrottles, b: IThrottles) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
      },
      clientType,
      {
        title: 'Broker',
        dataIndex: 'brokerIdList',
        key: 'brokerIdList',
        width: '20%',
        render: (value: number[]) => {
          const num = value ? `[${value.join(',')}]` : '';
          return(
            <span>{num}</span>
          );
        },
      },
    ];
    const { searchKey } = this.state;
    if (!admin.clustersThrottles ) return null;
    const clustersThrottles = admin.clustersThrottles.filter(d =>
      ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey.toLowerCase()))
      || ((d.appId !== undefined && d.appId !== null) && d.appId.toLowerCase().includes(searchKey.toLowerCase())));
    return (
      <Table
        columns={columns}
        dataSource={clustersThrottles}
        pagination={pagination}
        rowKey="key"
      />
    );
  }

  public componentDidMount() {
    admin.getClustersThrottles(this.clusterId);
  }

  public render() {
    return (
      <div className="k-row">
        <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch()}
        </ul>
        {this.renderController()}
      </div>
    );
  }
}
