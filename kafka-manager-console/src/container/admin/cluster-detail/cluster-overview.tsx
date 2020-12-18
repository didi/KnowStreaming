import * as React from 'react';
import { PageHeader, Descriptions, Divider, Tooltip, Icon, Spin } from 'component/antd';
import { ILabelValue, IMetaData, IOptionType, IClusterReal } from 'types/base-type';
import { controlOptionMap, clusterTypeMap } from 'constants/status-map';
import { copyString } from 'lib/utils';
import { observer } from 'mobx-react';
import { admin } from 'store/admin';
import Url from 'lib/url-parser';
import moment from 'moment';
import './index.less';
import { StatusGraghCom } from 'component/flow-table';
import { renderTrafficTable, NetWorkFlow } from 'container/network-flow';
import { timeFormat } from 'constants/strategy';

interface IOverview {
  basicInfo: IMetaData;
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
    const content = this.props.basicInfo as IMetaData;
    const gmtCreate = moment(content.gmtCreate).format(timeFormat);
    const clusterContent = [{
      value: content.clusterName,
      label: '集群名称',
    }, 
    // {
    //   value: clusterTypeMap[content.mode],
    //   label: '集群类型',
    // }, 
    {
      value: gmtCreate,
      label: '接入时间',
    }];
    const clusterInfo = [{
      value: content.kafkaVersion,
      label: 'kafka版本',
    }, {
      value: content.bootstrapServers,
      label: 'Bootstrap Severs',
    }, {
      value: content.zookeeper,
      label: 'Zookeeper',
    }];
    return (
      <>
        <div className="chart-title">基本信息</div>
        <PageHeader className="detail" title="">
          <Descriptions size="small" column={3}>
            {clusterContent.map((item: ILabelValue, index: number) => (
              <Descriptions.Item
                key={index}
                label={item.label}
              >{item.value}
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
    admin.getClusterRealTime(this.clusterId);
  }

  public onSelectChange(e: IOptionType) {
    return admin.changeType(e);
  }

  public getOptionApi = () => {
    return admin.getClusterMetrice(this.clusterId);
  }

  public componentDidMount() {
    admin.getClusterRealTime(this.clusterId);
  }

  public renderHistoryTraffic() {
    return (
      <NetWorkFlow
        key="1"
        selectArr={controlOptionMap}
        type={admin.type}
        selectChange={(value: IOptionType) => this.onSelectChange(value)}
        getApi={() => this.getOptionApi()}
      />
    );
  }

  public renderTrafficInfo = () => {
    return (
      <Spin spinning={admin.realClusterLoading}>
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
    return admin.clusterRealData;
  }
  public getLoading = () => {
    return admin.realClusterLoading;
  }
}
