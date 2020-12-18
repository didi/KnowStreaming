import * as React from 'react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { Table, Tabs, message, Tooltip, Spin } from 'component/antd';
import { tableFilter } from 'lib/utils';
import { IBaseOrder } from 'types/base-type';
import { orderStatusMap } from 'constants/status-map';
import { pagination, cellStyle } from 'constants/table';
import { order } from 'store/order';
import { region } from 'store';
import moment from 'moment';
import { observer } from 'mobx-react';
import { timeFormat } from 'constants/strategy';

const { TabPane } = Tabs;

const tabList = ['审批中', '已通过', '全部'];

interface IProps {
  type: string;
}

@observer
export class OrderList extends SearchAndFilterContainer {
  public orderType: string;
  public state: { [propname: string]: any } = {
    searchKey: '',
    currentTab: '0',
    filterTypeVisible: false,
    filterStatusVisible: false,
    selectedRows: [],
  };
  public currentItem = {} as IBaseOrder;

  constructor(props: IProps) {
    super(props);
    this.orderType = props.type;
    // this.state.b;
  }

  public cancelOrder(record: IBaseOrder) {
    order.cancelOrder(record.id).then(() => {
      message.success('撤销成功');
      this.getOrderList();
    });
  }

  public getOrderList() {
    const { currentTab } = this.state;
    this.orderType === 'apply' ? order.getApplyOrderList(+currentTab) : order.getApprovalList(+currentTab);
  }

  public getColumns(data: IBaseOrder[]) {
    const typeColumn = Object.assign({
      title: '工单类型',
      dataIndex: 'type',
      render: (t: number) => order.orderTypeMap[t] || '',
      filters: tableFilter<IBaseOrder>(data, 'type', order.orderTypeMap),
      onFilter: (text: number, record: IBaseOrder) => record.type === text,
    }, this.renderColumnsFilter('filterTypeVisible'));
    let orderStatus = [] as any;
    orderStatus = Object.getOwnPropertyNames(orderStatusMap).map((key) => {
      return {
        value: key,
        text: orderStatusMap[Number(key)],
      };
    });
    const status = Object.assign({
      title: '任务状态',
      dataIndex: 'status',
      key: 'status',
      filters: orderStatus,
      onFilter: (value: string, record: IBaseOrder) => record.status === +value,
      render: (t: number) => (
        <>
          <span className={t === 1 ? 'success' : (t === 2 ? 'fail' : '')}>
            {orderStatusMap[t] || ''}
          </span>
        </>
      ),
    }, this.renderColumnsFilter('filterStatusVisible'));

    const commonColumns = [
      typeColumn,
      {
        title: '工单ID',
        dataIndex: 'id',
        key: 'id',
      }, {
        title: '工单标题',
        dataIndex: 'title',
        key: 'title',
        onCell: () => ({
          style: {
            maxWidth: 200,
            ...cellStyle,
          },
        }),
        render: (text: string) => {
          return (<Tooltip placement="bottomLeft" title={text} > {text} </Tooltip>);
        },
      }, {
        title: '申请原因',
        dataIndex: 'description',
        key: 'description',
        onCell: () => ({
          style: {
            maxWidth: 200,
            ...cellStyle,
          },
        }),
        render: (text: string) => {
          return (<Tooltip placement="bottomLeft" title={text} > {text} </Tooltip>);
        },
      },
      status,
      {
        title: '申请时间',
        dataIndex: 'gmtTime',
        key: 'gmtTime',
        sorter: (a: IBaseOrder, b: IBaseOrder) => b.gmtTime - a.gmtTime,
        render: (t: number) => moment(t).format(timeFormat),
      }, {
        title: '操作',
        key: 'operation',
        dataIndex: 'operation',
        render: (text: string, r: IBaseOrder) => (
          <span className="table-operation">
            <a href={`${this.urlPrefix}/user/order-detail?orderId=${r.id}&region=${region.currentRegion}`}>详情</a>
          </span>
        ),
      },
    ];

    if (this.orderType === 'approval') {
      commonColumns.splice(3, 0, {
        title: '申请人',
        dataIndex: 'applicant',
        key: 'applicant',
      });
    }

    return commonColumns;
  }

  public getData<T extends IBaseOrder>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (searchKey) {
      data = origin.filter((d) => ((d.id !== undefined && d.id !== null) && (d.id + '').toLowerCase().includes(searchKey as string))
        || ((d.applicant !== undefined && d.applicant !== null) && d.applicant.toLowerCase().includes(searchKey as string))
        || ((d.title !== undefined && d.title !== null) && d.title.toLowerCase().includes(searchKey as string))
        || ((d.description !== undefined && d.description !== null) && d.description.toLowerCase().includes(searchKey as string)),
      );
    }

    return data;
  }

  public onChangeTab(e: string) {
    this.setState({
      currentTab: e,
    }, () => {
      this.getOrderList();
    });
  }

  public renderTableList(data: IBaseOrder[]) {
    return (
      <Spin spinning={order.loading}>
        <Table
          rowKey="id"
          columns={this.getColumns(data)}
          dataSource={data}
          pagination={pagination}
        />
      </Spin>
    );
  }

  public renderTab() {
    return (
      <Tabs defaultActiveKey="0" type="card" onChange={(e) => this.onChangeTab(e)}>
        {tabList.map((item, index) => (
          <TabPane tab={item} key={index + ''} />
        ))}
      </Tabs>
    );
  }
}
