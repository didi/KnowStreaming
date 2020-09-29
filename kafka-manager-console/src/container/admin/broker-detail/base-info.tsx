import * as React from 'react';
import { ILabelValue, IBrokersBasicInfo, IOptionType, IClusterReal } from 'types/base-type';
import { observer } from 'mobx-react';
import moment from 'moment';
import Url from 'lib/url-parser';
import { admin } from 'store/admin';
import { PageHeader, Descriptions, Spin } from 'component/antd';
import { selectBrokerMap } from 'constants/status-map';
import { StatusGraghCom } from 'component/flow-table';
import { NetWorkFlow, renderTrafficTable } from 'container/network-flow';
import { timeFormat } from 'constants/strategy';

@observer
export class BaseInfo extends React.Component {
  public clusterId: number;
  public brokerId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.brokerId = Number(url.search.brokerId);
  }

  public updateRealStatus = () => {
    admin.getBrokersMetrics(this.clusterId, this.brokerId);
  }

  public onSelectChange(e: IOptionType) {
    return admin.changeBrokerType(e);
  }

  public getOptionApi = () => {
    return admin.getBrokersMetricsHistory(this.clusterId, this.brokerId);
  }

  public componentDidMount() {
    admin.getBrokersBasicInfo(this.clusterId, this.brokerId);
    admin.getBrokersMetrics(this.clusterId, this.brokerId);
  }

  public renderBrokerContent() {
    let content = {} as IBrokersBasicInfo;
    content = admin.brokersBasicInfo ? admin.brokersBasicInfo : content;
    const brokerContent = [{
      value: content.host,
      label: '主机名',
    }, {
      value: content.port,
      label: '服务端口',
    }, {
      value: content.jmxPort,
      label: 'JMX端口',
    },  {
      value: content.topicNum,
      label: 'Topic数',
    }, {
      value: content.leaderCount,
      label: 'Leader分区数',
    }, {
      value: content.partitionCount,
      label: '分区数',
    }, {
      value: moment(content.startTime).format(timeFormat),
      label: '启动时间',
    }];
    return (
      <>
        <div className="chart-title">基本信息</div>
        <PageHeader className="detail" title="">
          <Descriptions size="small" column={3}>
            {brokerContent.map((item: ILabelValue, index: number) => (
              <Descriptions.Item key={index} label={item.label}>{item.value}</Descriptions.Item>
            ))}
          </Descriptions>
        </PageHeader>
      </>
    );
  }

  public renderHistoryTraffic() {
    return (
      <NetWorkFlow
        key="1"
        selectArr={selectBrokerMap}
        type={admin.type}
        selectChange={(value: IOptionType) => this.onSelectChange(value)}
        getApi={() => this.getOptionApi()}
      />
    );
  }

  public renderTrafficInfo = () => {
    return (
      <Spin spinning={admin.realBrokerLoading}>
        {renderTrafficTable(this.updateRealStatus, StatusGragh)}
      </Spin>
    );
  }

  public render() {
    return (
      <>
        {this.renderBrokerContent()}
        {this.renderTrafficInfo()}
        {this.renderHistoryTraffic()}
      </>
    );
  }
}

@observer
class StatusGragh extends StatusGraghCom<IClusterReal> {
  public getData = () => {
    return admin.brokersMetrics;
  }

  public getLoading = () => {
    return admin.realBrokerLoading;
  }
}
