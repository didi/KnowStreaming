import * as React from 'react';
import Url from 'lib/url-parser';
import { region } from 'store';
import { admin } from 'store/admin';
import { Table, notification, Tooltip, Popconfirm } from 'antd';
import { pagination, cellStyle } from 'constants/table';
import { observer } from 'mobx-react';
import { IClusterTopics } from 'types/base-type';
import { deleteClusterTopic } from 'lib/api';
import { SearchAndFilterContainer } from 'container/search-filter';
import { users } from 'store/users';
import { urlPrefix } from 'constants/left-menu';
import { transMSecondToHour } from 'lib/utils';
import './index.less';

import moment = require('moment');
import { ExpandPartitionFormWrapper } from 'container/modal/admin/expand-partition';
import { showEditClusterTopic } from 'container/modal/admin';
import { timeFormat } from 'constants/strategy';

@observer
export class ClusterTopic extends SearchAndFilterContainer {
  public clusterId: number;
  public clusterTopicsFrom: IClusterTopics;

  public state = {
    searchKey: '',
    expandVisible: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public getBaseInfo(item: IClusterTopics) {
    admin.getTopicsBasicInfo(item.clusterId, item.topicName).then(data => {
      showEditClusterTopic(data);
    });
  }

  public handleVisible(val: boolean) {
    this.setState({ expandVisible: val });
  }

  public expandPartition(item: IClusterTopics) {
    this.clusterTopicsFrom = item;
    this.setState({
      expandVisible: true,
    });
  }

  public deleteTopic(item: IClusterTopics) {
    const value = [{
      clusterId: item.clusterId,
      topicName: item.topicName,
      unForce: false,
    }];
    deleteClusterTopic(value).then(data => {
      notification.success({ message: '删除成功' });
      admin.getClusterTopics(this.clusterId);
    });
  }

  public getData<T extends IClusterTopics>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IClusterTopics) =>
      (item.appName !== undefined && item.appName !== null) && item.appName.toLowerCase().includes(searchKey as string)
      || (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public componentDidMount() {
    admin.getClusterTopics(this.clusterId);
  }

  public renderClusterTopicList() {
    const clusterColumns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, record: IClusterTopics) => {
          return (
            <Tooltip placement="bottomLeft" title={record.topicName} >
              <a
                // tslint:disable-next-line:max-line-length
                href={`${urlPrefix}/topic/topic-detail?clusterId=${record.clusterId || ''}&topic=${record.topicName || ''}&isPhysicalClusterId=true&region=${region.currentRegion}`}
              >
                {text}
              </a>
            </Tooltip>);
        },
      },
      {
        title: 'QPS',
        dataIndex: 'produceRequest',
        key: 'produceRequest',
        width: '10%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.produceRequest - a.produceRequest,
        render: (t: number) => t === null ? '' : t.toFixed(2),
      },
      {
        title: 'Bytes In(KB/s)',
        dataIndex: 'byteIn',
        key: 'byteIn',
        width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.byteIn - a.byteIn,
        render: (t: number) => t === null ? '' : (t / 1024).toFixed(2),
      },
      {
        title: '所属应用',
        dataIndex: 'appName',
        key: 'appName',
        width: '10%',
        render: (val: string, record: IClusterTopics) => (
          <Tooltip placement="bottomLeft" title={record.appId} >
            {val}
          </Tooltip>
        ),
      },
      {
        title: '保存时间(h)',
        dataIndex: 'retentionTime',
        key: 'retentionTime',
        width: '10%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.retentionTime - a.retentionTime,
        render: (time: any) =>  transMSecondToHour(time),
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        key: 'updateTime',
        render: (t: number) => moment(t).format(timeFormat),
        width: '10%',
      },
      {
        title: 'Topic说明',
        dataIndex: 'description',
        key: 'description',
        width: '15%',
        onCell: () => ({
          style: {
            maxWidth: 180,
            ...cellStyle,
          },
        }),
      },
      {
        title: '操作',
        width: '30%',
        render: (value: string, item: IClusterTopics) => (
          <>
            <a onClick={() => this.getBaseInfo(item)} className="action-button">编辑</a>
            <a onClick={() => this.expandPartition(item)} className="action-button">扩分区</a>
            <Popconfirm
              title="确定删除？"
              onConfirm={() => this.deleteTopic(item)}
            >
              <a>删除</a>
            </Popconfirm>
          </>
        ),
      },
    ];
    if (users.currentUser.role !== 2) {
      clusterColumns.splice(-1, 1);
    }

    return (
      <>
        <div className="k-row">
          <ul className="k-tab">
            <li>{this.props.tab}</li>
            {this.renderSearch('', '请输入Topic名称，应用名称')}
          </ul>
          <Table
            loading={admin.loading}
            rowKey="key"
            dataSource={this.getData(admin.clusterTopics)}
            columns={clusterColumns}
            pagination={pagination}
          />
        </div>
        {this.renderExpandModal()}
      </>
    );
  }

  public renderExpandModal() {
    let formData = {} as IClusterTopics;
    formData = this.clusterTopicsFrom ? this.clusterTopicsFrom : formData;
    return (
      <>
        {this.state.expandVisible && <ExpandPartitionFormWrapper
          handleVisible={(val: boolean) => this.handleVisible(val)}
          visible={this.state.expandVisible}
          formData={formData}
          clusterId={this.clusterId}
        />}
      </>
    );
  }

  public render() {
    return (
      admin.clusterTopics ? <> {this.renderClusterTopicList()} </> : null
    );
  }
}
