import api from '@src/api';
import CodeMirrorFormItem from '@src/components/CodeMirrorFormItem';
import customMessage from '@src/components/Message';
import { Button, Divider, Drawer, Form, message, Space, Utils } from 'knowdesign';
import React, { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ConnectCluster, ConnectorPlugin, ConnectorPluginConfig, OperateInfo } from './AddMM2';

const PLACEHOLDER = `配置格式如下

{
  "connectClusterId": 1, // ConnectID
  "connectorName": "", // MM2 名称
  "sourceKafkaClusterId": 1, // SourceKafka集群 ID
  "configs": { // Source 相关配置
    "name": "", // MM2 名称
    "source.cluster.alias": "", // SourceKafka集群 ID
    ...
  },
  "heartbeatConnectorConfigs": {  // Heartbeat 相关配置
    "name": "", // Heartbeat 对应的Connector名称
    "source.cluster.alias": "", // SourceKafka集群 ID
    ...
  },
  "checkpointConnectorConfigs": { // Checkpoint 相关配置
    "name": "", // Checkpoint 对应的Connector名称
    "source.cluster.alias": "", // SourceKafka集群 ID
    ...
  }
}`;

export default forwardRef((props: any, ref) => {
  // const { clusterId } = useParams<{
  //   clusterId: string;
  // }>();
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [type, setType] = useState('create');
  // const [connectClusters, setConnectClusters] = useState<{ label: string; value: number }[]>([]);
  const [defaultConfigs, setDefaultConfigs] = useState<{ [key: string]: any }>({});
  const [submitLoading, setSubmitLoading] = useState(false);

  // const getConnectClusters = () => {
  //   return Utils.request(api.getConnectClusters(clusterId)).then((res: ConnectCluster[]) => {
  //     setConnectClusters(
  //       res.map(({ name, id }) => ({
  //         label: name || '-',
  //         value: id,
  //       }))
  //     );
  //   });
  // };

  const onOpen = (type: 'create' | 'edit', defaultConfigs?: { [key: string]: any }) => {
    if (defaultConfigs) {
      setDefaultConfigs({ ...defaultConfigs });
      form.setFieldsValue({
        configs: JSON.stringify(defaultConfigs, null, 2),
      });
    }
    setType(type);
    setVisible(true);
  };

  const onSubmit = () => {
    setSubmitLoading(true);
    form.validateFields().then(
      (data) => {
        const postData = JSON.parse(data.configs);

        Object.entries(postData.configs).forEach(([key, val]) => {
          if (val === null) {
            delete postData.configs[key];
          }
        });
        Utils.put(api.validateMM2Config, postData).then(
          (res: ConnectorPluginConfig) => {
            if (res) {
              if (res?.errorCount > 0) {
                const errors: OperateInfo['errors'] = {};
                res?.configs
                  ?.filter((config) => config.value.errors.length !== 0)
                  .forEach(({ value }) => {
                    if (value.name.includes('transforms.')) {
                      errors['transforms'] = (errors['transforms'] || []).concat(value.errors);
                    } else {
                      errors[value.name] = value.errors;
                    }
                  });
                form.setFields([
                  {
                    name: 'configs',
                    errors: Object.entries(errors).map(([name, errorArr]) => `${name}: ${errorArr.join('; ')}\n`),
                  },
                ]);
                setSubmitLoading(false);
              } else {
                if (type === 'create') {
                  Utils.post(api.mirrorMakerOperates, postData)
                    .then(() => {
                      customMessage.success('新建成功');
                      onClose();
                      props?.refresh();
                    })
                    .finally(() => setSubmitLoading(false));
                } else {
                  Utils.put(api.updateMM2Config, postData)
                    .then(() => {
                      customMessage.success('编辑成功');
                      props?.refresh();
                      onClose();
                    })
                    .finally(() => setSubmitLoading(false));
                }
              }
            } else {
              setSubmitLoading(false);
              message.error('接口校验出错，请重新提交');
            }
          },
          () => setSubmitLoading(false)
        );
      },
      () => setSubmitLoading(false)
    );
  };

  const onClose = () => {
    setVisible(false);
    form.resetFields();
  };

  // useEffect(() => {
  //   getConnectClusters();
  // }, []);

  useImperativeHandle(ref, () => ({
    onOpen,
    onClose,
  }));

  return (
    <Drawer
      title={`${type === 'create' ? '新增' : '编辑'} MM2`}
      className="operate-connector-drawer-use-json"
      width={800}
      visible={visible}
      onClose={onClose}
      maskClosable={false}
      extra={
        <div className="operate-wrap">
          <Space>
            <Button size="small" onClick={onClose}>
              取消
            </Button>
            <Button size="small" type="primary" onClick={onSubmit} loading={submitLoading}>
              确定
            </Button>
            <Divider type="vertical" />
          </Space>
        </div>
      }
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="configs"
          validateTrigger="onBlur"
          rules={[
            {
              validator(rule, value) {
                if (!value) {
                  return Promise.reject('配置不能为空');
                }
                try {
                  const v = JSON.parse(value);
                  if (typeof v !== 'object') {
                    return Promise.reject('输入内容必须为 JSON');
                  }
                  // let connectClusterId = -1;
                  // ! 校验 connectorName 字段
                  if (!v.connectorName) {
                    return Promise.reject('内容缺少 MM2任务名称 字段或字段内容为空');
                  } else {
                    if (type === 'edit') {
                      if (v.connectorName !== defaultConfigs.connectorName) {
                        return Promise.reject('编辑模式下不允许修改 MM2任务名称 字段');
                      }
                    }
                  }

                  // ! 校验connectClusterId
                  if (!v.connectClusterId) {
                    return Promise.reject('内容缺少 connectClusterId 字段或字段内容为空');
                  }
                  // ! 校验sourceKafkaClusterId
                  if (!v.sourceKafkaClusterId) {
                    return Promise.reject('内容缺少 sourceKafkaClusterId 字段或字段内容为空');
                  }
                  // ! 校验configs
                  if (!v.configs || typeof v.configs !== 'object') {
                    return Promise.reject('内容缺少 configs 字段或字段格式错误');
                  } else {
                    // ! 校验Topic
                    if (!v.configs.topics) {
                      return Promise.reject('configs 字段下缺少 topics 项');
                    }

                    // 校验 connectorName 字段
                    // if (!v.configs.name) {
                    //   return Promise.reject('configs 字段下缺少 name 项');
                    // } else {
                    //   if (type === 'edit' && v.configs.name !== defaultConfigs.name) {
                    //     return Promise.reject('编辑模式下不允许修改 name 字段');
                    //   }
                    // }
                    // if (!v.configs['connector.class']) {
                    //   return Promise.reject('configs 字段下缺少 connector.class 项');
                    // } else if (type === 'edit' && v.configs['connector.class'] !== defaultConfigs['connector.class']) {
                    //   return Promise.reject('编辑模式下不允许修改 connector.class 字段');
                    // }
                  }
                  return Promise.resolve();
                  // if (type === 'create') {
                  //   // 异步校验 connector 名称是否重复 以及 className 是否存在
                  //   return Promise.all([
                  //     Utils.request(api.isConnectorExist(connectClusterId, v.configs.name)),
                  //     Utils.request(api.getConnectorPlugins(connectClusterId)),
                  //   ]).then(
                  //     ([data, plugins]: [any, ConnectorPlugin[]]) => {
                  //       return data?.exist
                  //         ? Promise.reject('name 与已有 Connector 重复')
                  //         : plugins.every((plugin) => plugin.className !== v.configs['connector.class'])
                  //         ? Promise.reject('该 connectCluster 下不存在 connector.class 项配置的插件')
                  //         : Promise.resolve();
                  //     },
                  //     () => {
                  //       return Promise.reject('接口校验出错，请重试');
                  //     }
                  //   );
                  // } else {
                  //   return Promise.resolve();
                  // }
                } catch (e) {
                  return Promise.reject('输入内容必须为 JSON');
                }
              },
            },
          ]}
        >
          {visible && (
            <div>
              <CodeMirrorFormItem
                resize
                defaultInput={form.getFieldValue('configs')}
                placeholder={PLACEHOLDER}
                onBeforeChange={(configs: string) => {
                  form.setFieldsValue({ configs });
                }}
              />
            </div>
          )}
        </Form.Item>
      </Form>
    </Drawer>
  );
});
