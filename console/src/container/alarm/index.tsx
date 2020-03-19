import * as React from 'react';

import { Table, Tabs, Select, Input, notification, Modal } from 'component/antd';
import { PaginationConfig } from 'antd/es/table/interface';
import { modal } from 'store';
import { observer } from 'mobx-react';
import { alarm, IAlarm } from 'store/alarm';
import { deleteAlarm } from 'lib/api';
import { SearchAndFilter } from 'container/cluster-topic';
import { getCookie } from 'lib/utils';
import moment = require('moment');

const TabPane = Tabs.TabPane;
const Search = Input.Search;

const handleDeleteAlarm = (record: IAlarm) => {
  Modal.confirm({
    title: `确认删除 ${record.alarmName} ？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      deleteAlarm(record.id).then(() => {
        notification.success({ message: '删除成功' });
        alarm.getAlarm();
      });
    },
  });
};

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 10,
  showTotal: (total) => `共 ${total} 条`,
};

@observer
export class Alarm extends SearchAndFilter {
  public state = {
    searchKey: '',
    filterVisible: false,
  };

  public componentDidMount() {
    alarm.getAlarm();
    alarm.getAlarmConstant();
  }

  public renderColumns = () => {
    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      filters: [{ text: '已启用', value: '1' }, { text: '暂停', value: '0' }],
      onFilter: (value: string, record: IAlarm) => record.status === +value,
      render: (t: string) => <span className={t ? 'success' : ''}>{t ? '已启用' : '暂停'}</span>,
    }, this.renderColumnsFilter('filterVisible'));

    return [
      {
        title: '配置名称',
        dataIndex: 'alarmName',
        key: 'alarmName',
        sorter: (a: IAlarm, b: IAlarm) => a.alarmName.charCodeAt(0) - b.alarmName.charCodeAt(0),
      },
      {
        title: '负责人',
        dataIndex: 'principalList',
        key: 'principalList',
        render: (t: string[]) => t.join(','),
      },
      {
        title: '创建时间',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        sorter: (a: IAlarm, b: IAlarm) => a.gmtCreate - b.gmtCreate,
        render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
      },
      status,
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        width: 200,
        render: (text: string, record: IAlarm) => {
          return (
            <span className="table-operation">
              <a onClick={this.handleModify.bind(null, record)}>修改</a>
              <a onClick={handleDeleteAlarm.bind(null, record)}>删除</a>
            </span>
          );
        },
      },
    ];
  }

  public renderAlarm() {
    const data = alarm.data.filter((d) => {
      return d.alarmName.includes(this.state.searchKey) || d.principalList.includes(this.state.searchKey);
    });
    return (
      <Table
        columns={this.renderColumns()}
        dataSource={data}
        pagination={pagination}
      />
    );
  }

  public handleModify = (record: IAlarm) => {
    if (!getCookie('username') && !record.principalList.includes(getCookie('username'))) {
      notification.success({ message: '抱歉，没有修改权限' });
      return false;
    }
    modal.showAlarmModify(record);
  }

  public render() {
    return (
      <>
        <ul className="table-operation-bar">
          <li className="new-topic" onClick={modal.showAlarm.bind(null, null)}>
            <i className="k-icon-xinjian didi-theme" />添加告警配置
          </li>
          {this.renderSearch('请输入关键字')}
        </ul>
        <Tabs defaultActiveKey="1" type="card">
          <TabPane tab="告警列表" key="1">
            {this.renderAlarm()}
          </TabPane>
        </Tabs>
      </>
    );
  }
}
