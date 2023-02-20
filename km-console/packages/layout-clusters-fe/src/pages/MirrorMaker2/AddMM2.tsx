import React, { createContext, createElement, forwardRef, useContext, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Alert, Button, Drawer, Form, Input, InputNumber, Radio, Select, Spin, Steps, Switch, Table, Transfer, Utils } from 'knowdesign';
import { FormInstance } from 'knowdesign/es/basic/form/Form';
import message from '@src/components/Message';
import api from '@src/api';
import { useParams } from 'react-router-dom';
import { IconFont } from '@knowdesign/icons';
import { regClusterName } from '@src/constants/reg';

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
  setSourceKafkaClusterId?: any;
  setBootstrapServers?: any;
  setSourceDetailConfigs?: any;
  setCheckoutPointDetailConfigs?: any;
  setHeartbeatDetailConfigs?: any;
}

const existConfigItems = {
  sourceConfigs: [
    'sync.topic.configs.enabled',
    'sync.topic.configs.interval.seconds',
    'sync.topic.acls.enabled',
    'sync.topic.acls.interval.seconds',
    'refresh.topics.enabled',
    'refresh.topics.interval.seconds',
    'refresh.groups.enabled',
    'refresh.groups.interval.seconds',
    'replication.policy.separator',
    'replication.policy.class',
    'topics',
  ],
  checkpointConfig: ['emit.checkpoints.enabled', 'emit.checkpoints.interval.seconds', 'checkpoints.topic.replication.factor'],
  heartbeatConfig: ['heartbeats.topic.replication.factor', 'emit.heartbeats.interval.seconds'],
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

// 步骤一
const StepFormFirst = (props: SubFormProps) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [form] = useStepForm(0);
  const [topicData, setTopicData] = useState([]);
  const [topicDataLoading, setTopicDataLoading] = useState(false);
  const [givenSourceKafkaId, setGivenSourceKafkaId] = useState(clusterId);
  const { type, detail, setSourceKafkaClusterId, setBootstrapServers, setSourceDetailConfigs, setCheckoutPointDetailConfigs } =
    useContext(StepsFormContent);
  const isEdit = type === 'edit';
  const [seniorConfig, setSeniorConfig] = useState(false);
  const [topicTargetKeys, setTopicTargetKeys] = useState([]);
  const [connectClusters, setConnectClusters] = useState<{ label: string; value: number }[]>([]);
  const [sourcekafkaClusters, setSourcekafkaClusters] = useState<{ label: string; value: number }[]>([]);
  const [headSourcekafkaClusters, setHeadSourcekafkaClusters] = useState<any[]>([]);
  const [formItemValues, setFormItemValues] = useState([]);
  const [formItemValue, setFormItemValue] = useState<any>({});
  const [sourcekafkaClustersId, setSourcekafkaClustersId] = useState(null);

  const topicChange = (val: any) => {
    setTopicTargetKeys(val);
  };

  // 获取Connect基本信息列表
  const getConnectClustersList = () => {
    Utils.request(api.getConnectClusters(clusterId)).then((res: any) => {
      const arr = res.map(({ name, id }: any) => ({
        label: name || '-',
        value: id,
      }));
      setConnectClusters(arr);
    });
  };

  // 获取 Source Kafka 集群列表
  const getSourceKafkaClustersList = () => {
    Utils.request(api.getSourceKafkaClusterBasic).then((res: any) => {
      setHeadSourcekafkaClusters(res || []);
      const arr = res
        .filter((o: any) => o.id !== +clusterId)
        .map(({ name, id }: any) => ({
          label: name || '-',
          value: id,
        }));
      setSourcekafkaClusters(arr);
    });
  };

  // 获取Topic列表
  const getTopicList = (givenSourceKafkaId: any) => {
    // ! 需整理
    setTopicDataLoading(true);
    Utils.request(api.getTopicMetaList(Number(givenSourceKafkaId)))
      .then((res: any) => {
        const dataDe = res || [];
        const dataHandle = dataDe.map((item: any) => {
          return {
            ...item,
            key: item.topicName,
            label: item.topicName,
            value: item.topicName,
            title: item.topicName,
          };
        });
        setTopicData(dataHandle);
      })
      .finally(() => {
        setTopicDataLoading(false);
      });
  };

  const getMM2Config = (connectClusterId: string | number) => {
    Promise.all(
      [
        Utils.request(api.getConnectorPluginConfig(connectClusterId, 'org.apache.kafka.connect.mirror.MirrorSourceConnector')),
        Utils.request(api.getConnectorPluginConfig(connectClusterId, 'org.apache.kafka.connect.mirror.MirrorCheckpointConnector')),
        isEdit ? Utils.request(api.getMirrorMakerConfig(connectClusterId, detail.connectorName)) : undefined,
      ].filter((r) => r)
    )
      .then((res: any) => {
        const detailConfigs: any[] = isEdit && res.length > 1 ? res?.[2] : [];
        let sourceConfigs: any;
        let checkpointConfigs: any;
        detailConfigs?.forEach((config) => {
          if (config['connector.class'] === 'org.apache.kafka.connect.mirror.MirrorCheckpointConnector') {
            checkpointConfigs = config;
            setCheckoutPointDetailConfigs(config);
          } else if (config['connector.class'] === 'org.apache.kafka.connect.mirror.MirrorSourceConnector') {
            sourceConfigs = config;
            setSourceDetailConfigs(config);
            setGivenSourceKafkaId(sourceConfigs['source.cluster.alias']);
          }
        });
        const formItemValue: any = {};
        const formItemValues: any = [];
        res?.[0].configs.forEach(({ definition }: any) => {
          if (existConfigItems.sourceConfigs.includes(definition.name)) {
            if (isEdit && sourceConfigs[definition.name]) {
              if (definition.name === 'topics') {
                formItemValue[definition.name] = topicTargetKeys || sourceConfigs[definition.name];
                if (sourceConfigs[definition.name] === '.*' && topicTargetKeys.length < 1) {
                  formItemValues.push({
                    name: 'priority',
                    value: 'allTopic',
                  });
                } else {
                  formItemValues.push(
                    {
                      name: 'topics',
                      value: topicTargetKeys.length > 0 ? topicTargetKeys : sourceConfigs[definition.name].split(','),
                    },
                    { name: 'priority', value: 'givenTopic' }
                  );
                  topicTargetKeys.length < 1 && setTopicTargetKeys(sourceConfigs[definition.name].split(','));
                }
              } else {
                formItemValue[definition.name] = sourceConfigs[definition.name];
                formItemValues.push({
                  name: definition.name,
                  value: sourceConfigs[definition.name] || null,
                });
              }
            } else {
              if (definition.name === 'topics') {
                formItemValue['topics'] = topicTargetKeys || definition.defaultValue.split(',');
                definition.defaultValue === '.*' && topicTargetKeys.length < 1
                  ? formItemValues.push({
                      name: 'priority',
                      value: 'allTopic',
                    })
                  : formItemValues.push(
                      {
                        name: 'topics',
                        value: topicTargetKeys || definition.defaultValue,
                      },
                      { name: 'priority', value: 'givenTopic' }
                    );
              } else {
                formItemValue[definition.name] = definition.defaultValue;
                formItemValues.push({
                  name: definition.name,
                  value: definition.defaultValue || null,
                });
              }
            }
          }
        });
        res?.[1].configs.forEach(({ definition }: any) => {
          if (existConfigItems.checkpointConfig.includes(definition.name)) {
            if (isEdit && checkpointConfigs[definition.name]) {
              formItemValue[definition.name] = checkpointConfigs[definition.name];
              formItemValues.push({
                name: definition.name,
                value: checkpointConfigs[definition.name] || null,
              });
            } else {
              formItemValue[definition.name] = definition.defaultValue;
              formItemValues.push({
                name: definition.name,
                value: definition.defaultValue || null,
              });
            }
            // formItemValue[definition.name] =
            //   definition.type === 'BOOLEAN' ? Boolean(definition.defaultValue) : definition.defaultValue || null;
            // formItemValues.push({
            //   name: definition.name,
            //   value: definition.type === 'BOOLEAN' ? Boolean(definition.defaultValue) : definition.defaultValue || null,
            // });
          }
        });
        setFormItemValue(formItemValue);
        setFormItemValues(formItemValues);
      })
      .finally(() => props.setSubmitLoading(false));
  };

  useEffect(() => {
    getConnectClustersList();
    getSourceKafkaClustersList();
  }, []);

  useEffect(() => {
    getTopicList(givenSourceKafkaId);
  }, [givenSourceKafkaId]);

  useEffect(() => {
    form.resetFields(existConfigItems.sourceConfigs);
    form.setFields(formItemValues);
  }, [formItemValues]);

  useEffect(() => {
    const bootstrapServers =
      headSourcekafkaClusters.length && headSourcekafkaClusters.find((item) => item.id === sourcekafkaClustersId)?.bootstrapServers;
    setBootstrapServers(bootstrapServers);
  }, [sourcekafkaClustersId]);

  useEffect(() => {
    form.setFieldsValue({ topics: topicTargetKeys });
  }, [topicTargetKeys]);
  // useEffect(() => {
  //   connectorConfig &&
  //     form.setFieldsValue({
  //       topics:
  //         typeof connectorConfig['topics'] === 'string' ? connectorConfig['topics'].split(',').map((i: string) => i.trim()) : undefined,
  //     });
  // }, [topicData, connectorConfig]);

  useEffect(() => {
    // 需要处理Topic和config配置，还需要考虑编辑时的回显问题
    isEdit && getMM2Config(detail.connectClusterId);
    const config = isEdit
      ? detail
      : {
          'sync.topic.configs.enabled': false,
          'sync.topic.configs.interval.seconds': 0,
          'sync.topic.acls.enabled': false,
          'sync.topic.acls.interval.seconds': 600,
          'refresh.topics.enabled': false,
          'refresh.topics.interval.seconds': 600,
          'refresh.groups.enabled': false,
          'refresh.groups.interval.seconds': 600,
          'emit.checkpoints.enabled': false,
          'emit.checkpoints.interval.seconds': 60,
          'checkpoints.topic.replication.factor': false,
          'replication.policy.separator': '.',
        };
    form.setFieldsValue(config);
    setFormItemValue((state: any) => {
      return { ...state, ...config };
    });
  }, [isEdit, detail]);

  const onConnectClusterChange = (value: string) => {
    value && getMM2Config(value);
  };

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="MM2任务名称"
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
                  return Promise.reject('MM2任务名称不能为空');
                }
                if (value?.length > 64) {
                  return Promise.reject('MM2任务名称长度限制在1～64字符');
                }
                if (!new RegExp(regClusterName).test(value)) {
                  return Promise.reject("MM2 名称支持中英文、数字、特殊字符 ! # $ % & ' ( ) * + , - . / : ; < = > ? @ [  ] ^ _ ` { | } ~");
                }
                return Promise.resolve();
                // return Utils.request(api.isConnectorExist(prevForm.getFieldValue('connectClusterId'), value)).then(
                //   (res: any) => {
                //     const data = res || {};
                //     return data?.exist ? Promise.reject('MM2 名称重复') : Promise.resolve();
                //   },
                //   () => Promise.reject('连接超时! 请重试或检查服务')
                // );
              },
            },
          ]}
        >
          <Input placeholder="请输入 MM2 名称" disabled={isEdit} />
        </Form.Item>
        <Form.Item
          name="sourceKafkaClusterId"
          label="Source Kafka 集群"
          tooltip="Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the keys in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro."
          rules={[{ required: true }]}
        >
          <Select
            placeholder="请选择 Source Kafka 集群"
            options={sourcekafkaClusters}
            showSearch
            filterOption={(input: any, option: any) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
            onChange={(e, option: any) => {
              setSourcekafkaClustersId(e);
              setSourceKafkaClusterId(option?.value);
              setGivenSourceKafkaId(option?.value);
            }}
          />
        </Form.Item>
        <Form.Item label="选择Topic" name="priority" initialValue="allTopic" rules={[{ required: true }]}>
          <Radio.Group>
            {/* 处理应用于所有Topic的字段 */}
            <Radio value="allTopic">应用于所有Topic</Radio>
            <Radio value="givenTopic">应用于特定Topic</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item className="clear-topic-minHeight" dependencies={['priority']} style={{ marginBottom: 0, minHeight: 0 }}>
          {({ getFieldValue }) => {
            return getFieldValue('priority') === 'givenTopic' ? (
              <Form.Item
                name="topics"
                label=""
                rules={[
                  {
                    required: false,
                    message: `请选择!`,
                  },
                ]}
              >
                <Spin spinning={topicDataLoading}>
                  <Transfer
                    dataSource={topicData}
                    titles={['待选Topic', '已选Topic']}
                    customHeader
                    showSelectedCount
                    locale={{
                      itemUnit: '',
                      itemsUnit: '',
                    }}
                    showSearch
                    filterOption={(inputValue, option) => option.topicName.indexOf(inputValue) > -1}
                    targetKeys={topicTargetKeys}
                    onChange={topicChange}
                    render={(item) => item.title}
                    suffix={<IconFont type="icon-fangdajing" />}
                  />
                </Spin>
              </Form.Item>
            ) : null;
          }}
        </Form.Item>
        <Form.Item
          name="connectClusterId"
          label="Connect 集群"
          tooltip="Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the keys in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro."
          rules={[{ required: true }]}
        >
          <Select onChange={onConnectClusterChange} allowClear placeholder="请选择 Connect 集群" options={connectClusters} />
        </Form.Item>
        {!seniorConfig && (
          <div className="add-mm2-config-title" onClick={() => setSeniorConfig(true)}>
            <span className="add-mm2-config-title-text">高级配置</span>
            <IconFont className="add-mm2-config-title-icon" type="icon-a-xialaIcon" />
          </div>
        )}
        <div style={{ display: seniorConfig ? 'block' : 'none' }}>
          <div className="add-mm2-flex-layout">
            <div className="add-mm2-flex-layout-item">
              <Form.Item
                className="senior-config-left"
                name="sync.topic.configs.enabled"
                label="sync.topic.configs.enabled"
                tooltip="whether to replicate topic configurations from the source cluster."
                labelAlign="right"
                labelCol={{ span: 19 }}
                valuePropName="checked"
              >
                <Switch
                  size="small"
                  onChange={(e) =>
                    setFormItemValue((state: any) => {
                      return { ...state, 'sync.topic.configs.enabled': e };
                    })
                  }
                />
              </Form.Item>
              {formItemValue['sync.topic.configs.enabled'] ? (
                <Form.Item
                  name="sync.topic.configs.interval.seconds"
                  label="sync.topic.configs.interval.seconds"
                  labelAlign="right"
                  labelCol={{ span: 17 }}
                >
                  <InputNumber defaultValue={600} size="small" min={1} max={99999} />
                </Form.Item>
              ) : null}
            </div>
            <div className="add-mm2-flex-layout-item">
              <Form.Item
                className="senior-config-left"
                name="sync.topic.acls.enabled"
                label="sync.topic.acls.enabled"
                tooltip="whether to sync ACLs from the source cluster."
                labelAlign="right"
                labelCol={{ span: 19 }}
                valuePropName="checked"
              >
                <Switch
                  size="small"
                  onChange={(e) =>
                    setFormItemValue((state: any) => {
                      return { ...state, 'sync.topic.acls.enabled': e };
                    })
                  }
                />
              </Form.Item>
              {formItemValue['sync.topic.acls.enabled'] ? (
                <Form.Item
                  name="sync.topic.acls.interval.seconds"
                  label="sync.topic.acls.interval.seconds"
                  labelAlign="right"
                  labelCol={{ span: 17 }}
                >
                  <InputNumber defaultValue={600} size="small" min={1} max={99999} />
                </Form.Item>
              ) : null}
            </div>
            <div className="add-mm2-flex-layout-item">
              <Form.Item
                className="senior-config-left"
                name="refresh.topics.enabled"
                label="refresh.topics.enabled"
                tooltip="whether to check for new topics in the source cluster periodically."
                labelAlign="right"
                labelCol={{ span: 19 }}
                valuePropName="checked"
              >
                <Switch
                  size="small"
                  onChange={(e) =>
                    setFormItemValue((state: any) => {
                      return { ...state, 'refresh.topics.enabled': e };
                    })
                  }
                />
              </Form.Item>
              {formItemValue['refresh.topics.enabled'] ? (
                <Form.Item
                  name="refresh.topics.interval.seconds"
                  label="refresh.topics.interval.seconds"
                  tooltip="frequency of checking for new topics in the source cluster; lower values than the default may lead to performance degradation"
                  labelAlign="right"
                  labelCol={{ span: 17 }}
                >
                  <InputNumber defaultValue={600} size="small" min={1} max={99999} />
                </Form.Item>
              ) : null}
            </div>
            <div className="add-mm2-flex-layout-item">
              <Form.Item
                className="senior-config-left"
                name="refresh.groups.enabled"
                label="refresh.groups.enabled"
                tooltip="whether to check for new consumer groups in the source cluster periodically."
                labelAlign="right"
                labelCol={{ span: 19 }}
                valuePropName="checked"
              >
                <Switch
                  size="small"
                  onChange={(e) =>
                    setFormItemValue((state: any) => {
                      return { ...state, 'refresh.groups.enabled': e };
                    })
                  }
                />
              </Form.Item>
              {formItemValue['refresh.groups.enabled'] ? (
                <Form.Item
                  name="refresh.groups.interval.seconds"
                  label="refresh.groups.interval.seconds"
                  tooltip="frequency of checking for new consumer groups in the source cluster; lower values than the default may lead to performance degradation"
                  labelAlign="right"
                  labelCol={{ span: 17 }}
                >
                  <InputNumber defaultValue={600} size="small" min={1} max={99999} />
                </Form.Item>
              ) : null}
            </div>
            <div className="add-mm2-flex-layout-item">
              <Form.Item
                className="senior-config-left"
                name="emit.checkpoints.enabled"
                label="emit.checkpoints.enabled"
                tooltip="whether to emit MirrorMaker's consumer offsets periodically."
                labelAlign="right"
                labelCol={{ span: 19 }}
                valuePropName="checked"
              >
                <Switch
                  size="small"
                  onChange={(e) =>
                    setFormItemValue((state: any) => {
                      return { ...state, 'emit.checkpoints.enabled': e };
                    })
                  }
                />
              </Form.Item>
              {formItemValue['emit.checkpoints.enabled'] ? (
                <div>
                  <Form.Item
                    name="emit.checkpoints.interval.seconds"
                    label="emit.checkpoints.interval.seconds"
                    tooltip="frequency at which checkpoints are emitted"
                    labelAlign="right"
                    labelCol={{ span: 17 }}
                  >
                    <InputNumber defaultValue={60} size="small" min={1} max={99999} />
                  </Form.Item>
                  <Form.Item
                    name="checkpoints.topic.replication.factor"
                    label="checkpoints.topic.replication.factor"
                    tooltip="replication factor of MirrorMaker's internal checkpoints topics"
                    labelAlign="right"
                    labelCol={{ span: 17 }}
                  >
                    <InputNumber defaultValue={3} size="small" min={1} max={99999} />
                  </Form.Item>
                </div>
              ) : null}
            </div>
          </div>
          <Form.Item
            name="replication.policy.class"
            label="replication.policy.class"
            tooltip="Behavior for tolerating errors during connector operation. 'none' is the default value and signals that any error will result in an immediate connector task failure; 'all' changes the behavior to skip over problematic records."
          >
            <Select
              options={[
                {
                  value: 'org.apache.kafka.connect.mirror.DefaultReplicationPolicy',
                  label: 'org.apache.kafka.connect.mirror.DefaultReplicationPolicy',
                },
                {
                  value: 'org.apache.kafka.connect.mirror.IdentityReplicationPolicy',
                  label: 'org.apache.kafka.connect.mirror.IdentityReplicationPolicy',
                },
              ]}
            />
          </Form.Item>
          <Form.Item
            name="replication.policy.separator"
            label="replication.policy.separator"
            rules={[{ required: false }]}
            normalize={(value) => value || null}
          >
            <Input defaultValue="." placeholder="请输入 Key converter class" />
          </Form.Item>
        </div>
        {seniorConfig && (
          <div className="add-mm2-config-title" onClick={() => setSeniorConfig(false)}>
            <span className="add-mm2-config-title-text">收起</span>
            <IconFont type="icon-a-xialaIcon" />
          </div>
        )}
      </Form>
    </div>
  );
};

// 步骤二
const StepFormSecond = (props: SubFormProps) => {
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [form] = useStepForm(1);
  const [firstForm] = useStepForm(0);
  const connectClusterId = firstForm.getFieldValue('connectClusterId');
  const [activeKey, setActiveKey] = useState([]);
  const { type, detail, errors, setHeartbeatDetailConfigs } = useContext(StepsFormContent);
  const isEdit = type === 'edit';
  const [groupOffset, setGroupOffset] = useState(false); // 控制是否同步开关
  const [heartbeat, setHeartbeat] = useState(true); // 控制是否同步开关
  const [groupLoading, setGroupLoading] = useState(false);
  const [groupBasicData, setGroupBasicData] = useState([]);

  const [formItemValues, setFormItemValues] = useState([]);
  const [formItemValue, setFormItemValue] = useState<any>({});
  const columns = [
    {
      title: 'ConsumerGroup',
      dataIndex: 'groupName',
      key: 'groupName',
      width: 200,
      lineClampOne: true,
      needTooltip: true,
    },
    {
      title: 'Topic',
      dataIndex: 'topicName',
      key: 'topicName',
      width: 200,
      lineClampOne: true,
      needTooltip: true,
    },
    {
      title: '状态',
      dataIndex: 'state',
      key: 'state',
      width: 60,
      lineClampOne: true,
      needTooltip: true,
    },
  ];

  const getGroupBasicData = () => {
    setGroupLoading(true);
    Utils.request(api.getGroupBasic(clusterId))
      .then((res: any) => {
        setGroupBasicData(res || []);
      })
      .finally(() => {
        setGroupLoading(false);
      });
  };

  const getMM2Config = async (connectClusterId: string | number) => {
    const formItemValue: any = {};
    const formItemValues: any = [];
    const result: any = await Utils.request(
      api.getConnectorPluginConfig(connectClusterId, 'org.apache.kafka.connect.mirror.MirrorHeartbeatConnector')
    );
    const editResult: any = isEdit ? await Utils.request(api.getMirrorMakerConfig(connectClusterId, detail.connectorName)) : undefined;
    let heartbeatConfigs: any;
    editResult?.forEach((config: any) => {
      if (config['connector.class'] === 'org.apache.kafka.connect.mirror.MirrorCheckpointConnector') {
        heartbeatConfigs = config;
        setHeartbeatDetailConfigs(config);
      }
    });
    result?.configs.forEach(({ definition }: any) => {
      if (existConfigItems.heartbeatConfig.includes(definition.name)) {
        if (isEdit && heartbeatConfigs[definition.name]) {
          formItemValue[definition.name] = heartbeatConfigs[definition.name];
          formItemValues.push({
            name: definition.name,
            value: heartbeatConfigs[definition.name] || null,
          });
        } else {
          formItemValue[definition.name] = definition.defaultValue;
          formItemValues.push({
            name: definition.name,
            value: definition.defaultValue || null,
          });
        }
        // formItemValue[definition.name] = definition.type === 'BOOLEAN' ? Boolean(definition.defaultValue) : definition.defaultValue || null;
        // formItemValues.push({
        //   name: definition.name,
        //   value: definition.type === 'BOOLEAN' ? Boolean(definition.defaultValue) : definition.defaultValue || null,
        // });
      }
    });
    if (formItemValue['heartbeats.topic.replication.factor'] || formItemValue['emit.heartbeats.interval.seconds']) {
      formItemValue['heartbeat'] = true;
      formItemValues.push({
        name: 'heartbeat',
        value: true,
      });
    }
    setFormItemValue(formItemValue);
    setFormItemValues(formItemValues);
  };

  useEffect(() => {
    form.resetFields(existConfigItems.sourceConfigs);
    form.setFields(formItemValues);
  }, [formItemValues]);

  useEffect(() => {
    connectClusterId
      ? getMM2Config(connectClusterId)
      : form.setFieldsValue({
          groupOffset: false,
          heartbeat: true,
          'offset-syncs.topic.replication.factor': 3,
          'sync.group.offsets.interval.seconds': 60,
          'heartbeats.topic.replication.factor': 3,
          'emit.heartbeats.interval.seconds': 1,
        });
  }, [connectClusterId]);

  useEffect(() => {
    groupOffset && getGroupBasicData();
  }, [groupOffset]);

  return (
    <div style={{ display: props.visible ? 'block' : 'none' }}>
      <Form form={form} layout="horizontal" colon={false}>
        <Form.Item
          className="custom-form-item-36"
          name="groupOffset"
          label="是否同步 Group Offset"
          tooltip="whether to check for new consumer groups in the source cluster periodically."
          valuePropName="checked"
        >
          <Switch onChange={(e) => setGroupOffset(e)} size="small" />
        </Form.Item>
        {groupOffset && (
          <div className="custom-form-item-27">
            <Form.Item name="offset-syncs.topic.replication.factor" label="offset-syncs.topic.replication.factor">
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
            <Form.Item name="sync.group.offsets.interval.seconds" label="sync.group.offsets.interval.seconds">
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
            <Table
              className="group-offset-table"
              rowKey={'key'}
              pagination={false}
              columns={columns}
              dataSource={groupBasicData}
              loading={groupLoading}
            />
          </div>
        )}
        <Form.Item
          className="custom-form-item-36"
          name="heartbeat"
          label="是否开启心跳"
          tooltip="whether to check for new consumer groups in the source cluster periodically."
          valuePropName="checked"
        >
          <Switch
            onChange={(e) =>
              setFormItemValue((state: any) => {
                return { ...state, heartbeat: e };
              })
            }
            size="small"
          />
        </Form.Item>
        {formItemValue['heartbeat'] ? (
          <div className="custom-form-item-27">
            <Form.Item name="heartbeats.topic.replication.factor" label="heartbeats.topic.replication.factor" labelCol={{ span: 9 }}>
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
            <Form.Item name="emit.heartbeats.interval.seconds" label="emit.heartbeats.interval.seconds" labelCol={{ span: 9 }}>
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
          </div>
        ) : null}
        {/* {heartbeat && (
          <div className="custom-form-item-27">
            <Form.Item name="heartbeats.topic.replication.factor" label="heartbeats.topic.replication.factor" labelCol={{ span: 9 }}>
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
            <Form.Item name="emit.heartbeats.interval.seconds" label="emit.heartbeats.interval.seconds" labelCol={{ span: 9 }}>
              <InputNumber min={1} max={99999} size="small" />
            </Form.Item>
          </div>
        )} */}
      </Form>
    </div>
  );
};

const steps = [
  {
    title: '步骤一',
    content: StepFormFirst,
  },
  {
    title: '步骤二',
    content: StepFormSecond,
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
    const [sourceKafkaClusterId, setSourceKafkaClusterId] = useState('');
    const [bootstrapServers, setBootstrapServers] = useState(null);
    const [sourceDetailConfigs, setSourceDetailConfigs] = useState<any>({});
    const [checkoutPointDetailConfigs, setCheckoutPointDetailConfigs] = useState<any>({});
    const [heartbeatDetailConfigs, setHeartbeatDetailConfigs] = useState<any>({});
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
        success?:
          | {
              connectClusterId: number;
              connectorName: string;
              configs: {
                [key: string]: any;
              };
              sourceKafkaClusterId: number;
              heartbeatConnectorConfigs: {
                [key: string]: any;
              };
              checkpointConnectorConfigs: {
                [key: string]: any;
              };
            }
          | any;
        error?: any;
      }) => void
    ) => {
      const promises: Promise<any>[] = [];
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
            ...res[0],
            ...res[1],
            // ...res[4],
          };
          const { detail } = operateInfo as any;
          const checkpointConnectorConfigs = result['emit.checkpoints.enabled'] && {
            ...checkoutPointDetailConfigs,
            'connector.class': 'org.apache.kafka.connect.mirror.MirrorCheckpointConnector',
            'emit.checkpoints.enabled': result['emit.checkpoints.enabled'],
            'emit.checkpoints.interval.seconds': result['emit.checkpoints.interval.seconds'],
            'checkpoints.topic.replication.factor': result['checkpoints.topic.replication.factor'],
            'source.cluster.alias': sourceKafkaClusterId || res[0].sourceKafkaClusterId,
            name: detail?.checkpointConnector || result.name,
            'source.cluster.bootstrap.servers': bootstrapServers || checkoutPointDetailConfigs?.['source.cluster.bootstrap.servers'],
          };
          const heartbeatConnectorConfigs = result['heartbeat'] && {
            ...heartbeatDetailConfigs,
            'connector.class': 'org.apache.kafka.connect.mirror.MirrorHeartbeatConnector',
            'heartbeats.topic.replication.factor': result['heartbeats.topic.replication.factor'],
            'emit.heartbeats.interval.seconds': result['emit.heartbeats.interval.seconds'],
            'source.cluster.alias': sourceKafkaClusterId || res[0].sourceKafkaClusterId,
            name: detail?.heartbeatConnector || result.name,
            'source.cluster.bootstrap.servers': bootstrapServers || heartbeatDetailConfigs?.['source.cluster.bootstrap.servers'],
          };
          const configs = {
            ...sourceDetailConfigs,
            'connector.class': 'org.apache.kafka.connect.mirror.MirrorSourceConnector',
            'sync.topic.configs.enabled': result['sync.topic.configs.enabled'],
            'sync.topic.configs.interval.seconds': result['sync.topic.configs.interval.seconds'],

            'sync.topic.acls.enabled': result['sync.topic.acls.enabled'],
            'sync.topic.acls.interval.seconds': result['sync.topic.acls.interval.seconds'],

            'refresh.topics.enabled': result['refresh.topics.enabled'],
            'refresh.topics.interval.seconds': result['refresh.topics.interval.seconds'],

            'refresh.groups.enabled': result['refresh.groups.enabled'],
            'refresh.groups.interval.seconds': result['refresh.groups.interval.seconds'],
            'replication.policy.class': result['replication.policy.class'],
            'replication.policy.separator': result['replication.policy.separator'],
            topics: result['priority'] === 'givenTopic' ? result['topics'].join() : '.*',
            'source.cluster.alias': sourceKafkaClusterId || res[0].sourceKafkaClusterId,
            name: result.name,
            'source.cluster.bootstrap.servers': bootstrapServers || sourceDetailConfigs?.['source.cluster.bootstrap.servers'],
          };
          // topics 配置格式化
          // res[1].topics && (result.topics = (res[1].topics as string[]).join(', '));
          // // transforms 配置格式化
          // res[2].transforms &&
          //   (res[2].transforms as string)
          //     .split('\n')
          //     .filter((l) => l)
          //     .forEach((l) => {
          //       const [k, ...v] = l.split('=');
          //       result[k] = v.join('=');
          //     });
          callback({
            success: {
              connectClusterId: res[0].connectClusterId,
              connectorName: result['name'],
              sourceKafkaClusterId: res[0].sourceKafkaClusterId,
              configs,
              heartbeatConnectorConfigs: heartbeatConnectorConfigs || undefined,
              checkpointConnectorConfigs: checkpointConnectorConfigs || undefined,
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
          (jsonRef as any)?.onOpen(operateInfo.type, info.success);
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
          Object.entries(info.success).forEach(([key, val]) => {
            if (val === null) {
              delete info.success[key];
            }
          });
          Utils.put(api.validateMM2Config, info.success).then(
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
                  // const items = getExistFormItems(stepsFormRef.current[0].getFieldValue('connectorType'));
                  // const keys = Object.keys(errors).filter((key) => items.includes(key));
                  // let jumpStep = 4;
                  // keys.forEach((key) => {
                  //   Object.values(existFormItems).some((items, i) => {
                  //     if (items.includes(key)) {
                  //       jumpStep > i + 1 && (jumpStep = i + 1);
                  //       return true;
                  //     }
                  //     return false;
                  //   });
                  // });
                  // setCurrentStep(jumpStep);
                  setSubmitLoading(false);
                  message.warning('字段校验失败，请检查');
                } else {
                  if (operateInfo.type === 'create') {
                    Utils.post(api.mirrorMakerOperates, info.success)
                      .then(() => {
                        message.success('新建成功');
                        onClose();
                        props?.refresh();
                      })
                      .finally(() => setSubmitLoading(false));
                  } else {
                    Utils.put(api.updateMM2Config, info.success)
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
        title={`${operateInfo.type === 'create' ? '新增' : '编辑'} MM2`}
        className="operate-connector-drawer"
        width={700}
        visible={visible}
        onClose={onClose}
        destroyOnClose
      >
        {operateInfo.type && visible && (
          <>
            <Steps className="mirror-maker-steps" current={currentStep} labelPlacement="vertical" onChange={(cur) => turnTo(cur)}>
              {steps.map(({ title }) => (
                <Step key={title} title={title} />
              ))}
            </Steps>
            <div style={{ padding: '48px 18px 0px' }}>
              <StepsFormContent.Provider
                value={{
                  ...operateInfo,
                  forms: stepsFormRef,
                  setSourceKafkaClusterId,
                  setBootstrapServers,
                  setSourceDetailConfigs,
                  setCheckoutPointDetailConfigs,
                  setHeartbeatDetailConfigs,
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
