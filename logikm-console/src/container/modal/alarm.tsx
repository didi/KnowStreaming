import { notification } from 'component/antd';
import { wrapper, region } from 'store';
import { IMonitorSilences } from 'types/base-type';
import { alarm } from 'store/alarm';
import { urlPrefix } from 'constants/left-menu';
import moment from 'moment';
import { timeFormat } from 'constants/strategy';

export const  createMonitorSilences = (monitorId: number, monitorName: string) => {
  const xFormWrapper = {
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
          message: '请输入说明',
        }],
        attrs: {
          placeholder: '请输入备注',
        },
      },
    ],
    formData: {
      monitorName,
    },
    okText: '确认',
    visible: true,
    width: 600,
    title: '屏蔽',
    onSubmit: (value: any) => {
      const params = {
        description: value.description,
        startTime: +moment(value.beginEndTime[0]).format('x'),
        endTime:  +moment(value.beginEndTime[1]).format('x'),
        monitorId,
      } as IMonitorSilences;
      alarm.createSilences(params, monitorId).then(data => {
        notification.success({ message: '屏蔽成功' });
        window.location.href = `${urlPrefix}/alarm/alarm-detail?id=${monitorId}&region=${region.currentRegion}#3`;
      });
    },
  };
  wrapper.open(xFormWrapper);
};
