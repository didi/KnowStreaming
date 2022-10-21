import React, { useState, useEffect, useRef } from 'react';
import { Utils, Drawer, Button, Form, Space, Divider, AppContainer, Radio, InputNumber, Transfer, Select, Tooltip } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import CronInput from './CronInput';
import BalanceEditTable from './BalanceEditTable';
import PlanDrawer from './PlanDrawer';
import api from '../../api';
import './style/BalanceDrawer.less';
interface PropsType {
  onClose: Function;
  visible: boolean;
  isCycle?: boolean;
  formData?: any;
  genData?: any;
}

const IndexCalculations = [
  {
    label: '近5mins',
    value: 5 * 60,
  },
  {
    label: '近10mins',
    value: 10 * 60,
  },
  {
    label: '近30mins',
    value: 30 * 60,
  },
  {
    label: '近1h',
    value: 60 * 60,
  },
  {
    label: '近6h',
    value: 6 * 60 * 60,
  },
  {
    label: '近12h',
    value: 12 * 60 * 60,
  },
  {
    label: '近24h',
    value: 24 * 60 * 60,
  },
];

const BalancedDimensions = [
  // 本期没有CPU
  // {
  //   label: 'CPU',
  //   value: 'cpu',
  // },
  {
    label: 'Disk',
    value: 'disk',
  },
  {
    label: 'BytesIn',
    value: 'bytesIn',
  },
  {
    label: 'BytesOut',
    value: 'bytesOut',
  },
];

const Schedules1 = [
  {
    label: '每天',
    value: 1,
  },
  {
    label: '每周',
    value: 2,
  },
  {
    label: '每月',
    value: 3,
  },
];

const SchedulesWeeks = [
  {
    label: '每周一',
    value: 1,
  },
  {
    label: '每周二',
    value: 2,
  },
  {
    label: '每周三',
    value: 3,
  },
  {
    label: '每周四',
    value: 4,
  },
  {
    label: '每周五',
    value: 5,
  },
  {
    label: '每周六',
    value: 6,
  },
  {
    label: '每周日',
    value: 7,
  },
];

const SchedulesMouths: any[] = [];
for (let i = 1; i <= 28; i++) {
  SchedulesMouths.push({
    label: `每月${i}日`,
    value: i,
  });
}

const BalanceDrawer: React.FC<PropsType> = ({ onClose, visible, isCycle = false, formData, genData }) => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const customFormRef = useRef<any>();
  const [nodeData, setNodeData] = useState([]);
  const [nodeTargetKeys, setNodeTargetKeys] = useState([]);
  const [topicData, setTopicData] = useState([]);
  const [topicTargetKeys, setTopicTargetKeys] = useState([]);
  const [tableData, setTableData] = useState<any[]>([]);
  const [dimension, setDimension] = useState<string[]>(['disk', 'bytesIn', 'bytesOut']);

  const [planVisible, setPlanVisible] = useState<boolean>(false);
  const [planDetailData, setPlanDetailData] = useState({});
  const [parallelNum, setParallelNum] = useState(0);
  const [executionStrategy, setExecutionStrategy] = useState(1);
  useEffect(() => {
    getNodeList();
    getTopicList();
  }, []);

  useEffect(() => {
    init();
  }, [visible, formData]);

  useEffect(() => {
    setNodeTargetKeys(formData?.brokers || []);
  }, [nodeData]);

  useEffect(() => {
    setTopicTargetKeys(formData?.topicBlackList || []);
  }, [topicData]);

  const init = () => {
    if (formData && Object.keys(formData).length > 0) {
      const tableData = formData?.clusterBalanceIntervalList?.map((item: any) => {
        const finfIndex = BalancedDimensions.findIndex((item1) => item1?.value === item?.type);
        return {
          ...item,
          name: BalancedDimensions[finfIndex]?.label,
        };
      });
      setTableData(tableData || []);
      const dimension = formData?.clusterBalanceIntervalList?.map((item: any) => {
        return item?.type;
      });

      form.setFieldsValue({
        ...formData,
        brokers: formData?.brokers || [],
        topicBlackList: formData?.topicBlackList || [],
        dimension,
        throttleUnitM: formData?.throttleUnitB / 1024 / 1024,
      });
    } else {
      const defaultDimension = ['disk', 'bytesIn', 'bytesOut'];
      form.resetFields();
      form.setFieldsValue({ dimension: defaultDimension, metricCalculationPeriod: 600 });
      const res = defaultDimension?.map((item, index) => {
        const finfIndex = BalancedDimensions.findIndex((item1) => item1.value === item);
        return {
          type: item,
          name: BalancedDimensions[finfIndex]?.label,
          intervalPercent: 10,
          priority: index + 1,
        };
      });
      setTableData(res);
      setDimension(['disk', 'bytesIn', 'bytesOut']);
      setNodeTargetKeys([]);
      setTopicTargetKeys([]);
    }
  };
  const submit = () => {
    // 周期均衡 / 立即均衡
    customFormRef.current.editTableValidate().then((res: any) => {
      form.validateFields().then((values) => {
        const params = {
          ...values,
          clusterId: global?.clusterInfo?.id,
          clusterBalanceIntervalList: tableData,
          scheduleJob: isCycle || false,
          throttleUnitB: values?.throttleUnitM * 1024 * 1024,
        };

        if (values?.priority === 'throughput') {
          params.parallelNum = 0;
          params.executionStrategy = 1;
        } else if (values?.priority === 'stability') {
          params.parallelNum = 1;
          params.executionStrategy = 2;
        }

        if (formData?.jobId) {
          const handledData = {
            creator: JSON.parse(global?.userInfo)?.userName,
            jobType: 2, // type 0 topic迁移 1 扩缩容 2集群均衡
            planTime: formData?.record?.planTime,
            jobStatus: formData?.record?.jobStatus || 2, //status 2 创建
            target: formData?.record?.target,
            id: formData?.record?.id,
            jobDesc: formData?.record?.description || '',
            jobData: JSON.stringify({ ...formData?.jobData, ...params }),
          };
          Utils.put(api.putJobsTaskData(global?.clusterInfo?.id), handledData)
            .then(() => {
              message.success('集群均衡任务编辑成功');
              drawerClose(true);
              setPlanVisible(false);
              genData();
            })
            .catch((err: any) => {
              console.log(err, 'err');
            });
        } else {
          Utils.request(api.balanceStrategy(global?.clusterInfo?.id), {
            method: 'POST',
            data: params,
          }).then((res: any) => {
            const dataDe = res || [];
            message.success(isCycle ? '成功创建周期均衡策略' : '成功创建立即均衡策略');
            drawerClose(true);
            setPlanVisible(false);
            isCycle && genData();
          });
        }
      });
    });
  };

  const preview = () => {
    // 立即均衡
    customFormRef.current.editTableValidate().then((res: any) => {
      form.validateFields().then((values) => {
        const params = {
          ...values,
          clusterId: global?.clusterInfo?.id,
          clusterBalanceIntervalList: tableData,
          scheduleJob: isCycle || false,
          throttleUnitB: values?.throttleUnitM * 1024 * 1024,
          // parallelNum: !isCycle ? values?.priority === 'throughput'? 0 :  : null
        };

        if (!isCycle) {
          if (values?.priority === 'throughput') {
            params.parallelNum = 0;
            params.executionStrategy = 1;
          } else if (values?.priority === 'stability') {
            params.parallelNum = 1;
            params.executionStrategy = 2;
          }
        }

        // 预览计划
        if (formData?.jobId) {
          Utils.request(api.getBalancePlan(global?.clusterInfo?.id, formData?.jobId), {
            method: 'GET',
          }).then((res: any) => {
            const dataDe = res || {};
            setPlanDetailData(dataDe);
            setPlanVisible(true);
          });
        } else {
          Utils.request(api.balancePreview(global?.clusterInfo?.id), {
            method: 'POST',
            data: params,
          }).then((res: any) => {
            const dataDe = res || {};
            setPlanDetailData(dataDe);
            setPlanVisible(true);
          });
        }
      });
    });
  };

  const nodeChange = (val: any) => {
    setNodeTargetKeys(val);
  };

  const topicChange = (val: any) => {
    setTopicTargetKeys(val);
  };

  const getNodeList = () => {
    Utils.request(api.getBrokersMetaList(global?.clusterInfo?.id), {
      method: 'GET',
    }).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.brokerId,
          title: `${item.brokerId} (${item.host})`,
        };
      });
      setNodeData(dataHandle);
    });
  };
  const getTopicList = () => {
    Utils.request(api.getTopicMetaList(global?.clusterInfo?.id), {
      method: 'GET',
    }).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.topicName,
          title: item.topicName,
        };
      });
      setTopicData(dataHandle);
    });
  };

  const dimensionChange = (val: string[]) => {
    const res = val?.map((item, index) => {
      const finfIndex = BalancedDimensions.findIndex((item1) => item1.value === item);
      const tableIndex = tableData?.findIndex((item2) => item2.type === item);
      return {
        type: item,
        name: BalancedDimensions[finfIndex]?.label,
        intervalPercent: tableIndex > -1 ? tableData[tableIndex].intervalPercent : 10,
        priority: index + 1,
      };
    });
    setTableData(res);
    setDimension(val);
  };

  const tableDataChange = (data: any[]) => {
    setTableData(data);
  };

  const planClose = () => {
    setPlanVisible(false);
    // onClose();
  };

  const balanceImmediate = () => {
    submit();
  };

  const drawerClose = (isArg?: boolean) => {
    isArg ? onClose(isArg) : onClose();
    setParallelNum(0);
    setExecutionStrategy(1);
    form.resetFields();
  };

  const priorityChange = (e: any) => {
    if (e.target.value === 'throughput') {
      setParallelNum(0);
      setExecutionStrategy(1);
    } else if (e.target.value === 'stability') {
      setParallelNum(1);
      setExecutionStrategy(2);
    } else {
      form.setFieldsValue({ parallelNum, executionStrategy });
    }
  };

  return (
    <>
      <PlanDrawer
        visible={planVisible}
        onClose={planClose}
        balanceImmediate={balanceImmediate}
        detailData={planDetailData}
        isPrevew={true}
        isEdit={formData?.jobId ? true : false}
      />
      <Drawer
        title={isCycle ? '周期均衡' : '立即均衡'}
        width="600px"
        destroyOnClose={true}
        className="balance-drawer"
        onClose={() => drawerClose()}
        visible={visible}
        maskClosable={false}
        extra={
          <Space>
            <Button size="small" onClick={() => drawerClose()}>
              取消
            </Button>
            {isCycle ? (
              <Button type="primary" size="small" disabled={false} onClick={submit}>
                确定
              </Button>
            ) : (
              <Button className="btn-width84" type="primary" size="small" disabled={false} onClick={preview}>
                预览计划
              </Button>
            )}

            <Divider type="vertical" />
          </Space>
        }
      >
        <Form
          form={form}
          layout="vertical"
          preserve={false}
          initialValues={{
            status: 1,
          }}
        >
          {/* {!isCycle && (
            <>
              <h6 className="form-title">均衡节点范围</h6>
              <Form.Item
                name="brokers"
                rules={[
                  {
                    required: true,
                    message: `请选择!`,
                  },
                ]}
              >
                <Transfer
                  dataSource={nodeData}
                  titles={['待选节点', '已选节点']}
                  customHeader
                  showSelectedCount
                  locale={{
                    itemUnit: '',
                    itemsUnit: '',
                  }}
                  showSearch
                  filterOption={(inputValue, option) => option.host.indexOf(inputValue) > -1}
                  targetKeys={nodeTargetKeys}
                  onChange={nodeChange}
                  render={(item) => item.title}
                  suffix={<IconFont type="icon-fangdajing" />}
                />
              </Form.Item>
            </>
          )} */}

          <h6 className="form-title">均衡策略</h6>
          <Form.Item
            name="metricCalculationPeriod"
            label="指标计算周期"
            rules={[
              {
                required: true,
                message: `请选择!`,
              },
            ]}
          >
            <Select placeholder={`请选择指标计算周期`} options={IndexCalculations} />
          </Form.Item>

          <Form.Item
            label="均衡维度"
            name="dimension"
            rules={[
              {
                required: true,
                message: `请选择!`,
              },
            ]}
          >
            <Select
              placeholder={`请选择均衡维度`}
              mode="multiple"
              value={dimension}
              options={BalancedDimensions}
              onChange={dimensionChange}
            />
          </Form.Item>

          <Form.Item>
            <BalanceEditTable ref={customFormRef} tableData={tableData} tableDataChange={tableDataChange} />
          </Form.Item>

          <Form.Item
            name="topicBlackList"
            label="Topic黑名单"
            rules={[
              {
                required: false,
                message: `请选择!`,
              },
            ]}
          >
            <Transfer
              dataSource={topicData}
              titles={['待选黑名单', '已选黑名单']}
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
          </Form.Item>

          <h6 className="form-title">运行配置</h6>
          {isCycle && (
            <Form.Item
              className="schedule-cron"
              name="scheduleCron"
              label="任务周期"
              rules={[
                {
                  required: true,
                  message: `请输入!`,
                },
                {
                  validator: (_, value) => {
                    const valArr = value.split(' ');
                    if (valArr[1] === '*' || valArr[2] === '*') {
                      return Promise.reject(new Error('任务周期必须指定分钟、小时'));
                    }
                    return Promise.resolve();
                  },
                },
              ]}
            >
              <CronInput />
            </Form.Item>
          )}
          <Form.Item label="" name="priority" rules={[{ required: true, message: 'Principle 不能为空' }]} initialValue="throughput">
            <Radio.Group onChange={priorityChange}>
              <Radio value="throughput">吞吐量优先</Radio>
              <Radio value="stability">稳定性优先</Radio>
              <Radio value="custom">自定义</Radio>
            </Radio.Group>
          </Form.Item>
          {
            <Form.Item dependencies={['priority']} style={{ marginBottom: 0 }}>
              {({ getFieldValue }) =>
                getFieldValue('priority') === 'custom' ? (
                  <div className="form-item-group">
                    <Form.Item
                      name="parallelNum"
                      label={
                        <span>
                          任务并行度
                          <Tooltip title="每个节点同时迁移的副本数量">
                            <IconFont style={{ fontSize: '14px', marginLeft: '5px' }} type="icon-zhushi" />
                          </Tooltip>
                        </span>
                      }
                      rules={[
                        {
                          required: true,
                          message: `请输入!`,
                        },
                      ]}
                    >
                      <InputNumber min={0} max={999} placeholder="请输入任务并行度" style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item
                      name="executionStrategy"
                      label={
                        <span>
                          执行策略
                          <Tooltip title="不同大小副本执行的顺序">
                            <IconFont style={{ fontSize: '14px', marginLeft: '5px' }} type="icon-zhushi" />
                          </Tooltip>
                        </span>
                      }
                      rules={[
                        {
                          required: true,
                          message: `请选择!`,
                        },
                      ]}
                    >
                      <Radio.Group>
                        <Radio value={1}>优先最大副本</Radio>
                        <Radio value={2}>优先最小副本</Radio>
                      </Radio.Group>
                    </Form.Item>
                  </div>
                ) : null
              }
            </Form.Item>
          }

          {/* {isCycle && (
            <Form.Item
              name="parallelNum"
              label={
                <span>
                  任务并行度
                  <Tooltip title="每个节点同时迁移的副本数量">
                    <IconFont style={{ fontSize: '14px', marginLeft: '5px' }} type="icon-zhushi" />
                  </Tooltip>
                </span>
              }
              rules={[
                {
                  required: true,
                  message: `请输入!`,
                },
              ]}
            >
              <InputNumber min={0} max={999} placeholder="请输入任务并行度" style={{ width: '100%' }} />
            </Form.Item>
          )} */}

          {/* {isCycle && (
            <Form.Item
              className="schedule-cron"
              name="scheduleCron"
              label="任务周期"
              rules={[
                {
                  required: true,
                  message: `请输入!`,
                },
                {
                  validator: (_, value) => {
                    const valArr = value.split(' ');
                    if (valArr[1] === '*' || valArr[2] === '*') {
                      return Promise.reject(new Error('任务周期必须指定分钟、小时'));
                    }
                    return Promise.resolve();
                  },
                },
              ]}
            >
              <CronInput />
            </Form.Item>
          )} */}

          {/* {isCycle && (
            <Form.Item
              name="executionStrategy"
              label={
                <span>
                  执行策略
                  <Tooltip title="不同大小副本执行的顺序">
                    <IconFont style={{ fontSize: '14px', marginLeft: '5px' }} type="icon-zhushi" />
                  </Tooltip>
                </span>
              }
              rules={[
                {
                  required: true,
                  message: `请选择!`,
                },
              ]}
            >
              <Radio.Group>
                <Radio value={1}>优先最大副本</Radio>
                <Radio value={2}>优先最小副本</Radio>
              </Radio.Group>
            </Form.Item>
          )} */}

          <Form.Item
            name="throttleUnitM"
            label="限流"
            rules={[
              {
                required: true,
                message: `请输入!`,
              },
            ]}
          >
            <InputNumber min={1} max={99999} placeholder="请输入限流" addonAfter="MB/s" style={{ width: '100%' }} />
          </Form.Item>

          {isCycle && (
            <>
              <Form.Item name="status" label="是否启用">
                <Radio.Group>
                  <Radio value={1}>启用</Radio>
                  <Radio value={0}>禁用</Radio>
                </Radio.Group>
              </Form.Item>
            </>
          )}
        </Form>
      </Drawer>
    </>
  );
};

export default BalanceDrawer;
