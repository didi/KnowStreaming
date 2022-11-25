import { Button, Divider, Drawer, Form, Input, InputNumber, Radio, Select, Spin, Space, Utils } from 'knowdesign';
import message from '@src/components/Message';
import * as React from 'react';
import { useIntl } from 'react-intl';
import api from '@src/api';
import { regClusterName, regUsername } from '@src/constants/reg';
import { bootstrapServersErrCodes, jmxErrCodes, zkErrCodes } from './config';
import CodeMirrorFormItem from '@src/components/CodeMirrorFormItem';

const LOW_KAFKA_VERSION = '2.8.0';
const CLIENT_PROPERTIES_PLACEHOLDER = `用于创建Kafka客户端进行信息获取的相关配置，
例如开启SCRAM-SHA-256安全管控模式的集群需输入如下配置，
未开启安全管控可不进行任何输入：
{
  "security.protocol": "SASL_PLAINTEXT",
  "sasl.mechanism": "SCRAM-SHA-256",
  "sasl.jaas.config": "org.apache.kafka.common.security.
scram.ScramLoginModule required username=\\"xxxxxx\\" pass
word=\\"xxxxxx\\";"
}
`;

const AccessClusters = (props: any): JSX.Element => {
  const { afterSubmitSuccess, clusterInfo, visible } = props;

  const intl = useIntl();
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [confirmLoading, setConfirmLoading] = React.useState(false);
  const [curClusterInfo, setCurClusterInfo] = React.useState<any>({});
  const [extra, setExtra] = React.useState({
    versionExtra: '',
    zooKeeperExtra: '',
    bootstrapExtra: '',
    jmxExtra: '',
  });

  const lastFormItemValue = React.useRef({
    bootstrapServers: curClusterInfo?.bootstrapServers || '',
    zookeeper: curClusterInfo?.zookeeper || '',
    clientProperties: curClusterInfo?.clientProperties || {},
  });

  const onHandleValuesChange = (changedValue: string[]) => {
    Object.keys(changedValue).forEach((key) => {
      switch (key) {
        case 'zookeeper':
        case 'bootstrapServers':
          setExtra({
            ...extra,
            zooKeeperExtra: '',
            bootstrapExtra: '',
            jmxExtra: '',
          });
          break;
        case 'kafkaVersion':
          setExtra({
            ...extra,
            versionExtra: '',
          });
          break;
      }
    });
  };

  const onCancel = () => {
    form.resetFields();
    setLoading(false);
    setExtra({
      versionExtra: '',
      zooKeeperExtra: '',
      bootstrapExtra: '',
      jmxExtra: '',
    });
    lastFormItemValue.current = { bootstrapServers: '', zookeeper: '', clientProperties: {} };
    props.setVisible && props.setVisible(false);
  };

  const onSubmit = () => {
    form.validateFields().then((res) => {
      setConfirmLoading(true);
      let clientProperties = null;
      try {
        clientProperties = res.clientProperties && JSON.parse(res.clientProperties);
      } catch (err) {
        console.error(err);
      }

      const params = {
        bootstrapServers: res.bootstrapServers,
        clientProperties: clientProperties || {},
        description: res.description || '',
        jmxProperties: {
          jmxPort: res.jmxPort,
          maxConn: res.maxConn,
          openSSL: res.openSSL || false,
          token: res.token,
          username: res.username,
        },
        kafkaVersion: res.kafkaVersion,
        name: res.name,
        zookeeper: res.zookeeper || '',
      };

      if (!isNaN(curClusterInfo?.id)) {
        Utils.put(api.phyCluster, {
          ...params,
          id: curClusterInfo?.id,
        })
          .then(() => {
            message.success('编辑成功');
            afterSubmitSuccess && afterSubmitSuccess();
            onCancel();
          })
          .finally(() => {
            setConfirmLoading(false);
          });
      } else {
        Utils.post(api.phyCluster, params)
          .then(() => {
            message.success('集群接入成功。注意：新接入集群数据稳定需要1-2分钟');
            afterSubmitSuccess && afterSubmitSuccess();
            onCancel();
          })
          .finally(() => {
            setConfirmLoading(false);
          });
      }
    });
  };

  const connectTest = () => {
    const bootstrapServers = form.getFieldValue('bootstrapServers');
    const zookeeper = form.getFieldValue('zookeeper');
    let clientProperties = {};
    try {
      clientProperties = form.getFieldValue('clientProperties') && JSON.parse(form.getFieldValue('clientProperties'));
    } catch (err) {
      console.error(`JSON.parse(form.getFieldValue('clientProperties')) ERROR: ${err}`);
    }

    setLoading(true);

    return Utils.post(api.kafkaValidator, {
      bootstrapServers: bootstrapServers || '',
      zookeeper: zookeeper || '',
      clientProperties: clientProperties || {},
    })
      .then(
        (res: {
          errList: { code: number; message: string; data: any }[];
          jmxPort: number | null;
          kafkaVersion: string | null;
          zookeeper: string | null;
        }) => {
          const changedValue: { jmxPort?: number; kafkaVersion?: string; zookeeper: string } = {
            zookeeper: zookeeper || res.zookeeper,
          };
          if (res.kafkaVersion && props.kafkaVersion.includes(res.kafkaVersion)) {
            changedValue.kafkaVersion = res.kafkaVersion;
          }
          if (res.jmxPort) {
            changedValue.jmxPort = res.jmxPort;
          }
          form.setFieldsValue(changedValue);

          const extraMsg = {
            ...extra,
            // 重置默认信息为连接成功
            bootstrapExtra: bootstrapServers ? '连接成功' : '',
            zooKeeperExtra: zookeeper ? '连接成功' : '',
          };

          const errList = res.errList || [];
          // 处理错误信息
          errList.forEach((item: any) => {
            const { code, message } = item;
            let modifyKey: 'bootstrapExtra' | 'zooKeeperExtra' | 'jmxExtra' | undefined;
            if (bootstrapServersErrCodes.includes(code)) {
              modifyKey = 'bootstrapExtra';
            } else if (zkErrCodes.includes(code)) {
              modifyKey = 'zooKeeperExtra';
            } else if (jmxErrCodes.includes(code)) {
              modifyKey = 'jmxExtra';
            }

            if (modifyKey) {
              extraMsg[modifyKey] = message;
            }
          });

          setExtra(extraMsg);
          return res;
        }
      )
      .finally(() => {
        setLoading(false);
      });
  };

  // 更新表单状态
  React.useEffect(() => {
    lastFormItemValue.current = {
      bootstrapServers: curClusterInfo?.bootstrapServers || '',
      zookeeper: curClusterInfo?.zookeeper || '',
      clientProperties: curClusterInfo?.clientProperties || {},
    };
    form.setFieldsValue({ ...curClusterInfo });
    if (curClusterInfo?.kafkaVersion) {
      form.validateFields(['kafkaVersion']);
    }
  }, [curClusterInfo]);

  // 获取集群详情数据
  React.useEffect(() => {
    if (visible) {
      if (clusterInfo?.id) {
        setLoading(true);

        const resolveJmxProperties = (obj: any) => {
          const res = { ...obj };
          try {
            const originValue = obj?.jmxProperties;
            if (originValue) {
              const jmxProperties = JSON.parse(originValue);
              typeof jmxProperties === 'object' && jmxProperties !== null && Object.assign(res, jmxProperties);
            }
          } catch (err) {
            console.error('jmxProperties not JSON: ', err);
          }
          return res;
        };

        Utils.request(api.getPhyClusterBasic(clusterInfo.id))
          .then((res: any) => {
            setCurClusterInfo(resolveJmxProperties(res));
          })
          .catch((err) => {
            setCurClusterInfo(resolveJmxProperties(clusterInfo));
          })
          .finally(() => {
            setLoading(false);
          });
      } else {
        setCurClusterInfo({});
      }
    }
  }, [visible, clusterInfo]);

  const validators = {
    name: async (_: any, value: string) => {
      if (!value) {
        return Promise.reject('集群名称不能为空');
      }
      if (value === curClusterInfo?.name) {
        return Promise.resolve();
      }
      if (value?.length > 128) {
        return Promise.reject('集群名称长度限制在1～128字符');
      }
      if (!new RegExp(regClusterName).test(value)) {
        return Promise.reject('集群名称支持中英文、数字、特殊字符 ! " # $ % & \' ( ) * + , - . / : ; < = > ? @ [  ] ^ _ ` { | } ~');
      }
      return Utils.request(api.getClusterBasicExit(value))
        .then((res: any) => {
          const data = res || {};
          return data?.exist ? Promise.reject('集群名称重复') : Promise.resolve();
        })
        .catch(() => Promise.reject('连接超时! 请重试或检查服务'));
    },
    bootstrapServers: async (_: any, value: string) => {
      if (!value) {
        return Promise.reject('Bootstrap Servers不能为空');
      }
      if (value.length > 2000) {
        return Promise.reject('Bootstrap Servers长度限制在2000字符');
      }
      if (value && value !== lastFormItemValue.current.bootstrapServers) {
        lastFormItemValue.current.bootstrapServers = value;
        return connectTest().catch(() => (lastFormItemValue.current.bootstrapServers = ''));
      }
      return Promise.resolve('');
    },
    zookeeper: async (_: any, value: string) => {
      if (!value) {
        return Promise.resolve('');
      }

      if (value.length > 2000) {
        return Promise.reject('Zookeeper长度限制在2000字符');
      }

      if (value && value !== lastFormItemValue.current.zookeeper) {
        lastFormItemValue.current.zookeeper = value;
        return connectTest().catch(() => (lastFormItemValue.current.zookeeper = ''));
      }
      return Promise.resolve('');
    },
    securityUserName: async (_: any, value: string) => {
      if (!value) {
        return Promise.reject('用户名不能为空');
      }
      if (!new RegExp(regUsername).test(value)) {
        return Promise.reject('仅支持大小写、下划线、短划线（-）');
      }
      if (value.length > 128) {
        return Promise.reject('用户名长度限制在1～128字符');
      }
      return Promise.resolve();
    },
    securityToken: async (_: any, value: string) => {
      if (!value) {
        return Promise.reject('密码不能为空');
      }
      if (!new RegExp(regUsername).test(value)) {
        return Promise.reject('密码只能由大小写、下划线、短划线（-）组成');
      }
      if (value.length < 6 || value.length > 32) {
        return Promise.reject('密码长度限制在6～32字符');
      }
      return Promise.resolve();
    },
    kafkaVersion: async (_: any, value: any) => {
      if (!value) {
        return Promise.reject('版本号不能为空');
      }
      // 检测版本号小于2.8.0，如果没有填zookeeper信息，才会提示
      const zookeeper = form.getFieldValue('zookeeper');
      let versionExtra = '';
      if (value < LOW_KAFKA_VERSION && !zookeeper) {
        versionExtra = intl.formatMessage({ id: 'access.cluster.low.version.tip' });
      }
      setExtra({
        ...extra,
        versionExtra,
      });
      return Promise.resolve();
    },
    clientProperties: async (_: any, value: string) => {
      try {
        if (value) {
          JSON.parse(value);
        }

        return Promise.resolve();
      } catch (e) {
        return Promise.reject(new Error('输入内容必须为 JSON'));
      }
    },
    description: async (_: any, value: string) => {
      if (!value) {
        return Promise.resolve('');
      }
      if (value && value.length > 200) {
        return Promise.reject('集群描述长度限制在200字符');
      }
      return Promise.resolve();
    },
  };

  return (
    <>
      <Drawer
        className="drawer-content drawer-access-cluster"
        onClose={onCancel}
        maskClosable={false}
        extra={
          <div className="operate-wrap">
            <Space>
              <Button size="small" onClick={onCancel}>
                取消
              </Button>
              <Button size="small" type="primary" loading={confirmLoading} onClick={onSubmit}>
                确定
              </Button>
              <Divider type="vertical" />
            </Space>
          </div>
        }
        title={intl.formatMessage({ id: props.title || clusterInfo?.id ? 'edit.cluster' : 'access.cluster' })}
        visible={props.visible}
        placement="right"
        width={480}
      >
        <Spin spinning={loading}>
          <Form form={form} layout="vertical" onValuesChange={onHandleValuesChange}>
            <Form.Item
              name="name"
              label="集群名称"
              validateTrigger="onBlur"
              rules={[
                {
                  required: true,
                  validator: validators.name,
                },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="bootstrapServers"
              label="Bootstrap Servers"
              extra={<span className={!extra.bootstrapExtra.includes('连接成功') ? 'error-extra-info' : ''}>{extra.bootstrapExtra}</span>}
              validateTrigger={'onBlur'}
              rules={[
                {
                  required: true,
                  validator: validators.bootstrapServers,
                },
              ]}
            >
              <Input.TextArea
                rows={3}
                placeholder="请输入Bootstrap Servers地址，例如：192.168.1.1:9092,192.168.1.2:9092,192.168.1.3:9092"
              />
            </Form.Item>
            <Form.Item
              name="zookeeper"
              label="Zookeeper"
              extra={<span className={!extra.zooKeeperExtra.includes('连接成功') ? 'error-extra-info' : ''}>{extra.zooKeeperExtra}</span>}
              validateTrigger={'onBlur'}
              rules={[
                {
                  validator: validators.zookeeper,
                },
              ]}
            >
              <Input.TextArea
                rows={3}
                placeholder="请输入Zookeeper地址，例如：192.168.0.1:2181,192.168.0.2:2181,192.168.0.2:2181/ks-kafka"
              />
            </Form.Item>
            <Form.Item className="metrics-form-item" label="Metrics">
              <div className="horizontal-form-container">
                <div className="inline-items">
                  <Form.Item name="jmxPort" label="JMX Port :" extra={extra.jmxExtra}>
                    <InputNumber min={0} max={99999} style={{ width: 129 }} />
                  </Form.Item>
                  <Form.Item name="maxConn" label="Max Conn :">
                    <InputNumber addonAfter="个" min={0} max={99999} style={{ width: 124 }} />
                  </Form.Item>
                </div>
                <Form.Item name="openSSL" label="Security :">
                  <Radio.Group>
                    <Radio value={false}>None</Radio>
                    <Radio value={true}>Password Authentication</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item dependencies={['openSSL']} noStyle>
                  {({ getFieldValue }) => {
                    return getFieldValue('openSSL') ? (
                      <div className="user-info-form-items">
                        <Form.Item className="user-info-label" label="User Info :" required />
                        <div className="inline-items">
                          <Form.Item
                            name="username"
                            rules={[
                              {
                                validator: validators.securityUserName,
                              },
                            ]}
                          >
                            <Input placeholder="请输入用户名" />
                          </Form.Item>
                          <Form.Item
                            className="token-form-item"
                            name="token"
                            rules={[
                              {
                                validator: validators.securityToken,
                              },
                            ]}
                          >
                            <Input placeholder="请输入密码" />
                          </Form.Item>
                        </div>
                      </div>
                    ) : null;
                  }}
                </Form.Item>
              </div>
            </Form.Item>
            <Form.Item
              name="kafkaVersion"
              label="Version"
              dependencies={['zookeeper']}
              extra={<span className="error-extra-info">{extra.versionExtra}</span>}
              rules={[
                {
                  required: true,
                  validator: validators.kafkaVersion,
                },
              ]}
            >
              <Select placeholder="请选择Kafka Version，如无匹配则选择相近版本">
                {(props.kafkaVersion || []).map((item: string) => (
                  <Select.Option key={item} value={item}>
                    {item}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              name="clientProperties"
              label="集群配置"
              rules={[
                {
                  validator: validators.clientProperties,
                },
              ]}
            >
              <div>
                <CodeMirrorFormItem
                  resize
                  defaultInput={form.getFieldValue('clientProperties')}
                  placeholder={CLIENT_PROPERTIES_PLACEHOLDER}
                  onBeforeChange={(clientProperties: string) => {
                    form.setFieldsValue({ clientProperties });
                    form.validateFields(['clientProperties']);
                  }}
                  onBlur={(value: any) => {
                    form.validateFields(['clientProperties']).then(() => {
                      const bootstrapServers = form.getFieldValue('bootstrapServers');
                      const zookeeper = form.getFieldValue('zookeeper');
                      const clientProperties = form.getFieldValue('clientProperties');

                      if (
                        clientProperties &&
                        clientProperties !== lastFormItemValue.current.clientProperties &&
                        (!!bootstrapServers || !!zookeeper)
                      ) {
                        connectTest()
                          .then((res: any) => {
                            lastFormItemValue.current.clientProperties = clientProperties;
                          })
                          .catch((err) => {
                            message.error('连接失败');
                          });
                      }
                    });
                  }}
                />
              </div>
            </Form.Item>
            <Form.Item
              name="description"
              label="集群描述"
              rules={[
                {
                  validator: validators.description,
                },
              ]}
            >
              <Input.TextArea rows={4} />
            </Form.Item>
          </Form>
        </Spin>
      </Drawer>
    </>
  );
};

export default AccessClusters;
