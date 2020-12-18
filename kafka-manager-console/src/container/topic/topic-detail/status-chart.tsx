import * as React from 'react';
import './index.less';
import Url from 'lib/url-parser';
import { topic } from 'store/topic';
import { NetWorkFlow } from './common-detail';
import { IOptionType, ITakeType } from 'types/base-type';
import { selectOptionMap, selectTakeMap } from 'constants/status-map';
import { Divider } from 'component/antd';
import { indexUrl } from 'constants/strategy';

export class StatusChart extends React.Component {
  public clusterId: number;
  public topicName: string;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
  }

  public onSelectChange(e: IOptionType) {
    return topic.changeType(e);
  }

  public onSelectTakeChange(e: ITakeType) {
    return topic.changeTakeType(e);
  }

  public getOptionApi = () => {
    return topic.getMetriceInfo(this.clusterId, this.topicName);
  }

  public getTakeApi = () => {
    return topic.getMetriceTake(this.clusterId, this.topicName);
  }

  public componentDidMount() {
    topic.getAppsIdInfo(this.clusterId, this.topicName);
  }

  public render() {
    return (
      <>
        <div className="chart-box-0">
          <div className="chart-title">
            <span className="action-button">历史流量</span>
            <a href={indexUrl.indexUrl} target="_blank">指标说明</a>
          </div>
          <Divider className="chart-divider" />
          <NetWorkFlow
            key="1"
            selectArr={selectOptionMap}
            type={topic.type}
            selectChange={(value: IOptionType) => this.onSelectChange(value)}
            getApi={() => this.getOptionApi()}
            clusterId={this.clusterId}
            topicName={this.topicName}
          />
        </div>
        <div className="chart-box-0">
          <div className="chart-title">
            <span className="action-button">历史耗时信息</span>
            <a href={indexUrl.indexUrl} target="_blank">指标说明</a>
          </div>
          <Divider className="chart-divider" />
          <NetWorkFlow
            key="2"
            selectArr={selectTakeMap}
            type={topic.takeType}
            selectChange={(value: ITakeType) => this.onSelectTakeChange(value)}
            getApi={() => this.getTakeApi()}
          />
        </div>
      </>
    );
  }
}
