import { observer } from 'mobx-react';
import { order } from 'store/order';
import { OrderList } from './order-list';
import * as React from 'react';
import { Table, Spin, Alert, Button } from 'component/antd';
import { modal } from 'store/modal';
import { pagination } from 'constants/table';
import { renderOrderOpModal } from 'container/modal/order';
import ReactDOM from 'react-dom';
import { IBaseOrder } from 'types/base-type';
// import {  } from 'antd/es/radio';

@observer
export class MyApproval extends OrderList {

  public static defaultProps = {
    type: 'approval',
  };
  public unpendinngRef: HTMLDivElement = null;
  public onSelectChange = {
    onChange: (selectedRowKeys: string[], selectedRows: []) => {
      const num = selectedRows.length;
      order.setSelectedRows(selectedRows);
      // console.log(selectedRows);
      // ReactDOM.render(
      //   selectedRows.length ? (
      //     <>
      //       <Alert
      //         type="warning"
      //         message={`已选择 ${num} 项 `}
      //         showIcon={true}
      //         closable={false}
      //       />
      //       <span className="k-coll-btn" >
      //         <a className="btn-right" onClick={renderOrderOpModal.bind(this, selectedRows, 1)}>通过</a>
      //         <a onClick={renderOrderOpModal.bind(this, selectedRows, 2)}>驳回</a>
      //       </span>
      //     </>
      //   ) : null,
      //   this.unpendinngRef,
      // );
    },
    // getCheckboxProps: (record: any) => {
    //   return {
    //     disabled: record.type === 0 || record.type === 12 || record.type === 1,
    //     name: record.name,
    //   };
    // },
  };

  constructor(defaultProps: any) {
    super(defaultProps);
  }

  public componentDidMount() {
    order.getApprovalList(+this.state.currentTab);
    order.getOrderTypeList();
  }

  public renderTableList(data: IBaseOrder[]) {
    const { currentTab, selectedRows } = this.state;
    // if (modal.actionAfterClose === 'close') {
    //   this.onSelectChange.onChange([], []);
    // }
    return (
      <Spin spinning={order.loading}>
        <div style={{ marginBottom: '0' }} className="k-collect" ref={(id) => this.unpendinngRef = id} />
        {/* 我的审批 业务逻辑修改 问题：state无法定义 */}
        <div style={{ paddingBottom: '10px' }}>
          <Button
            style={{ margin: '0 5px' }}
            disabled={order.selectedRows.filter(item => item.type === 0 || item.type === 12).length > 0 || order.selectedRows.length < 1 ? true : false}
            onClick={() => renderOrderOpModal(order.selectedRows, 1, this.onSelectChange)}
          >
            批量通过
          </Button>
          <Button
            disabled={order.selectedRows.length > 0 ? false : true}
            onClick={() => renderOrderOpModal(order.selectedRows, 2, this.onSelectChange)}
          >
            批量驳回
          </Button>
          <span style={{ color: '#a1a0a0', fontSize: '10px', display: 'inline-block', margin: '0 10px' }}>
            Topic申请、分区申请无法批量通过
          </span>
        </div>
        {/* 我的审批 业务逻辑修改 */}
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
