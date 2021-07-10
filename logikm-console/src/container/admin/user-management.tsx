import { observer } from 'mobx-react';
import * as React from 'react';
import { Table, Button, Spin } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IUser } from 'types/base-type';
import { users } from 'store/users';
import { pagination } from 'constants/table';
import { getUserColumns } from './config';
import { showApplyModal } from 'container/modal/admin';
import { roleMap } from 'constants/status-map';
import { tableFilter } from 'lib/utils';

@observer
export class UserManagement extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterRole: false,
  };

  public componentDidMount() {
    if (!users.userData.length) {
      users.getUserList();
    }
  }

  public getData<T extends IUser>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IUser) =>
      (item.username !== undefined && item.username !== null) && item.username.toLowerCase().includes(searchKey as string)) : origin;
    return data;
  }

  public renderTable() {
    const roleColumn = Object.assign({
      title: '角色权限',
      dataIndex: 'role',
      key: 'role',
      width: '35%',
      filters: tableFilter<IUser>(users.userData, 'role', roleMap),
      onFilter: (text: number, record: IUser) => record.role === text,
      render: (text: number) => roleMap[text] || '',

    }, this.renderColumnsFilter('filterRole')) as any;

    const userColumns = getUserColumns();

    userColumns.splice(1, 0, roleColumn);

    return (
      <Spin spinning={users.loading}>
        <Table
          rowKey="key"
          columns={userColumns}
          dataSource={this.getData(users.userData)}
          pagination={pagination}
        />
      </Spin>

    );
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入用户名或应用名称')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => showApplyModal()}>添加用户</Button>
        </li>
      </ul>
    );
  }

  public render() {
    return (
      <div className="container">
        <div className="table-operation-panel">
          {this.renderOperationPanel()}
        </div>
        <div className="table-wrapper">
          {this.renderTable()}
        </div>
      </div>
    );
  }
}
