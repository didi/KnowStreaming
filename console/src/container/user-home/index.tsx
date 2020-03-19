import * as React from 'react';
import './index.less';

import { Table, Tabs, Alert, notification, PaginationConfig, Modal, Tooltip } from 'component/antd';
import { modal } from 'store';
import { cluster } from 'store/cluster';
import { observer } from 'mobx-react';
import { topic } from 'store/topic';
import ReactDOM from 'react-dom';
import { collect, uncollect } from 'lib/api';
import { SearchAndFilter } from 'container/cluster-topic';
import moment from 'moment';
import { handleTabKey, tableFilter } from 'lib/utils';
import { ITopic } from 'types/base-type';

const TabPane = Tabs.TabPane;

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class UserHome extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterCollVisible: false,
    filterUnCollVisible: false,
    filterFavorite: false,
  };

  public collRef: HTMLDivElement = null;
  public uncollRef: HTMLDivElement = null;

  public rowSelection = {
    onChange: (selectedRowKeys: string[], selectedRows: ITopic[]) => {
      const num = selectedRows.length;
      ReactDOM.render(
        selectedRows.length ? (
          <>
            <Alert
              type="warning"
              message={`已选择 ${num} 项 `}
              showIcon={true}
              closable={false}
            />
            <a className="k-coll-btn didi-theme" onClick={this.collect.bind(this, selectedRows)}>收藏</a>
          </>) : null,
        this.collRef,
      );
    },
    getCheckboxProps: (record: any) => ({ disabled: record.favorite, className: 'icon' }),
  };

  public unrowSelection = {
    onChange: (selectedRowKeys: string[], selectedRows: ITopic[]) => {
      const num = selectedRows.length;
      ReactDOM.render(
        selectedRows.length ? (
          <>
            <Alert
              type="warning"
              message={`已选择 ${num} 项 `}
              showIcon={true}
              closable={false}
            />
            <a className="k-coll-btn didi-theme" onClick={this.uncollect.bind(this, selectedRows)}>取消收藏</a>
          </>) : null,
        this.uncollRef,
      );
    },
  };

  public renderColumns = (data: ITopic[], type: boolean) => {
    const cluster = Object.assign({
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      width: '12%',
      filters: tableFilter<ITopic>(data, 'clusterName'),
      onFilter: (value: string, record: ITopic) => record.clusterName.indexOf(value) === 0,
    }, this.renderColumnsFilter(type ? 'filterCollVisible' : 'filterUnCollVisible'));

    const favorite = Object.assign({
      title: '状态',
      dataIndex: 'favorite',
      key: 'favorite',
      filters: [{ text: '已收藏', value: 'true' }, { text: '未收藏', value: 'false' }],
      onFilter: (value: string, record: ITopic) => record.favorite + '' === value,
      render: (t: boolean) => t ? '已收藏' : '未收藏',
    }, this.renderColumnsFilter('filterFavorite'));

    const columns = [
      {
        title: 'Topic 名称',
        dataIndex: 'topicName',
        key: 'topicName',
        width: 250,
        onCell: () => ({
          style: {
            maxWidth: 250,
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            cursor: 'pointer',
          },
        }),
        sorter: (a: ITopic, b: ITopic) => a.topicName ? a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0) : null,
        render: (t: string, r: ITopic) => {
          return (
            <Tooltip placement="top" title={r.topicName} >
              <a href={`/user/topic_detail?clusterId=${r.clusterId}&topic=${r.topicName}`}>{t}</a>
            </Tooltip>);
        },
      },
      cluster,
      {
        title: '分区数',
        dataIndex: 'partitionNum',
        key: 'partitionNum',
        width: 120,
        sorter: (a: ITopic, b: ITopic) => b.partitionNum - a.partitionNum,
      },
      {
        title: '流入(KB/s)',
        dataIndex: 'byteIn',
        key: 'byteIn',
        width: 120,
        sorter: (a: ITopic, b: ITopic) => b.byteIn - a.byteIn,
        render: (t: number) => (t / 1024).toFixed(2),
      },
      {
        title: '流入(QPS)',
        dataIndex: 'produceRequest',
        key: 'produceRequest',
        width: 120,
        sorter: (a: ITopic, b: ITopic) => b.produceRequest - a.produceRequest,
        render: (t: number) => t.toFixed(2),
      },
      {
        title: '负责人',
        dataIndex: 'principals',
        key: 'principals',
        width: 120,
        onCell: () => ({
          style: {
            maxWidth: 100,
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            cursor: 'pointer',
          },
        }),
        render: (t: string) => <Tooltip placement="topLeft" title={t} >{t}</Tooltip>,
        sorter: (a: ITopic, b: ITopic) =>
          a.principals && b.principals ? a.principals.charCodeAt(0) - b.principals.charCodeAt(0) : (-1),
      },
      {
        title: '修改时间',
        dataIndex: 'updateTime',
        key: 'updateTime',
        width: '15%',
        sorter: (a: ITopic, b: ITopic) => a.updateTime - b.updateTime,
        render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
      },
      favorite,
    ];
    if (!type) return columns.splice(0, columns.length - 1);
    return columns;
  }

  public renderCollection(favData: ITopic[]) {
    return (
      <Table
        rowKey="key"
        rowSelection={this.unrowSelection}
        dataSource={favData}
        columns={this.renderColumns(favData, false)}
        pagination={pagination}
      />
    );
  }

  public renderList(data: ITopic[]) {
    return (
      <Table
        rowKey="key"
        rowSelection={this.rowSelection}
        columns={this.renderColumns(data, true)}
        dataSource={data}
        pagination={pagination}
      />
    );
  }

  public componentDidMount() {
    if (cluster.data.length === 0) {
      cluster.getClustersBasic();
    }

    if (topic.data.length === 0) {
      topic.getTopics();
    }
  }

  public collect = (selectedRowKeys: ITopic[]) => {
    collect(selectedRowKeys.map(s => ({ clusterId: s.clusterId, topicName: s.topicName } as ITopic))).then(() => {
      ReactDOM.unmountComponentAtNode(this.collRef);
      topic.getTopics();
      notification.success({ message: '收藏成功' });
    });
  }

  public uncollect = (selectedRowKeys: ITopic[]) => {
    Modal.confirm({
      title: `确认取消收藏？`,
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        uncollect(selectedRowKeys.map(s => ({ clusterId: s.clusterId, topicName: s.topicName } as ITopic))).then(() => {
          ReactDOM.unmountComponentAtNode(this.uncollRef);
          notification.success({ message: '取消收藏成功' });
          topic.getTopics();
        });
      },
    });
  }

  public getData<T extends ITopic>(origin: T[]) {
    let data: T[] = [];
    origin.forEach((d) => {
      if (cluster.active === -1 || d.clusterId === cluster.active) {
        return data.push(d);
      }
    });
    const { searchKey } = this.state;

    if (searchKey) {
      data = origin.filter((d) => d.topicName.includes(searchKey) ||
        (d.principals && d.principals.includes(searchKey)));
    }

    return data;
  }

  public renderTable() {
    return (
      <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="Topic收藏" key="1">
          <div className="k-collect" ref={(id) => this.uncollRef = id} />
          {this.renderCollection(this.getData(topic.favData))}
        </TabPane>
        <TabPane tab="Topic列表" key="2">
          <div className="k-collect" ref={(id) => this.collRef = id} />
          {this.renderList(this.getData(topic.data))}
        </TabPane>
      </Tabs>
    );
  }

  public renderClusterTopic() {
    return (
      <>
        {this.renderCluster()}
        {this.renderSearch('请输入Topic名称或者负责人')}
      </>
    );
  }

  public render() {
    const isAdmin = location.pathname.includes('admin');
    return (
      <>
        <ul className="table-operation-bar">
          <li className="new-topic" onClick={isAdmin ? modal.showAdimTopic.bind(null, null) : modal.showNewTopic.bind(null, null)}>
            <i className="k-icon-xinjian didi-theme" />{`Topic${isAdmin ? '创建' : '申请'}`}
          </li>
          {this.renderClusterTopic()}
        </ul>
        {this.renderTable()}
      </>
    );
  }
}
