import * as React from 'react';
import Url from 'lib/url-parser';
import { region } from 'store';
import { admin } from 'store/admin';
import { app } from 'store/app';
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
import { ConfirmDetailTopicFormWrapper } from 'container/modal/admin/confirm-detail-topic';

import { showEditClusterTopic } from 'container/modal/admin';
import { timeFormat } from 'constants/strategy';

@observer
export class ClusterTopic extends SearchAndFilterContainer {
  public clusterId: number;
  public clusterTopicsFrom: IClusterTopics;

  public state = {
    searchKey: '',
    expandVisible: false,
    detailTopicVisible: false,
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

  // 运维管控－集群列表－Topic列表修改删除业务逻辑-确认删除topic
  public handleConfirmVisible(val: boolean) {
    this.setState({ detailTopicVisible: val });
  }

  public expandPartition(item: IClusterTopics) {
    //   getTopicBasicInfo
    admin.getTopicsBasicInfo(item.clusterId, item.topicName).then(data => {
      this.clusterTopicsFrom = item;
      this.setState({
        expandVisible: true,
      });
    });
    // if (item.logicalClusterId) {
    //   topic.getTopicBasicInfo(item.logicalClusterId, item.topicName).then(data => {
    //     item.regionNameList = topic.baseInfo.regionNameList;
    //     this.clusterTopicsFrom = item;
    //     this.setState({
    //       expandVisible: true,
    //     });
    //   });
    // } else {
    //   this.clusterTopicsFrom = item;
    //   this.setState({
    //     expandVisible: true,
    //   });
    // }
  }

  // 运维管控－集群列表－Topic列表修改删除业务逻辑-确认删除topic
  public confirmDetailTopic(item: IClusterTopics) {
    this.clusterTopicsFrom = item;
    // console.log(this.clusterTopicsFrom);
    this.setState({
      detailTopicVisible: true,
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
    ) : origin;
    return data;
  }

  public componentDidMount() {
    admin.getClusterTopics(this.clusterId);
    app.getAdminAppList()
  }

  public renderClusterTopicList() {
    const clusterColumns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        width: '120px',
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
        title: '分区数',
        dataIndex: 'partitionNum',
        key: 'partitionNum',
        width: '90px',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.partitionNum - a.partitionNum,
      },
      {
        title: 'QPS',
        dataIndex: 'produceRequest',
        key: 'produceRequest',
        // width: '10%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.produceRequest - a.produceRequest,
        render: (t: number) => t === null ? '' : t.toFixed(2),
      },
      {
        title: 'Bytes In(KB/s)',
        dataIndex: 'byteIn',
        key: 'byteIn',
        // width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.byteIn - a.byteIn,
        render: (t: number) => t === null ? '' : (t / 1024).toFixed(2),
      },
      {
        title: 'Bytes Out(KB/s)',
        dataIndex: 'byteOut',
        key: 'byteOut',
        // width: '15%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.byteOut - a.byteOut,
        render: (t: number) => t && t === null ? '' : (t / 1024).toFixed(2),
      },
      {
        title: '所属应用',
        dataIndex: 'appName',
        key: 'appName',
        // width: '10%',
        render: (val: string, record: IClusterTopics) => (
          <Tooltip placement="bottomLeft" title={val} >
            {val}
          </Tooltip>
        ),
      },
      {
        title: '保存时间(h)',
        dataIndex: 'retentionTime',
        key: 'retentionTime',
        // width: '10%',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.retentionTime - a.retentionTime,
        render: (time: any) => transMSecondToHour(time),
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        key: 'updateTime',
        sorter: (a: IClusterTopics, b: IClusterTopics) => b.updateTime - a.updateTime,
        render: (t: number) => moment(t).format(timeFormat),
        // width: '10%',
      },
      {
        title: 'Topic说明',
        dataIndex: 'description',
        key: 'description',
        // width: '15%',
        onCell: () => ({
          style: {
            maxWidth: 180,
            ...cellStyle,
          },
        }),
      },
      {
        title: '操作',
        width: '120px',
        render: (value: string, item: IClusterTopics) => (
          <>
            <a onClick={() => this.getBaseInfo(item)} className="action-button">编辑</a>
            <a onClick={() => this.expandPartition(item)} className="action-button">扩分区</a>
            {/* <a onClick={() => this.expandPartition(item)} className="action-button">删除</a> */}
            <Popconfirm
              title="确定删除？"
              // 运维管控－集群列表－Topic列表修改删除业务逻辑
              onConfirm={() => this.confirmDetailTopic(item)}
              // onConfirm={() => this.deleteTopic(item)}
              cancelText="取消"
              okText="确认"
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
        {this.renderConfirmDetailModal()}
      </>
    );
  }
  // 运维管控－集群列表－Topic列表修改删除业务逻辑-确认删除topic
  public renderConfirmDetailModal() {
    let formData = {} as IClusterTopics;
    formData = this.clusterTopicsFrom ? this.clusterTopicsFrom : formData;
    // console.log(formData);
    return (
      <>
        {this.state.detailTopicVisible && <ConfirmDetailTopicFormWrapper
          deleteTopic={(val: IClusterTopics) => this.deleteTopic(val)}
          handleVisible={(val: boolean) => this.handleConfirmVisible(val)}
          visible={this.state.detailTopicVisible}
          formData={formData}
          clusterId={this.clusterId}
        />}
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
