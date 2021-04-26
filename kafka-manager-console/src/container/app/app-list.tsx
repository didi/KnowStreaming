import { Table, Tooltip, Spin } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { observer } from 'mobx-react';
import 'styles/table-filter.less';
import { IAppItem } from 'types/base-type';
import { app } from 'store/app';
import { pagination, cellStyle } from 'constants/table';
import { showEditModal } from 'container/modal';
import { modal } from 'store/modal';
import * as React from 'react';

interface IProps {
  from: string;
}
@observer
export class CommonAppList extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public from = 'topic';

  constructor(props: IProps) {
    super(props);
    this.from = props.from;
  }

  public getColumns = (data: IAppItem[]) => {

    const columns = [
      {
        title: 'AppID',
        dataIndex: 'appId',
        key: 'appId',
        width: '15%',
        sorter: (a: IAppItem, b: IAppItem) => a.appId.localeCompare(b.appId),
        render: (text: string, record: IAppItem) => {
          return (
            <a href={`${this.urlPrefix}/topic/app-detail?appId=${record.appId}`}>{text}</a>
          );
        },
      },
      {
        title: '应用名称',
        dataIndex: 'name',
        key: 'name',
        width: '20%',
        onCell: () => ({
          style: {
            maxWidth: 150,
            ...cellStyle,
          },
        }),
        render: (text: string, record: IAppItem) => {
          return (<Tooltip placement="bottomLeft" title={record.name}>{text}</Tooltip>);
        },
      },
      {
        title: '应用描述',
        dataIndex: 'description',
        key: 'description',
        width: '25%',
        onCell: () => ({
          style: {
            maxWidth: 150,
            ...cellStyle,
          },
        }),
        render: (text: string, record: IAppItem) => {
          return (
            <Tooltip placement="bottomLeft" title={record.description} >{text}</Tooltip>);
        },
      }, {
        title: '负责人',
        dataIndex: 'principals',
        key: 'principals',
        width: '25%',
        onCell: () => ({
          style: {
            maxWidth: 150,
            ...cellStyle,
          },
        }),
        render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: '15%',
        render: (text: any, record: IAppItem) => {
          return (
            <span className="table-operation">
              <a onClick={() => showEditModal(record, this.from)}>编辑</a>
              <a onClick={() => showEditModal(record, this.from, true)}>详情</a>
              <a onClick={() => this.getOnlineConnect(record)}>申请下线</a>
            </span>);
        },
      },
    ];
    return columns;
  }

  public getOnlineConnect(record: IAppItem) {
    modal.showOfflineAppNewModal(record.appId);
  }

  public getData<T extends IAppItem>(origin: T[]) {
    let data: T[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IAppItem) =>
      ((item.name !== undefined && item.name !== null) && item.name.toLowerCase().includes(searchKey as string)) ||
      ((item.principals !== undefined && item.principals !== null) && item.principals.toLowerCase().includes(searchKey as string)) ||
      ((item.appId !== undefined && item.appId !== null) && item.appId.toLowerCase().includes(searchKey as string))) : origin;

    return data;
  }

  public renderTableList(data: IAppItem[]) {
    return (
      <>
        <Spin spinning={app.loading}>
          <Table
            rowKey="key"
            columns={this.getColumns(data)}
            dataSource={data}
            pagination={pagination}
          />
        </Spin>
      </>
    );
  }
}
