import * as React from 'react';
import './index.less';
import Url from 'lib/url-parser';
import { observer } from 'mobx-react';
import { topic, IAppsIdInfo } from 'store/topic';
import { ITopic } from 'types/base-type';
import { Table, Tooltip } from 'component/antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IQuotaQuery } from 'types/base-type';
import { showApplyQuatoModal } from 'container/modal';
import { pagination, cellStyle } from 'constants/table';
import { transBToMB } from 'lib/utils';
import { topicStatusMap } from 'constants/status-map';
import { tableFilter } from 'lib/utils';
import { users } from 'store/users';
@observer
export class AppIdInformation extends SearchAndFilterContainer {
  public clusterId: number;
  public topicName: string;

  public state = {
    searchKey: '',
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.clusterId = Number(url.search.clusterId);
    this.topicName = url.search.topic;
  }

  public renderColumns = (data: any) => {
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

    return [{
      title: '应用Id',
      key: 'appId',
      dataIndex: 'appId',
      sorter: (a: IAppsIdInfo, b: IAppsIdInfo) => a.appId.charCodeAt(0) - b.appId.charCodeAt(0),
    }, {
      title: '应用名称',
      key: 'appName',
      dataIndex: 'appName',
      sorter: (a: IAppsIdInfo, b: IAppsIdInfo) => a.appName.charCodeAt(0) - b.appName.charCodeAt(0),
    }, {
      title: '负责人',
      key: 'appPrincipals',
      dataIndex: 'appPrincipals',
      onCell: () => ({
        style: {
          maxWidth: 120,
          ...cellStyle,
        },
      }),
      render: (text: string) => {
        return (
          <Tooltip placement="bottomLeft" title={text} >
            {text}
          </Tooltip>);
      },
    },
      statusColumn,
    {
      title: '生产配额(MB/s)',
      key: 'produceQuota',
      dataIndex: 'produceQuota',
      render: (val: number) => transBToMB(val),
    }, {
      title: '生产是否限流',
      key: 'produceThrottled',
      dataIndex: 'produceThrottled',
      render: (t: boolean) => <span className={t ? 'fail' : 'success'}>{t ? '是' : '否'}</span>,
    }, {
      title: '消费配额(MB/s)',
      key: 'consumerQuota',
      dataIndex: 'consumerQuota',
      render: (val: number) => transBToMB(val),
    }, {
      title: '消费是否限流',
      key: 'fetchThrottled',
      dataIndex: 'fetchThrottled',
      render: (t: boolean) => <span className={t ? 'fail' : 'success'}>{t ? '是' : '否'}</span>,
    }, {
      title: '操作',
      key: 'action',
      dataIndex: 'action',
      render: (val: string, item: IAppsIdInfo) => {
        const role = users.currentUser.role;
        const showEditBtn = (role == 1 || role == 2) || (item && item.appPrincipals.includes(users.currentUser.username));
        return (
          showEditBtn ? <a onClick={() => this.applyQuotaQuery(item)}>申请配额</a> : '--'
        )
      }
    }];
  }

  public applyQuotaQuery = (item: IAppsIdInfo) => {
    const isPhysicalClusterId = location.search.indexOf('isPhysicalClusterId') > -1;
    topic.getQuotaQuery(item.appId, this.clusterId, this.topicName).then((data) => {
      const record = data && data.length ? data[0] : {} as IQuotaQuery;
      item.clusterId = this.clusterId;
      item.isPhysicalClusterId = isPhysicalClusterId;
      showApplyQuatoModal(item, record);
    });
  }

  public getData(data: IAppsIdInfo[]) {
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    const filterData = searchKey ?
      (data || []).filter(d => ((d.appId !== undefined && d.appId !== null) && d.appId.toLowerCase().includes(searchKey as string))
        || ((d.appName !== undefined && d.appName !== null) && d.appName.toLowerCase().includes(searchKey as string)),
      ) : topic.appsIdInfo;
    return filterData;
  }

  public renderAppList() {
    const { searchKey } = this.state;

    return (
      <>
        <div className="k-row" >
          <ul className="k-tab">
            <li>应用信息</li>
            {this.renderSearch('', '请输入所属应用信息')}
          </ul>
          <div style={searchKey ? { minHeight: 700 } : null}>
            <Table
              loading={topic.loading}
              columns={this.renderColumns(this.getData(topic.appsIdInfo))}
              table-Layout="fixed"
              dataSource={this.getData(topic.appsIdInfo)}
              rowKey="key"
              pagination={pagination}
            />
          </div>
        </div>
      </>
    );
  }

  public componentDidMount() {
    topic.getAppsIdInfo(this.clusterId, this.topicName);
  }

  public render() {
    return (
      <>{this.renderAppList()}</>
    );
  }
}
