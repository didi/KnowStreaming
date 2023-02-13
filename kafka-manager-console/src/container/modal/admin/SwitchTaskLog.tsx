import * as React from 'react';
import { Modal, Progress, Tooltip } from 'antd';
import { IMetaData } from 'types/base-type';
import { Alert, Badge, Button, Input, message, notification, Table } from 'component/antd';
import { getJobDetail, getJobState, getJobLog, switchAsJobs } from 'lib/api';
import moment from 'moment';
import { timeFormat } from 'constants/strategy';

interface IProps {
  reload: any;
  visible?: boolean;
  handleVisible?: any;
  currentCluster?: IMetaData;
}

interface IJobState {
  failedNu: number;
  jobNu: number;
  runningNu: number;
  successNu: number;
  waitingNu: number;
  runningInTimeoutNu: number;
  progress: number;
}

interface IJobDetail {
  standbyClusterPhyId: number;
  status: number;
  sumLag: number;
  timeoutUnitSecConfig: number;
  topicName: string;
  activeClusterPhyName: string;
  standbyClusterPhyName: string;
}

interface ILog {
  bizKeyword: string;
  bizType: number;
  content: string;
  id: number;
  printTime: number;
}
interface IJobLog {
  logList: ILog[];
  endLogId: number;
}
const STATUS_MAP = {
  '-1': '未知',
  '30': '运行中',
  '32': '超时运行中',
  '101': '成功',
  '102': '失败',
} as any;
const STATUS_COLORS = {
  '-1': '#575757',
  '30': '#575757',
  '32': '#F5202E',
  '101': '#2FC25B',
  '102': '#F5202E',
} as any;
const STATUS_COLOR_MAP = {
  '-1': 'black',
  '30': 'black',
  '32': 'red',
  '101': 'green',
  '102': 'red',
} as any;

const getFilters = () => {
  const keys = Object.keys(STATUS_MAP);
  const filters = [];
  for (const key of keys) {
    filters.push({
      text: STATUS_MAP[key],
      value: key,
    });
  }
  return filters;
};

const columns = [
  {
    dataIndex: 'key',
    title: '编号',
    width: 60,
  },
  {
    dataIndex: 'topicName',
    title: 'Topic名称',
    width: 120,
    ellipsis: true,
  },
  {
    dataIndex: 'sumLag',
    title: '延迟',
    width: 100,
    render: (value: number) => value ?? '-',
  },
  {
    dataIndex: 'status',
    title: '状态',
    width: 100,
    filters: getFilters(),
    onFilter: (value: string, record: IJobDetail) => record.status === Number(value),
    render: (t: number) => (
      <span className={'col-status ' + STATUS_COLOR_MAP[t]}>
        <Badge color={STATUS_COLORS[t]} text={STATUS_MAP[t]} />
      </span>
    ),
  },
];

export class TopicSwitchLog extends React.Component<IProps> {
  public state = {
    radioCheck: 'all',
    jobDetail: [] as IJobDetail[],
    jobState: {} as IJobState,
    jobLog: {} as IJobLog,
    textStr: '',
    primaryTargetKeys: [] as string[],
    loading: false,
  };
  public timer = null as number;
  public jobId = this.props.currentCluster?.haClusterVO?.haASSwitchJobId as number;

  public handleOk = () => {
    this.props.handleVisible(false);
    this.props.reload();
  }

  public handleCancel = () => {
    this.props.handleVisible(false);
    this.props.reload();
  }

  public iTimer = () => {
    this.timer = window.setInterval(() => {
      const { jobLog } = this.state;
      this.getContentJobLog(jobLog.endLogId);
      this.getContentJobState();
      this.getContentJobDetail();
    }, 10 * 1 * 1000);
  }

  public getTextAreaStr = (logList: ILog[]) => {
    const strs = [];

    for (const item of logList) {
      strs.push(`${moment(item.printTime).format(timeFormat)} ${item.content}`);
    }

    return strs.join(`\n`);
  }

  public getContentJobLog = (startId?: number) => {
    getJobLog(this.jobId, startId).then((res: IJobLog) => {
      const { jobLog } = this.state;
      const logList = (jobLog.logList || []);
      logList.push(...(res?.logList || []));

      const newJobLog = {
        endLogId: res?.endLogId,
        logList,
      };

      this.setState({
        textStr: this.getTextAreaStr(logList),
        jobLog: newJobLog,
      });
    });
  }

  public getContentJobState = () => {
    getJobState(this.jobId).then((res: IJobState) => {
      // 成功后清除调用
      if (res?.jobNu === res.successNu) {
        clearInterval(this.timer);
      }
      this.setState({
        jobState: res || {},
      });
    });
  }
  public getContentJobDetail = () => {
    getJobDetail(this.jobId).then((res: IJobDetail[]) => {
      this.setState({
        jobDetail: (res || []).map((row, index) => ({
          ...row,
          key: index,
        })),
      });
    });
  }

  public switchJobs = () => {
    const { jobState } = this.state;
    Modal.confirm({
      title: '强制切换',
      content: `当前有${jobState.runningNu}个Topic切换中，${jobState.runningInTimeoutNu}个Topic切换超时，强制切换会使这些Topic有数据丢失的风险，确定强制切换吗？`,
      onOk: () => {
        this.setState({
          loading: true,
        });
        switchAsJobs(this.jobId, {
          action: 'force',
          allJumpWaitInSync: true,
          jumpWaitInSyncActiveTopicList: [],
        }).then(res => {
          message.success('强制切换成功');
        }).finally(() => {
          this.setState({
            loading: false,
          });
        });
      },
    });
  }

  public componentWillUnmount() {
    clearInterval(this.timer);
  }

  public componentDidMount() {
    this.getContentJobDetail();
    this.getContentJobState();
    this.getContentJobLog();
    setTimeout(this.iTimer, 0);
  }

  public render() {
    const { visible, currentCluster } = this.props;
    const { jobState, jobDetail, textStr, loading } = this.state;
    const runtimeJob = jobDetail.filter(item => item.status === 32);
    const percent = jobState?.progress;
    return (
      <Modal
        title="主备切换日志"
        wrapClassName="no-padding"
        visible={visible}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        maskClosable={false}
        width={590}
        okText="确认"
        cancelText="取消"
      >
        {runtimeJob.length ?
          <Alert
            message={`${runtimeJob[0].topicName}消息同步已经超时${runtimeJob[0].timeoutUnitSecConfig}s，建议立即强制切换`}
            type="warning"
            showIcon={true}
          />
          : null}
        <div className="switch-warning">
          <Tooltip title="不用等待所有topic数据完成同步，立即进行切换，但是有数据丢失的风险。">
            <Button loading={loading} type="primary" disabled={!runtimeJob.length} onClick={this.switchJobs} className={loading ? 'btn loading' : runtimeJob.length ? 'btn' : 'btn disabled'}>强制切换</Button>
          </Tooltip>
        </div>

        <div className="log-panel">
          <div className="title">
            <div className="divider" />
            <span>Topic切换详情:</span>
          </div>
          <div className="log-process">
            <div className="name">
              <span>源集群 {jobDetail?.[0]?.standbyClusterPhyName || ''}</span>
              <span>目标集群 {jobDetail?.[0]?.activeClusterPhyName || ''}</span>
            </div>
            <Progress percent={percent} strokeColor="#F38031" status={percent === 100 ? 'normal' : 'active'} />
          </div>
          <div className="log-info">
            Topic总数 <span className="text-num">{jobState.jobNu ?? '-'}</span> 个，
            切换成功 <span className="text-num">{jobState.successNu ?? '-'}</span> 个，
            切换超时  <span className="warning-num">{jobState.failedNu ?? '-'}</span> 个，
            待切换  <span className="warning-num">{jobState.waitingNu ?? '-'}</span> 个。
          </div>
          <Table
            className="log-table"
            columns={columns}
            dataSource={jobDetail}
            size="small"
            rowKey="topicName"
            pagination={false}
            bordered={false}
            scroll={{ y: 138 }}
          />
          <div className="title">
            <div className="divider" />
            <span>集群切换日志:</span>
          </div>
          <div>
            <Input.TextArea value={textStr} rows={7} />
          </div>
        </div>

      </Modal >
    );
  }
}
