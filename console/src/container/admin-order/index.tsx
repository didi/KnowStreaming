import * as React from 'react';

import { Table, Tabs } from 'component/antd';
import { PaginationConfig } from 'antd/es/table/interface';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { order, tableStatusFilter } from 'store/order';
import moment from 'moment';
import { handleTabKey, tableFilter } from 'lib/utils';
import { SearchAndFilter } from 'container/cluster-topic';
import { IBaseOrder } from 'types/base-type';

import './index.less';

const TabPane = Tabs.TabPane;

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class AdminOrder extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterClusterVisible: false,
    filterStatusVisible: false,
    filterSVisible: false,
    filterCVisible: false,
  };

  public componentDidMount() {
    order.getAdminOrder();
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
      title: '审批状态',
      dataIndex: 'statusStr',
      key: 'statusStr',
      width: 100,
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
        title: '申请人',
        dataIndex: 'applicant',
        key: 'applicant',
        sorter: (a: IBaseOrder, b: IBaseOrder) => a.applicant.charCodeAt(0) - b.applicant.charCodeAt(0),
      },
      {
        title: '审批人',
        dataIndex: 'approver',
        key: 'approver',
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
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: 100,
        render: (text: string, r: IBaseOrder) => {
          if (!+location.hash.substr(1)) {
            return (
              <span className="table-operation">
                <a onClick={modal.showOrderApprove.bind(null, r, 'showOrderDetail')}>详情</a>
                {r.orderStatus === 0 ? <a onClick={modal.showOrderApprove.bind(null, r, 'showOrderApprove')}>审批</a> : null}
              </span>
            );
          } else {
            return (
              <span className="table-operation">
                <a onClick={modal.showPartition.bind(null, r, 'showPartitionDetail')}>详情</a>
                {r.orderStatus === 0 ? <a onClick={modal.showPartition.bind(null, r, 'showPartition')}>审批</a> : null}
              </span>
            );
          }
        },
      },
    ];
  }

  public renderTopic() {
    const data = order.adminTopicOrder.filter((d) => d.topicName.includes(this.state.searchKey));
    return (
      <Table
        columns={this.renderColumns(data, true)}
        dataSource={data}
        pagination={pagination}
      />
    );
  }

  public renderPartition() {
    const data = order.adminPartitionOrder.filter((d) => d.topicName.includes(this.state.searchKey));
    return (
      <Table
        columns={this.renderColumns(data, false)}
        dataSource={data}
        pagination={pagination}
      />
    );
  }

  public render() {
    const defaultKey = location.hash.substr(1) || '0';
    return (
      <>
        <ul className="table-operation-bar">
          {this.renderSearch('请输入topic名称')}
        </ul>
        <Tabs activeKey={defaultKey} type="card" onChange={handleTabKey}>
          <TabPane
            key="0"
            tab={
              <div className="num-container">
                {order.pendingTopic ? <span className="num">{order.pendingTopic}</span> : null}
                Topic 申请
              </div>
            }
          >
            {this.renderTopic()}
          </TabPane>
          <TabPane
            tab={
              <div className="num-container">
                {order.pendingOrder ? <span className="num">{order.pendingOrder}</span> : null}
                扩容申请
            </div>
            }
            key="1"
          >
            {this.renderPartition()}
          </TabPane>
        </Tabs>
      </>
    );
  }
}
