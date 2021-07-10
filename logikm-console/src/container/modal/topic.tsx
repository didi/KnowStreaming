import { wrapper } from 'store';
import { notification } from 'component/antd';
import { IQuotaModelItem, ITopic, ILimitsItem, IQuotaQuery } from 'types/base-type';
import { topic, IAppsIdInfo } from 'store/topic';
import { users } from 'store/users';
import { app } from 'store/app';
import { cluster } from 'store/cluster';
import { AppSelect } from 'container/app-select';
import { PeakFlowInput } from '../topic/peak-flow';
import { urlPrefix } from 'constants/left-menu';
import { transMBToB, transBToMB } from 'lib/utils';
import { region } from 'store';
import * as React from 'react';
import '../app/index.less';
import { modal } from 'store/modal';
import { TopicAppSelect } from '../topic/topic-app-select';
import Url from 'lib/url-parser';
import { expandRemarks, quotaRemarks } from 'constants/strategy';

export const applyTopic = () => {
  const xFormModal = {
    formMap: [
      {
        key: 'clusterId',
        label: '所属集群：',
        type: 'select',
        options: cluster.clusterData,
        rules: [{ required: true, message: '请选择' }],
        attrs: {
          placeholder: '请选择',
        },
      }, {
        key: 'topicName',
        label: 'Topic名称：',
        attrs: {
          addonBefore: region.currentRegion === 'us' || region.currentRegion === 'ru' ? `${region.currentRegion}01_` : '',
        },
        rules: [
          {
            required: true,
            pattern: /^[-\w]{3,128}$/,
            message: '只能包含字母、数字、下划线（_）和短划线(-),长度限制在3-128字符之间',
          },
        ],
      },
      {
        key: 'appId',
        label: '所属应用：',
        type: 'custom',
        defaultValue: '',
        rules: [{ required: true, message: '请选择' }],
        customFormItem: <AppSelect selectData={app.data} />,
      }, {
        key: 'peakBytesIn',
        label: '峰值流量',
        type: 'custom',
        rules: [{
          required: true,
          pattern: /^[1-9]\d*$/,
          validator: (rule: any, value: any, callback: any) => {
            const regexp = /^[1-9]\d*$/;
            if (value.length <= 0) {
              callback('流量上限不能为空');
              return false;
            } else if (!regexp.test(value)) {
              callback('流量上限只能填写大于0的正整数');
              return false;
            }
            return true;
          },
        }],
        customFormItem: <PeakFlowInput />,
      },
      {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{ required: true, pattern: /^.{4,}.$/s, message: '请输入至少5个字符' }],
        attrs: {
          placeholder: `概要描述Topic的数据源, Topic数据的生产者/消费者, Topic的申请原因及备注信息等。（最多100个字）
例如:
  数据源：xxx
  生产消费方：xxx
  申请原因及备注：xxx`,
          rows: 7,
        },
      },
    ],
    formData: {},
    visible: true,
    title: <div><span>申请Topic</span><a className='applicationDocument' href="https://github.com/didi/Logi-KafkaManager/blob/master/docs/user_guide/resource_apply.md" target='_blank'>资源申请文档</a></div>,
    okText: '确认',
    // customRenderElement: <span className="tips">集群资源充足时，预计1分钟自动审批通过</span>,
    isWaitting: true,
    onSubmit: (value: any) => {
      value.topicName = region.currentRegion === 'us' || region.currentRegion === 'ru' ?
        `${region.currentRegion}01_` + value.topicName : value.topicName;
      value.peakBytesIn = transMBToB(value.peakBytesIn);
      const params = JSON.parse(JSON.stringify(value));
      delete (params.description);
      const quotaParams = {
        type: 0,
        applicant: users.currentUser.username,
        description: value.description,
        extensions: JSON.stringify(params),
      };
      return topic.applyTopic(quotaParams).then(data => {
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      })
    },
    onSubmitFaild: (err: any, ref: any, formData: any, formMap: any) => {
      if (err.message === 'topic already existed') {
        const topic = ref.getFieldValue('topicName');
        ref.setFields({
          topicName: {
            value: topic,
            errors: [new Error('该topic名称已存在')],
          }
        })
      }
    }
  };
  wrapper.open(xFormModal);
};

export const deferTopic = (item: ITopic) => {
  const xFormModal = {
    formMap: [
      {
        key: 'topicName',
        label: 'Topic名称',
        value: '',
        rules: [{ required: false, disabled: true }],
        attrs: {
          disabled: true,
        },
      },
      {
        key: 'retainDays',
        label: '延期时间',
        type: 'select',
        value: '',
        options: [{
          label: '一周',
          value: '7',
        }, {
          label: '一月',
          value: '30',
        }, {
          label: '三月',
          value: '90',
        }],
        rules: [{ required: false }],
        attrs: { placeholder: '请选择延期时间' },
      },
    ],
    formData: {
      topicName: item.topicName,
      retainDays: '',
    },
    visible: true,
    title: '申请延期',
    okText: '确认',
    onSubmit: (value: any) => {
      value.clusterId = item.clusterId;
      topic.deferTopic(value).then(data => {
        notification.success({ message: '申请延期成功' });
      });
    },
  };
  wrapper.open(xFormModal);
};

export const applyOnlineModal = (item: ITopic) => {
  modal.showOfflineTopicModal(item);
};

export const showApplyQuatoModal = (item: ITopic | IAppsIdInfo, record: IQuotaQuery) => {
  const isProduce = item.access === 0 || item.access === 1;
  const isConsume = item.access === 0 || item.access === 2;
  const xFormModal = {
    formMap: [
      // {
      //   key: 'clusterName',
      //   label: '逻辑集群名称',
      //   rules: [{ required: true, message: '' }],
      //   attrs: { disabled: true },
      //   invisible: !item.hasOwnProperty('clusterName'),
      // }, 
      {
        key: 'topicName',
        label: 'Topic名称',
        rules: [{ required: true, message: '' }],
        attrs: { disabled: true },
      }, {
        key: 'appId',
        label: '所属应用：',
        defaultValue: '',
        rules: [{ required: true, message: '请输入' }],
        attrs: { disabled: true },
      }, {
        key: 'produceQuota',
        label: '申请发送数据速率',
        attrs: {
          disabled: isProduce,
          placeholder: '请输入',
          suffix: 'MB/s',
        },
        rules: [{
          required: !isProduce,
          message: '请输入',
        }],
      }, {
        key: 'consumeQuota',
        label: '申请消费数据速率',
        attrs: {
          disabled: isConsume,
          placeholder: '请输入',
          suffix: 'MB/s',
        },
        rules: [{
          required: !isConsume,
          message: '请输入',
        }],
      }, {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{ required: true, pattern: /^.{4,}.$/, message: quotaRemarks }],
        attrs: {
          placeholder: quotaRemarks,
        },
      }],
    formData: {
      clusterName: item.clusterName,
      topicName: record.topicName || item.topicName,
      appId: record.appId || item.appId,
      produceQuota: transBToMB(record.produceQuota),
      consumeQuota: transBToMB(record.consumeQuota),
    },
    okText: '确认',
    visible: true,
    title: '申请配额',
    onSubmit: (value: any) => {
      const quota = {} as IQuotaModelItem;
      Object.assign(quota, {
        clusterId: record.clusterId || item.clusterId,
        topicName: record.topicName || item.topicName,
        appId: record.appId || item.appId,
        consumeQuota: transMBToB(value.consumeQuota),
        produceQuota: transMBToB(value.produceQuota),
      });
      if (item.isPhysicalClusterId) {
        Object.assign(quota, {
          isPhysicalClusterId: true,
        });
      }
      const quotaParams = {
        type: 2,
        applicant: users.currentUser.username,
        description: value.description,
        extensions: JSON.stringify(quota),
      };
      topic.applyQuota(quotaParams).then((data) => {
        notification.success({ message: '申请配额成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      });
    },
  };
  wrapper.open(xFormModal);
};

let permission: number = null;

const updateFormModal = (appId: string) => {
  const formMap = wrapper.xFormWrapper.formMap;
  const formData = wrapper.xFormWrapper.formData;
  const quota = app.appQuota.filter(ele => ele.appId === appId);
  permission = quota[0].access;
  const isProduce = quota[0].access === 0 || quota[0].access === 1;
  const isConsume = quota[0].access === 0 || quota[0].access === 2;

  formData.produceQuota = transBToMB(quota[0].produceQuota);
  formData.consumeQuota = transBToMB(quota[0].consumerQuota);
  formMap[3].attrs = { disabled: isProduce, suffix: 'MB/s' };
  formMap[3].rules = [{ required: !isProduce, message: '请输入' }];
  formMap[4].attrs = { disabled: isConsume, suffix: 'MB/s' };
  formMap[4].rules = [{ required: !isConsume, message: '请输入' }];
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, formData);
};

export const showTopicApplyQuatoModal = (item: ITopic) => {
  const xFormModal = {
    formMap: [
      // {
      //   key: 'clusterName',
      //   label: '逻辑集群名称',
      //   rules: [{ required: true, message: '' }],
      //   attrs: { disabled: true },
      //   defaultValue: item.clusterName,
      //   // invisible: !item.hasOwnProperty('clusterName'),
      // }, 
      {
        key: 'topicName',
        label: 'Topic名称',
        rules: [{ required: true, message: '' }],
        attrs: { disabled: true },
      }, {
        key: 'appId',
        label: '所属应用：',
        defaultValue: '',
        rules: [{
          required: true,
          validator: (rule: any, value: string, callback: any) => {
            if (!value) {
              callback('请选择应用');
              return false;
            }
            if (permission === 0) {
              callback('该应用无当前topic权限！请选择其他应用，或申请权限。');
              return false;
            }
            return true;
          },
        }],
        type: 'select',
        options: app.appQuota,
        attrs: {
          onChange(value: string) {
            updateFormModal(value);
          },
        },
      }, {  // 0 无权限 1可读 2可写 3 可读写 4可读写可管理
        key: 'produceQuota',
        label: '申请发送数据速率',
        attrs: {
          disabled: false,
          placeholder: '请输入',
          suffix: 'MB/s',
        },
        rules: [{
          required: true,
          message: '请输入',
        }],
      }, {
        key: 'consumeQuota',
        label: '申请消费数据速率',
        attrs: {
          disabled: false,
          placeholder: '请输入',
          suffix: 'MB/s',
        },
        rules: [{
          required: true,
          message: '请输入',
        }],
      }, {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{ required: true, pattern: /^.{5,}$/, message: quotaRemarks }],
        attrs: {
          placeholder: quotaRemarks,
        },
      }],
    formData: {
      clusterName: item.clusterName,
      topicName: item.topicName,
    },
    okText: '确认',
    visible: true,
    title: '申请配额',
    onSubmit: (value: any) => {
      const quota = {} as IQuotaModelItem;
      Object.assign(quota, {
        clusterId: item.clusterId,
        topicName: item.topicName,
        appId: value.appId,
        consumeQuota: transMBToB(value.consumeQuota),
        produceQuota: transMBToB(value.produceQuota),
      });
      const quotaParams = {
        type: 2,
        applicant: users.currentUser.username,
        description: value.description,
        extensions: JSON.stringify(quota),
      };
      topic.applyQuota(quotaParams).then((data) => {
        notification.success({ message: '申请配额成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      });
    },
  };
  wrapper.open(xFormModal);
};

export const updateAllTopicFormModal = () => {
  const formMap = wrapper.xFormWrapper.formMap;
  if (topic.authorities) {
    const { consume, send, checkStatus } = judgeAccessStatus(topic.authorities.access);
    formMap[2].defaultValue = checkStatus;
    formMap[2].options = [{
      label: `消费权限${consume ? '（已拥有）' : ''}`,
      value: '1',
      disabled: consume,
    }, {
      label: `发送权限${send ? '（已拥有）' : ''}`,
      value: '2',
      disabled: send,
    }];
    formMap[2].rules = [{
      required: true,
      validator: (rule: any, value: any, callback: any) => getPowerValidator(rule, value, callback, checkStatus, 'allTopic'),
    }];
  }
  // tslint:disable-next-line:no-unused-expression
  wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData, true, ['access', 'description']);
};

const getPowerValidator = (rule: any, value: any, callback: any, checkStatus: any, isAll?: any) => {
  if (
    (!checkStatus.length && !value.length) ||
    (checkStatus.indexOf('1') !== -1 || checkStatus.indexOf('2') !== -1) && value.length === 1
  ) {
    callback('请选择权限！');
    return false;
  }
  if (isAll && checkStatus.length === 2) {
    callback('您已拥有发送，消费权限!');
    return false;
  }
  return true;
};

const getCheckStatus = (checkStatus: string[], accessValue: string) => {
  let access = null as string;
  if (!checkStatus.length) {
    access = accessValue.length === 2 ? '3' : accessValue[0];
  } else if (checkStatus.indexOf('1') !== -1 && checkStatus.length === 1) {
    access = '2';
  } else if (checkStatus.indexOf('2') !== -1 && checkStatus.length === 1) {
    access = '1';
  }
  return access;
};

const judgeAccessStatus = (access: number) => {
  const consume = access === 1 || access === 3 || access === 4;
  const send = access === 2 || access === 3 || access === 4;
  const checkStatus = access === 0 ? [] : (access === 1 || access === 2) ? [access + ''] : ['1', '2'];
  return { consume, send, checkStatus };
};

export const showAllPermissionModal = (item: ITopic) => {
  let appId: string = null;

  if (!app.data || !app.data.length) {
    return notification.info({
      message: (
        <>
          <span>
            您的账号暂无可用应用，请先
            <a href={`${urlPrefix}/topic/app-list?application=1`}>申请应用</a>
          </span>
        </>),
    });
  }
  const index = app.data.findIndex(row => row.appId === item.appId);

  appId = index > -1 ? item.appId : app.data[0].appId;
  topic.getAuthorities(appId, item.clusterId, item.topicName).then((data) => {
    showAllPermission(appId, item, data.access);
  });
};

const showAllPermission = (appId: string, item: ITopic, access: number) => {
  const { consume, send, checkStatus } = judgeAccessStatus(access);
  const xFormModal = {
    formMap: [
      {
        key: 'topicName',
        label: 'Topic名称',
        defaultValue: item.topicName,
        rules: [{ required: true, message: '请输入Topic名称' }],
        attrs: {
          placeholder: '请输入Topic名称',
          disabled: true,
        },
      },
      {
        key: 'appId',
        label: '绑定应用',
        defaultValue: appId,
        rules: [{ required: true, message: '请选择应用' }],
        type: 'custom',
        customFormItem: <TopicAppSelect selectData={app.data} parameter={item} />,
      },
      {
        key: 'access',
        label: '权限',
        type: 'check_box',
        defaultValue: checkStatus,
        options: [{
          label: `消费权限${consume ? '（已拥有）' : ''}`,
          value: '1',
          disabled: consume,
        }, {
          label: `发送权限${send ? '（已拥有）' : ''}`,
          value: '2',
          disabled: send,
        }],
        rules: [{
          required: true,
          validator: (rule: any, value: any, callback: any) => getPowerValidator(rule, value, callback, checkStatus, 'allTopic'),
        }],
      },
      // {
      //   key: 'clusterName',
      //   label: '集群名称',
      //   defaultValue: item.clusterName,
      //   rules: [{ required: true, message: '请输入集群名称' }],
      //   attrs: {
      //     placeholder: '请输入集群名称',
      //     disabled: true,
      //   },
      // },
      // {
      //   key: 'clusterName',
      //   label: '集群名称',
      //   defaultValue: item.clusterName,
      //   rules: [{ required: true, message: '请输入集群名称' }],
      //   attrs: {
      //     placeholder: '请输入集群名称',
      //     disabled: true,
      //   },
      // },
      {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{
          required: true,
          validator: (rule: any, value: string, callback: any) => {
            const regexp = /^.{4,}.$/;
            value = value.trim();
            if (!regexp.test(value)) {
              callback('请输入至少5个字符');
              return false;
            }
            return true;
          },
        }],
        attrs: {
          placeholder: '请输入至少5个字符',
        },
      },
    ],
    formData: {},
    visible: true,
    title: '申请权限',
    okText: '确认',
    onSubmit: (value: ILimitsItem) => {
      const { checkStatus: originStatus } = judgeAccessStatus(topic.authorities.access);
      const access = getCheckStatus(originStatus, value.access);
      const params = {} as ILimitsItem;
      Object.assign(params, { clusterId: item.clusterId, topicName: item.topicName, appId: value.appId, access });
      const accessParams = {
        type: 3,
        applicant: users.currentUser.username,
        description: value.description.trim(),
        extensions: JSON.stringify(params),
      };
      topic.applyQuota(accessParams).then(data => {
        notification.success({ message: '申请权限成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      }).catch((err) => {
        notification.error({ message: '申请权限失败' });
      });
    },
  };
  wrapper.open(xFormModal);
};

export const showPermissionModal = (item: ITopic) => {
  const { consume, send, checkStatus } = judgeAccessStatus(item.access);
  const xFormModal = {
    formMap: [
      {
        key: 'topicName',
        label: 'Topic名称',
        defaultValue: item.topicName,
        rules: [{ required: true, message: '请输入Topic名称' }],
        attrs: {
          placeholder: '请输入Topic名称',
          disabled: true,
        },
      },
      // {
      //   key: 'clusterName',
      //   label: '集群名称',
      //   defaultValue: item.clusterName,
      //   rules: [{ required: true, message: '请输入集群名称' }],
      //   attrs: {
      //     placeholder: '请输入集群名称',
      //     disabled: true,
      //   },
      // },
      {
        key: 'appName',
        label: '绑定应用',
        defaultValue: `${item.appName}（${item.appId}）`,
        rules: [{ required: true, message: '请选择应用' }],
        attrs: {
          disabled: true,
        },
      },
      {
        key: 'access',
        label: '权限',
        type: 'check_box',
        defaultValue: checkStatus,
        options: [{
          label: `消费权限${consume ? '（已拥有）' : ''}`,
          value: '1',
          disabled: consume,
        }, {
          label: `发送权限${send ? '（已拥有）' : ''}`,
          value: '2',
          disabled: send,
        }],
        rules: [{
          required: true,
          validator: (rule: any, value: any, callback: any) => getPowerValidator(rule, value, callback, checkStatus),
        }],
      },
      {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{
          required: true,
          validator: (rule: any, value: string, callback: any) => {
            const regexp = /^.{4,}.$/;
            value = value.trim();
            if (!regexp.test(value)) {
              callback('请输入至少5个字符');
              return false;
            }
            return true;
          },
        }],
        attrs: {
          placeholder: '请输入至少5个字符',
        },
      },
    ],
    formData: {},
    visible: true,
    title: '申请权限',
    okText: '确认',
    onSubmit: (value: ILimitsItem) => {
      const access = getCheckStatus(checkStatus, value.access);
      const params = {} as ILimitsItem;
      Object.assign(params, { clusterId: item.clusterId, topicName: item.topicName, appId: item.appId, access });
      const accessParams = {
        type: 3,
        applicant: users.currentUser.username,
        description: value.description.trim(),
        extensions: JSON.stringify(params),
      };
      topic.applyQuota(accessParams).then(data => {
        notification.success({ message: '申请权限成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      });
    },
  };
  wrapper.open(xFormModal);
};

export const showTopicEditModal = (item: ITopic) => {
  const xFormModal = {
    formMap: [
      {
        key: 'topicName',
        label: 'Topic名称',
        attrs: { disabled: true },
        rules: [{ required: false }],
      }, {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{ required: false }, { pattern: /^.{4,}.$/, message: '请输入至少5个字符' }],
      },
    ],
    formData: {
      topicName: item.topicName,
      description: item.description,
    },
    visible: true,
    title: '编辑',
    onSubmit: (value: any) => {
      const params = {} as IQuotaModelItem;
      Object.assign(params,
        {
          clusterId: item.clusterId,
          topicName: value.topicName,
          description: value.description,
        });

      topic.updateTopic(params).then(() => {
        notification.success({ message: '编辑成功' });
        topic.getTopic();
        topic.getExpired();
      });
    },
  };
  wrapper.open(xFormModal);
};

export const applyExpandModal = (item: ITopic) => {
  const xFormModal = {
    formMap: [
      {
        key: 'topicName',
        label: 'Topic名称',
        attrs: { disabled: true },
        rules: [{ required: true }],
      }, {
        key: 'needIncrPartitionNum',
        label: '申请增加分区数量',
        type: 'input_number',
        rules: [{
          required: true,
          message: '请输入0-1000正整数',
          pattern: /^((?!0)\d{1,3}|1000)$/,
        }],
        attrs: { placeholder: '0-1000正整数' },
        renderExtraElement: () => <div className="form-tip mr--10">分区标准为3MB/s一个，请按需申请</div>,
      }, {
        key: 'description',
        label: '申请原因',
        type: 'text_area',
        rules: [{ required: true, pattern: /^.{5,}.$/, message: expandRemarks }],
        attrs: { placeholder: expandRemarks },
      },
    ],
    formData: {
      topicName: item.topicName,
      description: item.description,
    },
    visible: true,
    title: '申请分区',
    customRenderElement: <div className="expand-text">若Topic已被限流，则申请分区无效，请直接“申请配额”！</div>,
    onSubmit: (value: any) => {
      const isPhysicalClusterId = Url().search.hasOwnProperty('isPhysicalClusterId') && Url().search.isPhysicalClusterId;
      const offlineParams = {
        type: 12,
        applicant: users.currentUser.username,
        description: value.description,
        extensions: JSON.stringify({
          clusterId: item.clusterId,
          topicName: item.topicName,
          isPhysicalClusterId,
          needIncrPartitionNum: value.needIncrPartitionNum,
        }),
      };
      app.applyExpand(offlineParams).then((data: any) => {
        notification.success({ message: '申请分区成功' });
        window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
      }).catch((err) => {
        notification.error({ message: '申请权限失败' });
      });
    },
  };
  wrapper.open(xFormModal);
};
