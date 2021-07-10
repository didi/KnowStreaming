import * as React from 'react';
import { observer } from 'mobx-react';
import { Tabs, PageHeader, Button, notification, Popconfirm, Spin } from 'antd';
import { handleTabKey } from 'lib/utils';
import { EassentialInfo } from './essential-info';
import { TaskStatusDetails } from './taskStatus-details';
import { ITasksMetaData, ITrigger } from 'types/base-type';
import { triggerClusterTask } from 'lib/api';
import { handlePageBack } from 'lib/utils';
import { admin } from 'store/admin';
import Url from 'lib/url-parser';
import './index.less';

const { TabPane } = Tabs;
let showStatus: boolean = false;
let showContinue: boolean = false;

@observer
export class OperationDetail extends React.Component {
  public taskId: number;
  public taskName: string;

  public state = {
    showContinue: false,
  };

  constructor(props: any) {
    super(props);
    const url = Url();
    this.taskId = Number(url.search.taskId);
  }

  public bindClick() {
    const type = showContinue ? 'start' : 'pause';
    const params = {
      taskId: this.taskId,
      action: type,
      hostname: '',
    } as ITrigger;
    triggerClusterTask(params).then(data => {
      admin.getSubtasksStatus(this.taskId);
      notification.success({ message: `${showContinue ? '继续部署' : '暂停'}成功！` });
    });
  }

  public callBackOrCancel(type: string) {
    const params = {
      taskId: this.taskId,
      action: type,
      hostname: '',
    } as ITrigger;
    triggerClusterTask(params).then(data => {
      admin.getSubtasksStatus(this.taskId);
      notification.success({ message: `${type === 'rollback' ? '回滚任务' : '取消'}成功` });
    });
  }
  public handleVal(value: number) {
    showStatus = (value + '').includes('100') ? true : false;
  }

  public componentDidMount() {
    admin.getTasksMetadata(this.taskId);
    admin.getSubtasksStatus(this.taskId);
  }

  public render() {
    // 任务状态: 30:运行中(展示暂停), 40:暂停(展示开始), 100:完成(都置灰)
    showStatus = admin.taskStatusDetails && admin.taskStatusDetails.status === 100 ? true : false;
    showContinue = admin.taskStatusDetails && admin.taskStatusDetails.status === 40 ? true : false;
    const showRollBack = admin.taskStatusDetails && admin.taskStatusDetails.rollback;
    let tasks = {} as ITasksMetaData;
    tasks = admin.tasksMetaData ? admin.tasksMetaData : tasks;
    return (
      <>
      <Spin spinning={admin.loading}>
      <PageHeader
        className="detail hotspot-header"
        onBack={() => handlePageBack('/admin/operation#1')}
        title={`任务名称${tasks.taskName ? '/' + tasks.taskName : ''}`}
        extra={[
          <Button key="1" type="primary" disabled={showStatus} >
            <Popconfirm
              title={`确定${showContinue ? '开始' : '暂停'}？`}
              onConfirm={() => this.bindClick()}
              cancelText="取消"
              okText="确认"
            >
              <a>{showContinue ? '开始' : '暂停'}</a>
            </Popconfirm>
          </Button>,
          <Button
            key="2"
            type="primary"
            disabled={showRollBack || showStatus}
          >
            <Popconfirm
              title={`确定回滚？`}
              onConfirm={() => this.callBackOrCancel('rollback')}
              cancelText="取消"
              okText="确认"
            >
              <a>回滚</a>
            </Popconfirm>
          </Button>,
          <Button
            key="3"
            type="primary"
            disabled={showStatus}
          >
            <Popconfirm
              title={`确定回滚？`}
              onConfirm={() => this.callBackOrCancel('cancel')}
              cancelText="取消"
              okText="确认"
            >
              <a>取消</a>
            </Popconfirm>
          </Button>,
        ]}
      >
      <Tabs activeKey={location.hash.substr(1) || '1'} type="card" onChange={handleTabKey}>
        <TabPane tab="基本信息" key="1">
          <EassentialInfo tasksMetaData={tasks} />
        </TabPane>
        <TabPane tab="任务状态详情" key="2">
          <TaskStatusDetails handleVal={(value: number) => this.handleVal(value)} />
        </TabPane>
      </Tabs>
      </PageHeader>
      </Spin>
      </>
    );
  }
}
