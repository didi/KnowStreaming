import React, {
  createContext,
  createElement,
  forwardRef,
  useContext,
  useEffect,
  useImperativeHandle,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';
import { Alert, Button, Col, Collapse, Drawer, Form, Input, InputNumber, Row, Select, Steps, Switch, Table, Utils } from 'knowdesign';
import { FormInstance } from 'knowdesign/es/basic/form/Form';
import SwitchTab from '@src/components/SwitchTab';
import message from '@src/components/Message';
import api from '@src/api';
import { useParams } from 'react-router-dom';
import { regClusterName } from '@src/constants/reg';
import { IconFont } from '@knowdesign/icons';

const { Step } = Steps;

export interface ConnectCluster {
  id: number;
  name: string;
  groupName: string;
  state: number;
  version: string;
  jmxProperties: string;
  clusterUrl: string;
  memberLeaderUrl: string;
}

export interface ConnectorPlugin {
  type: 'source' | 'sink';
  version: string;
  className: string;
  helpDocLink: string;
}

interface ConnectorPluginConfigDefinition {
  name: string;
  type: string;
  required: boolean;
  defaultValue: string | null;
  importance: string;
  documentation: string;
  group: string;
  orderInGroup: number;
  width: string;
  displayName: string;
  dependents: string[];
}

interface ConnectorPluginConfigValue {
  errors: string[];
  name: string;
  recommendedValues: any[];
  value: any;
  visible: boolean;
}

export interface ConnectorPluginConfig {
  name: string;
  errorCount: number;
  groups: string[];
  configs: {
    definition: ConnectorPluginConfigDefinition;
    value: ConnectorPluginConfigValue;
  }[];
}

interface FormConnectorConfigs {
  pluginConfig: { [key: string]: ConnectorPluginConfigDefinition[] };
  connectorConfig?: { [key: string]: any };
}

interface SubFormProps {
  visible: boolean;
  setSubmitLoading: (loading: boolean) => void;
}

export interface OperateInfo {
  type: 'create' | 'edit';
  errors: {
    [key: string]: string[];
  };
  detail?: {
    connectClusterId: number;
    connectorName: string;
    connectorClassName: string;
    connectorType: 'source' | 'sink';
  };
}

const existFormItems = {
  basic: ['name', 'connector.class', 'tasks.max', 'key.converter', 'value.converter', 'header.converter'],
  transforms: ['transforms'],
  errorHandling: [
    'errors.retry.timeout',
    'errors.retry.delay.max.ms',
    'errors.tolerance',
    'errors.log.enable',
    'errors.log.include.messages',
  ],
};

const getExistFormItems = (type: 'source' | 'sink') => {
  return [...existFormItems.basic, ...existFormItems.transforms, ...existFormItems.errorHandling, type === 'sink' ? 'topics' : ''].filter(
    (k) => k
  );
};

const StepsFormContent = createContext<
  OperateInfo & {
    forms: { current: { [key: string]: FormInstance } };
  }
>({
  type: 'create',
  errors: {},
  forms: { current: {} },
});

function useStepForm(key: string | number) {
  const { forms } = useContext(StepsFormContent);
  const [form] = Form.useForm();
  let formInstace = form;

  if (forms.current[key]) {
    formInstace = forms.current[key] as FormInstance;
  } else {
    forms.current[key] = formInstace;
  }

  return [formInstace];
}

// 步骤一：设置插件类型
const StepFormFirst = (props: SubFormProps) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [form] = useStepForm(0);
  const { type, detail } = useContext(StepsFormContent);
  const isEdit = type === 'edit';
  const [connectClusters, setConnectClusters] = useState<{ label: string; value: number }[]>([]);
  const [selectedConnectClusterId, setSelectedConnectClusterId] = useState(detail?.connectClusterId);
  const [input, setInput] = useState<string>('');
  const [plugins, setPlugins] = useState<ConnectorPlugin[]>([]);
  const [selectedPlugin, setSelectedPlugin] = useState<string>(detail?.connectorClassName);
  const [pluginType, setPluginType] = useState<'source' | 'sink'>((detail?.connectorType.toLowerCase() as 'source' | 'sink') || 'source');
  const [loading, setLoading] = useState(false);

  const getConnectClusters = () => {
    return Utils.request(api.getConnectClusters(clusterId)).then((res: ConnectCluster[]) => {
      const arr = res.map(({ name, id }) => ({
        label: name || '-',
        value: id,
      }));
      setConnectClusters(arr);
      form.setFieldsValue({
        connectClusters: arr,
      });
    });
  };

  const getConnectorPlugins = () => {
    setLoading(true);
    return Utils.request(api.getConnectorPlugins(selectedConnectClusterId))
      .then((res: ConnectorPlugin[]) => {
        setPlugins(res);
      })
      .finally(() => setLoading(false));
  };

  const getConnectorPluginConfig = (pluginName: string) => {
    props.setSubmitLoading(true);

    Promise.all(
      [
        Utils.request(api.getConnectorPluginConfig(selectedConnectClusterId, pluginName)),
        isEdit ? Utils.request(api.getCurPluginConfig(selectedConnectClusterId, detail.connectorName)) : undefined,
      ].filter((r) => r)
    )
      .then((res: [ConnectorPluginConfig, { [key: string]: any }]) => {
        const [pluginConfig, connectorConfigs] = res;

        // 格式化插件配置
        const result: FormConnectorConfigs = {
          pluginConfig: {},
        };

        // 获取一份默认配置
        const defaultPluginConfig: any = {};

        pluginConfig.configs.forEach(({ definition }) => {
          // 获取一份默认配置
          defaultPluginConfig[definition.name] = definition?.defaultValue;

          if (!getExistFormItems(pluginType).includes(definition.name)) {
            const pluginConfigs = result.pluginConfig;
            const group = definition.group || 'Others';
            pluginConfigs[group] ? pluginConfigs[group].push(definition) : (pluginConfigs[group] = [definition]);
          }
        });
        Object.values(result.pluginConfig).forEach((arr) => arr.sort((a, b) => a.orderInGroup - b.orderInGroup));

        // 加入当前 connector 的配置
        if (isEdit) {
          result.connectorConfig = connectorConfigs;
        }

        Object.keys(result).length &&
          form.setFieldsValue({
            configs: { ...result, defaultPluginConfig, editConnectorConfig: result.connectorConfig },
          });
      })
      .finally(() => props.setSubmitLoading(false));
  };

  useEffect(() => {
    if (selectedPlugin) {
      getConnectorPluginConfig(selectedPlugin);
    }
  }, [selectedPlugin]);

  useEffect(() => {
    if (selectedConnectClusterId) {
      getConnectorPlugins();
    }
  }, [selectedConnectClusterId]);

  useEffect(() => {
    getConnectClusters();
  }, []);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        <Form.Item
          name="connectClusterId"
          label="Connect 集群"
          rules={[{ required: true, message: '请选择 Connect 集群' }]}
          initialValue={detail?.connectClusterId}
        >
          <Select options={connectClusters} placeholder="请选择 Connect 集群" disabled={isEdit} />
        </Form.Item>
        <Form.Item noStyle dependencies={['connectClusterId']}>
          {({ getFieldValue }) => {
            const curConnector = getFieldValue('connectClusterId');
            if (selectedConnectClusterId !== curConnector) {
              form.resetFields(['connectorClassName']);
            }
            setSelectedConnectClusterId(curConnector);

            return curConnector ? (
              <>
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    margin: '26px 0 18px 0',
                  }}
                >
                  <SwitchTab
                    activeKey={pluginType}
                    onChange={(type: 'source' | 'sink') => {
                      if (isEdit) {
                        message.warning('编辑状态不可修改插件');
                        return;
                      }
                      setPluginType(type);
                      form.setFieldsValue({
                        connectorType: type,
                      });
                      form.resetFields(['connectorClassName']);
                    }}
                  >
                    <SwitchTab.TabItem key="source">
                      <div style={{ width: 151, height: 32, lineHeight: '32px' }}>
                        Source{' '}
                        <span style={{ fontSize: 12 }}>
                          (数据
                          <IconFont type="icon-jiantou" />
                          Kafka)
                        </span>
                      </div>
                    </SwitchTab.TabItem>
                    <SwitchTab.TabItem key="sink">
                      <div style={{ width: 151, height: 32, lineHeight: '32px' }}>
                        Sink{' '}
                        <span style={{ fontSize: 12 }}>
                          (Kafka
                          <IconFont type="icon-jiantou" />
                          数据)
                        </span>
                      </div>
                    </SwitchTab.TabItem>
                  </SwitchTab>
                  <Input
                    placeholder="请输入插件名称"
                    style={{ width: 241 }}
                    onChange={(e) => {
                      setInput(e.target.value);
                    }}
                  />
                </div>
                <Table
                  rowKey="className"
                  loading={loading}
                  columns={[
                    {
                      title: '插件名称',
                      dataIndex: 'className',
                      render: (value, record) => {
                        return (
                          <span>
                            {value}
                            {record?.helpDocLink && (
                              <span
                                style={{
                                  background: 'rgba(85, 110, 230, 0.1)',
                                  padding: '2px 8px',
                                  borderRadius: 4,
                                  marginLeft: 4,
                                  color: '#5664FF',
                                  cursor: 'pointer',
                                  fontSize: 12,
                                }}
                                onClick={() => window.open(record.helpDocLink)}
                              >
                                help
                              </span>
                            )}
                          </span>
                        );
                      },
                    },
                  ]}
                  dataSource={plugins.filter((plugin) => plugin.type === pluginType && (!input || plugin.className.includes(input)))}
                  pagination={false}
                  rowSelection={{
                    type: 'radio',
                    preserveSelectedRowKeys: false,
                    selectedRowKeys: [selectedPlugin],
                    getCheckboxProps: (record) => {
                      return {
                        disabled: isEdit && record.className !== selectedPlugin,
                      };
                    },
                    onChange: (keys) => {
                      setSelectedPlugin(keys[0] as string);
                      form.setFieldsValue({
                        connectorClassName: keys[0],
                      });
                    },
                  }}
                />
                <div className="add-container-plugin-select">
                  <Form.Item name="connectClusters" />
                  <Form.Item name="connectorType" initialValue={pluginType} />
                  <Form.Item
                    name="connectorClassName"
                    initialValue={selectedPlugin}
                    rules={[
                      {
                        validator: (rule, value) => {
                          if (!value) {
                            return Promise.reject('请选择 Connector 插件');
                          }
                          return Promise.resolve();
                        },
                      },
                    ]}
                  />
                  <Form.Item
                    name="configs"
                    rules={[
                      {
                        validator: (rule, value) => {
                          if (!form.getFieldValue('connectorClassName')) {
                            return Promise.resolve(true);
                          }
                          if (!value) {
                            return Promise.reject(isEdit ? '插件或 connector 配置获取失败' : '插件配置获取失败，请重新选择插件');
                          }
                          return Promise.resolve();
                        },
                      },
                    ]}
                  />
                </div>
              </>
            ) : (
              <></>
            );
          }}
        </Form.Item>
      </Form>
    </div>
  );
};

// 步骤二：基础设置
const StepFormSecond = (props: SubFormProps) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [prevForm] = useStepForm(0);
  const [form] = useStepForm(1);
  const [topicData, setTopicData] = useState([]);
  const { type, detail, errors } = useContext(StepsFormContent);
  const isEdit = type === 'edit';
  const connectorConfig = (prevForm.getFieldValue('configs') as FormConnectorConfigs)?.connectorConfig;

  const getTopicList = () => {
    Utils.request(api.getTopicMetaList(Number(clusterId)), {
      method: 'GET',
    }).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.topicName,
          label: item.topicName,
          value: item.topicName,
        };
      });
      setTopicData(dataHandle);
    });
  };

  useEffect(() => {
    getTopicList();
  }, []);

  useEffect(() => {
    connectorConfig &&
      form.setFieldsValue({
        topics:
          typeof connectorConfig['topics'] === 'string' ? connectorConfig['topics'].split(',').map((i: string) => i.trim()) : undefined,
      });
  }, [topicData, connectorConfig]);

  useEffect(() => {
    const curConfig = connectorConfig || {};
    form.setFieldsValue({
      'connector.class': curConfig['connector.class'] || prevForm.getFieldValue('connectorClassName'),
      'tasks.max': curConfig['tasks.max'] || 1,
      'key.converter': curConfig['key.converter'],
      'value.converter': curConfig['value.converter'],
      'header.converter': curConfig['header.converter'],
    });
  }, [connectorConfig]);

  useEffect(() => {
    form.setFieldsValue({
      'connector.class': prevForm.getFieldValue('connectorClassName'),
    });
  }, [prevForm.getFieldValue('connectorClassName')]);

  useEffect(() => {
    form.setFields([
      ...existFormItems.basic.map((name) => ({ name, errors: errors[name] || [] })),
      { name: 'topics', errors: prevForm.getFieldValue('connectorType') === 'sink' ? errors['topics'] || [] : [] },
    ]);
  }, [errors]);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="Connector 名称"
          validateTrigger="onBlur"
          initialValue={detail?.connectorName}
          rules={[
            {
              required: true,
              validator(_: any, value: string) {
                if (isEdit) {
                  return Promise.resolve();
                }
                if (!value) {
                  return Promise.reject('Connector 名称不能为空');
                }
                if (isEdit) {
                  return Promise.resolve();
                }
                if (value?.length > 64) {
                  return Promise.reject('Connector 名称长度限制在1～128字符');
                }
                if (!new RegExp(regClusterName).test(value)) {
                  return Promise.reject(
                    "Connector 名称支持中英文、数字、特殊字符 ! # $ % & ' ( ) * + , - . / : ; < = > ? @ [  ] ^ _ ` { | } ~"
                  );
                }
                return Utils.request(api.isConnectorExist(prevForm.getFieldValue('connectClusterId'), value)).then(
                  (res: any) => {
                    const data = res || {};
                    return data?.exist ? Promise.reject('Connector 名称重复') : Promise.resolve();
                  },
                  () => Promise.reject('连接超时! 请重试或检查服务')
                );
              },
            },
          ]}
        >
          <Input placeholder="请输入 Connector 名称" disabled={isEdit} />
        </Form.Item>
        <Form.Item name="connector.class">
          <div className="connector-plugin-desc">
            <span className="connector-plugin-title">Connector 插件类型</span>: {form.getFieldValue('connector.class') || '-'}
          </div>
        </Form.Item>
        <Form.Item
          name="tasks.max"
          label="最大 Task 数"
          tooltip="The maximum number of tasks that should be created for this connector. The connector may create fewer tasks if it cannot achieve this level of parallelism."
          rules={[{ required: true, message: '最大 Task 数不能为空' }]}
        >
          <InputNumber min={1} max={99999} />
        </Form.Item>
        <Form.Item
          name="key.converter"
          label="Key converter class"
          tooltip="Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the keys in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro."
          rules={[{ required: false }]}
          normalize={(value) => value || null}
        >
          <Input placeholder="请输入 Key converter class" />
        </Form.Item>
        <Form.Item
          name="value.converter"
          label="Value converter class"
          tooltip="Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the values in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro."
          rules={[{ required: false }]}
          normalize={(value) => value || null}
        >
          <Input placeholder="请输入 Value converter class" />
        </Form.Item>
        <Form.Item
          name="header.converter"
          label="Header converter class"
          tooltip="HeaderConverter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the header values in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro. By default, the SimpleHeaderConverter is used to serialize header values to strings and deserialize them by inferring the schemas."
          rules={[{ required: false }]}
          normalize={(value) => value || null}
        >
          <Input placeholder="请输入 Header converter class" />
        </Form.Item>
        {/* Connector 类型为 Sink 时才有 */}
        {prevForm.getFieldValue('connectorType') === 'sink' && (
          <Form.Item name="topics" label="Topics" tooltip="从哪些Topic消费消息" rules={[{ required: true, message: 'Topics 不能为空' }]}>
            <Select mode="multiple" allowClear placeholder="请选择 Topics" options={topicData} />
          </Form.Item>
        )}
      </Form>
    </div>
  );
};

// 步骤三：Transforms
const StepFormThird = (props: SubFormProps) => {
  const [firstForm] = useStepForm(0);
  const [form] = useStepForm(2);
  const { type, errors } = useContext(StepsFormContent);
  const isEdit = type === 'edit';
  const configs = (firstForm.getFieldValue('configs') as FormConnectorConfigs)?.connectorConfig;

  useLayoutEffect(() => {
    if (isEdit) {
      const transforms = configs?.['transforms'];
      const otherConfigs: {
        [key: string]: any;
      } = {};
      if (transforms) {
        const keys = transforms.split(',').map((l: string) => l.trim());
        Object.entries(configs).forEach(([k, v]) => {
          if (keys.some((key: string) => k.includes(`transforms.${key}`))) {
            otherConfigs[k] = v;
          }
        });

        const result = `transforms=${transforms}\n${Object.entries(otherConfigs).map(([k, v]) => `${k}=${v}\n`)}`;
        form.setFieldsValue({
          transforms: result,
        });
      }
    }
  }, [configs]);

  useEffect(() => {
    form.setFields([{ name: 'transforms', errors: errors['transforms'] || [] }]);
  }, [errors]);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        <Form.Item
          name="transforms"
          label={
            <>
              <span>Transforms</span>
              <span
                style={{
                  background: 'rgba(85, 110, 230, 0.1)',
                  padding: '0px 8px',
                  borderRadius: 4,
                  marginLeft: 4,
                  color: '#5664FF',
                  cursor: 'pointer',
                  fontSize: 12,
                  height: '20px',
                  lineHeight: '20px',
                }}
                onClick={() => window.open('https://kafka.apache.org/documentation/#connect_transforms')}
              >
                help
              </span>
            </>
          }
          rules={[
            {
              validator(_: any, value: string) {
                if (!value) {
                  return Promise.resolve();
                }
                if (
                  value
                    .split('\n')
                    .filter((l) => l)
                    .some((l) => !l.includes('='))
                ) {
                  return Promise.reject('格式应为 key=value，使用换行符分隔');
                }
                return Promise.resolve();
              },
            },
          ]}
        >
          <Input.TextArea
            rows={8}
            placeholder={`transforms=MakeMap, InsertSource
transforms.MakeMap.type=org.apache.kafka.connect.transforms.HoistField$Value
transforms.MakeMap.field=line
transforms.InsertSource.type=org.apache.kafka.connect.transforms.InsertField$Value
transforms.InsertSource.static.field=data_source
transforms.InsertSource.static.value=test-file-source`}
          />
        </Form.Item>
      </Form>
    </div>
  );
};

// 步骤四：Error Handling
const StepFormForth = (props: SubFormProps) => {
  const [firstForm] = useStepForm(0);
  const [form] = useStepForm(3);
  const { errors } = useContext(StepsFormContent);
  const configs = (firstForm.getFieldValue('configs') as FormConnectorConfigs)?.connectorConfig;

  useEffect(() => {
    const curConfig = configs || {};
    form.setFieldsValue({
      'errors.retry.timeout': curConfig['errors.retry.timeout'] || 0,
      'errors.retry.delay.max.ms': curConfig['errors.retry.delay.max.ms'] || 60000,
      'errors.tolerance': curConfig['errors.tolerance'] || 'none',
      'errors.log.enable':
        (curConfig['errors.log.enable'] && (curConfig['errors.log.enable'] || curConfig['errors.log.enable'] === 'true' ? true : false)) ||
        false,
      'errors.log.include.messages':
        (curConfig['errors.log.include.messages'] &&
          (curConfig['errors.log.include.messages'] || curConfig['errors.log.include.messages'] === 'true' ? true : false)) ||
        false,
    });
  }, [configs]);

  useEffect(() => {
    form.setFields(existFormItems.errorHandling.map((name) => ({ name, errors: errors[name] || [] })));
  }, [errors]);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        <Row gutter={[12, 0]}>
          <Col span={12}>
            <Form.Item
              name="errors.retry.timeout"
              label="Retry Timeout for Errors"
              tooltip="The maximum duration in milliseconds that a failed operation will be reattempted. The default is 0, which means no retries will be attempted. Use -1 for infinite retries."
            >
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="errors.retry.delay.max.ms"
              label="Maximum Delay Between Retries for Errors"
              tooltip="The maximum duration in milliseconds between consecutive retry attempts. Jitter will be added to the delay once this limit is reached to prevent thundering herd issues."
            >
              <InputNumber min={0} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item
          name="errors.tolerance"
          label="Error Tolerance"
          tooltip="Behavior for tolerating errors during connector operation. 'none' is the default value and signals that any error will result in an immediate connector task failure; 'all' changes the behavior to skip over problematic records."
        >
          {/* <Input placeholder="none" /> */}
          <Select
            defaultValue="none"
            options={[
              {
                value: 'none',
                label: 'none',
              },
              {
                value: 'all',
                label: 'all',
              },
            ]}
          />
        </Form.Item>
        <Form.Item
          name="errors.log.enable"
          label="Log Errors"
          valuePropName="checked"
          tooltip="If true, write each error and the details of the failed operation and problematic record to the Connect application log. This is 'false' by default, so that only errors that are not tolerated are reported."
        >
          <Switch size="small" />
        </Form.Item>
        <Form.Item
          name="errors.log.include.messages"
          label="Log Error Detials"
          valuePropName="checked"
          tooltip="Whether to include in the log the Connect record that resulted in a failure.For sink records, the topic, partition, offset, and timestamp will be logged. For source records, the key and value (and their schemas), all headers, and the timestamp, Kafka topic, Kafka partition, source partition, and source offset will be logged. This is 'false' by default, which will prevent record keys, values, and headers from being written to log files."
        >
          <Switch size="small" />
        </Form.Item>
      </Form>
    </div>
  );
};

// 步骤五：高级设置
const StepFormFifth = (props: SubFormProps) => {
  const [form] = useStepForm(4);
  const [firstForm] = useStepForm(0);
  const { errors } = useContext(StepsFormContent);
  const { pluginConfig, connectorConfig = {} }: FormConnectorConfigs = firstForm.getFieldValue('configs') || {};
  const [activeKey, setActiveKey] = useState([]);
  const switchCollapse = (keys: string[]) => {
    if (keys.length < activeKey.length) {
      const hideKey = activeKey.find((key) => !keys.includes(key));
      if (hideKey) {
        const validateFormNames = pluginConfig[hideKey].map((item) => item.name);
        form.validateFields(validateFormNames).then(
          () => {
            setActiveKey(keys);
          },
          () => {
            message.warning('收起前请先填写必填项');
          }
        );
      }
    } else {
      setActiveKey(keys);
    }
  };

  useLayoutEffect(() => {
    setActiveKey(pluginConfig ? Object.keys(pluginConfig) : []);
  }, [pluginConfig]);

  useEffect(() => {
    const fieldsState: any[] = [];
    Object.entries(errors).forEach(([key, val]) => {
      if (!getExistFormItems(firstForm.getFieldValue('connectorType')).includes(key)) {
        fieldsState.push({ name: key, errors: val });
      }
    });
    form.setFields(fieldsState);
  }, [errors]);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        {pluginConfig && (
          <Collapse className="add-connector-collapse" ghost activeKey={activeKey} onChange={switchCollapse}>
            {Object.entries(pluginConfig)
              .sort((a, b) => Number(a[0] > b[0]) - 0.5)
              .map(([group, configs]) => {
                return (
                  <Collapse.Panel className="add-connector-collapse-panel" header={group} key={group}>
                    {configs.map(({ name, displayName, type, required, defaultValue, documentation }) => {
                      return (
                        <Form.Item
                          key={name}
                          name={name}
                          label={displayName}
                          rules={[{ required, message: required ? `${displayName} 不能为空` : '' }]}
                          initialValue={connectorConfig?.[name] || defaultValue}
                          normalize={(value) => (defaultValue === null && !value ? null : value)}
                          tooltip={documentation}
                        >
                          {name === 'config.action.reload' ? (
                            <Select
                              defaultValue="restart"
                              options={[
                                {
                                  value: 'restart',
                                  label: 'restart',
                                },
                                {
                                  value: 'none',
                                  label: 'none',
                                },
                              ]}
                            />
                          ) : type.toUpperCase() === 'INT' || type.toUpperCase() === 'LONG' ? (
                            <InputNumber />
                          ) : type.toUpperCase() === 'BOOLEAN' ? (
                            <Switch size="small" />
                          ) : type.toUpperCase() === 'PASSWORD' ? (
                            <Input.Password />
                          ) : (
                            <Input />
                          )}
                        </Form.Item>
                      );
                    })}
                  </Collapse.Panel>
                );
              })}
          </Collapse>
        )}
      </Form>
    </div>
  );
};

const steps = [
  {
    title: '设置插件类型',
    content: StepFormFirst,
  },
  {
    title: '基础设置',
    content: StepFormSecond,
  },
  {
    title: 'Transforms',
    content: StepFormThird,
  },
  {
    title: 'Error Handling',
    content: StepFormForth,
  },
  {
    title: '高级设置',
    content: StepFormFifth,
  },
];

export default forwardRef(
  (
    props: {
      refresh: () => void;
    },
    ref
  ) => {
    const [visible, setVisible] = useState(false);
    const [jsonRef, setJsonRef] = useState({});
    const [currentStep, setCurrentStep] = useState(0);
    const [stepInitState, setStepInitState] = useState([1]);
    const [submitLoading, setSubmitLoading] = useState(false);
    const [operateInfo, setOperateInfo] = useState<OperateInfo>({
      type: undefined,
      errors: {},
    });
    const stepsFormRef = useRef<{
      [key: string]: FormInstance;
    }>({});

    const onOpen = (type: OperateInfo['type'], jsonRef: any, detail?: OperateInfo['detail']) => {
      if (type === 'create') {
        setStepInitState([1]);
      } else {
        setStepInitState([1, 2, 3, 4]);
      }
      setOperateInfo({
        type,
        detail,
        errors: {},
      });
      setJsonRef(jsonRef);
      setVisible(true);
    };

    const onClose = () => {
      Object.values(stepsFormRef.current).forEach((form) => {
        form.resetFields();
      });
      stepsFormRef.current = {};
      setVisible(false);
      setCurrentStep(0);
      setStepInitState([]);
    };

    const turnTo = (jumpStep: number) => {
      if (submitLoading) {
        message.warning('加载中，请稍后重试');
        return;
      }
      if (jumpStep > currentStep) {
        const prevInit = stepInitState[jumpStep - 1];
        if (!prevInit) {
          message.warning('请按照顺序填写');
        } else {
          stepsFormRef.current[currentStep].validateFields().then(() => {
            const prevStep = jumpStep - 1;
            if (currentStep < prevStep) {
              stepsFormRef.current[prevStep]
                .validateFields()
                .then(() => {
                  setStepInitState((prev) => {
                    const cur = [...prev];
                    cur[jumpStep] = 1;
                    return cur;
                  });
                  setCurrentStep(jumpStep);
                })
                .catch(() => {
                  setCurrentStep(prevStep);
                });
            } else {
              setStepInitState((prev) => {
                const cur = [...prev];
                cur[jumpStep] = 1;
                return cur;
              });
              setCurrentStep(jumpStep);
            }
          });
        }
      } else {
        setCurrentStep(jumpStep);
      }
    };

    // 校验所有表单
    const validateForms = (
      callback: (info: {
        success?: {
          connectClusterId: number;
          connectorName: string;
          config: {
            [key: string]: any;
          };
        };
        error?: any;
      }) => void
    ) => {
      const promises: Promise<any>[] = [];
      const compareConfig = stepsFormRef.current[0].getFieldValue('configs'); // 获取步骤一的form信息
      Object.values(stepsFormRef.current).forEach((form, i) => {
        const promise = form
          .validateFields()
          .then((res) => {
            return res;
          })
          .catch(() => {
            return Promise.reject(i);
          });
        promises.push(promise);
      });

      Promise.all(promises).then(
        (res) => {
          const result = {
            ...res[1],
            ...res[3],
            ...res[4],
          };
          // topics 配置格式化
          res[1].topics && (result.topics = (res[1].topics as string[]).join(', '));
          // transforms 配置格式化
          res[2].transforms &&
            (res[2].transforms as string)
              .split('\n')
              .filter((l) => l)
              .forEach((l) => {
                const [k, ...v] = l.split('=');
                result[k] = v.join('=');
              });

          const editConnectorConfig = operateInfo.type === 'edit' ? compareConfig.editConnectorConfig : {}; // 编辑状态时拿到config配置
          const newCompareConfig = { ...compareConfig.defaultPluginConfig, ...editConnectorConfig, ...result }; // 整合后的表单提交信息
          Object.keys(newCompareConfig).forEach((item) => {
            if (
              newCompareConfig[item] === compareConfig.defaultPluginConfig[item] ||
              newCompareConfig[item]?.toString() === compareConfig.defaultPluginConfig[item]?.toString()
            ) {
              delete newCompareConfig[item]; // 清除默认值
            }
          });
          callback({
            success: {
              connectClusterId: res[0].connectClusterId,
              connectorName: result['name'],
              config: newCompareConfig,
            },
          });
        },
        (error) => {
          callback({
            error,
          });
        }
      );
    };

    const toJsonMode = () => {
      validateForms((info) => {
        if (info.error) {
          message.warning('校验失败，请检查填写内容');
          setCurrentStep(info.error);
        } else {
          let curClusterName = '';
          stepsFormRef.current[0].getFieldValue('connectClusters').some((cluster: { label: string; value: number }) => {
            if (cluster.value === info.success.connectClusterId) {
              curClusterName = cluster.label;
            }
          });
          (jsonRef as any)?.onOpen(operateInfo.type, curClusterName, info.success.config);
          onClose();
        }
      });
    };

    const onSubmit = () => {
      validateForms((info) => {
        if (info.error) {
          message.warning('校验失败，请检查填写内容');
          setCurrentStep(info.error);
        } else {
          setSubmitLoading(true);
          Object.entries(info.success.config).forEach(([key, val]) => {
            if (val === null) {
              delete info.success.config[key];
            }
          });
          Utils.put(api.validateConnectorConfig, info.success).then(
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
                  setOperateInfo((cur) => ({
                    ...cur,
                    errors,
                  }));

                  // 步骤跳转
                  const items = getExistFormItems(stepsFormRef.current[0].getFieldValue('connectorType'));
                  const keys = Object.keys(errors).filter((key) => items.includes(key));
                  let jumpStep = 4;
                  keys.forEach((key) => {
                    Object.values(existFormItems).some((items, i) => {
                      if (items.includes(key)) {
                        jumpStep > i + 1 && (jumpStep = i + 1);
                        return true;
                      }
                      return false;
                    });
                  });
                  setCurrentStep(jumpStep);
                  setSubmitLoading(false);
                  message.warning('字段校验失败，请检查');
                } else {
                  if (operateInfo.type === 'create') {
                    Utils.post(api.connectorsOperates, info.success)
                      .then(() => {
                        message.success('新建成功');
                        onClose();
                        props?.refresh();
                      })
                      .finally(() => setSubmitLoading(false));
                  } else {
                    Utils.put(api.updateConnectorConfig, info.success)
                      .then(() => {
                        message.success('编辑成功');
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
        }
      });
    };

    useImperativeHandle(ref, () => ({
      onOpen,
      onClose,
    }));

    return (
      <Drawer
        title={`${operateInfo.type === 'create' ? '新建' : '编辑'} Connector`}
        className="operate-connector-drawer"
        width={800}
        visible={visible}
        onClose={onClose}
        destroyOnClose
      >
        {operateInfo.type && visible && (
          <>
            <Steps current={currentStep} labelPlacement="vertical" onChange={(cur) => turnTo(cur)}>
              {steps.map(({ title }) => (
                <Step key={title} title={title} />
              ))}
            </Steps>
            <div style={{ padding: '48px 24px 0px' }}>
              <StepsFormContent.Provider
                value={{
                  ...operateInfo,
                  forms: stepsFormRef,
                }}
              >
                {steps.map((step, i) => {
                  return createElement(step.content, {
                    visible: i === currentStep,
                    setSubmitLoading,
                  });
                })}
              </StepsFormContent.Provider>
              {currentStep === steps.length - 1 && (
                <Alert
                  type="warning"
                  message={
                    <span>
                      如果你想自定义更多配置，可以点击
                      <Button type="link" onClick={toJsonMode}>
                        JSON 模式
                      </Button>
                      继续补充
                    </span>
                  }
                />
              )}
              <div className="steps-action">
                {currentStep > 0 && (
                  <Button style={{ margin: '0 8px' }} onClick={() => turnTo(currentStep - 1)}>
                    上一步
                  </Button>
                )}
                {currentStep < steps.length - 1 && (
                  <Button type="primary" loading={submitLoading} onClick={() => turnTo(currentStep + 1)}>
                    下一步
                  </Button>
                )}
                {currentStep === steps.length - 1 && (
                  <Button type="primary" loading={submitLoading} onClick={() => onSubmit()}>
                    提交
                  </Button>
                )}
              </div>
            </div>
          </>
        )}
      </Drawer>
    );
  }
);
