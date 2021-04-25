import * as React from 'react';
import './index.less';
import Url from 'lib/url-parser';
import { observer } from 'mobx-react';
import { topic, IRealConsumeDetail, ITopicBaseInfo, IRealTimeTraffic } from 'store/topic';
import { Table, Tooltip, Icon, PageHeader, Descriptions, Spin, Switch } from 'component/antd';
import { ILabelValue } from 'types/base-type';
import { copyString, transMSecondToHour } from 'lib/utils';
import moment from 'moment';
import { StatusGraghCom } from 'component/flow-table';
import { renderTrafficTable } from 'container/network-flow';
import { timeFormat, indexUrl } from 'constants/strategy';

interface IInfoProps {
  baseInfo: ITopicBaseInfo;
}

@observer
export class BaseInformation extends React.Component<IInfoProps> {
  public clusterId: number;
  public topicName: string;
  public percentile: string;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
    this.percentile = "75thPercentile";
  }

  public updateRealStatus = () => {
    topic.getRealTimeTraffic(this.clusterId, this.topicName);
  }

  public updateConsumeStatus = () => {
    topic.getRealConsume(this.clusterId, this.topicName, this.percentile);
  }
  public onSwitch = (val: boolean) => {
    this.percentile = val ? '99thPercentile' : "75thPercentile"
    topic.getRealConsume(this.clusterId, this.topicName, this.percentile);
  }

  public fillBaseInfo() {
    const { baseInfo } = this.props;
    const createTime = moment(baseInfo.createTime).format(timeFormat);
    const modifyTime = moment(baseInfo.modifyTime).format(timeFormat);
    const retentionTime = transMSecondToHour(baseInfo.retentionTime);
    if (baseInfo) {
      const infoList: ILabelValue[] = [{
        label: '健康分',
        value: baseInfo.score,
      }, {
        label: '分区数',
        value: baseInfo.partitionNum,
      }, {
        label: '副本数',
        value: baseInfo.replicaNum,
      }, {
        label: '存储时间',
        value: `${retentionTime} 小时`,
      }, {
        label: '创建时间',
        value: createTime,
      }, {
        label: '更改时间',
        value: modifyTime,
      }, {
        label: '压缩格式',
        value: baseInfo.topicCodeC,
      }, {
        label: '集群ID',
        value: baseInfo.clusterId,
      }, {
        label: '所属region',
        value: baseInfo.regionNameList && baseInfo.regionNameList.join(','),
      }];
      const infoHide: ILabelValue[] = [{
        label: 'Bootstrap Severs',
        value: baseInfo.bootstrapServers,
      }, {
        label: 'Topic说明',
        value: baseInfo.description,
      }];
      return (
        <>
          <div className="chart-title">基本信息</div>
          <PageHeader className="detail" title="">
            <Descriptions size="small" column={3}>
              <Descriptions.Item key={baseInfo.appName} label="所属应用">{baseInfo.appName}</Descriptions.Item>
              <Descriptions.Item key={baseInfo.principals} label="应用负责人">
                <Tooltip placement="bottomLeft" title={baseInfo.principals}>
                  <span className="overview-bootstrap">
                    <Icon
                      onClick={() => copyString(baseInfo.principals)}
                      type="copy"
                      className="didi-theme overview-theme"
                    />
                    <i className="overview-boot">{baseInfo.principals}</i>
                  </span>
                </Tooltip>
              </Descriptions.Item>
              {infoList.map((item: ILabelValue, index: number) => (
                <Descriptions.Item
                  key={index}
                  label={item.label}
                >{item.value}
                </Descriptions.Item>
              ))}
              {infoHide.map((item: ILabelValue, index: number) => (
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
  }

  public realTimeTraffic() {
    // const realTraffic = topic.realTraffic;

    // if (realTraffic) {
    return (
      <>
        <Spin spinning={topic.realLoading}>
          {renderTrafficTable(this.updateRealStatus, StatusGragh)}
        </Spin>
      </>
    );
    // }
  }

  public realTimeConsume() {
    const consumeColumns = [{
      title: ' ',
      dataIndex: 'metricsName',
      key: 'metricsName',
    },
    {
      title: 'RequestQueueTime',
      dataIndex: 'requestQueueTimeMs',
      key: 'requestQueueTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'LocalTime',
      dataIndex: 'localTimeMs',
      key: 'localTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'RemoteTime',
      dataIndex: 'remoteTimeMs',
      key: 'remoteTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'ThrottleTime',
      dataIndex: 'throttleTimeMs',
      key: 'throttleTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'ResponseQueueTime',
      dataIndex: 'responseQueueTimeMs',
      key: 'responseQueueTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'ResponseSendTime',
      dataIndex: 'responseSendTimeMs',
      key: 'responseSendTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    },
    {
      title: 'TotalTime',
      dataIndex: 'totalTimeMs',
      key: 'totalTimeMs',
      render: (t: number) => t === null ? '' : (t ? t.toFixed(2) : 0),
    }];
    const realConsume = topic.realConsume;
    if (realConsume) {
      const consumeData: IRealConsumeDetail[] = [];
      Object.keys(realConsume).map((key: string) => {
        if (realConsume[key]) {
          realConsume[key].metricsName = (key === '0') ? 'Produce' : 'Fetch';
          consumeData.push(realConsume[key]);
        }
      });
      return (
        <>
          <div className="traffic-table">
            <div className="traffic-header">
              <span>
                <span className="action-button">实时耗时</span>
                <a href={indexUrl.indexUrl} target="_blank">指标说明</a>
              </span>
              <span className="switch">
                <span>默认展示75分位数据，点击开启99分位数据</span>
                <Switch checkedChildren="开启" unCheckedChildren="关闭" onChange={this.onSwitch} />
              </span>
              <span className="k-abs" onClick={this.updateConsumeStatus}>
                <i className="k-icon-shuaxin didi-theme mr-5" />
                <a>刷新</a>
              </span>
            </div>
            <Table
              columns={consumeColumns}
              dataSource={consumeData}
              pagination={false}
              loading={topic.consumeLoading}
              rowKey="metricsName"
            />
          </div>
        </>
      );
    }
  }

  public componentDidMount() {
    topic.getRealTimeTraffic(this.clusterId, this.topicName);
    topic.getRealConsume(this.clusterId, this.topicName, this.percentile);
  }

  public render() {
    return (
      <>
        <div className="base-info">
          {this.fillBaseInfo()}
          {this.realTimeTraffic()}
          {this.realTimeConsume()}
        </div>
      </>
    );
  }
}

@observer
export class StatusGragh extends StatusGraghCom<IRealTimeTraffic> {
  public getData = () => {
    return topic.realTraffic;
  }

  public getLoading = () => {
    return topic.realLoading;
  }
}
