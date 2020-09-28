import * as React from 'react';

import { Table, notification, Popconfirm } from 'component/antd';
import { observer } from 'mobx-react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { pagination } from 'constants/table';
import Url from 'lib/url-parser';
import moment from 'moment';
import { admin } from 'store/admin';
import { cluster } from 'store/cluster';
import { ILogicalCluster } from 'types/base-type';
import './index.less';
import { app } from 'store/app';
import { showLogicalClusterOpModal } from 'container/modal';
import { timeFormat } from 'constants/strategy';

@observer
export class LogicalCluster extends SearchAndFilterContainer {
  public clusterId: number;

  public state = {
    searchKey: '',
    filterStatus: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public renderColumns = () => {
    return [
      {
        title: '逻辑集群ID',
        dataIndex: 'logicalClusterId',
        key: 'logicalClusterId',
      },
      {
        title: '逻辑集群名称',
        dataIndex: 'logicalClusterName',
        key: 'logicalClusterName',
      },
      {
        title: '应用ID',
        dataIndex: 'appId',
        key: 'appId',
      },
      {
        title: 'RegionIdList',
        dataIndex: 'regionIdList',
        key: 'regionIdList',
        render: (value: number[]) => {
          const num = value ? `[${value.join(',')}]` : '';
          return(
            <span>{num}</span>
          );
        },
      },
      {
        title: '集群模式',
        dataIndex: 'mode',
        key: 'mode',
        render: (value: number) => {
          let val = '';
          cluster.clusterModes.forEach((ele: any) => {
            if (value === ele.code) {
              val = ele.message;
            }
          });
          return(<span>{val}</span>);
        },
      },
      {
        title: '修改时间',
        dataIndex: 'gmtModify',
        key: 'gmtModify',
        render: (t: number) => moment(t).format(timeFormat),
      },
      {
        title: '备注',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: '操作',
        render: (text: string, record: ILogicalCluster) => {
          return (
            <span className="table-operation">
              <a onClick={() => this.editRegion(record)}>编辑</a>
              <Popconfirm
                title="确定删除？"
                onConfirm={() => this.handleDeleteRegion(record)}
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          );
        },
      },
    ];
  }

  public handleDeleteRegion = (record: ILogicalCluster) => {
    admin.deteleLogicalClusters(this.clusterId, record.logicalClusterId).then(() => {
      notification.success({ message: '删除成功' });
    });
  }

  public async editRegion(record: ILogicalCluster) {
    await admin.queryLogicalClusters(record.logicalClusterId);
    await this.addOrEditLogicalCluster(admin.queryLogical);
  }

  public addOrEditLogicalCluster(record?: ILogicalCluster) {
    showLogicalClusterOpModal(this.clusterId, record);
  }

  public componentDidMount() {
    admin.getLogicalClusters(this.clusterId);
    cluster.getClusterModes();

    admin.getBrokersRegions(this.clusterId);
    if (!app.adminAppData.length) {
      app.getAdminAppList();
    }

  }

  public getData<T extends ILogicalCluster>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: ILogicalCluster) =>
      (item.logicalClusterName !== undefined && item.logicalClusterName !== null)
        && item.logicalClusterName.toLowerCase().includes(searchKey as string)
      || (item.appId !== undefined && item.appId !== null) && item.appId.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public renderLogicalCluster() {
    return (
      <Table
        columns={this.renderColumns()}
        dataSource={this.getData(admin.logicalClusters)}
        pagination={pagination}
        rowKey="key"
      />
    );
  }

  public render() {
    return (
      <div className="k-row">
        <ul className="k-tab">
        <li>{this.props.tab}</li>
          <li className="k-add" onClick={() => this.addOrEditLogicalCluster()}>
            <i className="k-icon-xinjian didi-theme" />
            <span>新增逻辑集群</span>
          </li>
          {this.renderSearch('', '请输入逻辑集群名称或AppId')}
        </ul>
        {this.renderLogicalCluster()}
      </div>
    );
  }
}
