import * as React from 'react';
import { observer } from 'mobx-react';
import { Table, Tooltip } from 'component/antd';
import { diskDefault } from 'constants/status-map';
import Url from 'lib/url-parser';
import { pagination } from 'constants/table';
import { admin } from 'store/admin';
import { IPartitionsLocation } from 'types/base-type';
import { SearchAndFilterContainer } from 'container/search-filter';
import './index.less';

@observer
export class DiskInfo extends SearchAndFilterContainer {
  public clusterId: number;
  public brokerId: number;

  public state = {
    searchKey: '',
    filterStatusVisible: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
  }

  public getDescription = (value: any, record: any) => {
    return Object.keys(value).map((key: keyof any, index: any) => {
      return (
        <>
          <p key={index}>
            <span>{value[key]}</span>
            {(record[key] as []).join(',')}（共{(record[key] as []).length}个)
          </p>
        </>
      );
    });
  }

  public getMoreDetail = (record: IPartitionsLocation) => {
    return (
      <div className="p-description" key={record.key}>
        <p><span>diskName: </span>{record.diskName}</p>
        <p><span>brokerId: </span>{record.brokerId}</p>
        <p><span>isUnderReplicated:</span>{record.underReplicated + ''}</p>
        <p><span>topic: </span>{record.topicName}</p>
        {this.getDescription(diskDefault, record)}
        <p><span>clusterId: </span>{record.clusterId}</p>
        <p><span>underReplicatedPartitions: </span></p>
      </div>
    );
  }

  public componentDidMount() {
    admin.getPartitionsLocation(this.clusterId, this.brokerId);
  }

  public getData<T extends IPartitionsLocation>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IPartitionsLocation) =>
      (item.diskName !== undefined && item.diskName !== null) && item.diskName.toLowerCase().includes(searchKey as string)
      || (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public renderDiskInfo() {
    const underReplicated = Object.assign({
      title: '状态',
      dataIndex: 'underReplicated',
      key: 'underReplicated',
      filters: [{ text: '已同步', value: 'false' }, { text: '未同步', value: 'true' }],
      onFilter: (value: string, record: IPartitionsLocation) => record.underReplicated + '' === value,
      render: (t: boolean) => <span>{t ? '未同步' : '已同步'}</span>,
    }, this.renderColumnsFilter('filterStatusVisible'));
    const columns = [{
      title: '磁盘名称',
      dataIndex: 'diskName',
      key: 'diskName',
      sorter: (a: IPartitionsLocation, b: IPartitionsLocation) => a.diskName.charCodeAt(0) - b.diskName.charCodeAt(0),
      render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
    }, {
      title: 'Topic名称',
      dataIndex: 'topicName',
      key: 'topicName',
      sorter: (a: IPartitionsLocation, b: IPartitionsLocation) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
      render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
    }, {
      title: 'Leader分区',
      dataIndex: 'leaderPartitions',
      key: 'leaderPartitions',
      onCell: () => ({
        style: {
          maxWidth: 250,
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          textOverflow: 'ellipsis',
          cursor: 'pointer',
        },
      }),
      render: (value: number[]) => {
        return (
          <Tooltip placement="bottomLeft" title={value.join('、')}>
            {value.map(i => <span key={i} className="p-params">{i}</span>)}
          </Tooltip>
        );
      },
    }, {
      title: 'Follow分区',
      dataIndex: 'followerPartitions',
      key: 'followerPartitions',
      onCell: () => ({
        style: {
          maxWidth: 250,
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          textOverflow: 'ellipsis',
          cursor: 'pointer',
        },
      }),
      render: (value: number[]) => {
        return (
          <Tooltip placement="bottomLeft" title={value.join('、')}>
            {value.map(i => <span key={i} className="p-params">{i}</span>)}
          </Tooltip>
        );
      },
    }, {
      title: '未同步副本',
      dataIndex: 'notUnderReplicatedPartitions',
      render: (value: number[]) => {
        return (
          <Tooltip placement="bottomLeft" title={value.join('、')}>
            {value.map(i => <span key={i} className="p-params p-params-unFinished">{i}</span>)}
          </Tooltip>
        );
      },
    },
    underReplicated,
    ];

    return (
          <>
          <div className="k-row">
            <ul className="k-tab">
              <li>{this.props.tab}</li>
              {this.renderSearch('', '请输入磁盘名或者Topic名称')}
            </ul>
            <Table
              columns={columns}
              expandIconColumnIndex={-1}
              expandedRowRender={this.getMoreDetail}
              dataSource={this.getData(admin.partitionsLocation)}
              rowKey="key"
              pagination={pagination}
            />
          </div>
          </>
    );
  }

  public render() {
    return (
      admin.partitionsLocation ? <> {this.renderDiskInfo()} </>  : null
    );
  }
}
