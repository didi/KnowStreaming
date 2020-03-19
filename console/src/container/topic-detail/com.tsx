import * as React from 'react';
import { Table, DatePicker, Select, Button, PaginationConfig, notification, Spin, Tooltip } from 'component/antd';
import echarts from 'echarts/lib/echarts';
import { cluster } from 'store/cluster';

// 引入柱状图
import 'echarts/lib/chart/line';
// 引入提示框和标题组件
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import { observer } from 'mobx-react';
import { topic, IConsumeInfo, ITopicStatusInfo } from 'store/topic';
import { StatusGraghCom } from 'component/flow-table';
import { IOptionType } from 'types/base-type';
import moment from 'moment';

export const Base = observer(() => {
  if (!topic.baseInfo) return null;
  return (
    <ul className="base-detail">
      <li className="special"><div><span>负责人：</span>
        <Tooltip placement="top" title={topic.baseInfo.principals}>
          <span className="principal">{topic.baseInfo.principals}</span>
        </Tooltip>
      </div></li>
      <li><span>分区数： </span>{topic.baseInfo.partitionNum} 个</li>
      <li><span>储存时间： </span>{(topic.baseInfo.retentionTime / 3600000).toFixed(0)} 小时</li>
      <li><span>副本数： </span>{topic.baseInfo.replicaNum} 个</li>
      <li><span>创建时间：</span>{moment(topic.baseInfo.createTime).format('YYYY-MM-DD HH:mm:ss')}</li>
      <li><span>Broker数：</span>{topic.baseInfo.brokerNum} 个</li>
      <li><span>修改时间：</span>{moment(topic.baseInfo.modifyTime).format('YYYY-MM-DD HH:mm:ss')}</li>
      <li className="special"><div><span>Region：</span>{topic.baseInfo.regionNames}</div></li>
      <li className="special"><div><span>描述信息：</span>
        <Tooltip placement="top" title={topic.baseInfo.description}>
          <span className="principal">{topic.baseInfo.description}</span>
        </Tooltip>
      </div></li>
    </ul>
  );
});

const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  pageSize: 6,
  showTotal: (total) => `共 ${total} 条`,
};

interface IGroupProps {
  data: IConsumeInfo[];
  pagination?: PaginationConfig;
}

// export const Group = (props: IGroupProps) => {
//   return (
//     <div className="group-detail">
//       <Table columns={groupColumns} dataSource={props.data} pagination={props.pagination || pagination}/>
//     </div>
//   );
// };

export class Group extends React.Component<IGroupProps> {
  public columns = [{
    title: '消费组名称',
    dataIndex: 'consumerGroup',
    key: 'consumerGroup',
    width: '80%',
    render: (t: string, r: IConsumeInfo) => this.renderOp(r),
  }, {
    title: 'location',
    dataIndex: 'location',
    key: 'location',
    width: '20%',
    render: (t: string) => t.toLowerCase(),
  },
  ];

  public renderOp = (record: IConsumeInfo) => {
    return (
      <span className="table-operation">
        <a
          // tslint:disable-next-line:max-line-length
          href={`/user/consumer?topic=${topic.currentTopicName}&clusterId=${topic.currentClusterId}&group=${record.consumerGroup}&location=${record.location.toLowerCase()}#2`}
        >
          {record.consumerGroup}
        </a>
      </span>
    );
  }

  public render() {
    return (
      <div className="group-detail">
        <Table
          columns={this.columns}
          dataSource={this.props.data}
          pagination={this.props.pagination || pagination}
          rowKey="consumerGroup"
          scroll={{ y: 400 }}
        />
      </div>
    );
  }
}

@observer
export class StatusGragh extends StatusGraghCom<ITopicStatusInfo> {
  public getData = () => {
    return topic.statusInfo;
  }
}
@observer
export class NetWorkFlow extends React.Component<any> {
  public id: HTMLDivElement = null;
  public chart: echarts.ECharts;

  public state = {
    loading: true,
    data: false,
    type: 'normal',
  };

  public componentDidMount() {
    this.chart = echarts.init(this.id);
    cluster.initTime();
    this.handleSearch();
  }

  public handleApi = () => {
    const { topicName, brokerId, clusterId } = this.props;
    if (topicName) {
      this.setState({ type: 'topic' });
      return cluster.getMetriceInfo(clusterId, topicName);
    }
    if (brokerId !== undefined) return cluster.getBrokerMetrics(clusterId, brokerId);
    return cluster.getClusterMetricsHistory(clusterId);
  }

  public handleSearch = () => {
    const { startTime, endTime } = cluster;
    if (startTime >= endTime) {
      notification.error({ message: '开始时间不能大于或等于结束时间' });
      return false;
    }
    this.setState({ loading: true });
    this.handleApi().then(data => {
      this.setState({ loading: false });
      this.setState({ data: !data.xAxis.data.length });
      this.chart.setOption(data as any, true);
    });
  }
  public handleChange = (value: IOptionType) => {
    this.chart.setOption(cluster.changeType(value) as any, true);
  }

  public handleStartTimeChange(value: moment.Moment) {
    cluster.changeStartTime(value);
  }

  public handleEndTimeChange(value: moment.Moment) {
    cluster.changeEndTime(value);
  }

  public render() {
    return (
      <>
        <div className="status-graph">
          <ul className="k-toolbar topic-line-tool">
            <li>
              <span className="label">开始时间</span>
              <DatePicker showTime={true} value={cluster.startTime} onChange={this.handleStartTimeChange} />
            </li>
            <li>
              <span className="label" >结束时间</span>
              <DatePicker showTime={true} value={cluster.endTime} onChange={this.handleEndTimeChange} />
            </li>
            <li>
              <span className="label">类型</span>
              <Select defaultValue={cluster.type} style={{ width: '160px' }} onChange={this.handleChange}>
                <Select.Option value="byteIn/byteOut">Bytes In/Bytes Out</Select.Option>
                <Select.Option value="byteRejected">Bytes Rejected</Select.Option>
                {this.state.type === 'topic' ? <Select.Option value="messageIn/totalProduceRequests">Message In/TotalProduceRequests</Select.Option> :
                  <Select.Option value="messageIn">Message In</Select.Option>}
              </Select>
            </li>
            <li><Button type="primary" size="small" onClick={this.handleSearch}>查询</Button></li>
            {/* <li><Button type="primary" size="small">配额信息</Button></li> */}
          </ul>
        </div>
        <Spin spinning={this.state.loading} >
          {this.state.data ? <div className="nothing-style">暂无数据</div> : null}
          <div style={{ height: 400 }} ref={(id) => this.id = id} />
        </Spin>
      </>
    );
  }
}
