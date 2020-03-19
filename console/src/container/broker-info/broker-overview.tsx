import * as React from 'react';
import { Select, Button, Tooltip } from 'component/antd';
import { observer } from 'mobx-react';
import urlQuery from 'store/url-query';
import './index.less';
import moment = require('moment');
import { modal } from 'store';
import { selecOptions } from './constant';
import { broker, IBrokerPartition, IOverviewKey } from 'store/broker';

@observer
export class BrokerOverview extends React.Component {
  public renderSquare = (type: IOverviewKey) => {
    return broker.realPartitions.map((item: IBrokerPartition) => {
      const brokerDetail = (
        <div>
          <p><span>ID:</span>{item.brokerId}</p>
          <p><span>{selecOptions[type]}</span>{item[type]}</p>
          <p><span>主机: </span>{item.host}</p>
          <p><span>端口:</span>{item.port}</p>
          <p><span>jmx端口:</span>{item.jmxPort}</p>
          <p><span>启动时间:</span>{moment(item.startTime).format('YYYY-MM-DD HH:mm:ss')}</p>
        </div>
      );
      return (
        <Tooltip key={item.brokerId} placement="right" title={brokerDetail}>
          <a
            className={type === 'notUnderReplicatedPartitionCount' && item[type] ? 'finished' : ''}
            href={`/admin/broker_detail?clusterId=${urlQuery.clusterId}&brokerId=${item.brokerId}`}
            target="_blank"
          >{item[type]}
          </a>
        </Tooltip>
      );
    });
  }

  public render() {
    return (
      <>
        <ul className="topic-line-tool">
          <li>
            <span className="label">Region</span>
            <Select defaultValue="all" style={{ width: '260px' }} onChange={broker.filterSquare}>
              <Select.Option value="all">全部</Select.Option>
              {broker.regionOption.map((i: any) => <Select.Option
                key={i.brokerId}
                value={i.regionName}
              >{i.regionName}
              </Select.Option>)}
            </Select>
          </li>
          <li>
            <span className="label">维度</span>
            <Select defaultValue={broker.viewType} style={{ width: '160px' }} onChange={broker.handleOverview}>
              {Object.keys(selecOptions)
                .map((i: IOverviewKey) => <Select.Option value={i} key={i}>{selecOptions[i]}</Select.Option>)}
            </Select>
          </li>
          <li>
            <Button type="primary" onClick={modal.showLeaderRebalance}>Leader Rebalance</Button>
          </li>
          {
            broker.viewType === 'notUnderReplicatedPartitionCount' ?
              <li className="introduce">
                <span className="common common-green" />
                <span className="label">同步</span>
                <span className="common common-red" />
                <span className="label">未同步</span>
              </li> : ''
          }
        </ul>
        <div className="square-container">
          {this.renderSquare(broker.viewType)}
        </div>
      </>
    );
  }
}
