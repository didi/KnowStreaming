import * as React from 'react';
import { Table } from 'component/antd';
import Url from 'lib/url-parser';
import { cluster } from 'store/cluster';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import { IBrokerData, IEnumsMap } from 'types/base-type';
import { admin } from 'store/admin';
import { SearchAndFilterContainer } from 'container/search-filter';
import { transBToMB } from 'lib/utils';
import moment from 'moment';
import './index.less';
import { timeFormat } from 'constants/strategy';

@observer
export class ClusterBroker extends SearchAndFilterContainer {
  public clusterId: number;
  public clusterName: string;

  public state = {
    filterPeakFlowVisible: false,
    filterReplicatedVisible: false,
    filterStatusVisible: false,
    searchKey: '',
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.clusterName = decodeURI(url.search.clusterName);
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

  public renderBrokerData() {
    let peakFlow = [] as IEnumsMap[];
    peakFlow =  admin.peakFlowStatusList ?  admin.peakFlowStatusList :  peakFlow;
    const peakFlowStatus = Object.assign({
      title: '峰值状态',
      dataIndex: 'peakFlowStatus',
      key: 'peakFlowStatus',
      filters: peakFlow.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: string, record: IBrokerData) => record.peakFlowStatus === +value,
      render: (value: number) => {
        let messgae: string;
        peakFlow.map(ele => {
          if (ele.code === value) {
            messgae = ele.message;
          }
        });
        return(
          <span>{messgae}</span>
        );
      },
    }, this.renderColumnsFilter('filterPeakFlowVisible'));

    const underReplicated = Object.assign({
      title: '副本状态',
      dataIndex: 'underReplicated',
      key: 'underReplicated',
      filters: [{ text: '同步', value: 'false' }, { text: '未同步', value: 'true' }],
      onFilter: (value: string, record: IBrokerData) => record.underReplicated === (value === 'true') ? true :  false,
      render: (t: boolean) => <span className={t ? 'fail' : 'success'}>{t ? '未同步' : '同步'}</span>,
    }, this.renderColumnsFilter('filterReplicatedVisible'));

    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      filters: [{ text: '未使用', value: '-1' }, { text: '使用中', value: '0' }],
      onFilter: (value: string, record: IBrokerData) => record.status === Number(value),
      render: (t: number) => t === 0 ? '使用中' : '未使用',
    }, this.renderColumnsFilter('filterStatusVisible'));
    const columns = [
      {
        title: 'ID',
        dataIndex: 'brokerId',
        key: 'brokerId',
        sorter: (a: IBrokerData, b: IBrokerData) => b.brokerId - a.brokerId,
        render: (text: number, record: IBrokerData) => <span>{text}</span>,
      },
      {
        title: '主机',
        dataIndex: 'host',
        key: 'host',
        sorter: (a: any, b: any) => a.host.charCodeAt(0) - b.host.charCodeAt(0),
      },
      {
        title: 'Port',
        dataIndex: 'port',
        key: 'port',
        sorter: (a: IBrokerData, b: IBrokerData) => b.port - a.port,
      },
      {
        title: 'JMX Port',
        dataIndex: 'jmxPort',
        key: 'jmxPort',
        sorter: (a: IBrokerData, b: IBrokerData) => b.jmxPort - a.jmxPort,
      },
      {
        title: '启动时间',
        dataIndex: 'startTime',
        key: 'startTime',
        sorter: (a: IBrokerData, b: IBrokerData) => b.startTime - a.startTime,
        render: (time: number) => moment(time).format(timeFormat),
      },
      {
        title: 'Bytes In（MB/s）',
        dataIndex: 'byteIn',
        key: 'byteIn',
        sorter: (a: IBrokerData, b: IBrokerData) => b.byteIn - a.byteIn,
        render: (t: number) => transBToMB(t),
      },
      {
        title: 'Bytes Out（MB/s）',
        dataIndex: 'byteOut',
        key: 'byteOut',
        sorter: (a: IBrokerData, b: IBrokerData) => b.byteOut - a.byteOut,
        render: (t: number) => transBToMB(t),
      },
      // peakFlowStatus,
      underReplicated,
      status,
    ];
    return (
      <Table dataSource={this.getData(cluster.clusterBroker)} columns={columns} pagination={pagination} loading={cluster.loading} />
    );
  }

  public componentDidMount() {
    cluster.getClusterDetailBroker(this.clusterId);
    // admin.getBrokersMetadata(this.clusterId);
  }

  public render() {
    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入ID或主机')}
          </ul>
          {this.renderBrokerData()}
        </div>
      </>
    );
  }
}
