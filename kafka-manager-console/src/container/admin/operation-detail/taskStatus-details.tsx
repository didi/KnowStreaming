import * as React from 'react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { pagination } from 'constants/table';
import { admin } from 'store/admin';
import { Table, Popconfirm, notification } from 'component/antd';
import { IEnumsMap, ITaskStatusDetails, ISubtasksStatus, ITrigger, IXFormWrapper } from 'types/base-type';
import { tableFilter } from 'lib/utils';
import { triggerClusterTask } from 'lib/api';
import { wrapper } from 'store';
import { observer } from 'mobx-react';
import Url from 'lib/url-parser';
import './index.less';

let taskStatus = [] as IEnumsMap[];
let task = {} as ITaskStatusDetails;
let subTaskStatusList = [] as ISubtasksStatus[];
let statusNum: number;

@observer
export class TaskStatusDetails extends SearchAndFilterContainer {
  public taskId: number;
  public timer = null as any;

  public state = {
    searchKey: '',
    filterGroupVisible: false,
    filterStatusVisible: false,
  };

  private xFormWrapper: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.taskId = Number(url.search.taskId);
  }

  public renderDataMigrationTasks(subTaskStatusList: ISubtasksStatus[]) {
    const groupId = Object.assign({
      title: '分组ID',
      dataIndex: 'groupId',
      key: 'groupId',
      width: '20%',
      filters: tableFilter<any>(subTaskStatusList, 'groupId'),
      onFilter: (value: number, record: ISubtasksStatus) => record.groupId === value,
    }, this.renderColumnsFilter('filterGroupVisible'));
    const status = Object.assign({
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '20%',
      filters: taskStatus.map(ele => ({ text: ele.message, value: ele.code + '' })),
      onFilter: (value: number, record: ISubtasksStatus) => record.status === +value,
      render: (t: number) => {
        let messgae: string;
        taskStatus.map(ele => {
          if (ele.code === t) {
            messgae = ele.message;
          }
        });
        return(
          <span className={t === 102 ? 'fail' : t === 101 ? 'succee' : ''}>{messgae}</span>
        );
      },
    }, this.renderColumnsFilter('filterStatusVisible'));
    const columns = [{
      title: '主机名',
      dataIndex: 'hostname',
      key: 'hostname',
      width: '20%',
      sorter: (a: ISubtasksStatus, b: ISubtasksStatus) => a.hostname.charCodeAt(0) - b.hostname.charCodeAt(0),
    }, {
      title: '机器角色',
      dataIndex: 'kafkaRoles',
      key: 'kafkaRoles',
      width: '20%',
    },
    groupId,
    status,
    {
      title: '操作',
      width: '20%',
      render: (value: any, record: ISubtasksStatus) => {
        return (
          <>
          <span className="btn-position">
          <Popconfirm
            title={`确定忽略？`}
            onConfirm={() => this.bindClick(record, 'ignore')}
            cancelText="取消"
            okText="确认"
          >
            <a>忽略</a>
          </Popconfirm>
          </span>
          <a onClick={() => this.handleViewLog(record)}>查看日志</a>
          </>
        );
      },
    },
    ];
    return (
      <>
        <Table
          columns={columns}
          dataSource={subTaskStatusList}
          pagination={pagination}
          rowKey="hostname"
        />
      </>
    );
  }

  public handleViewLog = async (record: ISubtasksStatus) => {
    await admin.getClusterTaskLog(this.taskId, record.hostname);
    this.xFormWrapper = {
      type: 'drawer',
      visible: true,
      width: 600,
      title: '查看日志',
      customRenderElement: this.showLog(),
      nofooter: true,
      noform: true,
      onSubmit: (value: any) => {
        // TODO:
      },
    };
    await wrapper.open(this.xFormWrapper);
  }

  public showLog() {
    return (
      <>
        <div className="config-info">
          {admin.clusterTaskLog ? admin.clusterTaskLog : '暂无数据'}
        </div>
      </>
    );
  }

  public bindClick(record: ISubtasksStatus, type: string) {
    const params = {
      taskId: this.taskId,
      action: type,
      hostname: record.hostname,
    } as ITrigger;
    triggerClusterTask(params).then(data => {
      admin.getSubtasksStatus(this.taskId);
      notification.success({ message: `${type === 'cancel' ? '取消' : '忽略'}成功！` });
    });
  }

  public iTimer = () => {
    this.timer = setInterval(() => {
      admin.getSubtasksStatus(this.taskId);
    }, 3 * 1 * 1000);
  }

  public componentDidMount() {
    setTimeout(this.iTimer, 0);
    admin.getConfigsTaskStatus();
  }

  public componentWillUnmount() {
    clearInterval(this.timer);
  }

  public getData<T extends ISubtasksStatus>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: ISubtasksStatus) =>
      (item.hostname !== undefined && item.hostname !== null) && item.hostname.toLowerCase().includes(searchKey as string),
    ) : origin ;
    return data;
  }

  public render() {
    let message = '';
    if (admin.taskStatusDetails) {
      task = admin.taskStatusDetails;
      subTaskStatusList = admin.taskStatusDetails.subTaskStatusList;
      statusNum = admin.taskStatusDetails.status;
      this.props.handleVal(statusNum);
    }
    taskStatus = admin.configsTaskStatus ? admin.configsTaskStatus : [] as IEnumsMap[];
    taskStatus.forEach(ele => {
      if (ele.code === task.status) {
        message = ele.message;
      }
    });
    return(
      <>
       <div className="k-row" >
          <ul className="k-tab task-status">
            <li>
              状态：<span className="complete">{message}</span>
              ｜ 总数：<span className="complete">{task.sumCount}</span>
              ｜ 成功：<span className="success">{task.successCount}</span>
              ｜ 失败：<span className="fail">{task.failedCount}</span>
              ｜ 执行中：<span className="executing">{task.runningCount}</span>
              ｜ 待执行：<span className="pending">{task.waitingCount}</span>
            </li>
            {this.renderSearch('', '请输入主机名')}
          </ul>
          {this.renderDataMigrationTasks(this.getData(subTaskStatusList))}
        </div>
      </>
    );
  }

}
