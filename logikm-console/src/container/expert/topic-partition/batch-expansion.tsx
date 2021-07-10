import * as React from 'react';
import './index.less';
import { Table, Button, InputNumber, notification, Tooltip } from 'antd';
import { IPartition, IEepand } from 'types/base-type';
import { pagination } from 'constants/table';
import { observer } from 'mobx-react';
import { getExpandTopics } from 'lib/api';
import { urlPrefix } from 'constants/left-menu';
import { region } from 'store/region';

@observer
export class BatchExpansion extends React.Component<any> {
  public estimateData = [] as IPartition[];

  public onPartitionChange(value: number, item: IPartition, index: number) {
    this.estimateData.forEach((element: IPartition) => {
      if (element.topicName === item.topicName) {
        element.suggestedPartitionNum = value;
      }
    });
  }

  public createPartition() {
    return (
      <>
        <div className="create-partition">
          <h2>新建Topic分区扩容</h2>
          <div>
            <Button onClick={() => this.cancelExpansion()} className="create-button">取消</Button>
            <Button type="primary" onClick={() => this.ConfirmExpansion()}>确认扩容</Button>
          </div>
        </div>
      </>
    );
  }

  public ConfirmExpansion() {
    const paramsData = [] as IEepand[];
    this.estimateData.forEach(item => {
      const hash = {
        brokerIdList: item.brokerIdList,
        clusterId: item.clusterId,
        topicName: item.topicName,
        partitionNum: item.suggestedPartitionNum,
        regionId: '',
      } as IEepand;
      paramsData.push(hash);
    });
    getExpandTopics(paramsData).then(data => {
      notification.success({ message: '扩容成功' });
      this.props.history.push(`${urlPrefix}/expert#2`);
    });
  }

  public cancelExpansion() {
    const { onChange } = this.props;
    onChange(true);
  }

  public partitionExpansion() {
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        sorter: (a: IPartition, b: IPartition) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, item: IPartition) => (
          <Tooltip placement="bottomLeft" title={text}>
            <a
              // tslint:disable-next-line:max-line-length
              href={`${urlPrefix}/topic/topic-detail?clusterId=${item.clusterId}&topic=${item.topicName}&isPhysicalClusterId=true&region=${region.currentRegion}`}
            >{text}
            </a>
          </Tooltip>),
      },
      {
        title: '所在集群',
        dataIndex: 'clusterName',
      },
      {
        title: '当前分区数量',
        dataIndex: 'presentPartitionNum',
      },
      {
        title: '预计分区数量',
        dataIndex: 'suggestedPartitionNum',
        render: (val: number, item: IPartition, index: number) => (
            // tslint:disable-next-line:max-line-length
            <InputNumber className="batch-input" min={0} defaultValue={val} onChange={(value) => this.onPartitionChange(value, item, index)} />
        ),
      },
      {
        title: '新分区broker',
        dataIndex: 'brokerIdList',
        render: (val: number, item: IPartition, index: number) => (
          item.brokerIdList.map((record: number) => (
            <span className="p-params">{record}</span>
          ))
        ),
      },
    ];
    this.estimateData = this.props.capacityData;
    return (
      <>
        <Table
          rowKey="key"
          columns={columns}
          dataSource={this.estimateData}
          pagination={pagination}
        />
      </>
    );
  }

  public render() {
    return (
      <>
        {this.createPartition()}
        {this.partitionExpansion()}
      </>
    );
  }
}
