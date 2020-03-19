import * as React from 'react';

import { Table, Tabs, Modal, PaginationConfig, notification } from 'component/antd';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { IRegionData, region, statusMap, levelMap } from 'store/region';
import urlQuery from 'store/url-query';
import { SearchAndFilter } from 'container/cluster-topic';

const TabPane = Tabs.TabPane;

const handleDeleteRegion = (record: IRegionData) => {
  Modal.confirm({
    title: `确认删除 ${record.regionName} ？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      region.delRegion(record.regionId).then(() => {
        notification.success({ message: '删除成功' });
        region.getRegions(urlQuery.clusterId);
      });
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
export class AdminRegion extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterStatus: false,
    filterLevel: false,
  };

  public renderColumns = () => {
    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      filters: statusMap.map((ele, index) => ({ text: ele, value: index + '' })),
      render: (t: number) => <span className={!(t + 1) ? 'fail' : t === 1 ? '' : 'success'}>{statusMap[t + 1]}</span>,
      onFilter: (value: string, record: IRegionData) => record.status + 1 === +value,
    }, this.renderColumnsFilter('filterStatus'));

    const level = Object.assign({
      title: '重要程度',
      dataIndex: 'level',
      key: 'level',
      filters: levelMap.map((ele, index) => ({ text: ele, value: index + '' })),
      render: (t: number) => {
        return levelMap[t];
      },
      onFilter: (value: string, record: IRegionData) => record.level === +value,
    }, this.renderColumnsFilter('filterLevel'));

    return [
      {
        title: 'Region名称',
        dataIndex: 'regionName',
        key: 'regionName',
      },
      {
        title: 'BrokerList',
        key: 'brokerIdList',
        render: (text: string, record: IRegionData) => {
          return <span>{record.brokerIdList.join(', ')}</span>;
        },
      },
      {
        title: '操作者',
        dataIndex: 'operator',
        key: 'operator',
      },
      status,
      level,
      {
        title: '备注',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: 200,
        render: (text: string, record: IRegionData) => {
          return (
            <span className="table-operation">
              <a onClick={modal.showRegion.bind(null, record)}>编辑</a>
              <a onClick={handleDeleteRegion.bind(null, record)}>删除</a>
            </span>
          );
        },
      },
    ];
  }

  public componentDidMount() {
    region.getRegions(urlQuery.clusterId);
  }

  public renderRegion() {
    if (!region.data) return null;
    const data = region.data.filter((d) => d.regionName.includes(this.state.searchKey));
    return (
      <Table
        columns={this.renderColumns()}
        dataSource={data}
        pagination={pagination}
        rowKey="regionId"
      />
    );
  }

  public render() {
    return (
      <>
        <ul className="table-operation-bar">
          <li className="new-topic" onClick={modal.showRegion.bind(null, null)}>
            <i className="k-icon-xinjian didi-theme" />新增Region
          </li>
          {this.renderSearch('请输入关键词')}
        </ul>
        <Tabs defaultActiveKey="1" type="card">
          <TabPane tab="Region管理" key="1">
            {this.renderRegion()}
          </TabPane>
        </Tabs>
      </>
    );
  }
}
