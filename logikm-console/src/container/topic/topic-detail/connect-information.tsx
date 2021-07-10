import * as React from 'react';
import './index.less';
import { observer } from 'mobx-react';
import { topic, IConnectionInfo } from 'store/topic';
import { Table, Tooltip } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import Url from 'lib/url-parser';
import { pagination, cellStyle } from 'constants/table';

@observer
export class ConnectInformation extends SearchAndFilterContainer {
  public clusterId: number;
  public topicName: string;

  public state = {
    searchKey: '',
  };
  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
  }

  public renderConnectionInfo(connectInfo: IConnectionInfo[]) {
    const clientType = Object.assign({
      title: '客户端类型',
      dataIndex: 'clientType',
      key: 'clientType',
      width: '20%',
      filters: [{ text: '消费', value: 'consumer' }, { text: '生产', value: 'produce' }],
      onFilter: (value: string, record: IConnectionInfo) => record.clientType.indexOf(value) === 0,
      render: (t: string) =>
        <span>{t === 'consumer' ? '消费' : '生产'}</span>,
    }, this.renderColumnsFilter('filterVisible'));

    const columns = [{
      title: 'AppID',
      dataIndex: 'appId',
      key: 'appId',
      width: '20%',
      sorter: (a: IConnectionInfo, b: IConnectionInfo) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
    },
    {
      title: '主机名',
      dataIndex: 'hostname',
      key: 'hostname',
      width: '40%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      render: (t: string) => {
        return (
          <Tooltip placement="bottomLeft" title={t} >{t}</Tooltip>
        );
      },
    },
    {
      title: '客户端版本',
      dataIndex: 'clientVersion',
      key: 'clientVersion',
      width: '20%',
    },
      clientType,
    ];
    if (connectInfo) {
      return (
        <>
          <Table dataSource={connectInfo} columns={columns} pagination={pagination} loading={topic.loading} />
        </>
      );
    }
  }

  public getData<T extends IConnectionInfo>(origin: T[]) {
    let data: T[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (searchKey !== '') {
      data = origin.filter(d =>
        ((d.appId !== undefined && d.appId !== null) && d.appId.toLowerCase().includes(searchKey as string))
        || ((d.hostname !== undefined && d.hostname !== null) && d.hostname.toLowerCase().includes(searchKey as string)),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public componentDidMount() {
    // const appId = this.props.baseInfo.appId;
    topic.getConnectionInfo(this.clusterId, this.topicName, '');
  }

  public render() {
    return (
      <>
        <div className="k-row" >
          <ul className="k-tab">
            <li>
              连接信息 <span style={{ color: '#a7a8a9', fontSize: '12px', padding: '0 15px' }}>展示近20分钟的连接信息</span>
            </li>
            {this.renderSearch('', '请输入连接信息', 'searchKey')}
          </ul>
          {this.renderConnectionInfo(this.getData(topic.connectionInfo))}
        </div>
      </>
    );
  }
}
