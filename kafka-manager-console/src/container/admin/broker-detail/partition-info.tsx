import * as React from 'react';
import { observer } from 'mobx-react';
import { Table, Tooltip } from 'component/antd';
import { columsDefault } from 'constants/status-map';
import Url from 'lib/url-parser';
import { pagination } from 'constants/table';
import { admin } from 'store/admin';
import { IBrokersPartitions } from 'types/base-type';
import './index.less';
import { SearchAndFilterContainer } from 'container/search-filter';
import { getPartitionInfoColumns } from '../config';

@observer
export class PartitionInfo extends SearchAndFilterContainer {
  public clusterId: number;
  public brokerId: number;
  public state = {
    searchKey: '',
    filterStatusVisible: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
  }

  public getColumns = () => {
    const columns = getPartitionInfoColumns();
    const status = Object.assign({
      title: '状态',
      dataIndex: 'underReplicated',
      key: 'underReplicated',
      onCell: null,
      width: '7%',
      filters: [{ text: '已同步', value: true }, { text: '未同步', value: false }],
      onFilter: (value: string, record: IBrokersPartitions) => record.underReplicated === Boolean(value),
      render: (value: string) => <span>{Boolean(value) ? '已同步' : '未同步'}</span>,
    }, this.renderColumnsFilter('filterStatusVisible'));

    const col = columns.splice(4, 0, status);
    return columns;
  }

  public getDescription = (value: any, record: any) => {
    return Object.keys(value).map((key: keyof any, index: number) => {
      return (
        <>
          <p key={index}>
            <span>{value[key]}</span>
            {(record[key] as []).join(',')}（共{(record[key] as []).length}个)
          </p>
        </>
      );
    });
  }

  public getMoreDetail = (record: IBrokersPartitions) => {
    return (
      <div className="p-description">
        <p><span>Topic: </span>{record.topicName}</p>
        <p><span>isUnderReplicated:</span>{record.underReplicated ? '已同步' : '未同步'}</p>
        {this.getDescription(columsDefault, record)}
      </div>
    );
  }

  public getData<T extends IBrokersPartitions>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IBrokersPartitions) =>
      (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public componentDidMount() {
    admin.getBrokersPartitions(this.clusterId, this.brokerId);
  }

  public render() {
    return (
      <div className="k-row">
        <ul className="k-tab">
          <li>{this.props.tab}</li>
          {this.renderSearch('', '请输入Topic')}
        </ul>
        <Table
          loading={admin.realBrokerLoading}
          columns={this.getColumns()}
          expandIconAsCell={true}
          expandIconColumnIndex={-1}
          expandedRowRender={this.getMoreDetail}
          dataSource={this.getData(admin.brokersPartitions)}
          rowKey="key"
          pagination={pagination}
        />
      </div>
    );
  }
}
