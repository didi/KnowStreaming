import { Button, Divider, Drawer, Form, Input, InputNumber, message, Radio, Select, Spin, Space, Utils } from 'knowdesign';
import * as React from 'react';
import { useIntl } from 'react-intl';
import api from '@src/api';
import { regClusterName, regUsername } from '@src/constants/reg';
import { bootstrapServersErrCodes, jmxErrCodes, zkErrCodes } from './config';
import CodeMirrorFormItem from '@src/components/CodeMirrorFormItem';

const rows = 4;
const lowKafkaVersion = '2.8.0';
const clientPropertiesPlaceholder = `用于创建Kafka客户端进行信息获取的相关配置，
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
  const [curClusterInfo, setCurClusterInfo] = React.useState<any>({});
  const [security, setSecurity] = React.useState(curClusterInfo?.security || 'None');
  const [extra, setExtra] = React.useState({
    versionExtra: '',
    zooKeeperExtra: '',
    bootstrapExtra: '',
    jmxExtra: '',
  });
  const [isLowVersion, setIsLowVersion] = React.useState<boolean>(false);
  const [zookeeperErrorStatus, setZookeeperErrorStatus] = React.useState<boolean>(false);

  const lastFormItemValue = React.useRef({
    bootstrap: curClusterInfo?.bootstrapServers || '',
    zookeeper: curClusterInfo?.zookeeper || '',
    clientProperties: curClusterInfo?.clientProperties || {},
  });

  const onHandleValuesChange = (value: any, allValues: any) => {
    Object.keys(value).forEach((key) => {
      switch (key) {
        case 'security':
          setSecurity(value.security);
          break;
        case 'zookeeper':
          setExtra({
            ...extra,
            zooKeeperExtra: '',
            bootstrapExtra: '',
            jmxExtra: '',
          });
          break;
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
    setZookeeperErrorStatus(false);
    setIsLowVersion(false);
    setSecurity('None');
    setExtra({
      versionExtra: '',
      zooKeeperExtra: '',
      bootstrapExtra: '',
      jmxExtra: '',
    });
    lastFormItemValue.current = { bootstrap: '', zookeeper: '', clientProperties: {} };
    props.setVisible && props.setVisible(false);
  };

  const onSubmit = () => {
    form.validateFields().then((res) => {
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
          openSSL: res.security === 'Password',
          token: res.token,
          username: res.username,
        },
        kafkaVersion: res.kafkaVersion,
        name: res.name,
        zookeeper: res.zookeeper || '',
      };
      setLoading(true);
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
            setLoading(false);
          });
      } else {
        Utils.post(api.phyCluster, params)
          .then(() => {
            message.success('集群接入成功。注意：新接入集群数据稳定需要1-2分钟');
            afterSubmitSuccess && afterSubmitSuccess();
            onCancel();
          })
          .finally(() => {
            setLoading(false);
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
    setIsLowVersion(false);
    setZookeeperErrorStatus(false);

    return Utils.post(api.kafkaValidator, {
      bootstrapServers: bootstrapServers || '',
      zookeeper: zookeeper || '',
      clientProperties,
    })
      .then((res: any) => {
        form.setFieldsValue({
          jmxPort: res.jmxPort,
        });

        if (props.kafkaVersion.indexOf(res.kafkaVersion) > -1) {
          form.setFieldsValue({
            kafkaVersion: res.kafkaVersion,
          });
        } else {
          form.setFieldsValue({
            kafkaVersion: undefined,
          });
        }

        form.setFieldsValue({
          zookeeper: zookeeper || res.zookeeper,
        });

        const errList = res.errList || [];

        const extraMsg = extra;

        // 初始化信息为连接成功
        extraMsg.bootstrapExtra = bootstrapServers ? '连接成功' : '';
        extraMsg.zooKeeperExtra = zookeeper ? '连接成功' : '';

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

        // 如果kafkaVersion小于最低版本则提示
        const showLowVersion = !(
          curClusterInfo?.zookeeper ||
          !curClusterInfo?.kafkaVersion ||
          curClusterInfo?.kafkaVersion >= lowKafkaVersion
        );
        setIsLowVersion(showLowVersion);
        setExtra({
          ...extraMsg,
          versionExtra: showLowVersion ? intl.formatMessage({ id: 'access.cluster.low.version.tip' }) : '',
        });
        return res;
      })
      .finally(() => {
        setLoading(false);
      });
  };

  React.useEffect(() => {
    const showLowVersion = !(curClusterInfo?.zookeeper || !curClusterInfo?.kafkaVersion || curClusterInfo?.kafkaVersion >= lowKafkaVersion);
    lastFormItemValue.current = {
      bootstrap: curClusterInfo?.bootstrapServers || '',
      zookeeper: curClusterInfo?.zookeeper || '',
      clientProperties: curClusterInfo?.clientProperties || {},
    };
    setIsLowVersion(showLowVersion);
    setExtra({
      ...extra,
      versionExtra: showLowVersion ? intl.formatMessage({ id: 'access.cluster.low.version.tip' }) : '',
    });
    form.setFieldsValue({ ...curClusterInfo });
  }, [curClusterInfo]);

  React.useEffect(() => {
    if (visible) {
      if (clusterInfo?.id) {
        setLoading(true);
        Utils.request(api.getPhyClusterBasic(clusterInfo.id))
          .then((res: any) => {
            let jmxProperties = null;
            try {
              jmxProperties = JSON.parse(res?.jmxProperties);
            } catch (err) {
              console.error(err);
            }

            // 转化值对应成表单值
            if (jmxProperties?.openSSL) {
              jmxProperties.security = 'Password';
            }

            if (jmxProperties) {
              res = Object.assign({}, res || {}, jmxProperties);
            }
            setCurClusterInfo(res);
            setLoading(false);
          })
          .catch((err) => {
            setCurClusterInfo(clusterInfo);
            setLoading(false);
          });
      } else {
        setCurClusterInfo(clusterInfo);
      }
    }
  }, [visible, clusterInfo]);

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
              <Button size="small" type="primary" onClick={onSubmit}>
                确定
              </Button>
              <Divider type="vertical" />
            </Space>
          </div>
        }
        title={intl.formatMessage({ id: props.title || 'access.cluster' })}
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
                  validator: async (rule: any, value: string) => {
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
                      return Promise.reject(
                        '集群名称支持中英文、数字、特殊字符 ! " # $ % & \' ( ) * + , - . / : ; < = > ? @ [  ] ^ _ ` { | } ~'
                      );
                    }
                    return Utils.request(api.getClusterBasicExit(value)).then((res: any) => {
                      const data = res || {};
                      if (data?.exist) {
                        return Promise.reject('集群名称重复');
                      } else {
                        return Promise.resolve();
                      }
                    });
                  },
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
                  validator: async (rule: any, value: string) => {
                    if (!value) {
                      return Promise.reject('Bootstrap Servers不能为空');
                    }
                    if (value.length > 2000) {
                      return Promise.reject('Bootstrap Servers长度限制在2000字符');
                    }
                    if (value && value !== lastFormItemValue.current.bootstrap) {
                      return connectTest()
                        .then((res: any) => {
                          lastFormItemValue.current.bootstrap = value;

                          return Promise.resolve('');
                        })
                        .catch((err) => {
                          return Promise.reject('连接失败');
                        });
                    }
                    return Promise.resolve('');
                  },
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
              validateStatus={zookeeperErrorStatus ? 'error' : 'success'}
              validateTrigger={'onBlur'}
              rules={[
                {
                  required: false,
                  validator: async (rule: any, value: string) => {
                    if (!value) {
                      setZookeeperErrorStatus(false);
                      return Promise.resolve('');
                    }

                    if (value.length > 2000) {
                      return Promise.reject('Zookeeper长度限制在2000字符');
                    }

                    if (value && value !== lastFormItemValue.current.zookeeper) {
                      return connectTest()
                        .then((res: any) => {
                          lastFormItemValue.current.zookeeper = value;
                          setZookeeperErrorStatus(false);
                          return Promise.resolve('');
                        })
                        .catch((err) => {
                          setZookeeperErrorStatus(true);
                          return Promise.reject('连接失败');
                        });
                    }
                    return Promise.resolve('');
                  },
                },
              ]}
            >
              <Input.TextArea
                rows={3}
                placeholder="请输入Zookeeper地址，例如：192.168.0.1:2181,192.168.0.2:2181,192.168.0.2:2181/ks-kafka"
              />
            </Form.Item>
            <Form.Item
              className="no-item-control"
              name="Metrics"
              label="Metrics"
              rules={[
                {
                  required: false,
                  message: '',
                },
              ]}
            >
              <></>
            </Form.Item>
            <Form.Item
              name="jmxPort"
              label="JMX Port"
              className="inline-item adjust-height-style"
              extra={extra.jmxExtra}
              rules={[
                {
                  required: false,
                  message: '',
                },
              ]}
            >
              <InputNumber style={{ width: 134 }} min={0} max={99999} />
            </Form.Item>
            <Form.Item
              name="maxConn"
              label="MaxConn"
              className="inline-item adjust-height-style"
              rules={[
                {
                  required: false,
                  message: '',
                },
              ]}
            >
              <InputNumber style={{ width: 134 }} min={0} max={99999} />
            </Form.Item>
            <Form.Item
              name="security"
              label="Security"
              className="inline-item adjust-height-style"
              rules={[
                {
                  required: false,
                  message: '',
                },
              ]}
            >
              <Radio.Group>
                <Radio value="None">None</Radio>
                <Radio value="Password">Password Authentication</Radio>
              </Radio.Group>
            </Form.Item>
            {security === 'Password' ? (
              <>
                <Form.Item
                  className="inline-item max-width-66"
                  name="username"
                  label="User Info"
                  style={{ width: '58%' }}
                  rules={[
                    {
                      required: security === 'Password' || curClusterInfo?.security === 'Password',
                      validator: async (rule: any, value: string) => {
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
                    },
                  ]}
                >
                  <Input placeholder="请输入用户名" />
                </Form.Item>
                <Form.Item
                  className="inline-item"
                  name="token"
                  label=""
                  style={{ width: '38%', marginRight: 0 }}
                  rules={[
                    {
                      required: security === 'Password' || curClusterInfo?.security === 'Password',
                      validator: async (rule: any, value: string) => {
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
                    },
                  ]}
                >
                  <Input placeholder="请输入密码" />
                </Form.Item>
              </>
            ) : null}
            <Form.Item
              name="kafkaVersion"
              label="Version"
              extra={<span className="error-extra-info">{extra.versionExtra}</span>}
              validateStatus={isLowVersion ? 'error' : 'success'}
              rules={[
                {
                  required: true,
                  validator: async (rule: any, value: any) => {
                    if (!value) {
                      setIsLowVersion(true);
                      return Promise.reject('版本号不能为空');
                    }
                    // 检测版本号小于2.8.0，如果没有填zookeeper信息，才会提示
                    const zookeeper = form.getFieldValue('zookeeper');
                    if (value < lowKafkaVersion && !zookeeper) {
                      setIsLowVersion(true);
                      setExtra({
                        ...extra,
                        versionExtra: intl.formatMessage({ id: 'access.cluster.low.version.tip' }),
                      });
                      return Promise.resolve();
                    }
                    setIsLowVersion(false);
                    return Promise.resolve();
                  },
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
                  required: false,
                  message: '请输入集群配置',
                },
                () => ({
                  validator(_, value) {
                    try {
                      if (value) {
                        JSON.parse(value);
                      }

                      return Promise.resolve();
                    } catch (e) {
                      return Promise.reject(new Error('输入内容必须为 JSON'));
                    }
                  },
                }),
              ]}
            >
              <div>
                <CodeMirrorFormItem
                  resize
                  defaultInput={form.getFieldValue('clientProperties')}
                  placeholder={clientPropertiesPlaceholder}
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
                  required: false,
                  validator: async (rule: any, value: string) => {
                    if (!value) {
                      return Promise.resolve('');
                    }
                    if (value && value.length > 200) {
                      return Promise.reject('集群描述长度限制在200字符');
                    }
                    return Promise.resolve();
                  },
                },
              ]}
            >
              <Input.TextArea rows={rows} />
            </Form.Item>
          </Form>
        </Spin>
      </Drawer>
    </>
  );
};

export default AccessClusters;
