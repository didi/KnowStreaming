import * as React from 'react';
import { PageHeader, Descriptions, Tooltip, Icon, Spin } from 'component/antd';
import { ILabelValue, IBasicInfo, IOptionType, IClusterReal } from 'types/base-type';
import { selectOptionMap } from 'constants/status-map';
import { observer } from 'mobx-react';
import { cluster } from 'store/cluster';
import { clusterTypeMap } from 'constants/status-map';
import { copyString } from 'lib/utils';
import Url from 'lib/url-parser';
import moment from 'moment';
import './index.less';
import { StatusGraghCom } from 'component/flow-table';
import { renderTrafficTable, NetWorkFlow } from 'container/network-flow';
import { timeFormat } from 'constants/strategy';

interface IOverview {
  basicInfo: IBasicInfo;
}

@observer
export class ClusterOverview extends React.Component<IOverview> {
  public clusterId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
  }

  public clusterContent() {
    const content = this.props.basicInfo as IBasicInfo;
    const clusterContent = [{
      value: content.clusterName,
      label: '集群名称',
    },
    {
      value: content.clusterIdentification,
      label: '集群标识',
    },
    {
      value: clusterTypeMap[content.mode],
      label: '集群类型',
    }, {
      value: moment(content.gmtCreate).format(timeFormat),
      label: '接入时间',
    }, {
      value: content.clusterId,
      label: '集群ID',
    }];
    const clusterInfo = [{
      value: content.clusterVersion,
      label: 'kafka版本',
    }, {
      value: content.bootstrapServers,
      label: 'Bootstrap Severs',
    }];
    return (
      <>
        <div className="chart-title">基本信息</div>
        <PageHeader className="detail" title="">
          <Descriptions size="small" column={3}>
            {clusterContent.map((item: ILabelValue, index: number) => (
              <Descriptions.Item key={index} label={item.label} >
                {item.value}
              </Descriptions.Item>
            ))}
            {clusterInfo.map((item: ILabelValue, index: number) => (
              <Descriptions.Item key={index} label={item.label}>
                <Tooltip placement="bottomLeft" title={item.value}>
                  <span className="overview-bootstrap">
                    <Icon
                      onClick={() => copyString(item.value)}
                      type="copy"
                      className="didi-theme overview-theme"
                    />
                    <i className="overview-boot">{item.value}</i>
                  </span>
                </Tooltip>
              </Descriptions.Item>
            ))}
          </Descriptions>
        </PageHeader>
      </>
    );
  }

  public updateRealStatus = () => {
    cluster.getClusterDetailRealTime(this.clusterId);
  }

  public onSelectChange(e: IOptionType) {
    return cluster.changeType(e);
  }

  public getOptionApi = () => {
    return cluster.getClusterDetailMetrice(this.clusterId);
  }

  public componentDidMount() {
    cluster.getClusterBasicInfo(this.clusterId);
    cluster.getClusterDetailRealTime(this.clusterId);
  }

  public renderHistoryTraffic() {
    return (
      <NetWorkFlow
        key="1"
        selectArr={selectOptionMap}
        type={cluster.type}
        selectChange={(value: IOptionType) => this.onSelectChange(value)}
        getApi={() => this.getOptionApi()}
      />
    );
  }

  public renderTrafficInfo = () => {
    return (
      <Spin spinning={cluster.realLoading}>
        {renderTrafficTable(this.updateRealStatus, StatusGragh)}
      </Spin>
    );
  }

  public render() {
    return (
      <>
        <div className="base-info">
          {this.clusterContent()}
          {this.renderTrafficInfo()}
          {this.renderHistoryTraffic()}
        </div>
      </>
    );
  }
}

@observer
export class StatusGragh extends StatusGraghCom<IClusterReal> {
  public getData = () => {
    return cluster.clusterRealData;
  }
  public getLoading = () => {
    return cluster.realLoading;
  }
}
