import * as React from 'react';
import './index.less';
import { Table, DatePicker } from 'antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { expert } from 'store/expert';
import { observer } from 'mobx-react';
import { IAnomalyFlow } from 'types/base-type';
import { pagination } from 'constants/table';
import { timeMinute } from 'constants/strategy';

import moment = require('moment');
import { region } from 'store/region';

@observer
export class Diagnosis extends SearchAndFilterContainer {
  public onOk(value: any) {
    const timestamp = +moment(value).format('x');
    expert.getAnomalyFlow(timestamp);
  }

  public selectTime() {
    return (
      <>
        <div className="zoning-otspots">
          <div>
            <span>选择时间：</span>
            <DatePicker showTime={true} format={timeMinute} defaultValue={moment()} onOk={this.onOk} />
          </div>
        </div>
      </>
    );
  }

  public pendingTopic() {
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        width: '30%',
        sorter: (a: IAnomalyFlow, b: IAnomalyFlow) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, item: IAnomalyFlow) =>
          (
            <a
              // tslint:disable-next-line:max-line-length
              href={`${this.urlPrefix}/topic/topic-detail?clusterId=${item.clusterId}&topic=${item.topicName}&region=${region.currentRegion}`}
            >
              {text}
            </a>),
      },
      {
        title: '所在独享集群',
        dataIndex: 'clusterName',
        width: '20%',
      },
      {
        title: 'IOPS',
        dataIndex: 'iops',
        width: '20%',
        sorter: (a: IAnomalyFlow, b: IAnomalyFlow) => b.iops - a.iops,
        render: (val: number, item: IAnomalyFlow) => (
            // tslint:disable-next-line:max-line-length
            <span> {val === null ? '' : (val / 1024).toFixed(2)}{item.iopsIncr === null ? '' : `（${(item.iopsIncr / 1024).toFixed(2)}）`}</span> ),
      },
      {
        title: '流量',
        dataIndex: 'bytesIn',
        width: '20%',
        sorter: (a: IAnomalyFlow, b: IAnomalyFlow) => b.bytesIn - a.bytesIn,
        render: (val: number, item: IAnomalyFlow) => (
          // tslint:disable-next-line:max-line-length
          <span> {val === null ? '' : (val / 1024).toFixed(2)}{item.bytesInIncr === null ? '' : `（${(item.bytesInIncr / 1024).toFixed(2)}）`}</span> ),
      },
    ];
    return (
      <>
        {this.selectTime()}
        <Table
          columns={columns}
          dataSource={expert.anomalyFlowData}
          pagination={pagination}
        />
      </>
    );
  }

  public componentDidMount() {
    const timestamp = +moment(moment()).format('x');
    expert.getAnomalyFlow(timestamp);
  }

  public render() {
    return (
      <>
        {this.pendingTopic()}
      </>
    );
  }
}
