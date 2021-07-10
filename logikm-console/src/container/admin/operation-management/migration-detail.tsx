import * as React from 'react';
import './index.less';
import { Table, PageHeader, Descriptions, Divider, Tooltip } from 'component/antd';
import { wrapper } from 'store';
import Url from 'lib/url-parser';
import { expert } from 'store/expert';
import { classStatusMap } from 'constants/status-map';
import { admin } from 'store/admin';
import { observer } from 'mobx-react';
import { IXFormWrapper, IReassign, IDetailVO, ILabelValue, IEnumsMap } from 'types/base-type';
import { modifyTransferTask } from 'container/modal';
import { SearchAndFilterContainer } from 'container/search-filter';
import { handlePageBack } from 'lib/utils';
import moment from 'moment';
import './index.less';
import { timeFormat } from 'constants/strategy';

@observer
export class MigrationDetail extends SearchAndFilterContainer {
  public taskId: number;

  public state = {
    filterStatusVisible: false,
  };
  private xFormModal: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.taskId = Number(url.search.taskId);
  }

  public showDetails() {
    const isUrl = window.location.href.includes('/expert') ? '/expert#2' : '/admin/operation';
    const detail = expert.tasksDetail;
    const gmtCreate = moment(detail.gmtCreate).format(timeFormat);
    const startTime = moment(detail.beginTime).format(timeFormat);
    const endTime = detail.endTime == null ? '任务运行中' : moment(detail.endTime).format(timeFormat);
    const options = [{
      value: detail.taskName,
      label: '任务名称',
    }, {
      value: gmtCreate,
      label: '创建时间',
    }, {
      value: detail.operator,
      label: '创建人',
    }, {
      value: startTime,
      label: '计划开始时间',
    }, {
      value: endTime,
      label: '完成时间',
    }];
    return (
      <>
        <PageHeader
          className="detail hotspot-header"
          onBack={() => handlePageBack(isUrl)}
          title={`Topic数据迁移任务/${detail.taskName || ''}`}
        >
          <Divider className="hotspot-divider" />
          <Descriptions column={3}>
            {options.map((item: ILabelValue, index) => (
              <Descriptions.Item key={index} label={item.label}>{item.value}</Descriptions.Item>
            ))}
            <Descriptions.Item label="任务说明">
              <Tooltip placement="bottomLeft" title={detail.description}>
                <span className="overview">
                  {detail.description}
                </span>
              </Tooltip>
            </Descriptions.Item>
          </Descriptions>
        </PageHeader>
      </>
    );
  }

  public detailsTable() {
    let taskList = [] as IReassign[];
    taskList = expert.tasksStatus ? expert.tasksStatus : taskList;
    const taskStatus = admin.configsTaskStatus as IEnumsMap[];
    const status = Object.assign({
      title: '任务状态',
      dataIndex: 'status',
      key: 'status',
      filters: taskStatus.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: number, record: IReassign) => record.status === +value,
      render: (t: number) => {
        let message = '';
        taskStatus.forEach((ele: any) => {
            if (ele.code === t) {
              message = ele.message;
            }
        });
        let statusName = '';
        if (t === 100 || t === 101) {
          statusName = 'success';
        } else if ( t === 40 || t === 99 || t === 102 || t === 103 || t === 104 || t === 105 || t === 106) {
          statusName = 'fail';
        }
        return <span className={statusName}>{message}</span>;
      },
    }, this.renderColumnsFilter('filterStatusVisible'));
    const columns = [
      {
        title: 'Topic名称',
        dataIndex: 'topicName',
        key: 'topicName',
        sorter: (a: IReassign, b: IReassign) => a.topicName.charCodeAt(0) - b.topicName.charCodeAt(0),
        render: (val: string) => <Tooltip placement="bottomLeft" title={val}> {val} </Tooltip>,
      },
      {
        title: '所在集群',
        dataIndex: 'clusterName',
        key: 'clusterName',
      },
      {
        title: '迁移进度',
        dataIndex: 'PartitionNum',
        key: 'PartitionNum',
        render: (text: string, item: IReassign) => <span>{item.completedPartitionNum}/{item.totalPartitionNum}</span>,
      },
      status,
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        render: (text: string, item: IReassign) => (
          <>
          <a onClick={() => this.renderRessignDetail(item)} style={{ marginRight: 16 }}>详情</a>
          <a onClick={() => modifyTransferTask(item, 'modify', this.taskId)}>编辑</a>
          </>
        ),
      },
    ];
    return (
      <>
        <Table rowKey="key" dataSource={taskList} columns={columns} />
      </>
    );
  }

  public renderRessignDetail(item: IReassign) {
    let statusList = [] as IDetailVO[];
    statusList = item.reassignList ? item.reassignList : statusList;
    this.xFormModal = {
      type: 'drawer',
      noform: true,
      nofooter: true,
      visible: true,
      title: '查看任务状态',
      customRenderElement: this.renderInfo(statusList),
      width: 500,
      onSubmit: () => {
        // TODO:
      },
    };
    wrapper.open(this.xFormModal);
  }

  public renderInfo(statusList: IDetailVO[]) {
    const statusColumns = [
      {
        title: '分区ID',
        dataIndex: 'partitionId',
        key: 'partitionId',
      },
      {
        title: '目标BrokerID',
        dataIndex: 'destReplicaIdList',
        key: 'destReplicaIdList',
        onCell: () => ({
          style: {
            maxWidth: 180,
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            cursor: 'pointer',
          },
        }),
        render: (t: []) => {
          return t.map(i => <span key={i} className="p-params">{i}</span>);
        },
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        render: (t: number) => {
          let message = '';
          const taskStatus = admin.configsTaskStatus ? admin.configsTaskStatus : [] as IEnumsMap[];
          taskStatus.forEach((ele: any) => {
              if (ele.code === t) {
                message = ele.message;
              }
          });
          return <span className={`${classStatusMap[t]} p-params`}>{message}</span>;
        },
      },
    ];

    return (
      <Table rowKey="key" dataSource={statusList} columns={statusColumns} />
    );
  }

  public componentDidMount() {
    expert.getReassignTasksDetail(this.taskId);
    expert.getReassignTasksStatus(this.taskId);
    admin.getConfigsTaskStatus();
  }

  public render() {
    return (
      expert.tasksDetail ?
      (
        <>
          {this.showDetails()}
          {admin.configsTaskStatus ? this.detailsTable() : null}
        </>
      ) : null
    );
  }
}
