import * as React from 'react';

import { Table, Tabs, PaginationConfig } from 'component/antd';
import { observer } from 'mobx-react';
import urlQuery from 'store/url-query';
import { controller } from 'store/controller';
import { SearchAndFilter } from 'container/cluster-topic';
import moment from 'moment';

const TabPane = Tabs.TabPane;

const columns = [
  {
    title: 'BrokerId',
    dataIndex: 'brokerId',
    key: 'brokerId',
    sorter: (a: any, b: any) => a.brokerNum - b.brokerNum,
  },
  {
    title: 'host',
    key: 'host',
    dataIndex: 'host',
    render: (r: string, t: any) => {
      return (
        <a href={`/admin/broker_detail?clusterId=${urlQuery.clusterId}&brokerId=${t.brokerId}`} target="_blank">{r}
        </a>
    ); },
  },
  {
    title: '版本',
    dataIndex: 'controllerVersion',
    key: 'controllerVersion',
  },
  {
    title: '变更时间',
    dataIndex: 'controllerTimestamp',
    key: 'controllerTimestamp',
    sorter: (a: any, b: any) => a.controllerTimestamp - b.updacontrollerTimestampteTime,
    render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
  },
];

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class AdminController extends SearchAndFilter {
  public state = {
    searchKey: '',
  };

  public componentDidMount() {
    controller.getController(urlQuery.clusterId);
  }

  public renderController() {
    if (!controller.data) return null;
    const data = controller.data.filter((d) => d.host.includes(this.state.searchKey));
    return (
      <Table
        columns={columns}
        dataSource={data}
        pagination={pagination}
        rowKey="controllerTimestamp"
      />
    );
  }

  public render() {
    return (
      <>
        <ul className="table-operation-bar">
          {this.renderSearch('请输入关键词')}
        </ul>
        <Tabs defaultActiveKey="1" type="card">
          <TabPane tab="Controller变更历史" key="1">
            {this.renderController()}
          </TabPane>
        </Tabs>
      </>
    );
  }
}
