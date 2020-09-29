import { observer } from 'mobx-react';
import { order } from 'store/order';
import { OrderList } from './order-list';
import * as React from 'react';
import { Table, Spin, Alert } from 'component/antd';
import { modal } from 'store/modal';
import { pagination } from 'constants/table';
import { renderOrderOpModal } from 'container/modal/order';
import ReactDOM from 'react-dom';
import { IBaseOrder } from 'types/base-type';

@observer
export class MyApproval extends OrderList {

  public static defaultProps = {
    type: 'approval',
  };

  public unpendinngRef: HTMLDivElement = null;

  public onSelectChange = {
    onChange: (selectedRowKeys: string[], selectedRows: []) => {
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
            <span className="k-coll-btn" >
              <a className="btn-right" onClick={renderOrderOpModal.bind(this, selectedRows, 1)}>通过</a>
              <a onClick={renderOrderOpModal.bind(this, selectedRows, 2)}>驳回</a>
            </span>
          </>) : null,
        this.unpendinngRef,
      );
    },
  };

  constructor(defaultProps: any) {
    super(defaultProps);
  }

  public componentDidMount() {
    order.getApprovalList(+this.state.currentTab);
    order.getOrderTypeList();
  }

  public renderTableList(data: IBaseOrder[]) {
    const { currentTab } = this.state;
    if (modal.actionAfterClose === 'close') {
      this.onSelectChange.onChange([], []);
    }
    return (
      <Spin spinning={order.loading}>
        <div className="k-collect" ref={(id) => this.unpendinngRef = id} />
        <Table
          rowKey="id"
          columns={this.getColumns(data)}
          dataSource={data}
          pagination={pagination}
          rowSelection={(currentTab === '0' && this.onSelectChange)}
        />
      </Spin>
    );
  }

  public renderTable() {
    return this.renderTableList(this.getData(order.approvalList));
  }

  public render() {

   return (
      <div className="container">
        {this.renderTab()}
        <div className="operation-panel">
          <ul>
            {this.renderSearch('', '工单ID，标题，申请人或原因')}
          </ul>
        </div>
        {this.renderTable()}
      </div>);
  }
}
