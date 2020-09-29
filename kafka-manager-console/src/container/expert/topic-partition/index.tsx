import * as React from 'react';
import './index.less';
import { SearchAndFilterContainer } from 'container/search-filter';
import { expert } from 'store/expert';
import { Table, Button, Tooltip } from 'antd';
import { observer } from 'mobx-react';
import { IPartition } from 'types/base-type';
import { pagination } from 'constants/table';
import { BatchExpansion } from './batch-expansion';
import { region } from 'store/region';
import { transBToMB } from 'lib/utils';

@observer
export class PartitionTopic extends SearchAndFilterContainer {
  public capacityData: IPartition[];
  public selectedRows: IPartition[];

  public state = {
    searchKey: '',
    loading: false,
    hasSelected: false,
    partitionVisible: true,
  };

  public onSelectChange = {
    onChange: (selectedRowKeys: string[], selectedRows: []) => {
      this.selectedRows = selectedRows;
      this.setState({
        hasSelected: !!selectedRowKeys.length,
      });
    },
  };

  public InsufficientPartition(partitionData: IPartition[]) {
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        width: '30%',
        sorter: (a: IPartition, b: IPartition) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, item: IPartition) => (
          <Tooltip placement="bottomLeft" title={item.topicName} >
            <a
              // tslint:disable-next-line:max-line-length
              href={`${this.urlPrefix}/topic/topic-detail?clusterId=${item.clusterId}&topic=${item.topicName}&isPhysicalClusterId=true&region=${region.currentRegion}`}
            >
              {text}
            </a>
          </Tooltip>),
      },
      {
        title: '所在集群',
        dataIndex: 'clusterName',
        width: '15%',
      },
      {
        title: '分区个数',
        dataIndex: 'presentPartitionNum',
        width: '10%',
      },
      {
        title: '分区平均流量（MB/s）',
        dataIndex: 'bytesInPerPartition',
        width: '10%',
        sorter: (a: IPartition, b: IPartition) => b.bytesInPerPartition - a.bytesInPerPartition,
        render: (t: number) => transBToMB(t),
      },
      {
        title: '近三天峰值流量（MB/s）',
        dataIndex: 'maxAvgBytesInList',
        width: '25%',
        render: (val: number, item: IPartition, index: number) => (
          item.maxAvgBytesInList.map((record: number) => (
            <span className="p-params">{transBToMB(record)}</span>
          ))
        ),
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: '10%',
        render: (val: string, item: IPartition, index: number) => (
          <>
            <a onClick={() => this.dataMigration([item])}>扩分区</a>
          </>
        ),
      },
    ];
    const { loading, hasSelected } = this.state;
    return (
      <>
        <div className="table-operation-panel">
          <ul>
            {this.renderPhysical('物理集群：')}
            {this.renderSearch('Topic名称：', '请输入Topic名称')}
          </ul>
        </div>
        <Table
          rowKey="key"
          rowSelection={this.onSelectChange}
          columns={columns}
          dataSource={partitionData}
          pagination={pagination}
        />
        <div className="zoning-button">
          <Button disabled={!hasSelected} loading={loading}>
            <a onClick={() => this.dataMigration(this.selectedRows)}>批量扩分区</a>
          </Button>
        </div>
      </>
    );
  }

  public dataMigration(item: IPartition[]) {
    this.capacityData = item;
    this.setState({
      partitionVisible: false,
    });
  }

  public getData(origin: IPartition[]) {
    let data: IPartition[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (expert.active !== -1 || searchKey !== '') {
      data = origin.filter(d =>
        ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey as string))
        && (expert.active === -1 || +d.clusterId === expert.active),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public componentDidMount() {
    expert.getInsufficientPartition();
    if (!expert.metaData.length) {
      expert.getMetaData(false);
    }
  }

  public onChangeVisible(value?: boolean) {
    this.setState({
      partitionVisible: value,
    });
  }

  public render() {
    return (
      this.state.partitionVisible ?
        <>{this.InsufficientPartition(this.getData(expert.partitionedData))}</>
        : <BatchExpansion onChange={(value: boolean) => this.onChangeVisible(value)} capacityData={this.capacityData} />
    );
  }
}
