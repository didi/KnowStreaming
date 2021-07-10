import { observer } from 'mobx-react';
import * as React from 'react';
import { Table, Button, Spin } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IUploadFile } from 'types/base-type';
import { version } from 'store/version';
import { pagination } from 'constants/table';
import { getVersionColumns } from './config';
import { showUploadModal } from 'container/modal/admin';
import { tableFilter } from 'lib/utils';
import { admin } from 'store/admin';

@observer
export class VersionManagement extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
    filterClusterNameVisible: false,
    filterConfigTypeVisible: false,
  };

  public async componentDidMount() {
    if (!version.fileTypeList.length) {
      await version.getFileTypeList();
    }

    if (!version.fileList.length) {
      version.getFileList();
    }

    if (!admin.metaList.length) {
      admin.getMetaData(false);
    }
  }
  public getColumns = () => {
    const columns = getVersionColumns();
    const clusterName = Object.assign({
      title: '集群名称',
      dataIndex: 'clusterName',
      key: 'clusterName',
      filters: tableFilter<any>(this.getData(version.fileList), 'clusterName'),
      onFilter: (value: string, record: IUploadFile) => record.clusterName === value,
    }, this.renderColumnsFilter('filterClusterNameVisible'));
    const configType = Object.assign({
      title: '配置类型',
      dataIndex: 'configType',
      key: 'configType',
      filters: tableFilter<any>(this.getData(version.fileList), 'configType'),
      onFilter: (value: string, record: IUploadFile) => record.configType === value,
    }, this.renderColumnsFilter('filterConfigTypeVisible'));
    const col = columns.splice(1, 0, clusterName, configType);
    return columns;
  }

  public getData<T extends IUploadFile>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (searchKey) {
      data = origin.filter((item: IUploadFile) => item.id + '' === searchKey
        || ((item.fileName !== undefined && item.fileName !== null) && item.fileName.toLowerCase().includes(searchKey as string)));
    }
    return data;
  }

  public renderTable() {
    return (
      <Spin spinning={version.loading}>
        <Table
          rowKey="key"
          columns={this.getColumns()}
          dataSource={this.getData(version.fileList)}
          pagination={pagination}
        />
      </Spin>

    );
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入ID或文件名')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => showUploadModal()}>上传配置</Button>
        </li>
      </ul>
    );
  }

  public render() {
    const currentFileType = version.currentFileType;
    const acceptFileMap = version.acceptFileMap;
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
