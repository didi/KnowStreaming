import { Table } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { tableFilter } from 'lib/utils';
import { observer } from 'mobx-react';
import * as React from 'react';
import { cluster } from 'store/cluster';
import { topic } from 'store/topic';
import { app } from 'store/app';
import { getAllTopicColumns } from './config';
import { ITopic } from 'types/base-type';
import { pagination } from 'constants/table';
import { topicStatusMap } from 'constants/status-map';
import 'styles/table-filter.less';

@observer
export class AllTopic extends SearchAndFilterContainer {
  public state = {
    searchKey: ''
  };

  public componentDidMount() {
    if (!cluster.allData.length) {
      cluster.getAllClusters();
    }
    if (!topic.allTopicData.length) {
      topic.getAllTopic();
    }
    if (!app.data.length) {
      app.getAppList();
    }
  }

  public getColumns = (data: ITopic[]) => {
    const statusColumn = Object.assign({
      title: '状态',
      dataIndex: 'access',
      key: 'access',
      width: '10%',
      filters: tableFilter<ITopic>(data, 'access', topicStatusMap),
      onFilter: (text: number, record: ITopic) => record.access === text,
      render: (val: number) => (
        <div className={val === 0 ? '' : 'success'}>
          {topicStatusMap[val] || ''}
        </div>
      ),
    }, this.renderColumnsFilter('filterStatus')) as any;

    const columns = getAllTopicColumns(this.urlPrefix);

    // columns.splice(-2, 0, statusColumn);

    return columns;
  }

  public getData<T extends ITopic>(origin: T[]) {
    let data: T[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (cluster.allActive !== -1 || searchKey !== '') {
      data = origin.filter(d =>
        ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey as string)
          || ((d.appPrincipals !== undefined && d.appPrincipals !== null) && d.appPrincipals.toLowerCase().includes(searchKey as string)))
        && (cluster.allActive === -1 || d.clusterId === cluster.allActive),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public renderTableList(data: ITopic[]) {
    return (
      <Table
        rowKey="key"
        columns={this.getColumns(data)}
        dataSource={data}
        pagination={pagination}
        loading={topic.loading}
      />
    );
  }

  public renderTable() {
    return this.renderTableList(this.getData(topic.allTopicData));
  }

  public renderOperationPanel() {
    return (
      <>
        {this.renderAllCluster('集群：')}
        {this.renderSearch('名称：', '请输入Topic名称或负责人')}
      </>
    );
  }

  public render() {
    return (
      <div className="container">
        <div className="table-operation-panel">
          <ul>
            {this.renderOperationPanel()}
          </ul>
        </div>
        <div className="table-wrapper">
          {this.renderTable()}
        </div>
      </div>
    );
  }
}
