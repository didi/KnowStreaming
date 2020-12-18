import * as React from 'react';
import { Table, notification, Modal, Popconfirm } from 'component/antd';
import moment from 'moment';
import { alarm } from 'store/alarm';
import { wrapper } from 'store';
import Url from 'lib/url-parser';
import { IMonitorSilences, IXFormWrapper } from 'types/base-type';
import { observer } from 'mobx-react';
import './index.less';
import { timeFormat } from 'constants/strategy';

@observer
export class ShieldHistory extends React.Component {
  public id: number = null;

  private xFormWrapper: IXFormWrapper;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.id = Number(url.search.id);
  }

  public silencesDetail(record: IMonitorSilences) {
    alarm.getSilencesDetail(record.silenceId).then((data) => {
      if (alarm.silencesDetail) {
        this.modifyInfo(alarm.silencesDetail);
      }
    });
  }

  public modifyInfo(record: IMonitorSilences) {
    Modal.info({
      title: '详情',
      content: (
        <ul className="monitor-detail">
          <li><b>告警规则：</b>{record.monitorName}</li>
          <li><b>开始时间：</b>{moment(record.startTime).format(timeFormat)}</li>
          <li><b>结束时间：</b>{moment(record.endTime).format(timeFormat)}</li>
          <li><b>说明：</b>{record.description}</li>
        </ul>
      ),
    });
  }

  public modifyMonitor(record: IMonitorSilences) {
    this.xFormWrapper = {
      formMap: [
        {
          key: 'monitorName',
          label: '告警规则',
          rules: [{
            required: true,
            message: '请输入告警规则',
          }],
          attrs: {
            disabled: true,
          },
        },
        {
          key: 'beginEndTime',
          label: '开始～结束时间',
          type: 'range_picker',
          rules: [{
            required: true,
            message: '请输入开始～结束时间',
          }],
          attrs: {
            placeholder: ['开始时间', '结束时间'],
            format: timeFormat,
            showTime: true,
            disabled: false,
            ranges: {
              '1小时': [moment(), moment().add(1, 'hour')],
              '2小时': [moment(), moment().add(2, 'hour')],
              '6小时': [moment(), moment().add(6, 'hour')],
              '12小时': [moment(), moment().add(12, 'hour')],
              '1天': [moment(), moment().add(1, 'day')],
              '2天': [moment(), moment().add(7, 'day')],
              '7天': [moment(), moment().add(7, 'day')],
            },
          },
        },
        {
          key: 'description',
          label: '说明',
          type: 'text_area',
          rules: [{
            required: true,
          }],
          attrs: {
            disabled: false,
            placeholder: '请输入备注',
          },
        },
      ],
      formData: {
        monitorName: record.monitorName,
        beginEndTime: [moment(record.startTime), moment(record.endTime)],
        description: record.description,
      },
      okText: '确认',
      visible: true,
      width: 600,
      title: '编辑',
      onSubmit: (value: any) => {
        const params = {
          description: value.description,
          startTime: +moment(value.beginEndTime[0]).format('x'),
          endTime:  +moment(value.beginEndTime[1]).format('x'),
          id: record.silenceId,
          monitorId: record.monitorId,
        } as IMonitorSilences;
        alarm.modifyMask(params, this.id).then(data => {
          notification.success({ message: '编辑成功' });
        });
      },
    };
    wrapper.open(this.xFormWrapper);
  }

  public deleteSilences(record: IMonitorSilences) {
    alarm.deleteSilences(this.id, record.silenceId).then(data => {
      notification.success({ message: '删除成功' });
    });
  }

  public componentDidMount() {
    alarm.getMonitorSilences(this.id);
  }

  public render() {
    const monitorSilences: IMonitorSilences[] = alarm.monitorSilences ? alarm.monitorSilences : [];
    const monitorColumns = [
      {
        title: '监控名称',
        dataIndex: 'monitorName',
        key: 'monitorName',
        render: (text: string) => <span>{text}</span>,
      }, {
        title: '开始时间',
        dataIndex: 'startTime',
        key: 'startTime',
        render: (t: number) => moment(t).format(timeFormat),
      }, {
        title: '结束时间',
        dataIndex: 'endTime',
        key: 'endTime',
        render: (t: number) => moment(t).format(timeFormat),
      }, {
        title: '备注',
        dataIndex: 'description',
        key: 'description',
      }, {
        title: '操作',
        dataIndex: 'option',
        key: 'option',
        render: (action: any, record: IMonitorSilences) => {
          return(
            <>
              <a onClick={() => this.modifyMonitor(record)} className="action-button">编辑</a>
              <a onClick={() => this.silencesDetail(record)} className="action-button">详情</a>
              <Popconfirm
                title="确定删除？"
                onConfirm={() => this.deleteSilences(record)}
                cancelText="取消"
                okText="确认"
              >
                <a>删除</a>
              </Popconfirm>
            </>
          );
        },
      },
    ];
    return(
      <>
        <Table dataSource={monitorSilences} columns={monitorColumns} />
      </>
    );
  }
}
