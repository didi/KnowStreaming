import * as React from 'react';

import { Table, Tabs, Select, Input, notification, Modal } from 'component/antd';
import { PaginationConfig } from 'antd/es/table/interface';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { order, tableStatusFilter } from 'store/order';
import { cluster } from 'store/cluster';
import { recallPartition, recallOrder } from 'lib/api';
import moment from 'moment';
import { handleTabKey, tableFilter } from 'lib/utils';
import { SearchAndFilter } from 'container/cluster-topic';
import { IBaseOrder } from 'types/base-type';

const TabPane = Tabs.TabPane;
const Search = Input.Search;
const Option = Select.Option;

const handleRecallOrder = (record: IBaseOrder) => {
  const flag = +location.hash.substr(1);
  Modal.confirm({
    title: `确认撤回 Topic: ${record.topicName}${flag ? ' 扩容的' : ''}申请 ？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      if (flag) {
        recallPartition(record.orderId).then(() => {
          notification.success({ message: '撤回成功' });
          order.getOrder();
        });
      } else {
        recallOrder(record.orderId).then(() => {
          notification.success({ message: '撤回成功' });
          order.getOrder();
        });
      }
    },
  });
};

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class MyOrder extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterStatusVisible: false,
    filterClusterVisible: false,
    filterSVisible: false,
    filterCVisible: false,
  };

  public componentDidMount() {
    order.getOrder();
    cluster.getClustersBasic();
  }

  public renderColumns = (data: IBaseOrder[], type: boolean) => {
    const cluster = Object.assign({
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      filters: tableFilter<IBaseOrder>(data, 'clusterName'),
      onFilter: (value: string, record: IBaseOrder) => record.clusterName.indexOf(value) === 0,
    }, this.renderColumnsFilter(type ? 'filterClusterVisible' : 'filterCVisible'));

    const status = Object.assign({
      title: '工单状态',
      dataIndex: 'statusStr',
      key: 'statusStr',
      filters: tableStatusFilter,
      onFilter: (value: string, record: IBaseOrder) => record.statusStr.indexOf(value) === 0,
      render: (t: string) => <span className={t === '通过' ? 'success' : t === '拒绝' ? 'fail' : ''}>{t}</span>,
    }, this.renderColumnsFilter(type ? 'filterStatusVisible' : 'filterSVisible'));

    return [
      {
        title: '工单 ID',
        dataIndex: 'orderId',
        key: 'orderId',
        sorter: (a: IBaseOrder, b: IBaseOrder) => a.orderId - b.orderId,
      },
      cluster,
      {
        title: 'Topic 名称',
        dataIndex: 'topicName',
        key: 'topicName',
        sorter: (a: IBaseOrder, b: IBaseOrder) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      },
      {
        title: 'Topic申请人',
        dataIndex: 'applicant',
        key: 'applicant',
        sorter: (a: IBaseOrder, b: IBaseOrder) => a.principals.charCodeAt(0) - b.principals.charCodeAt(0),
      },
      {
        title: '申请时间',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        sorter: (a: IBaseOrder, b: IBaseOrder) => a.gmtCreate - b.gmtCreate,
        render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
      },
      status,
      {
        title: '审批人',
        dataIndex: 'approver',
        key: 'approver',
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        render: (text: string, r: IBaseOrder) => {
          return (
            <span className="table-operation">
              <a
                onClick={+location.hash.substr(1) ? modal.showExpandTopic.bind(null, r) : modal.showNewTopic.bind(null, r)}
              >详情
              </a>
              {r.orderStatus === 0 ? <a onClick={handleRecallOrder.bind(null, r)}>撤回</a> : null}
            </span>
          );
        },
      },
    ];
  }

  public renderTopic() {
    return (
      <Table
        columns={this.renderColumns(order.topicOrder, true)}
        dataSource={this.getData(order.topicOrder)}
        pagination={pagination}
      />
    );
  }

  public renderPartition() {
    return (
      <Table
        columns={this.renderColumns(order.partitionOrder, false)}
        dataSource={this.getData(order.partitionOrder)}
        pagination={pagination}
      />
    );
  }

  public getData<T extends IBaseOrder>(origin: T[]) {
    let data: T[] = [];
    origin.forEach((d) => {
      if (cluster.active === -1 || d.clusterId === cluster.active) {
        return data.push(d);
      }
    });
    const { searchKey } = this.state;

    if (searchKey) {
      data = data.filter((d) => d.topicName.includes(searchKey));
    }

    return data;
  }

  public render() {
    const activeKey = location.hash.substr(1);
    return (
      <>
        <ul className="table-operation-bar">
          <li
            className="new-topic"
            onClick={+activeKey ? modal.showExpandTopic.bind(null, null) : modal.showNewTopic.bind(null, null)}
          >
            <i className="k-icon-xinjian didi-theme" />{+activeKey ? '扩容申请' : 'Topic申请'}
          </li>
          <li>
            <Select value={cluster.active} onChange={cluster.changeCluster}>
              {cluster.data.map((d) => <Option value={d.clusterId} key={d.clusterId}>{d.clusterName}</Option>)}
            </Select>
          </li>
          <li><Search placeholder="请输入Topic名称" onChange={this.onSearch} /></li>
        </ul>
        <Tabs activeKey={activeKey || '0'} type="card" onChange={handleTabKey}>
          <TabPane tab="Topic 申请" key="0">
            {this.renderTopic()}
          </TabPane>
          <TabPane tab="Topic 扩容" key="1">
            {this.renderPartition()}
          </TabPane>
        </Tabs>
      </>
    );
  }

  private onSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const searchKey = e.target.value.trim();
    this.setState({
      searchKey,
    });
  }
}
