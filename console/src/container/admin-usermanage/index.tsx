import * as React from 'react';

import { Table, notification, PaginationConfig, Modal } from 'component/antd';
import { observer } from 'mobx-react';
import { users } from 'store/users';
import { deleteUser } from 'lib/api';
import { modal } from 'store';
import { SearchAndFilter } from 'container/cluster-topic';
import './index.less';

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

const handleForbidden = (record: any) => {
  Modal.confirm({
    title: `确认删除 ${record.username} ？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteUser(record.username).then(() => {
        notification.success({ message: '删除成功' });
        users.getUsers();
      });
    },
  });
};

@observer
export class UserManage extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterVisible: false,
  };

  public renderColumns = () => {
    const role = Object.assign({
      title: '角色',
      key: 'roleName',
      dataIndex: 'roleName',
      filters: users.filterRole,
      onFilter: (value: string, record: any) => record.roleName.indexOf(value) === 0,
    }, this.renderColumnsFilter('filterVisible'));

    return [
      {
        title: '用户名',
        dataIndex: 'username',
        key: 'username',
      },
      role,
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        render: (t: any, r: any) => {
          return (
            <span className="table-operation">
              <a onClick={modal.showNewUser.bind(null, r)}>修改</a>
              <a onClick={handleForbidden.bind(null, r)}>删除</a>
            </span>
          );
        },
      },
    ];
  }

  public componentDidMount() {
    users.getUsers();
  }

  public render() {
    const data = users.userData.filter((d) => d.username.includes(this.state.searchKey));
    return (
      <>
        <div className="u-container">
          <span>管理员授权</span>
          <ul className="table-operation-bar">
            <li className="new-topic" onClick={modal.showNewUser.bind(null, null)}>
              <i className="k-icon-xinjian didi-theme" />新建用户
            </li>
            {this.renderSearch('用户名称')}
          </ul>
        </div>
        <Table
          columns={this.renderColumns()}
          dataSource={data}
          pagination={pagination}
          rowKey="username"
        />
      </>
    );
  }
}
