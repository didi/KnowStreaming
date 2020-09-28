import * as React from 'react';
import { Table, Tooltip } from 'component/antd';
import { observer } from 'mobx-react';
import { admin } from 'store/admin';
import { brokerMetrics } from 'constants/status-map';
import { IBrokerHistory, IAnalysisTopicVO } from 'types/base-type';
import Url from 'lib/url-parser';
import './index.less';

const columns = [{
  title: 'Topic名称',
  dataIndex: 'topicName',
  key: 'topicName',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
  render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
},
{
  title: 'Bytes In(KB/s)',
  dataIndex: 'bytesInRate',
  key: 'bytesInRate',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => Number(b.bytesIn) - Number(a.bytesIn),
  render: (t: number, record: any) => `${record && (record.bytesIn / 1024).toFixed(2)} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Bytes Out(KB/s)',
  dataIndex: 'bytesOutRate',
  key: 'bytesOutRate',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => Number(b.bytesOut) - Number(a.bytesOut),
  render: (t: number, record: any) => `${record && (record.bytesOut / 1024).toFixed(2)} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Message In(秒)',
  dataIndex: 'messagesInRate',
  key: 'messagesInRate',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => Number(b.messagesIn) - Number(a.messagesIn),
  render: (t: number, record: any) => `${record && record.messagesIn} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Total Fetch Requests(秒)',
  dataIndex: 'totalFetchRequestsRate',
  key: 'totalFetchRequestsRate',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => Number(b.totalFetchRequests) - Number(a.totalFetchRequests),
  render: (t: number, record: any) => `${record && record.totalFetchRequests} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Total Produce Requests(秒)',
  dataIndex: 'totalProduceRequestsRate',
  key: 'totalProduceRequestsRate',
  sorter: (a: IAnalysisTopicVO, b: IAnalysisTopicVO) => Number(b.totalProduceRequests) - Number(a.totalProduceRequests),
  render: (t: number, record: any) => `${record && record.totalProduceRequests} (${+Math.ceil((t * 100))}%)`,
}];

@observer
export class TopicAnalysis extends React.Component {
  public clusterId: number;
  public brokerId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
  }

  public brokerStatus() {
    return (
      <div className="k-summary">
        <div className="k-row-3">
          <div>
            <span>Broker ID</span>
            <p>{this.brokerId}</p>
          </div>
          {admin.brokersAnalysis ?
            Object.keys(brokerMetrics).map((i: keyof IBrokerHistory) => {
              return (
                <div key={i}>
                  <span className={brokerMetrics[i].length > 25 ? 'long-text' : ''}>
                  {brokerMetrics[i]}</span>
                  <p>{(admin.brokersAnalysis[i] === null || admin.brokersAnalysis[i] === undefined) ?
                    '' : admin.brokersAnalysis[i].toFixed(2)}</p>
                </div>
              );
            }) : ''}
        </div>
      </div>
    );
  }

  public componentDidMount() {
    admin.getBrokersAnalysis(this.clusterId, this.brokerId);
  }

  public render() {
    let analysisTopic = [] as IAnalysisTopicVO[];
    analysisTopic = admin.brokersAnalysisTopic ? admin.brokersAnalysisTopic : analysisTopic;
    return(
      <>
        <div className="k-row right-flow mb-24">
          <p className="k-title">Broker 状态</p>
          {this.brokerStatus()}
        </div>
        <div className="k-row right-flow">
          <div className="title-flow">
            <p className="k-title">Topic 状态</p>
            <span className="didi-theme k-text">说明：数值后的百分比表示“占Broker总量的百分比”</span>
          </div>
          <Table
            rowKey="key"
            columns={columns}
            dataSource={analysisTopic}
            pagination={false}
          />
        </div>
      </>
    );
  }
}
