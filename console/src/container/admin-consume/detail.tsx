import * as React from 'react';

import { PaginationConfig, Table } from 'component/antd';
import { observer } from 'mobx-react';
import urlQuery from 'store/url-query';
import { IConsumeInfo } from 'store/topic';
import { consume } from 'store/consume';
import { SearchAndFilter } from 'container/cluster-topic';
import { modal } from 'store';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class AdminConsume extends SearchAndFilter {
  public state = {
    searchKey: '',
  };

  public columns = [{
    title: '消费组名称',
    dataIndex: 'consumerGroup',
    key: 'consumerGroup',
    width: '70%',
    sorter: (a: IConsumeInfo, b: IConsumeInfo) => a.consumerGroup.charCodeAt(0) - b.consumerGroup.charCodeAt(0),
  }, {
    title: 'location',
    dataIndex: 'location',
    key: 'location',
    width: '20%',
    render: (t: string) => t.toLowerCase(),
  }, {
    title: '操作',
    key: 'operation',
    width: '10%',
    render: (t: string, r: IConsumeInfo) => {
      return (
        <a onClick={modal.showConsumerTopic.bind(null, Object.assign({ clusterId: urlQuery.clusterId }, r))}>详情</a>);
    },
  },
  ];

  public componentDidMount() {
    consume.getConsumeInfo(urlQuery.clusterId);
  }

  public render() {
    const data = consume.data.filter((d) => d.consumerGroup.includes(this.state.searchKey));
    return (
      <>
        <ul className="table-operation-bar">
          {this.renderSearch('请输入消费组名称')}
        </ul>
        <div style={{ marginTop: '48px' }}>
          <Table
            columns={this.columns}
            dataSource={data}
            pagination={pagination}
            rowKey="consumerGroup"
          />
        </div>
      </>
    );
  }
}
