import * as React from 'react';
import { Tabs, PageHeader, Button } from 'antd';
import { observer } from 'mobx-react';
import { AlarmHistory } from './alarm-history';
import { handleTabKey } from 'lib/utils';
import { ShieldHistory } from './shield-history';
import { IXFormWrapper } from 'types/base-type';
import { alarm } from 'store/alarm';
import { IMonitorStrategyDetail } from 'types/alarm';
import { urlPrefix } from 'constants/left-menu';
import { createMonitorSilences } from 'container/modal';
import { AddAlarm } from '../add-alarm';
import { handlePageBack } from 'lib/utils';
import Url from 'lib/url-parser';

const { TabPane } = Tabs;

@observer
export class AlarmDetail extends React.Component {
  public id: number = null;
  public monitorName: any = null;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.id = Number(url.search.id);
  }

  public render() {
    let baseInfo = {}  as IMonitorStrategyDetail;
    if (alarm.monitorStrategyDetail) {
      baseInfo = alarm.monitorStrategyDetail;
    }
    this.monitorName = baseInfo.name;
    return(
      <>
        <PageHeader
          className="detail topic-detail-header"
          onBack={() => handlePageBack('/alarm')}
          title={`${baseInfo.name || ''}`}
          extra={[
            <Button key="1" type="primary">
             <a href={`${urlPrefix}/alarm/modify?id=${this.id}`}>编辑</a>
            </Button>,
            <Button onClick={() => {createMonitorSilences(this.id, this.monitorName); }} key="2" >
              屏蔽
            </Button>,
          ]}
        />
        <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
          <TabPane tab="基本信息" key="1">
            <AddAlarm />
          </TabPane>
          <TabPane tab="告警历史" key="2">
            <AlarmHistory />
          </TabPane>
          <TabPane tab="屏蔽历史" key="3">
            <ShieldHistory />
          </TabPane>
        </Tabs>
      </>
    );
  }
}
