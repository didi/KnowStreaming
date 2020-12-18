import * as React from 'react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Button, Spin } from 'component/antd';
import { admin } from 'store/admin';
import { observer } from 'mobx-react';
import { IConfigure } from 'types/base-type';
import { users } from 'store/users';
import { pagination } from 'constants/table';
import { getConfigureColumns } from './config';
import { showConfigureModal } from 'container/modal/admin';

@observer
export class ConfigureManagement extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterRole: '',
  };

  public componentDidMount() {
    admin.getConfigure();
  }

  public getData<T extends IConfigure>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IConfigure) =>
      ((item.configKey !== undefined && item.configKey !== null) && item.configKey.toLowerCase().includes(searchKey as string))
      || ((item.configValue !== undefined && item.configValue !== null) && item.configValue.toLowerCase().includes(searchKey as string))
      || ((item.configDescription !== undefined && item.configDescription !== null) &&
        item.configDescription.toLowerCase().includes(searchKey as string)),
    ) : origin;
    return data;
  }

  public renderTable() {
    return (
      <Spin spinning={users.loading}>
        <Table
          rowKey="key"
          columns={getConfigureColumns()}
          dataSource={this.getData(admin.configureList)}
          pagination={pagination}
        />
      </Spin>

    );
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入配置键、值或描述')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => showConfigureModal()}>增加配置</Button>
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
