import * as React from 'react';
import { Tabs, Table, Button } from 'component/antd';
import { cluster } from 'store/cluster';
import { observer } from 'mobx-react';
import { topic } from 'store/topic';
import { app } from 'store/app';
import { SearchAndFilterContainer } from 'container/search-filter';
import { ITopic } from 'types/base-type';
import { getExpireColumns, getMyTopicColumns } from './config';
import { applyTopic, deferTopic, applyOnlineModal } from 'container/modal';
import { pagination } from 'constants/table';
import { tableFilter } from 'lib/utils';
import { topicStatusMap } from 'constants/status-map';
import './index.less';

const { TabPane } = Tabs;

@observer
export class MineTopic extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterAccess: false,
  };

  public getData<T extends ITopic>(origin: T[]) {
    let data: T[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (cluster.active !== -1 || app.active !== '-1' || searchKey !== '') {
      data = origin.filter(d =>
        ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey as string)
          || ((d.appName !== undefined && d.appName !== null) && d.appName.toLowerCase().includes(searchKey as string)))
        && (cluster.active === -1 || d.clusterId === cluster.active)
        && (app.active === '-1' || d.appId === (app.active + '')),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public componentDidMount() {
    topic.getTopic();
    topic.getExpired();

    if (!cluster.clusterData.length) {
      cluster.getClusters();
    }
    if (!app.data.length) {
      app.getAppList();
    }
  }

  public renderOperationPanel(key: number) {
    return (
      <div className="table-operation-panel">
        <ul>
          {this.renderApp('关联应用：')}
          {this.renderCluster('集群：')}
          {this.renderSearch('名称：', '请输入Topic名称或者应用')}
          {
            key === 1 && <li className="right-btn-1">
              <Button type="primary" onClick={() => { applyTopic(); }}>
                申请Topic
              </Button>
            </li>
          }
        </ul>

      </div>
    );
  }

  public getColumns = (mytopicData: ITopic[]) => {
    const access = Object.assign({
      title: '权限',
      dataIndex: 'access',
      key: 'access',
      width: '10%',
      filters: tableFilter<ITopic>(mytopicData, 'access', topicStatusMap),
      onFilter: (text: number, record: ITopic) => record.access === text,
      render: (val: number) => (
        <div className={val === 0 ? '' : 'success'}>
          {topicStatusMap[val] || ''}
        </div>
      ),
    }, this.renderColumnsFilter('filterAccess')) as any;

    const columns = getMyTopicColumns(this.urlPrefix);

    columns.splice(-2, 0, access);

    return columns;
  }

  public renderMyTopicTable(mytopicData: ITopic[]) {
    return (
      <div>
        <Table
          rowKey="key"
          loading={topic.loading}
          dataSource={mytopicData}
          columns={this.getColumns(mytopicData)}
          pagination={pagination}
        />
      </div>
    );
  }

  public renderDeprecatedTopicTable(expireData: ITopic[]) {
    const operationCol = {
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      width: '15%',
      render: (val: string, item: ITopic, index: number) => (
        <>
          <span>
            <a style={{ marginRight: 16 }} onClick={() => deferTopic(item)}>申请延期</a>
            <a style={{ marginRight: 16 }} onClick={() => applyOnlineModal(item)}>申请下线</a>
          </span>
        </>
      ),
    } as any;
    const expireColumns = getExpireColumns(this.urlPrefix);
    expireColumns.push(operationCol);

    return (
      <div>
        <Table
          rowKey="key"
          dataSource={expireData}
          columns={expireColumns}
          pagination={pagination}
          loading={topic.expiredLoading}
        />
      </div>
    );
  }

  public handleTabKey(key: string) {
    location.hash = key;
    cluster.changeCluster(-1);
    app.changeActiveApp('-1');
    this.setState({
      searchKey: '',
    });
    topic.setLoading(false);
  }

  public render() {
    return (
      <>
        <div className="min-width">
          <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={(key) => this.handleTabKey(key)}>
            <TabPane tab="有效Topic" key="1" >
              {this.renderOperationPanel(1)}
              {this.renderMyTopicTable(this.getData(topic.mytopicData))}
            </TabPane>
            <TabPane tab="已过期Topic" key="2">
              {this.renderOperationPanel(2)}
              {this.renderDeprecatedTopicTable(this.getData(topic.expireData))}
            </TabPane>
          </Tabs>
        </div>
      </>
    );
  }
}
