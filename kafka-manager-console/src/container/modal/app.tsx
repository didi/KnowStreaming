import { IAppItem, IOrderParams } from 'types/base-type';
import * as React from 'react';
import { wrapper, region } from 'store';
import { users } from 'store/users';
import { app } from 'store/app';
import { StaffSelect } from 'container/staff-select';
import { message, Icon } from 'component/antd';
import { urlPrefix } from 'constants/left-menu';
import { copyString } from 'lib/utils';

export const showEditModal = (record?: IAppItem, from?: string, isDisabled?: boolean) => {
  const xFormModal = {
    formMap: [
      {
        key: 'appId',
        label: '应用ID',
        invisible: !record,
        defaultValue: record && record.appId || '',
        rules: [{
          required: isDisabled ? false : record && record.appId,
          message: '请输入不得超过64个字符',
          pattern: /^.{1,64}$/,
        }],
        attrs: { disabled: true },
      }, {
        key: 'name',
        label: '应用名称',
        defaultValue: record && record.name || '',
        rules: [{
          required: isDisabled ? false : true,
          message: '应用名称只支持中文、字母、数字、下划线、短划线，长度限制在3-64字符',
          pattern: /[\u4e00-\u9fa5_a-zA-Z0-9_-]{3,64}$/,
        }],
        attrs: { disabled: isDisabled },
      }, {
        key: 'password',
        label: '密码',
        invisible: !record,
        defaultValue: record && record.password || '',
        rules: [{
          required: isDisabled ? false : record && record.password,
          message: '请输入不得超过64个字符',
          pattern: /^.{1,64}$/,
        }],
        attrs: {
          disabled: true,
          suffix: (
            <Icon
              onClick={() => copyString(record.password)}
              type="copy"
              className="icon-color"
            />),
        },
      }, {
        key: 'idc',
        label: '数据中心',
        defaultValue: region.regionName,
        rules: [{ required: isDisabled ? false : true, message: '请输入' }],
        attrs: {
          placeholder: '请输入',
          disabled: true,
        },
      }, {
        key: 'principalList',
        label: '负责人',
        type: 'custom',
        customFormItem: <StaffSelect isDisabled={isDisabled} />,
        rules: [{
          required: isDisabled ? false : true,
          message: '请选择负责人（至少两人）',
          validator: (rule: any, value: []) => {
            if (value.length < 2) {
              return false;
            }
            return true;
          },
        }],
      }, {
        key: 'description',
        label: '应用描述',
        type: 'text_area',
        rules: [{ required: isDisabled ? false : true, message: '请输入描述' }],
        attrs: { disabled: isDisabled },
      },
    ],
    formData: record,
    visible: true,
    title: isDisabled ? '详情' : record ? '编辑' : <div><span>应用申请</span><a className='applicationDocument' href="https://github.com/didi/Logi-KafkaManager/blob/master/docs/user_guide/resource_apply.md" target='_blank'>资源申请文档</a></div>,
    // customRenderElement: isDisabled ? '' : record ? '' : <span className="tips">集群资源充足时，预计1分钟自动审批通过</span>,
    isWaitting: true,
    onSubmit: (value: IAppItem) => {
      if (isDisabled) {
        return;
      }
      value.idc = region.currentRegion;
      return operateApp(!!record, value, record, from).then((data: any) => {
        message.success('操作成功');
        if (!record) {
          window.location.href = `${urlPrefix}/user/order-detail/?orderId=${data.id}&region=${region.currentRegion}`;
        }
      });
    },
    onSubmitFaild: (err: any, ref: any, formData: any, formMap: any) => {
      if (err.message == '资源已经存在') {
        const topic = ref.getFieldValue('name');
        ref.setFields({
          name: {
            value: topic,
            errors: [new Error('该应用名称已存在')],
          }
        })
      }
    }
  };
  wrapper.open(xFormModal);
};

const operateApp = (isEdit: boolean, value: IAppItem, record?: IAppItem, from?: string) => {
  const params: IOrderParams = {
    description: value.description,
    type: 1,
    applicant: users.currentUser.username,
  };
  let principals = '';

  if (value.principalList && value.principalList.length) {
    principals = value.principalList.join(',');
  }
  params.extensions = JSON.stringify({ principals, idc: value.idc, name: value.name });
  let modifyParams = {};
  if (isEdit) {
    modifyParams = {
      appId: record.appId,
      description: value.description,
      name: value.name,
      principals,
    };
  }
  return isEdit ? app.modfiyApplication(modifyParams, from) : app.applyApplication(params);
};
