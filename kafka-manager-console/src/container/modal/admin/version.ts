import * as React from 'react';
import { notification, Select } from 'component/antd';
import { IUploadFile, IConfigure, IConfigGateway } from 'types/base-type';
import { version } from 'store/version';
import { admin } from 'store/admin';
import { wrapper } from 'store';
import { computeChecksumMd5 } from 'lib/utils';
import format2json from 'format-to-json';

interface ISearchAndFilterState {
  [filter: string]: boolean | string | number | any[];
}
const handleSelectChange = (e: number) => {
  version.setAcceptFileType(e);
  updateFormModal(e);
};

export const showUploadModal = () => {
  const xFormModal = {
    formMap: [
      {
        key: 'fileType',
        label: '文件类型',
        type: 'select',
        options: version.fileTypeList,
        attrs: {
          onChange: (e: number) => handleSelectChange(e),
        },
        rules: [{ required: true, message: '请选择文件类型' }],
      }, {
        key: 'clusterId',
        label: '集群',
        type: 'select',
        invisible: true,
        options: admin.metaList.map(item => ({
          ...item,
          label: item.clusterName,
          value: item.clusterId,
        })),
        rules: [{ required: false, message: '请选择集群' }],
      }, {
        key: 'uploadFile',
        label: '上传文件',
        type: 'upload',
        attrs: {
          accept: version.fileSuffix,
        },
        rules: [{
          required: true,
          validator: (rule: any, value: any, callback: any) => {
            if (value.length) {
              if (value.length > 1) {
                callback('一次仅支持上传一份文件！');
                return false;
              }
              return true;
            } else {
              callback(`请上传文件`);
              return false;
            }
          },
        }],
      }, {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{ required: false, message: '请输入备注' }],
      },
    ],
    formData: {},
    visible: true,
    title: '上传',
    onText: '保存',
    isWaitting: true,
    onSubmit: (value: IUploadFile) => {
      value.file = value.uploadFile[0].originFileObj;
      return computeChecksumMd5(value.file).then(md5 => {
        const params = {
          fileName: value.file.name,
          fileMd5: md5,
          clusterId: value.clusterId || -1,
          ...value,
        };
        return version.addFile(params);
      });
    },
  };
  wrapper.open(xFormModal);
};

const updateFormModal = (type: number) => {
  const formMap = wrapper.xFormWrapper.formMap;

  if (formMap && formMap.length > 2) {
    formMap[1].invisible = !version.currentFileType;
    formMap[1].rules = [{ required: version.currentFileType, message: '请上传文件' }];
    formMap[2].attrs = {
      accept: version.fileSuffix,
    },
      // tslint:disable-next-line:no-unused-expression
      wrapper.ref && wrapper.ref.updateFormMap$(formMap, wrapper.xFormWrapper.formData, true);
  }
};

export const showModifyModal = (record: IUploadFile) => {
  version.setAcceptFileType(record.fileType);
  const xFormModal = {
    formMap: [
      {
        key: 'uploadFile',
        label: '上传文件',
        type: 'upload',
        attrs: {
          accept: version.fileSuffix,
        },
        rules: [{
          required: true,
          validator: (rule: any, value: any, callback: any) => {
            if (value.length) {
              if (value.length > 1) {
                callback('一次仅支持上传一份文件！');
                return false;
              }
              return true;
            } else {
              callback(`请上传文件`);
              return false;
            }
          },
        }],
      }, {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{ required: false, message: '请输入备注' }],
      },
    ],
    formData: record || {},
    visible: true,
    isWaitting: true,
    title: '编辑',
    onSubmit: async (value: IUploadFile) => {
      value.file = value.uploadFile[0].originFileObj;
      const md5 = await computeChecksumMd5(value.file);
      const params = {
        fileName: value.file.name,
        fileMd5: md5 as string,
        description: value.description,
        file: value.file,
        id: record.id,
      };
      return version.modfiyFile(params);
    },
  };
  wrapper.open(xFormModal);
};

export const showConfigureModal = async (record?: IConfigure) => {
  if (record) {
    const result: any = await format2json(record.configValue);
    record.configValue = result.result || record.configValue;
  }
  const xFormModal = {
    formMap: [
      {
        key: 'configKey',
        label: '配置键',
        rules: [{ required: true, message: '请输入配置键' }],
        attrs: {
          disabled: record ? true : false,
        },
      }, {
        key: 'configValue',
        label: '配置值',
        type: 'monaco_editor',
        rules: [{
          required: true,
          message: '请输入配置值',
        }],
      }, {
        key: 'configDescription',
        label: '备注',
        type: 'text_area',
        rules: [{ required: true, message: '请输入备注' }],
      },
    ],
    formData: record || {},
    visible: true,
    isWaitting: true,
    title: `${record ? '编辑配置' : '新建配置'}`,
    onSubmit: async (value: IConfigure) => {
      if (record) {
        return admin.editConfigure(value).then(data => {
          notification.success({ message: '编辑配置成功' });
        });
      } else {
        return admin.addNewConfigure(value).then(data => {
          notification.success({ message: '新建配置成功' });
        });
      }
    },
  };
  wrapper.open(xFormModal);
};

export const showConfigGatewayModal = async (record?: IConfigGateway) => {
  const xFormModal = {
    formMap: [
      {
        key: 'type',
        label: '配置类型',
        rules: [{ required: true, message: '请选择配置类型' }],
        type: "select",
        options: admin.gatewayType.map((item: any, index: number) => ({
          key: index,
          label: item.configName,
          value: item.configType,
        })),
        attrs: {
          disabled: record ? true : false,
        }
      }, {
        key: 'name',
        label: '配置键',
        rules: [{ required: true, message: '请输入配置键' }],
        attrs: {
          disabled: record ? true : false,
        },
      }, {
        key: 'value',
        label: '配置值',
        type: 'text_area',
        rules: [{
          required: true,
          message: '请输入配置值',
        }],
      }, {
        key: 'description',
        label: '描述',
        type: 'text_area',
        rules: [{ required: true, message: '请输入备注' }],
      },
    ],
    formData: record || {},
    visible: true,
    isWaitting: true,
    title: `${record ? '编辑配置' : '新建配置'}`,
    onSubmit: async (parmas: IConfigGateway) => {
      if (record) {
        parmas.id = record.id;
        return admin.editConfigGateway(parmas).then(data => {
          notification.success({ message: '编辑配置成功' });
        });
      } else {
        return admin.addNewConfigGateway(parmas).then(data => {
          notification.success({ message: '新建配置成功' });
        });
      }
    },
  };
  wrapper.open(xFormModal);
};
