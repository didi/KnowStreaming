import * as React from 'react';

import { Table, Tabs, Form, notification, Modal, Tooltip } from 'component/antd';
import { PaginationConfig } from 'antd/es/table/interface';
import { UserHome } from 'container/user-home';
import { topic } from 'store/topic';
import urlQuery from 'store/url-query';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { deleteTopic } from 'lib/api';
import { cluster } from 'store/cluster';
import { ITopic } from 'types/base-type';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
class AdminTopic extends UserHome {

  public cols = [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      width: 350,
      onCell: () => ({
        style: {
          maxWidth: 250,
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          textOverflow: 'ellipsis',
          cursor: 'pointer',
        },
      }),
      sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (t: string, r: ITopic) => {
        return <a href={`/user/topic_detail?clusterId=${r.clusterId}&topic=${r.topicName}`}>{t}</a>;
      },
    },
    {
      title: '分区数',
      dataIndex: 'partitionNum',
      key: 'partitionNum',
      sorter: (a: ITopic, b: ITopic) => b.partitionNum - a.partitionNum,
    },
    {
      title: '副本数',
      dataIndex: 'replicaNum',
      key: 'replicaNum',
      sorter: (a: ITopic, b: ITopic) => b.replicaNum - a.replicaNum,
    },
    {
      title: '流入 (KB/s)',
      dataIndex: 'byteIn',
      key: 'byteIn',
      sorter: (a: ITopic, b: ITopic) => b.byteIn - a.byteIn,
      render: (t: number) => (t / 1024).toFixed(2),
    },
    {
      title: '流入(QPS)',
      dataIndex: 'produceRequest',
      key: 'produceRequest',
      width: 150,
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
      render: (t: string) => <Tooltip placement="top" title={t} >{t}</Tooltip>,
      sorter: (a: ITopic, b: ITopic) =>
        a.principals && b.principals ? a.principals.charCodeAt(0) - b.principals.charCodeAt(0) : (-1),
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: 200,
      render: (text: string, r: ITopic) => {
        return (
          <span className="table-operation">
            <a onClick={modal.showExpandAdmin.bind(null, r)}>扩分区</a>
            <a onClick={modal.showAdimTopic.bind(null, r)}>编辑</a>
            <a onClick={this.handleDelete.bind(null, r)}>删除</a>
          </span>
        );
      },
    },
  ];

  public handleDelete = ({ clusterId, topicName }: ITopic) => {
    const topicNameList = [topicName];
    Modal.confirm({
      title: `确认删除${topicName}？`,
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        deleteTopic({ clusterId, topicNameList }).then(() => {
          notification.success({ message: '删除成功' });
          topic.getAdminTopics(urlQuery.clusterId);
        });
      },
    });
  }

  public renderTable() {

    return (
      <>
        <Tabs type="card">
          <Tabs.TabPane tab="Topic管理" key="0">
            <Table pagination={pagination} columns={this.cols} dataSource={this.getData(topic.data)} rowKey="topicName" />
          </Tabs.TabPane>
        </Tabs>
      </>
    );
  }

  public componentDidMount() {
    cluster.getClusters();
    topic.getAdminTopics(urlQuery.clusterId);
  }

  public renderClusterTopic() {
    return (
      <>
        {this.renderSearch('请输入Topic名称或者负责人')}
      </>
    );
  }
}

export default Form.create({ name: 'aminTopic' })(AdminTopic);
