import * as React from 'react';
import { Table, notification, Button, Divider, Popconfirm } from 'component/antd';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import { IBrokerData, IEnumsMap, IMetaData } from 'types/base-type';
import { admin } from 'store/admin';
import { tableFilter, transBToMB } from 'lib/utils';
import { SearchAndFilterContainer } from 'container/search-filter';
import { DoughnutChart } from 'component/chart';
import { LeaderRebalanceWrapper } from 'container/modal/admin/leader-rebalance';
import { timeFormat } from 'constants/strategy';
import Url from 'lib/url-parser';
import moment from 'moment';
import './index.less';

@observer
export class ClusterBroker extends SearchAndFilterContainer {
  public clusterId: number;
  public clusterName: string;

  public state = {
    searchKey: '',
    filterPeakFlowVisible: false,
    filterReplicatedVisible: false,
    filterRegionVisible: false,
    filterStatusVisible: false,
    reblanceVisible: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public renderBrokerData(clusterBroker: IBrokerData[]) {
    let peakFlow = [] as IEnumsMap[];
    peakFlow = admin.peakFlowStatusList ? admin.peakFlowStatusList : peakFlow;

    const peakFlowStatus = Object.assign({
      title: '峰值状态',
      dataIndex: 'peakFlowStatus',
      key: 'peakFlowStatus',
      width: '8%',
      filters: peakFlow.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: string, record: IBrokerData) => record.peakFlowStatus === +value,
      render: (value: number) => {
        let messgae: string;
        peakFlow.map(ele => {
          if (ele.code === value) {
            messgae = ele.message;
          }
        });
        return (
          <span>{messgae}</span>
        );
      },
    }, this.renderColumnsFilter('filterPeakFlowVisible'));

    const underReplicated = Object.assign({
      title: '副本状态',
      dataIndex: 'underReplicated',
      key: 'underReplicated',
      width: '8%',
      filters: [{ text: '同步', value: 'false' }, { text: '未同步', value: 'true' }],
      onFilter: (value: string, record: IBrokerData) => record.underReplicated === (value === 'true') ? true : false,
      render: (t: boolean) => t !== null ? <span className={t ? 'fail' : 'success'}>{t ? '未同步' : '同步'}</span> : '',
    }, this.renderColumnsFilter('filterReplicatedVisible'));

    const region = Object.assign({
      title: 'regionName',
      dataIndex: 'regionName',
      key: 'regionName',
      width: '10%',
      filters: tableFilter<any>(clusterBroker, 'regionName'),
      onFilter: (value: string, record: IBrokerData) => record.regionName === value,
    }, this.renderColumnsFilter('filterRegionVisible'));

    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '8%',
      filters: [{ text: '未使用', value: '-1' }, { text: '使用中', value: '0' }],
      onFilter: (value: string, record: IBrokerData) => record.status === Number(value),
      render: (t: number) => <span className={t === 0 ? 'success' : 'fail'}>{t === 0 ? '使用中' : '未使用'}</span>,
    }, this.renderColumnsFilter('filterStatusVisible'));
    const columns = [
      {
        title: 'ID',
        dataIndex: 'brokerId',
        key: 'brokerId',
        width: '5%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.brokerId - a.brokerId,
        render: (text: number, record: IBrokerData) => {
          // tslint:disable-next-line:max-line-length
          const query = `clusterId=${this.clusterId}&brokerId=${record.brokerId}`;
          const judge = record.underReplicated === false && record.status !== 0;
          return (
            <span className={judge ? 'fail' : ''}>
              {
              // tslint:disable-next-line:max-line-length
              record.status === 0 ? <a href={`${this.urlPrefix}/admin/broker-detail?${query}`}>{text}</a>
                  : <a style={{ cursor: 'not-allowed', color: '#999' }}>{text}</a>}
            </span>);
        },
      },
      {
        title: '主机',
        dataIndex: 'host',
        key: 'host',
        width: '10%',
        sorter: (a: any, b: any) => a.host.charCodeAt(0) - b.host.charCodeAt(0),
      },
      {
        title: 'Port',
        dataIndex: 'port',
        key: 'port',
        width: '6%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.port - a.port,
      },
      {
        title: 'JMX Port',
        dataIndex: 'jmxPort',
        key: 'jmxPort',
        width: '7%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.jmxPort - a.jmxPort,
      },
      {
        title: '启动时间',
        dataIndex: 'startTime',
        key: 'startTime',
        width: '10%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.startTime - a.startTime,
        render: (time: number) => moment(time).format(timeFormat),
      },
      {
        title: 'Bytes In（MB/s）',
        dataIndex: 'byteIn',
        key: 'byteIn',
        width: '10%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.byteIn - a.byteIn,
        render: (t: number) => transBToMB(t),
      },
      {
        title: 'Bytes Out（MB/s）',
        dataIndex: 'byteOut',
        key: 'byteOut',
        width: '10%',
        sorter: (a: IBrokerData, b: IBrokerData) => b.byteOut - a.byteOut,
        render: (t: number) => transBToMB(t),
      },
      peakFlowStatus,
      underReplicated,
      region,
      status,
      {
        title: '操作',
        width: '10%',
        render: (text: string, record: IBrokerData) => {
          // tslint:disable-next-line:max-line-length
          const query = `clusterId=${this.clusterId}&brokerId=${record.brokerId}`;
          return ( // 0 监控中 可点击详情，不可删除  -1 暂停监控 不可点击详情，可删除
            <>
              {record.status === 0 ?
                <a href={`${this.urlPrefix}/admin/broker-detail?${query}`} className="action-button">详情</a>
                  : <a style={{ cursor: 'not-allowed', color: '#999' }}>详情</a>}
              <Popconfirm
                title="确定删除？"
                onConfirm={() => this.deteleTopic(record)}
                disabled={record.status === 0}
                cancelText="取消"
                okText="确认"
              >
                <a style={record.status === 0 ? { cursor: 'not-allowed', color: '#999' } : {}}>
                  删除
                </a>
              </Popconfirm>
            </>
          );
        },
      },
    ];
    return (
      <Table dataSource={clusterBroker} columns={columns} pagination={pagination} />
    );
  }

  public deteleTopic(record: any) {
    admin.deteleClusterBrokers(this.clusterId, record.brokerId).then(data => {
      notification.success({ message: '删除成功' });
    });
  }

  public reblanceInfo() {
    this.setState({ reblanceVisible: true });
  }

  public handleVisible(val: boolean) {
    this.setState({ reblanceVisible: val });
  }

  public async componentDidMount() {
    await admin.getPeakFlowStatus();
    admin.getClusterBroker(this.clusterId);
    admin.getBrokersMetadata(this.clusterId);
    admin.getBrokersStatus(this.clusterId);
  }

  public getData<T extends IBrokerData>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IBrokerData) =>
      (item.brokerId !== undefined && item.brokerId !== null) && (item.brokerId + '').toLowerCase().includes(searchKey as string)
      || (item.host !== undefined && item.host !== null) && item.host.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public render() {
    const content = this.props.basicInfo as IMetaData;
    if (content) {
      this.clusterName = content.clusterName;
    }
    return (
      <>
        <div className="diagram">
          <div className="diagram-box">
            <h2>峰值使用率分布图</h2>
            <Divider className="hotspot-divider" />
            {admin.peakValueList.length ? <DoughnutChart
              getChartData={() => admin.getPeakFlowChartData(admin.peakValueList, admin.peakValueMap)}
            /> : null}
          </div>
          <div className="diagram-box">
            <h2>副本状态图</h2>
            <Divider className="hotspot-divider" />
            {admin.copyValueList.length ? <DoughnutChart
              getChartData={() => admin.getSideStatusChartData(admin.copyValueList)}
            /> : null}
          </div>
        </div>
        <div className="leader-seacrh">
          <div className="search-top">
            {this.renderSearch('', '请输入ID或主机')}
            <Button onClick={() => this.reblanceInfo()} type="primary">Leader Rebalance</Button>
          </div>
          {this.renderBrokerData(this.getData(admin.clusterBroker))}
        </div>
        { this.state.reblanceVisible && <LeaderRebalanceWrapper
          changeVisible={(val: boolean) => this.handleVisible(val)}
          visible={this.state.reblanceVisible}
          clusterId={this.clusterId}
          clusterName={this.clusterName}
        /> }
      </>
    );
  }
}
