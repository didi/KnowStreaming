import * as React from 'react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Button, Spin } from 'component/antd';
import { admin } from 'store/admin';
import { observer } from 'mobx-react';
import { IConfigure, IConfigGateway } from 'types/base-type';
import { users } from 'store/users';
import { pagination } from 'constants/table';
import { getConfigureColumns, getConfigColumns } from './config';
import { showConfigureModal, showConfigGatewayModal } from 'container/modal/admin';

@observer
export class ConfigureManagement extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterRole: '',
  };

  public componentDidMount() {
    if (this.props.isShow) {
      admin.getGatewayList();
      admin.getGatewayType();
    } else {
      admin.getConfigure();
    }
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

  public getGatewayData<T extends IConfigGateway>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IConfigGateway) =>
      ((item.name !== undefined && item.name !== null) && item.name.toLowerCase().includes(searchKey as string))
      || ((item.value !== undefined && item.value !== null) && item.value.toLowerCase().includes(searchKey as string))
      || ((item.description !== undefined && item.description !== null) &&
        item.description.toLowerCase().includes(searchKey as string)),
    ) : origin;
    return data;
  }

  public renderTable() {
    return (
      <Spin spinning={users.loading}>
        {this.props.isShow ? <Table
          rowKey="key"
          columns={getConfigColumns()}
          dataSource={this.getGatewayData(admin.configGatewayList)}
          pagination={pagination}
        /> : <Table
          rowKey="key"
          columns={getConfigureColumns()}
          dataSource={this.getData(admin.configureList)}
          pagination={pagination}
        />}
      </Spin>

    );
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入配置键、值或描述')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => this.props.isShow ? showConfigGatewayModal() : showConfigureModal()}>增加配置</Button>
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
