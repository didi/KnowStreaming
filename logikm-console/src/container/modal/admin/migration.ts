import { wrapper } from 'store';
import { IReassignTasks, IExecute, IReassign, INewBulidEnums, IEnumsMap } from 'types/base-type';
import { notification } from 'component/antd';
import { expert } from 'store/expert';
import { transMBToB, transBToMB } from 'lib/utils';
import moment = require('moment');
import { admin } from 'store/admin';
import { timeFormat } from 'constants/strategy';

export const startMigrationTask = (item: IReassignTasks, action: string) => {
  const params = {
    action,
    beginTime: +moment(item.beginTime).format('x'),
    taskId: item.taskId,
  } as IExecute;
  expert.getExecuteTask(params).then(data => {
    notification.success({ message: '操作成功' });
  });
};

export const modifyMigrationTask = (item: IReassignTasks, action: string) => {
  const status: number = item.status;
  const xFormModal = {
    formMap: [
      {
        key: 'beginTime',
        label: '计划开始时间',
        type: 'date_picker',
        rules: [{
          required: status === 0,
          message: '请输入计划开始时间',
        }],
        attrs: {
          placeholder: '请输入计划开始时间',
          format: timeFormat,
          showTime: true,
          disabled: status !== 0,
        },
      },
    ],
    formData: {
      beginTime: moment(item.beginTime),
    },
    visible: true,
    title: '操作迁移任务',
    onSubmit: (value: IExecute) => {
      const params = {
        action,
        beginTime: +moment(value.beginTime).format('x'),
        taskId: item.taskId,
      } as IExecute;
      expert.getExecuteTask(params).then(data => {
        notification.success({ message: '操作成功' });
      });
    },
  };
  wrapper.open(xFormModal);
};

export const modifyTransferTask = (item: IReassign, action: string, taskId: number) => {
  const status: number = item.status;
  const xFormModal = {
    formMap: [
      {
        key: 'throttle',
        label: '初始限流',
        rules: [{
          required: true,
          message: '请输入初始限流',
        }],
        attrs: {
          placeholder: '请输入初始限流',
          suffix: 'MB/s',
        },
      },
      {
        key: 'maxThrottle',
        label: '限流上限',
        rules: [{
          required: true,
          message: '请输入限流上限',
        }],
        attrs: {
          placeholder: '请输入限流上限',
          suffix: 'MB/s',
        },
      },
      {
        key: 'minThrottle',
        label: '限流下限',
        rules: [{
          required: true,
          message: '请输入限流下限',
        }],
        attrs: {
          placeholder: '请输入限流下限',
          suffix: 'MB/s',
        },
      },
    ],
    formData: {
      throttle: transBToMB(item.realThrottle),
      maxThrottle: transBToMB(item.maxThrottle),
      minThrottle: transBToMB(item.minThrottle),
    },
    visible: true,
    title: '编辑',
    onSubmit: (value: IExecute) => {
      const params = {
        action,
        throttle: transMBToB(value.throttle),
        maxThrottle: transMBToB(value.maxThrottle),
        minThrottle: transMBToB(value.minThrottle),
        subTaskId: item.subTaskId,
      } as IExecute;
      expert.getExecuteSubTask(params, taskId).then(data => {
        notification.success({ message: '操作成功' });
      });
    },
  };
  wrapper.open(xFormModal);
};

const updateFormModal = () => {
  const formMap = wrapper.xFormWrapper.formMap;
  formMap[2].options = admin.packageList;
  formMap[3].options = admin.serverPropertiesList;
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData);
};

const updateFormExclude = (value: boolean) => {
  const formMap = wrapper.xFormWrapper.formMap;
  if (value) {
    formMap[4].invisible = false;
    formMap[5].invisible = false;
    formMap[6].invisible = true;

    formMap[4].rules = [{
      required: true,
    }];
    formMap[5].rules = [{
      required: false,
    }];
    formMap[6].rules = [{
      required: false,
    }];
  } else {
    formMap[4].invisible = true;
    formMap[5].invisible = true;
    formMap[6].invisible = false;

    formMap[4].rules = [{
      required: false,
    }];
    formMap[5].rules = [{
      required: false,
    }];
    formMap[6].rules = [{
      required: true,
    }];
  }
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData);
};

export const addMigrationTask = () => {
  const taskStatus = admin.configsTaskStatus ? admin.configsTaskStatus : [] as IEnumsMap[];
  const xFormModal = {
    formMap: [
      {
        key: 'clusterId',
        label: '集群',
        type: 'select',
        options: admin.metaList.map(item => {
          return {
            label: item.clusterName,
            value: item.clusterId,
          };
        }),
        rules: [{
          required: true,
        }],
        attrs: {
          placeholder: '请选择集群',
          onChange: (value: number) => {
            admin.getTasksKafkaFiles(value).then(() => {
              updateFormModal();
            });
          },
        },
      },
      {
        key: 'taskType',
        label: '任务类型',
        type: 'select',
        options: admin.tasksEnums,
        rules: [{
          required: true,
          message: '请选择集群任务',
        }],
        attrs: {
          placeholder: '请选择集群任务',
          onChange: (value: string) => {
            value === 'role_upgrade' ? updateFormExclude(true) : updateFormExclude(false);
          },
        },
      },
      {
        key: 'kafkafileNameMd5',
        label: '包版本',
        type: 'select',
        options: admin.packageList,
        rules: [{
          required: true,
          message: '请选择包版本',
        }],
        attrs: {
          placeholder: '请选择包版本',
        },
      },
      {
        key: 'serverfileNameMd5',
        label: 'server配置',
        type: 'select',
        options: admin.serverPropertiesList,
        rules: [{
          required: true,
          message: '请选择server配置',
        }],
        attrs: {
          placeholder: '请选择server配置',
        },
      },
      {
        key: 'upgradeSequenceList',
        label: '升级顺序',
        type: 'select',
        options: admin.kafkaRoles.map(item => {
          return {
            label: item.role,
            value: item.role,
          };
        }),
        rules: [{
          required: true,
          message: '请输入升级顺序',
        }],
        defaultValue: [] as any,
        attrs: {
          mode: 'multiple',
          placeholder: '请选择升级顺序',
        },
      },
      {
        key: 'ignoreList',
        label: '排除主机列表',
        type: 'select',
        invisible: true,
        rules: [{
          required: false,
          message: '请输入排除主机列表',
        }],
        defaultValue: [] as any,
        attrs: {
          placeholder: '请输入排除主机列表',
          mode: 'tags',
          tokenSeparators: [','],
        },
      },
      {
        key: 'hostList',
        label: '主机列表',
        type: 'select',
        rules: [{
          required: true,
          message: '请输入主机列表',
        }],
        defaultValue: [] as any,
        attrs: {
          placeholder: '请输入主机列表',
          mode: 'tags',
          tokenSeparators: [' '],
        },
      },
    ],
    formData: {},
    visible: true,
    title: '新建集群任务',
    isWaitting: true,
    onSubmit: (value: INewBulidEnums) => {
      value.kafkaPackageName = value.kafkafileNameMd5.split(',')[0];
      value.kafkaPackageMd5 = value.kafkafileNameMd5.split(',')[1];
      value.serverPropertiesName = value.serverfileNameMd5.split(',')[0];
      value.serverPropertiesMd5 = value.serverfileNameMd5.split(',')[1];
      delete value.kafkafileNameMd5;
      delete value.serverfileNameMd5;
      return admin.addMigrationTask(value).then(data => {
        notification.success({ message: '新建集群任务成功' });
      });
    },
    onSubmitFaild: (err: any, ref: any, formData: any, formMap: any) => {
      if (err.message === '主机列表错误，请检查主机列表') {
        const hostList = ref.getFieldValue('hostList');
        ref.setFields({
          hostList: {
            value: hostList,
            errors: [new Error('主机列表错误，请检查主机列表')],
          }
        })
      }
    }
  };
  wrapper.open(xFormModal);
};
