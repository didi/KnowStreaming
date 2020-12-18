import * as React from 'react';
import { Table, Tabs, PageHeader, Descriptions, Divider, Spin, Icon, Tooltip } from 'component/antd';
import { ILabelValue, ITopic, IAppItem, IConnectionInfo } from 'types/base-type';
import urlQuery from 'store/url-query';
import { tableFilter } from 'lib/utils';
import { app } from 'store/app';
import { topicStatusMap } from 'constants/status-map';
import { urlPrefix } from 'constants/left-menu';
import { observer } from 'mobx-react';
import { pagination } from 'constants/table';
import { copyString } from 'lib/utils';
import { region } from 'store/region';
import { timeFormat } from 'constants/strategy';
import { modal } from 'store/modal';
import { SearchAndFilterContainer } from 'container/search-filter';
import { handlePageBack } from 'lib/utils';
import moment = require('moment');
import './index.less';

const { TabPane } = Tabs;
@observer
export class AppDetail extends SearchAndFilterContainer {
  public state = {
    searchKey: '',
  };

  public getColumns(data: ITopic[]) {
    const statusColumn = Object.assign({
      title: '权限',
      dataIndex: 'access',
      key: 'access',
      filters: tableFilter<ITopic>(data, 'access', topicStatusMap),
      onFilter: (text: number, record: ITopic) => record.access === text,
      render: (val: number) => (
        <div className={val === 0 ? '' : 'success'}>
          {topicStatusMap[val] || ''}
        </div>
      ),
    }, this.renderColumnsFilter('filterStatus')) as any;

    const { currentTab } = app;
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        sorter: (a: ITopic, b: ITopic) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, r: ITopic) => (
          <Tooltip placement="bottomLeft" title={text}>
            <a
              // tslint:disable-next-line:max-line-length
              href={`${urlPrefix}/topic/topic-detail?clusterId=${r.clusterId}&topic=${r.topicName}&region=${region.currentRegion}`}
            >{text}
            </a>
          </Tooltip>),
      }, {
        title: '集群名称',
        dataIndex: 'clusterName',
        key: 'clusterName',
      }, {
        title: '申请时间',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        sorter: (a: any, b: any) => a.gmtCreate - b.gmtCreate,
        render: (t: number) => moment(t).format(timeFormat),
      },
      statusColumn,
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        render: (text: string, record: ITopic) =>
          <a key={record.key} onClick={() => this.cancelPermission(record)}>取消权限</a>,
      },
    ];
    const tableColumns = [].concat(columns);

    if (currentTab === '1') {
      tableColumns.splice(4, 2);
    }

    return tableColumns;
  }

  public cancelPermission(record: ITopic) {
    modal.showCancelTopicPermission(record);
  }

  public componentDidMount() {
    if (urlQuery.appId) {
      app.getAppDetail(urlQuery.appId);
      app.getAppTopicList(urlQuery.appId);
    }
  }

  public renderBaseInfo(baseInfo: IAppItem) {
    const infoList: ILabelValue[] = [{
      label: '应用名称',
      value: baseInfo.name,
    }, {
      label: '负责人',
      value: baseInfo.principals,
    }];
    const infoCopy: ILabelValue[] = [{
      label: 'AppID',
      value: baseInfo.appId,
    }, {
      label: '密钥',
      value: baseInfo.password,
    }];
    return (
      <PageHeader
        onBack={() => handlePageBack('/topic/app-list')}
        title={baseInfo.name || ''}
      >
        <Divider />
        <Descriptions column={2}>
          {infoList.map((item, key) => (
            <Descriptions.Item key={key} label={item.label}>
              <Tooltip placement="bottomLeft" title={item.value}>
                <span className="overview-bootstrap">
                  <i className="overview-boot">{item.value}</i>
                </span>
              </Tooltip>
            </Descriptions.Item>
          ))}
        </Descriptions>
        <Descriptions column={2}>
          {infoCopy.map((item, key) => (
            <Descriptions.Item key={key} label={item.label}>
              <Icon
                onClick={() => copyString(item.value)}
                type="copy"
                className="didi-theme"
              /> {item.value}
            </Descriptions.Item>
          ))}
        </Descriptions>
        <Descriptions size="small" column={1}>
          <Descriptions.Item label="应用描述">
            <Tooltip placement="bottomLeft" title={baseInfo.description}>
              <span className="overview-bootstrap" style={{ width: '600px' }}>
                <i className="overview-boot"> {baseInfo.description} </i>
              </span>
            </Tooltip>
          </Descriptions.Item>
        </Descriptions>
      </PageHeader>
    );
  }

  public getData<T extends ITopic>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: ITopic) =>
      (item.topicName !== undefined && item.topicName !== null) && item.topicName.toLowerCase().includes(searchKey as string)
      || (item.clusterName !== undefined && item.clusterName !== null) && item.clusterName.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  }

  public renderTable() {
    return (
      <Spin spinning={app.loading}>
        <Table
          columns={this.getColumns(app.topicList)}
          dataSource={this.getData(app.topicList)}
          pagination={pagination}
        />
      </Spin>
    );
  }

  public onChangeTab(e: string) {
    app.setCurrentTab(e);
    if (urlQuery.appId) {
      app.getAppTopicList(urlQuery.appId);
    }
  }

  public render() {
    const { currentTab } = app;
    return (
      <>
        <div className="app-container">
          <div className="base-info">
            {this.renderBaseInfo(app.baseInfo)}
          </div>
          <div className="k-row">
            <Tabs defaultActiveKey="1" type="card" onChange={(e) => this.onChangeTab(e)}>
              <TabPane tab="创建的Topic" key="1" />
              <TabPane tab="有权限Topic" key="2" />
            </Tabs>
            <ul className="k-tab">
              <li>{currentTab === '1' ? '创建的Topic' : '有权限Topic'}</li>
              {this.renderSearch('', '请输入Topic名称/集群名称')}
            </ul>
            {this.renderTable()}
          </div>
        </div>
      </>
    );
  }
}
