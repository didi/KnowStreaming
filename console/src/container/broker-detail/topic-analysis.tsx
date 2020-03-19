import * as React from 'react';
import { Table } from 'component/antd';
import urlQuery from 'store/url-query';
import { broker, IBrokerMetrics } from 'store/broker';
import { brokerMetrics } from './constant';
import { observer } from 'mobx-react';

const columns = [{
  title: 'Topic名称',
  dataIndex: 'topicName',
  key: 'topicName',
},
{
  title: 'Bytes In(MB/s)',
  dataIndex: 'bytesInRate',
  key: 'bytesInRate',
  render: (t: number, record: any) => `${record && record.bytesIn} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Bytes Out(MB/s)',
  dataIndex: 'bytesOutRate',
  key: 'bytesOutRate',
  render: (t: number, record: any) => `${record && record.bytesOut} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Message In(秒)',
  dataIndex: 'messagesInRate',
  key: 'messagesInRate',
  render: (t: number, record: any) => `${record && record.messagesIn} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Total Fetch Requests(秒)',
  dataIndex: 'totalFetchRequestsRate',
  key: 'totalFetchRequestsRate',
  render: (t: number, record: any) => `${record && record.totalFetchRequests} (${+Math.ceil((t * 100))}%)`,
},
{
  title: 'Total Produce Requests(秒)',
  dataIndex: 'totalProduceRequestsRate',
  key: 'totalProduceRequestsRate',
  render: (t: number, record: any) => `${record && record.totalProduceRequests} (${+Math.ceil((t * 100))}%)`,
}];
@observer
export class TopicAnalysis extends React.Component {
  public componentDidMount() {
    broker.getOneBrokerNetwork(urlQuery.clusterId, urlQuery.brokerId);
    broker.getBrokerTopicAnalyzer(urlQuery.clusterId, urlQuery.brokerId);
  }

  public render() {
    return (
      <>
        <div className="k-row right-flow mb-24">
          <p className="k-title">Broker 状态</p>
          <BrokerStatus />
        </div>
        <div className="k-row right-flow">
          <p className="k-title">Topic 状态</p>
          <span className="k-abs didi-theme" style={{ fontSize: '14px' }}>说明：数值后的百分比表示“占Broker总量的百分比”</span>
          <Table
            rowKey="name"
            columns={columns}
            dataSource={broker.analyzerData.topicAnalysisVOList}
            pagination={false}
          />;
        </div>
      </>
    );
  }
}
@observer
class BrokerStatus extends React.Component {
  public render() {
    return (
      <div className="k-summary">
        <div className="k-row-3">
          <div>
            <span>Broker ID</span>
            <p>{urlQuery.brokerId}</p>
          </div>
          {broker.analyzerData ?
            Object.keys(brokerMetrics).map((i: keyof IBrokerMetrics) => {
              return (
                <div key={i}>
                  <span className={brokerMetrics[i] && brokerMetrics[i].length > 25 ? 'long-text' : ''}>{brokerMetrics[i]}</span>
                  <p>{broker.analyzerData[i] && broker.analyzerData[i].toFixed(2)}</p>
                </div>
              );
            }) : ''}
        </div>
      </div>
    );
  }
}
