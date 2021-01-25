import * as React from 'react';

import { Table, notification, Popconfirm } from 'component/antd';
import { Modal } from 'antd';
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
    deteleCluster: false,
    logicalClusterId: -1,
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
        width: '150px'
      },
      {
        title: '逻辑集群标识',
        dataIndex: 'logicalClusterIdentification',
        key: 'logicalClusterIdentification',
        width: '150px'
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
          return (
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
          cluster.clusterModes && cluster.clusterModes.forEach((ele: any) => {
            if (value === ele.code) {
              val = ele.message;
            }
          });
          return (<span>{val}</span>);
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
        width: '120px',
        render: (text: string, record: ILogicalCluster) => {
          return (
            <span className="table-operation">
              <a onClick={() => this.editRegion(record)}>编辑</a>
              <a onClick={() => this.handleDeleteRegion(record)}>删除</a>
              {/* <Popconfirm
                title="确定删除？"
                cancelText="取消"
                okText="确认"
                onConfirm={() => this.handleDeleteRegion(record)}
              >
                <a>删除</a>
              </Popconfirm> */}
            </span>
          );
        },
      },
    ];
  }

  public handleDeleteRegion = (record: ILogicalCluster) => {
    this.setState({ deteleCluster: true, logicalClusterId: record.logicalClusterId });
    // admin.deteleLogicalClusters(this.clusterId, record.logicalClusterId).then(() => {
    //   notification.success({ message: '删除成功' });
    // });
  }

  // -删除逻辑集群

  public handleExpandOk = () => {
    const { logicalClusterId } = this.state;
    admin.deteleLogicalClusters(this.clusterId, logicalClusterId).then(() => {
      notification.success({ message: '删除成功' });
    });
    this.setState({ deteleCluster: false });
  }

  public handleExpandCancel = () => {
    this.setState({ deteleCluster: false });
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
    ) : origin;
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

  // -删除逻辑集群
  public renderDeleteCluster() {
    return (
      <Modal
        title="提示"
        visible={this.state.deteleCluster}
        okText="确认删除"
        cancelText="取消"
        maskClosable={false}
        onOk={() => this.handleExpandOk()}
        onCancel={() => this.handleExpandCancel()}
      >
        <div className="cluster-prompt">
          <span>
            若逻辑集群上存在Topic，则删除逻辑集群后，用户将无法在Topic管理页查看到该Topic
          </span>
        </div>
        <div className="cluster-explain">
          <span>
            说明：删除逻辑集群不会真实删除该逻辑集群上创建的topic
          </span>
        </div>
      </Modal>
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
        {this.renderDeleteCluster()}
      </div>
    );
  }
}
