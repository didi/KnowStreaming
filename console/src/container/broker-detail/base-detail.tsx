import * as React from 'react';
import { TopicDetail } from 'container/topic-detail';
import './index.less';
import { broker, IBrokerNetworkInfo } from 'store/broker';
import { observer } from 'mobx-react';
import { StatusGraghCom } from 'component/flow-table';
import urlQuery from 'store/url-query';
import { NetWorkFlow } from 'container/topic-detail/com';

@observer
export class OneBrokerStatus extends StatusGraghCom<IBrokerNetworkInfo> {
  public getData() {
    return broker.oneNetwork;
  }
}

export class BrokerBaseDetail extends TopicDetail {
  public componentDidMount() {
    broker.getOneBrokerNetwork(urlQuery.clusterId, urlQuery.brokerId);
    broker.getBrokerTopicAnalyzer(urlQuery.clusterId, urlQuery.brokerId);
  }

  public render() {
    return (
      <>
        <div className="k-row right-flow mb-24">
          <p className="k-title">基本信息</p>
          <Summary />
        </div>
        <div className="k-row right-flow mb-24">
          <p className="k-title">历史流量</p>
          <NetWorkFlow clusterId={urlQuery.clusterId} brokerId={urlQuery.brokerId} />
        </div>
        <div className="k-row right-flow">
          <p className="k-title">实时流量</p>
          <span className="k-abs"><i className="k-icon-shuaxin didi-theme" />刷新</span>
          <OneBrokerStatus />
        </div>
      </>
    );
  }
}

@observer
class Summary extends React.Component {
  public componentDidMount() {
    broker.getBrokerBaseInfo(urlQuery.clusterId, urlQuery.brokerId);
  }

  public render() {
    return (
      <div className="k-summary">
        <div className="k-row-1">
          <div>主机：{broker.brokerBaseInfo.host}</div>
          <div>启动时间：{broker.brokerBaseInfo.startTime}</div>
        </div>
        <div className="k-row-3">
          <div>
            <span>Topic数</span>
            <p>{broker.brokerBaseInfo.topicNum}</p>
          </div>
          <div>
            <span>分区数</span>
            <p>{broker.brokerBaseInfo.partitionCount}</p>
          </div>
          <div>
            <span>分区Leader</span>
            <p>{broker.brokerBaseInfo.leaderCount}</p>
          </div>
          <div>
            <span>Port</span>
            <p>{broker.brokerBaseInfo.port}</p>
          </div>
          <div>
            <span>JMX Port</span>
            <p>{broker.brokerBaseInfo.jmxPort}</p>
          </div>
        </div>
      </div>
    );
  }
}
