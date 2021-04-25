import { wrapper } from 'store';
import { notification } from 'component/antd';
import { expert } from 'store/expert';
import { admin } from 'store/admin';
import { IMigration, IReassignTasks, IExecute } from 'types/base-type';
import { createMigrationTask } from 'lib/api';
import { transMBToB, transHourToMSecond, transMSecondToHour } from 'lib/utils';
import moment = require('moment');
import { timeFormat } from 'constants/strategy';

const updateFormModal = (topicName?: string) => {
  const formMap = wrapper.xFormWrapper.formMap;
  const formData = wrapper.xFormWrapper.formData;
  if (topicName) {
    formMap[2].options = expert.partitionIdMap[topicName]; // 3
    formData.originalRetentionTime = transMSecondToHour(admin.topicsBasic.retentionTime);
  } else {
    formMap[1].options = expert.taskTopicMetadata;
    formMap[4].options = admin.brokersMetadata; // 2
    formMap[5].options = admin.brokersRegions;
  }
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData, !!topicName, ['partitionIdList']);
};

const updateInputModal = (status?: string) => {
  const formMap = wrapper.xFormWrapper.formMap;
  formMap[4].invisible = status === 'region';
  formMap[5].invisible = status !== 'region';

  formMap[4].rules = [{ required: status !== 'region' }];
  formMap[5].rules = [{ required: status === 'region' }];
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData);
};

let clusterId = 0 as number;

export const createMigrationTasks = () => {
  const xFormModal = {
    type: 'drawer',
    width: 700,
    formMap: [
      {
        key: 'clusterId',
        label: '集群名称',
        type: 'select',
        options: expert.metaData ? expert.metaData.slice(1).map(item => {
          return {
            label: item.clusterName,
            value: item.clusterId,
          };
        }) : [],
        rules: [{
          required: true,
        }],
        attrs: {
          async onChange(value: number) {
            clusterId = value;
            await admin.getBrokersMetadata(value);
            await admin.getBrokersRegions(value);
            await expert.getTaskTopicMetadata(value);
            updateFormModal();
          },
        },
      },
      {
        key: 'topicName',
        label: 'Topic名称',
        type: 'select',
        options: (expert.taskTopicMetadata),
        rules: [{
          required: true,
        }],
        attrs: {
          showSearch: true,
          optionFilterProp: 'children',
          async onChange(value: string) {
            await admin.getTopicsBasicInfo(clusterId, value);
            updateFormModal(value);
          },
        },
      },
      {
        key: 'partitionIdList',
        label: '分区ID',
        type: 'select',
        defaultValue: [] as any,
        rules: [{
          required: false,
        }],
        attrs: {
          mode: 'tags',
          placeholder: '请选择PartitionIdList',
        },
      },
      {
        key: 'species',
        label: '类型',
        type: 'radio_group',
        defaultValue: 'broker',
        options: [{
          label: 'Region',
          value: 'region',
        }, {
          label: 'Broker',
          value: 'broker',
        }],
        rules: [{
          required: false,
          message: '请选择类型',
        }],
        attrs: {
          onChange(item: any) {
            updateInputModal(item.target.value);
          },
        },
      },
      {
        key: 'brokerIdList',
        label: 'Broker',
        type: 'select',
        defaultValue: [] as any,
        invisible: false,
        options: admin.brokersMetadata,
        rules: [{ required: true, message: '请选择目标Broker,Broker数量需大于等于副本数量' }],
        attrs: {
          mode: 'multiple',
          placeholder: '请选择目标Broker,Broker数量需大于等于副本数量',
        },
      },
      {
        key: 'regionId',
        label: 'Region',
        type: 'select',
        defaultValue: [] as any,
        invisible: true,
        options: admin.brokersRegions,
        rules: [{ required: false, message: '请选择目标Region' }],
        attrs: {
          placeholder: '请选择目标Region',
        },
      },

      {
        key: 'beginTime',
        label: '计划开始时间',
        type: 'date_picker',
        rules: [{
          required: true,
          message: '请输入计划开始时间',
        }],
        attrs: {
          placeholder: '请输入计划开始时间',
          format: timeFormat,
          showTime: true,
        },
      },
      {
        key: 'originalRetentionTime',
        label: '原Topic保存时间',
        rules: [{
          required: true,
          message: '请输入原Topic保存时间',
        }],
        attrs: {
          disabled: true,
          placeholder: '请输入原Topic保存时间',
          suffix: '小时',
        },
      },
      {
        key: 'reassignRetentionTime',
        label: '迁移后Topic保存时间',
        rules: [{
          required: true,
          message: '请输入迁移后Topic保存时间',
        }],
        attrs: {
          placeholder: '请输入迁移后Topic保存时间',
          suffix: '小时',
        },
      },
      {
        key: 'throttle',
        label: '初始限流',
        rules: [{
          required: true,
          message: '请输入初始限流，并按照：“限流上限>初始限流>限流下限”的大小顺序',
        }],
        attrs: {
          placeholder: '请输入初始限流，并按照：“限流上限>初始限流>限流下限”的大小顺序',
          suffix: 'MB/s',
        },
      },
      {
        key: 'maxThrottle',
        label: '限流上限',
        rules: [{
          required: true,
          message: '请输入限流上限，并按照：“限流上限>初始限流>限流下限”的大小顺序',
        }],
        attrs: {
          placeholder: '请输入限流上限，并按照：“限流上限>初始限流>限流下限”的大小顺序',
          suffix: 'MB/s',
        },
      },
      {
        key: 'minThrottle',
        label: '限流下限',
        rules: [{
          required: true,
          message: '请输入限流下限，并按照：“限流上限>初始限流>限流下限”的大小顺序',
        }],
        attrs: {
          placeholder: '请输入限流下限，并按照：“限流上限>初始限流>限流下限”的大小顺序',
          suffix: 'MB/s',
        },
      },
      {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{
          required: false,
          message: '请输入至少5个字符',
          pattern: /^.{4,}.$/,
        }],
        attrs: {
          placeholder: '请输入备注',
        },
      },
    ],
    formData: {},
    visible: true,
    title: '新建迁移任务',
    onSubmit: (value: any) => {
      const params = {
        clusterId: value.clusterId,
        beginTime: +moment(value.beginTime).format('x'),
        originalRetentionTime: transHourToMSecond(value.originalRetentionTime),
        reassignRetentionTime: transHourToMSecond(value.reassignRetentionTime),
        throttle: transMBToB(value.throttle),
        maxThrottle: transMBToB(value.maxThrottle),
        minThrottle: transMBToB(value.minThrottle),
        description: value.description,
        brokerIdList: value.brokerIdList,
        regionId: value.regionId,
        partitionIdList: value.partitionIdList,
        topicName: value.topicName,
      } as IMigration;
      if (value.regionId) {
        delete params.brokerIdList;
      } else {
        delete params.regionId;
      }
      createMigrationTask([params]).then(data => {
        notification.success({ message: '新建迁移任务成功' });
        expert.getReassignTasks();
      });
    },
  };
  wrapper.open(xFormModal);
};

export const cancelMigrationTask = (item: IReassignTasks, action: string) => {
  const params = {
    action,
    taskId: item.taskId,
    beginTime: +moment(item.beginTime).format('x'),
    throttle: Number(item.throttle),
    maxThrottle: Number(item.maxThrottle),
    minThrottle: Number(item.minThrottle),
  } as IExecute;
  expert.getExecuteTask(params).then(data => {
    notification.success({ message: '操作成功' });
  });
};
