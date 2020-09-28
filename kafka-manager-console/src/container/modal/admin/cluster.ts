import { notification } from 'component/antd';
import { wrapper } from 'store';
import { IClusterTopics, IEditTopic, IConfigInfo, ILogicalCluster, INewLogical, IMetaData, IBrokersRegions, INewRegions } from 'types/base-type';
import { editTopic } from 'lib/api';
import { transMSecondToHour, transHourToMSecond } from 'lib/utils';
import { cluster } from 'store/cluster';
import { admin } from 'store/admin';
import { app } from 'store/app';

export const showEditClusterTopic = (item: IClusterTopics) => {
  const xFormModal = {
    formMap: [
      {
        key: 'clusterName',
        label: '集群名称',
        rules: [{
          required: true,
        }],
        attrs: {
          disabled: true,
        },
      },
      {
        key: 'appId',
        label: '应用ID',
        rules: [{
          required: true,
        }],
        attrs: {
          disabled: true,
        },
      },
      {
        key: 'topicName',
        label: 'Topic名称',
        rules: [{
          required: true,
        }],
        attrs: {
          disabled: true,
        },
      },
      {
        key: 'retentionTime',
        label: '保存时间',
        rules: [{
          required: true,
          message: '请输入保存时间',
        }],
        attrs: {
          placeholder: '请输入保存时间',
          suffix: '小时',
        },
      },
      {
        key: 'properties',
        label: 'Topic属性列表',
        type: 'text_area',
        rules: [{ required: false}],
        attrs: {
          placeholder: '请输入Topic属性列表',
        },
      },
      {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{
          required: false,
        }],
        attrs: {
          placeholder: '请输入备注',
        },
      },
    ],
    formData: {
      clusterName: item.clusterName,
      appId: item.appId,
      topicName: item.topicName,
      retentionTime:  transMSecondToHour(item.retentionTime),
      properties: JSON.stringify(item.properties, null, 4),
      description: item.description,
    },
    visible: true,
    title: 'Topic编辑',
    onSubmit: (value: IEditTopic) => {
      value.clusterId = item.clusterId;
      value.properties = value.properties ? JSON.parse(value.properties) : {};
      value.retentionTime = transHourToMSecond(value.retentionTime);
      editTopic(value).then(data => {
        notification.success({ message: '编辑Topic成功' });
      });
    },
  };
  wrapper.open(xFormModal);
};

export const showLogicalClusterOpModal = (clusterId: number, record?: ILogicalCluster) => {
  let clusterModes = [] as  IConfigInfo[];
  clusterModes = cluster.clusterModes ?  cluster.clusterModes : clusterModes;
  const xFormModal = {
    formMap: [
      {
        key: 'logicalClusterName',
        label: '逻辑集群名称',
        rules: [{ required: true, message: '请输入逻辑集群名称' }],
        attrs: {
          disabled: record ? true : false,
        },
      },
      {
        key: 'appId',
        label: '所属应用',
        rules: [{ required: true, message: '请选择所属应用' }],
        type: 'select',
        options: app.adminAppData.map(item => {
          return {
            label: item.name,
            value: item.appId,
          };
        }),
        attrs: {
          placeholder: '请选择所属应用',
        },
      },
      {
        key: 'mode',
        label: '集群模式',
        type: 'select',
        rules: [{ required: true, message: '请选择集群模式' }],
        options: clusterModes.map(item => {
          return {
            label: item.message,
            value: item.code,
          };
        }),
        attrs: {
        },
      },
      {
        key: 'regionIdList',
        label: 'RegionIdList',
        type: 'select',
        defaultValue: [] as any,
        options: admin.brokersRegions.map(item => {
          return {
            label: item.name,
            value: item.id,
          };
        }),
        rules: [{ required: true, message: '请选择BrokerIdList' }],
        attrs: {
          mode: 'multiple',
          placeholder: '请选择BrokerIdList',
        },
      },
      {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{
          required: false,
        }],
        attrs: {
          placeholder: '请输入备注',
        },
      },
    ],
    formData: record,
    visible: true,
    title: '新增逻辑集群',
    onSubmit: (value: INewLogical) => {
      const params = {
        appId: value.appId,
        clusterId,
        description: value.description,
        id: record ? record.logicalClusterId : '',
        mode: value.mode,
        name: value.logicalClusterName,
        regionIdList: value.regionIdList,
      } as INewLogical;
      if (record) {
        return admin.editLogicalClusters(clusterId, params).then(data => {
          notification.success({ message: '编辑逻辑集群成功' });
        });
      }
      return admin.createLogicalClusters(clusterId, params).then(data => {
        notification.success({ message: '新建逻辑集群成功' });
      });
    },
  };
  wrapper.open(xFormModal);
};

export const showClusterRegionOpModal = (clusterId: number, content: IMetaData, record?: IBrokersRegions) => {
 const xFormModal = {
    formMap: [
      {
        key: 'name',
        label: 'Region名称',
        rules: [{ required: true, message: '请输入Region名称' }],
        attrs: { placeholder: '请输入Region名称' },
      },
      {
        key: 'clusterName',
        label: '集群名称',
        rules: [{ required: true, message: '请输入集群名称' }],
        defaultValue: content.clusterName,
        attrs: {
          disabled: true,
          placeholder: '请输入集群名称',
        },
      },
      {
        key: 'brokerIdList',
        label: 'Broker列表',
        defaultValue: record ? record.brokerIdList.join(',') : [] as any,
        rules: [{ required: true, message: '请输入BrokerIdList' }],
        attrs: {
          placeholder: '请输入BrokerIdList',
        },
      },
      {
        key: 'status',
        label: '状态',
        type: 'select',
        options: [
          {
            label: '正常',
            value: 0,
          },
          {
            label: '容量已满',
            value: 1,
          },
        ],
        defaultValue: 0,
        rules: [{ required: true, message: '请选择状态' }],
        attrs: {
          placeholder: '请选择状态',
        },
      },
      {
        key: 'description',
        label: '备注',
        type: 'text_area',
        rules: [{
          required: false,
        }],
        attrs: {
          placeholder: '请输入备注',
        },
      },
    ],
    formData: record,
    visible: true,
    title: `${record ? '编辑' : '新增Region'}`,
    onSubmit: (value: INewRegions) => {
      value.clusterId = clusterId;
      value.brokerIdList = value.brokerIdList && Array.isArray(value.brokerIdList) ?
        value.brokerIdList : value.brokerIdList.split(',');
      if (record) {
        value.id = record.id;
      }
      delete value.clusterName;
      if (record) {
        return admin.editRegions(clusterId, value).then(data => {
          notification.success({ message: '编辑Region成功' });
        });
      }
      return admin.addNewRegions(clusterId, value).then(data => {
        notification.success({ message: '新建Region成功' });
      });
    },
  };
 wrapper.open(xFormModal);
};
