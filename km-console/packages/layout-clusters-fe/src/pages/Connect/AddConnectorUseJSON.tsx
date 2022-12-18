import api from '@src/api';
import CodeMirrorFormItem from '@src/components/CodeMirrorFormItem';
import customMessage from '@src/components/Message';
import { Button, Divider, Drawer, Form, message, Space, Utils } from 'knowdesign';
import React, { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ConnectCluster, ConnectorPlugin, ConnectorPluginConfig, OperateInfo } from './AddConnector';

const PLACEHOLDER = `配置格式如下

{
  "connectClusterName": "",  // Connect Cluster 名称
  "configs": {  // 具体配置项
    "name": "",
    "connector.class": "",
    "tasks.max": 1,
    ...
  }
}`;

export default forwardRef((props: any, ref) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const [type, setType] = useState('create');
  const [connectClusters, setConnectClusters] = useState<{ label: string; value: number }[]>([]);
  const [defaultConfigs, setDefaultConfigs] = useState<{ [key: string]: any }>({});
  const [submitLoading, setSubmitLoading] = useState(false);

  const getConnectClusters = () => {
    return Utils.request(api.getConnectClusters(clusterId)).then((res: ConnectCluster[]) => {
      setConnectClusters(
        res.map(({ name, id }) => ({
          label: name || '-',
          value: id,
        }))
      );
    });
  };

  const onOpen = (type: 'create' | 'edit', connectClusterName?: string, defaultConfigs?: { [key: string]: any }) => {
    if (defaultConfigs) {
      setDefaultConfigs({ ...defaultConfigs, connectClusterName });
      form.setFieldsValue({
        configs: JSON.stringify(
          {
            connectClusterName,
            configs: defaultConfigs,
          },
          null,
          2
        ),
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
        postData.connectorName = postData.configs.name;
        postData.connectClusterId = connectClusters.find((cluster) => cluster.label === postData.connectClusterName).value;
        delete postData.connectClusterName;

        Object.entries(postData.configs).forEach(([key, val]) => {
          if (val === null) {
            delete postData.configs[key];
          }
        });
        Utils.put(api.validateConnectorConfig, postData).then(
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
                  Utils.post(api.connectorsOperates, postData)
                    .then(() => {
                      customMessage.success('新建成功');
                      onClose();
                      props?.refresh();
                    })
                    .finally(() => setSubmitLoading(false));
                } else {
                  Utils.put(api.updateConnectorConfig, postData)
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

  useEffect(() => {
    getConnectClusters();
  }, []);

  useImperativeHandle(ref, () => ({
    onOpen,
    onClose,
  }));

  return (
    <Drawer
      title={`${type === 'create' ? '新建' : '编辑'} Connector`}
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
                  let connectClusterId = -1;
                  // 校验 connectClusterName 字段
                  if (!v.connectClusterName) {
                    return Promise.reject('内容缺少 connectClusterName 字段或字段内容为空');
                  } else {
                    if (type === 'edit') {
                      if (v.connectClusterName !== defaultConfigs.connectClusterName) {
                        return Promise.reject('编辑模式下不允许修改 connectClusterName 字段');
                      }
                    } else {
                      if (!connectClusters.length) {
                        getConnectClusters();
                        return Promise.reject('connectClusterName 列表获取失败，请重试');
                      }
                      const targetConnectCluster = connectClusters.find((cluster) => cluster.label === v.connectClusterName);
                      if (!targetConnectCluster) {
                        return Promise.reject('connectClusterName 不存在，请检查');
                      } else {
                        connectClusterId = targetConnectCluster.value;
                      }
                    }
                  }

                  if (!v.configs || typeof v.configs !== 'object') {
                    return Promise.reject('内容缺少 configs 字段或字段格式错误');
                  } else {
                    // 校验 connectorName 字段
                    if (!v.configs.name) {
                      return Promise.reject('configs 字段下缺少 name 项');
                    } else {
                      if (type === 'edit' && v.configs.name !== defaultConfigs.name) {
                        return Promise.reject('编辑模式下不允许修改 name 字段');
                      }
                    }
                    if (!v.configs['connector.class']) {
                      return Promise.reject('configs 字段下缺少 connector.class 项');
                    } else if (type === 'edit' && v.configs['connector.class'] !== defaultConfigs['connector.class']) {
                      return Promise.reject('编辑模式下不允许修改 connector.class 字段');
                    }
                  }

                  if (type === 'create') {
                    // 异步校验 connector 名称是否重复 以及 className 是否存在
                    return Promise.all([
                      Utils.request(api.isConnectorExist(connectClusterId, v.configs.name)),
                      Utils.request(api.getConnectorPlugins(connectClusterId)),
                    ]).then(
                      ([data, plugins]: [any, ConnectorPlugin[]]) => {
                        return data?.exist
                          ? Promise.reject('name 与已有 Connector 重复')
                          : plugins.every((plugin) => plugin.className !== v.configs['connector.class'])
                          ? Promise.reject('该 connectCluster 下不存在 connector.class 项配置的插件')
                          : Promise.resolve();
                      },
                      () => {
                        return Promise.reject('接口校验出错，请重试');
                      }
                    );
                  } else {
                    return Promise.resolve();
                  }
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
