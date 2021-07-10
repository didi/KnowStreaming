import * as React from 'react';
import './index.less';
import ReactDOM from 'react-dom';
import { Tabs, Table, Alert, Modal, Tooltip, notification } from 'antd';
import { SearchAndFilterContainer } from 'container/search-filter';
import { expert } from 'store/expert';
import { observer } from 'mobx-react';
import { IUtils, IResource } from 'types/base-type';
import { getUtilsTopics } from 'lib/api';
import { offlineStatusMap } from 'constants/status-map';
import { region } from 'store/region';

interface IUncollect {
  clusterId: number;
  code: number;
  message: string;
  topicName: string;
}

@observer
export class GovernanceTopic extends SearchAndFilterContainer {

  public unpendinngRef: HTMLDivElement = null;
  public unofflineRef: HTMLDivElement = null;

  public state = {
    searchKey: '',
  };
  public onSelectChange = {
    onChange: (selectedRowKeys: string[], selectedRows: []) => {
      this.setState({
        hasSelected: !!selectedRowKeys.length,
      });
      const num = selectedRows.length;
      ReactDOM.render(
        selectedRows.length ? (
          <>
            <Alert
              type="warning"
              message={`已选择 ${num} 项 `}
              showIcon={true}
              closable={false}
            />
            <a className="k-coll-btn" onClick={this.uncollect.bind(this, selectedRows)}>下线</a>
          </>) : null,
        this.unpendinngRef,
      );
    },
  };

  public onOfflineChange = {
    onChange: (selectedRowKeys: string[], selectedRows: []) => {
      const num = selectedRows.length;
      ReactDOM.render(
        selectedRows.length ? (
          <>
            <Alert
              type="warning"
              message={`已选择 ${num} 项 `}
              showIcon={true}
              closable={false}
            />
          </>) : null,
        this.unofflineRef,
      );
    },
  };

  public uncollect = (selectedRowKeys: IResource) => {
    let selectedRow = [] as IResource[];
    if (selectedRowKeys instanceof Object) {
      selectedRow.push(selectedRowKeys);
    } else {
      selectedRow = selectedRowKeys;
    }
    Modal.confirm({
      title: `确认下线？`,
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        ReactDOM.unmountComponentAtNode(this.unpendinngRef);
        const paramsData = [] as IUtils[];
        let params = {} as IUtils;
        selectedRow.forEach((item: IResource) => {
          params = {
            clusterId: item.clusterId,
            force: true,
            topicName: item.topicName,
          };
          paramsData.push(params);
        });
        getUtilsTopics(params).then((data: IUncollect[]) => {
          if (data) {
            data.map((ele: IUncollect) => {
              if (ele.code === 0) {
                notification.success({ message: `${ele.topicName}下线成功` });
              } else {
                notification.error({ message: `${ele.topicName}下线失败` });
              }
            });
          }
        }, (err) => {
          notification.error({ message: '操作失败' });
        });
      },
    });
  }

  public getData(origin: IResource[]) {
    let data: IResource[] = [];
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    if (expert.active !== -1 || searchKey !== '') {
      data = origin.filter(d =>
        (
          ((d.topicName !== undefined && d.topicName !== null) && d.topicName.toLowerCase().includes(searchKey as string))
          || ((d.appId !== undefined && d.appId !== null) && d.appId.toLowerCase().includes(searchKey as string))
        )
        && (expert.active === -1 || +d.clusterId === expert.active),
      );
    } else {
      data = origin;
    }
    return data;
  }

  public pendingTopic(resourceData: IResource[]) {
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        width: '30%',
        sorter: (a: IResource, b: IResource) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (text: string, item: IResource) =>
        (
          <Tooltip placement="bottomLeft" title={text}>
            <a
              // tslint:disable-next-line:max-line-length
              href={`${this.urlPrefix}/topic/topic-detail?clusterId=${item.clusterId}&topic=${item.topicName}&isPhysicalClusterId=true&region=${region.currentRegion}`}
            >
              {text}
            </a>
          </Tooltip>),
      },
      {
        title: '所在集群',
        dataIndex: 'clusterName',
        width: '10%',
      },
      {
        title: '过期天数(天)',
        dataIndex: 'expiredDay',
        width: '10%',
      },
      {
        title: '发送连接',
        dataIndex: 'produceConnectionNum',
        width: '10%',
      },
      {
        title: '消费连接',
        dataIndex: 'fetchConnectionNum',
        width: '10%',
      },
      {
        title: '创建人',
        dataIndex: 'appName',
        width: '10%',
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: '10%',
        render: (text: number) => <span>{offlineStatusMap[Number(text)]}</span>,
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: '10%',
        render: (val: string, item: IResource, index: number) => (
          <>
            {item.status !== -1 && <a className="action-button" >通知用户</a>}
            {item.status === -1 && <a onClick={this.uncollect.bind(this, item)}>下线</a>}
          </>
        ),
      },
    ];
    return (
      <>
        <div className="table-operation-panel">
          <ul>
            {this.renderPhysical('物理集群：')}
            {this.renderSearch('名称：', '请输入Topic名称')}
          </ul>
        </div>
        <div className="k-collect" ref={(id) => this.unpendinngRef = id} />
        <Table
          rowKey="key"
          columns={columns}
          dataSource={resourceData}
        />
      </>
    );
  }

  public componentDidMount() {
    expert.getResourceManagement();
    if (!expert.metaData.length) {
      expert.getMetaData(false);
    }
  }

  public render() {

    return (
      <>
        {this.pendingTopic(this.getData(expert.resourceData))}
      </>
    );
  }
}
