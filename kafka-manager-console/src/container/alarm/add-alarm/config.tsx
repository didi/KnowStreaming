import * as React from 'react';
import { Tooltip, notification, Radio, Icon, Popconfirm, RadioChangeEvent } from 'component/antd';
import { IMonitorStrategies, ILabelValue } from 'types/base-type';
import { IFormItem, IFormSelect } from 'component/x-form';
import { AlarmSelect } from 'container/alarm/add-alarm/alarm-select';
import { weekOptions } from 'constants/status-map';
import { alarm } from 'store/alarm';
import { app } from 'store/app';
import moment from 'moment';
import { cellStyle } from 'constants/table';
import { timeFormat } from 'constants/strategy';
import { region } from 'store/region';

export const getAlarmColumns = (urlPrefix: string) => {
  const columns = [
    {
      title: '告警规则',
      dataIndex: 'name',
      key: 'name',
      width: '25%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      sorter: (a: IMonitorStrategies, b: IMonitorStrategies) => a.name.charCodeAt(0) - b.name.charCodeAt(0),
      render: (text: string, record: IMonitorStrategies) => {
        return (
          <Tooltip placement="bottomLeft" title={record.name} >
            <a href={`${urlPrefix}/alarm/alarm-detail?id=${record.id}&region=${region.currentRegion}`}> {text} </a>
          </Tooltip>);
      },
    }, {
      title: '所属应用',
      dataIndex: 'appName',
      key: 'appName',
      width: '25%',
      onCell: () => ({
        style: {
          maxWidth: 250,
          ...cellStyle,
        },
      }),
      sorter: (a: IMonitorStrategies, b: IMonitorStrategies) => a.appName.charCodeAt(0) - b.appName.charCodeAt(0),
      render: (text: string, record: IMonitorStrategies) =>
        <Tooltip placement="bottomLeft" title={record.principals} >{text}</Tooltip>,
    }, {
      title: '操作人',
      dataIndex: 'operator',
      key: 'operator',
      width: '20%',
      onCell: () => ({
        style: {
          maxWidth: 100,
          ...cellStyle,
        },
      }),
      sorter: (a: IMonitorStrategies, b: IMonitorStrategies) => a.operator.charCodeAt(0) - b.operator.charCodeAt(0),
      render: (text: string) => <Tooltip placement="bottomLeft" title={text} >{text}</Tooltip>,
    }, {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: '20%',
      sorter: (a: IMonitorStrategies, b: IMonitorStrategies) => b.createTime - a.createTime,
      render: (time: number) => moment(time).format(timeFormat),
    }, {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: '10%',
      render: (text: string, item: IMonitorStrategies) => (
        <>
          <a href={`${urlPrefix}/alarm/modify?id=${item.id}`} className="action-button">编辑</a>
          <Popconfirm
            title="确定删除？"
            onConfirm={() => deteleMonitor(item)}
            cancelText="取消"
            okText="确认"
          >
            <a>删除</a>
          </Popconfirm>
        </>
      ),
    },
  ];
  return columns;
};

export const getRandomKey = () => {
  return (new Date()).getTime();
};

export const deteleMonitor = (item: IMonitorStrategies) => {
  alarm.deteleMonitorStrategies(item.id).then(data => {
    notification.success({ message: '删除成功' });
  });
};

export const getAlarmTime = () => {
  const timeOptions = [] as ILabelValue[];
  const defaultTime = [] as number[];

  for (let i = 0; i < 24; i++) {
    timeOptions.push({
      label: `${i}点`,
      value: i,
    });
    defaultTime.push(i);
  }
  return { timeOptions, defaultTime };
};
export const getAlarmWeek = () => {
  const defWeek = [] as number[];
  for (let i = 0; i < 7; i++) {
    defWeek.push(i);
  }
  return { defWeek, weekOptions };
};

interface IRadioProps {
  onChange?: (result: number) => any;
  value?: number;
}

const isDetailPage = window.location.pathname.includes('/alarm-detail'); // 判断是否为详情

class RadioIcon extends React.Component<IRadioProps> {
  public onRadioChange = (e: RadioChangeEvent) => {
    const { onChange } = this.props;
    if (onChange) {
      onChange(e.target.value);
    }
  }

  public render() {
    const { value } = this.props;
    return (
      <Radio.Group
        name="radiogroup"
        value={value || 3}
        disabled={isDetailPage}
        onChange={this.onRadioChange}
      >
        <Radio value={1} key={1}>
          一级告警
          <Icon type="phone" />
          <Icon type="message" />
          <Icon type="mail" />
          <Icon type="dingding" />
        </Radio>
        <Radio value={2} key={2}>
          二级告警
          <Icon type="message" />
          <Icon type="mail" />
          <Icon type="dingding" />
        </Radio>
        <Radio value={3} key={3}>
          三级告警
          <Icon type="mail" />
          <Icon type="dingding" />
        </Radio>
      </Radio.Group>
    );
  }
}

export const xActionFormMap = [{
  key: 'level',
  label: '报警级别',
  type: 'custom',
  defaultValue: 3,
  customFormItem: <RadioIcon />,
  rules: [{ required: true, message: '请输入报警接收组' }],
}, {
  key: 'alarmPeriod',
  label: '报警周期(分钟)',
  type: 'input_number',
  attrs: {
    min: 0,
    disabled: isDetailPage,
  },
  rules: [{ required: true, message: '请输入报警周期' }],
}, {
  key: 'alarmTimes',
  label: '周期内报警次数',
  type: 'input_number',
  attrs: {
    min: 0,
    disabled: isDetailPage,
  },
  rules: [{ required: true, message: '请输入周期内报警次数' }],
}, {
  key: 'acceptGroup',
  label: '报警接收组',
  type: 'custom',
  customFormItem: <AlarmSelect isDisabled={isDetailPage} />,
  rules: [{ required: true, message: '请输入报警接收组' }],
},
{
  key: 'callback',
  label: '回调地址',
  rules: [{ required: false, message: '请输入回调地址' }],
  attrs: { disabled: isDetailPage },
}] as unknown as IFormSelect[]; // as IFormItem[];

export const xTypeFormMap = [{
  key: 'alarmName',
  label: '告警规则名称',
  rules: [{ required: true, message: '请输入告警规则' }],
  attrs: { placeholder: '请输入告警规则名称', disabled: isDetailPage },
}, {
  key: 'app',
  label: '所属应用',
  type: 'select',
  attrs: {
    placeholder: '请选择',
    optionFilterProp: 'children',
    showSearch: true,
    filterOption: (input: any, option: any) => {
      if (typeof option.props.children === 'object') {
        const { props } = option.props.children as any;
        return (props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
      }
      return (option.props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    onChange: (e: string) => app.changeActiveApp(e),
    disabled: isDetailPage,
  },
  rules: [{ required: true, message: '请输入报警接收组' }],
}] as unknown as IFormSelect[];

export const xTimeFormMap = [{
  key: 'weeks',
  label: '每周',
  type: 'check_box',
  defaultValue: getAlarmWeek().defWeek,
  options: getAlarmWeek().weekOptions,
  rules: [{ required: true, message: '请选择' }],
}, {
  key: 'hours',
  label: '每天',
  type: 'check_box',
  defaultValue: getAlarmTime().defaultTime,
  options: getAlarmTime().timeOptions,
  rules: [{ required: true, message: '请选择' }],
}] as unknown as IFormSelect[];
