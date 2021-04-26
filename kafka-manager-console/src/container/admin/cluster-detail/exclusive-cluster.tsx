import * as React from 'react';

import { Table, notification, Tooltip, Popconfirm, Modal, Button } from 'component/antd';
import { observer } from 'mobx-react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { pagination, cellStyle } from 'constants/table';
import { wrapper } from 'store';
import { admin } from 'store/admin';
import { IXFormWrapper, IBrokersRegions, INewRegions, IMetaData } from 'types/base-type';
import { deleteRegions } from 'lib/api';
import { transBToMB } from 'lib/utils';
import Url from 'lib/url-parser';
import moment from 'moment';
import './index.less';
import { timeFormat } from 'constants/strategy';

@observer
export class ExclusiveCluster extends SearchAndFilterContainer {
  public clusterId: number;

  public state = {
    searchKey: '',
    filterStatus: false,
    deteleRegion: false,
    logicalClusterName: '',
  };

  private xFormModal: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public renderColumns = () => {
    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '10%',
      filters: [{ text: '正常', value: '0' }, { text: '容量已满', value: '1' }],
      onFilter: (value: string, record: IBrokersRegions) => record.status === Number(value),
      render: (t: number) => <span className={t === 0 ? 'success' : 'fail'}>{t === 0 ? '正常' : '容量已满'}</span>,
    }, this.renderColumnsFilter('filterStatus'));
    return [
      {
        title: 'RegionID',
        dataIndex: 'id',
        key: 'id',
        width: '7%',
      },
      {
        title: 'Region名称',
        dataIndex: 'name',
        key: 'name',
        width: '13%',
        onCell: () => ({
          style: {
            maxWidth: 160,
            ...cellStyle,
          },
        }),
        sorter: (a: IBrokersRegions, b: IBrokersRegions) => a.name.charCodeAt(0) - b.name.charCodeAt(0),
        render: (text: string, r: IBrokersRegions) => (
          <Tooltip placement="bottomLeft" title={text}>
            {text}
          </Tooltip>),
      },
      {
        title: 'BrokerIdList',
        dataIndex: 'brokerIdList',
        key: 'brokerIdList',
        width: '10%',
        onCell: () => ({
          style: {
            maxWidth: 200,
            ...cellStyle,
          },
        }),
        render: (value: number[]) => {
          const num = value ? value.join(',') : '';
          return (
            <Tooltip placement="bottomLeft" title={num}>
              {num}
            </Tooltip>);
        },
      },
      {
        title: '预估容量（MB/s）',
        dataIndex: 'capacity',
        key: 'capacity',
        width: '10%',
        sorter: (a: IBrokersRegions, b: IBrokersRegions) => b.capacity - a.capacity,
        render: (t: number) => transBToMB(t),
      },
      {
        title: '实际流量（MB/s）',
        dataIndex: 'realUsed',
        key: 'realUsed',
        width: '10%',
        sorter: (a: IBrokersRegions, b: IBrokersRegions) => b.realUsed - a.realUsed,
        render: (t: number) => transBToMB(t),
      },
      {
        title: '预估流量（MB/s）',
        dataIndex: 'estimateUsed',
        key: 'estimateUsed',
        width: '10%',
        sorter: (a: IBrokersRegions, b: IBrokersRegions) => b.estimateUsed - a.estimateUsed,
        render: (t: number) => transBToMB(t),
      },
      {
        title: '修改时间',
        dataIndex: 'gmtModify',
        key: 'gmtModify',
        width: '10%',
        sorter: (a: IBrokersRegions, b: IBrokersRegions) => b.gmtModify - a.gmtModify,
        render: (t: number) => moment(t).format(timeFormat),
      },
      status,
      {
        title: '备注',
        dataIndex: 'description',
        key: 'description',
        width: '10%',
        onCell: () => ({
          style: {
            maxWidth: 200,
            ...cellStyle,
          },
        }),
        render: (text: string, r: IBrokersRegions) => (
          <Tooltip placement="bottomLeft" title={text}>
            {text}
          </Tooltip>),
      },
      {
        title: '操作',
        width: '10%',
        render: (text: string, record: IBrokersRegions) => {
          return (
            <span className="table-operation">
              <a onClick={() => this.addOrModifyRegion(record)}>编辑</a>
              <Popconfirm
                title="确定删除？"
                onConfirm={() => this.handleDeleteRegion(record)}
                cancelText="取消"
                okText="确认"
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          );
        },
      },
    ];
  }

  public handleDeleteRegion = (record: IBrokersRegions) => {
    const filterRegion = admin.logicalClusters.filter(item => item.regionIdList.includes(record.id));
    if (!filterRegion) {
      return;
    }
    if (filterRegion && filterRegion.length < 1) {
      deleteRegions(record.id).then(() => {
        notification.success({ message: '删除成功' });
        admin.getBrokersRegions(this.clusterId);
      });
      return;
    }
    this.setState({ deteleRegion: true, logicalClusterName: filterRegion[0].logicalClusterName });
    // deleteRegions(record.id).then(() => {
    //   notification.success({ message: '删除成功' });
    //   admin.getBrokersRegions(this.clusterId);
    // });
  }

  public handleExpandOk = () => {
    this.setState({ deteleRegion: false });
  }

  public handleExpandCancel = () => {
    this.setState({ deteleCluster: false });
  }

  public addOrModifyRegion(record?: IBrokersRegions) {
    const content = this.props.basicInfo as IMetaData;

    this.xFormModal = {
      formMap: [
        {
          key: 'name',
          label: 'Region名称',
          rules: [{ required: true, message: '请输入Region名称' }],
          attrs: { placeholder: '请输入Region名称' },
        },
        {
          key: 'clusterName',
          label: '集群名称',
          rules: [{ required: true, message: '请输入集群名称' }],
          defaultValue: content.clusterName,
          attrs: {
            disabled: true,
            placeholder: '请输入集群名称',
          },
        },
        {
          key: 'brokerIdList',
          label: 'Broker列表',
          defaultValue: record ? record.brokerIdList.join(',') : [],
          rules: [{ required: true, message: '请输入BrokerID,多个BrokerID用半角逗号分隔' }],
          attrs: {
            placeholder: '请输入BrokerID,多个BrokerID用半角逗号分隔',
          },
        },
        {
          key: 'status',
          label: '状态',
          type: 'select',
          options: [
            {
              label: '正常',
              value: 0,
            },
            {
              label: '容量已满',
              value: 1,
            },
          ],
          defaultValue: 0,
          rules: [{ required: true, message: '请选择状态' }],
          attrs: {
            placeholder: '请选择状态',
          },
        },
        {
          key: 'description',
          label: '备注',
          type: 'text_area',
          rules: [{
            required: false,
          }],
          attrs: {
            placeholder: '请输入备注',
          },
        },
      ],
      formData: record,
      visible: true,
      title: `${record ? '编辑' : '新增Region'}`,
      onSubmit: (value: INewRegions) => {
        value.clusterId = this.clusterId;
        value.brokerIdList = value.brokerIdList && Array.isArray(value.brokerIdList) ?
          value.brokerIdList : value.brokerIdList.split(',');
        if (record) {
          value.id = record.id;
        }
        delete value.clusterName;
        if (record) {
          return admin.editRegions(this.clusterId, value).then(data => {
            notification.success({ message: '编辑Region成功' });
          });
        }
        return admin.addNewRegions(this.clusterId, value).then(data => {
          notification.success({ message: '新建Region成功' });
        });
      },
    };
    wrapper.open(this.xFormModal);
  }

  public componentDidMount() {
    admin.getBrokersRegions(this.clusterId);
    admin.getLogicalClusters(this.clusterId);
    admin.getBrokersMetadata(this.clusterId);
  }

  public getData<T extends IBrokersRegions>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();
    data = searchKey ? origin.filter((item: IBrokersRegions) =>
      (item.name !== undefined && item.name !== null) && item.name.toLowerCase().includes(searchKey as string)
      || item.brokerIdList && item.brokerIdList.map(item => "" + item).join(',').includes(searchKey as string),
    ) : origin;
    return data;
  }

  public renderRegion() {
    return (
      <Table
        columns={this.renderColumns()}
        dataSource={this.getData(admin.brokersRegions)}
        pagination={pagination}
        rowKey="id"
      />
    );
  }
  // -删除RegionModal
  public renderDeleteRegionModal() {
    return (
      <Modal
        title="提示"
        visible={this.state.deteleRegion}
        // okText="确定"
        // cancelText="取消"
        maskClosable={false}
        // onCancel={() => this.handleExpandCancel()}
        closable={false}
        // onOk={() => this.handleExpandOk()}
        footer={<Button style={{ width: '80px' }} type="primary" onClick={() => this.handleExpandOk()}>确定</Button>}
      // onCancel={() => this.handleExpandCancel()}
      >
        <div className="region-prompt">
          <span>
            该Region已被逻辑集群【 {this.state.logicalClusterName} 】使用，请先解除Region与逻辑集群的关系
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
          <li className="k-add" onClick={() => this.addOrModifyRegion()}>
            <i className="k-icon-xinjian didi-theme" />
            <span>新增Region</span>
          </li>
          {this.renderSearch('', '请输入Region名称／broker ID')}
        </ul>
        {this.renderRegion()}
        {this.renderDeleteRegionModal()}
      </div >
    );
  }
}
