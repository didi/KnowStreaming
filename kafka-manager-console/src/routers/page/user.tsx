import * as React from 'react';
import CommonRoutePage from './common';
import { MyApproval, MyBill, MyOrder, BillDetail } from 'container/user-center';
import { OrderDetail } from 'container/user-center/order-detail';

export default class User extends React.Component {
  public pageRoute = [{
    path: '/user',
    exact: true,
    component: MyOrder,
  }, {
    path: '/user/my-order',
    exact: true,
    component: MyOrder,
  }, {
    path: '/user/my-approval',
    exact: true,
    component: MyApproval,
  }, {
    path: '/user/order-detail',
    exact: true,
    component: OrderDetail,
  }, {
    path: '/user/bill',
    exact: true,
    component: MyBill,
  }, {
    path: '/user/bill-detail',
    exact: true,
    component: BillDetail,
  }];

  public render() {
    return (
      <CommonRoutePage pageRoute={this.pageRoute} mode="user" active="user"/>
    );
  }
}
