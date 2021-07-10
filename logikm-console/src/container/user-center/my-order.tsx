import { observer } from 'mobx-react';
import { order } from 'store/order';
import { OrderList } from './order-list';
import * as React from 'react';

@observer
export class MyOrder extends OrderList {

  public static defaultProps = {
    type: 'apply',
  };

  constructor(defaultProps: any) {
    super(defaultProps);
  }

  public componentDidMount() {
    order.getApplyOrderList(+this.state.currentTab);
    order.getOrderTypeList();
  }

  public renderTable() {
    return this.renderTableList(this.getData(order.applyList));
  }

  public render() {
    return (
      <div className="container">
        {this.renderTab()}
        <div className="operation-panel">
          <ul>
            {this.renderSearch('', '工单ID，标题或申请原因')}
          </ul>
        </div>
        {this.renderTable()}
      </div>);
  }
}
