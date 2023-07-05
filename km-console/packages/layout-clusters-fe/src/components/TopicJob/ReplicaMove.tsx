// 批量迁移
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  Button,
  Col,
  DatePicker,
  Drawer,
  Form,
  Input,
  InputNumber,
  Row,
  Select,
  Table,
  Utils,
  AppContainer,
  Space,
  Divider,
  Transfer,
  Tooltip,
} from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import './index.less';
import Api, { MetricType } from '@src/api/index';
import moment from 'moment';
import PreviewTaskPlan from './PreviewTaskPlan';
import { timeFormater } from '@src/constants/common';
import type { RangePickerProps } from 'knowdesign/es/basic/date-picker';

const { TextArea } = Input;
const { Option } = Select;

const jobNameMap: any = {
  expandAndReduce: '扩缩副本',
  transfer: '迁移副本',
};

interface DefaultConfig {
  jobId?: number | string;
  type?: string;
  topics: Array<any>;
  drawerVisible: boolean;
  onClose: () => void;
  genData?: () => any;
  jobStatus?: number;
}

export default (props: DefaultConfig) => {
  const { type = 'transfer', topics, drawerVisible, onClose, jobId, genData, jobStatus } = props;
  const routeParams = useParams<{ clusterId: string }>();
  const [visible, setVisible] = useState(drawerVisible);
  const [topicData, setTopicData] = useState([]);
  const [brokerList, setBrokerList] = useState([]);
  const [taskPlanData, setTaskPlanData] = useState([]);
  const [selectBrokerList, setSelectBrokerList] = useState([]);
  const [topicNewReplicas, setTopicNewReplicas] = useState([]);
  const [needMovePartitions, setNeedMovePartitions] = useState([]);
  const [moveDataTimeRanges, setMoveDataTimeRanges] = useState([]);
  const [moveDataTimeRangesType, setMoveDataTimeRangesType] = useState([]);
  const [form] = Form.useForm();
  const [global] = AppContainer.useGlobalValue();
  const [loadingTopic, setLoadingTopic] = useState<boolean>(true);
  const [topicMetaData, setTopicMetaData] = useState([]);
  const [topicSelectValue, setTopicSelectValue] = useState(topics);

  const topicDataColumns = [
    {
      title: 'Topic名称',
      dataIndex: 'topicName',
    },
    {
      title: '近三天平均流量',
      dataIndex: 'latestDaysAvgBytesInList',
      render: (value: any) => {
        return (
          <div className="custom-tag-nowrap">
            {value.map((item: any, index: any) => (
              <div key={index} className="custom-tag">
                {item && item.value ? `${Utils.formatSize(+item.value)}/S` : '-'}
              </div>
            ))}
          </div>
        );
      },
    },
    {
      title: '近三天峰值流量&时间',
      dataIndex: 'latestDaysMaxBytesInList',
      render: (value: any) => {
        return (
          <div className="custom-tag-wrap">
            {value.map((item: any, index: any) => (
              <div className="custom-tag" key={index}>
                <div>{item && item.value ? `${Utils.formatSize(+item.value)}/S` : '-'}</div>
                <div className="time">{item && item.timeStamp ? moment(item.timeStamp * 1000).format('HH:mm:ss') : '-'}</div>
              </div>
            ))}
          </div>
        );
      },
    },
    {
      title: '需迁移Partition',
      dataIndex: 'partitionIdList',
      width: 220,
      render: (v: any, r: any, i: number) => {
        return (
          <Select
            style={{ width: '100%' }}
            placeholder="下拉多选分区ID"
            defaultValue={v}
            value={needMovePartitions[i]}
            mode="multiple"
            maxTagCount={'responsive'}
            allowClear
            onChange={(a: any) => {
              const needMovePartitionsCopy = JSON.parse(JSON.stringify(needMovePartitions));
              needMovePartitionsCopy[i] = a;
              setNeedMovePartitions(needMovePartitionsCopy);
            }}
          >
            {v.map((p: any, index: any) => (
              <Option key={index} value={p}>
                {p}
              </Option>
            ))}
          </Select>
        );
      },
    },
    {
      title: '副本数',
      dataIndex: 'replicaNum',
    },
    {
      title: '数据保存时间',
      dataIndex: 'retentionMs',
      render: (v: any) => {
        return timeFormater(v);
      },
    },
    {
      title: '迁移数据时间范围',
      dataIndex: 'newRetentionMs',
      render: (v: any, r: any, i: number) => {
        const selectAfter = (
          <Select
            onChange={(n: any) => {
              const moveDataTimeRangesCopyType = JSON.parse(JSON.stringify(moveDataTimeRangesType));
              moveDataTimeRangesCopyType[i] = n === 'h' ? 1 : 60;
              setMoveDataTimeRangesType(moveDataTimeRangesCopyType);
            }}
            defaultValue="h"
            style={{ width: 82 }}
          >
            <Option value="m">Minute</Option>
            <Option value="h">Hour</Option>
          </Select>
        );
        return (
          <InputNumber
            width={80}
            min={0}
            max={99999}
            defaultValue={moveDataTimeRanges[i]}
            value={moveDataTimeRanges[i]}
            onChange={(n: number) => {
              const moveDataTimeRangesCopy = JSON.parse(JSON.stringify(moveDataTimeRanges));
              moveDataTimeRangesCopy[i] = n;
              setMoveDataTimeRanges(moveDataTimeRangesCopy);
            }}
            className={'move-dete-time-tanges'}
            // formatter={(value) => (value ? `${value} h` : '')}
            // parser={(value) => value.replace('h', '')}
            addonAfter={selectAfter}
          ></InputNumber>
        );
      },
    },
  ];
  const onDrawerClose = () => {
    form.resetFields();
    setTopicData([]);
    setSelectBrokerList([]);
    setTopicNewReplicas([]);
    setNeedMovePartitions([]);
    setMoveDataTimeRanges([]);
    // setLoadingTopic(true);
    setVisible(false);
    onClose();
  };
  const getReassignmentList = (topiclist?: any) => {
    return Utils.post(Api.getReassignmentList(), {
      clusterId: Number(routeParams.clusterId),
      topicNameList: topiclist,
    });
  };
  const getJobsTaskData = () => {
    const params = {
      clusterId: routeParams.clusterId,
      jobId: jobId,
    };
    return Utils.request(Api.getJobsTaskData(params.clusterId, params.jobId), params);
  };
  const getTaskPlanData = (params: any) => {
    return Utils.post(Api.getMovePlanTaskData(), params);
  };
  const onClickPreview = (data?: any) => {
    if (selectBrokerList.length === 0) return;
    if (topicNewReplicas.find((item) => item > selectBrokerList.length)) return;
    !data &&
      form.validateFields(['brokerList']).then((e) => {
        const planParams = topicSelectValue.map((item, index) => {
          return {
            brokerIdList: selectBrokerList,
            clusterId: routeParams.clusterId,
            enableRackAwareness: false,
            newReplicaNum: topicNewReplicas[index],
            partitionIdList: needMovePartitions[index],
            topicName: item,
          };
        });
        getTaskPlanData(planParams).then((res: any) => {
          setTaskPlanData(res.topicPlanList);
        });
      });
  };
  const onClickSavePreview = (data: any) => {
    const taskPlanDataCopy = JSON.parse(JSON.stringify(taskPlanData));
    const hasError: any[] = [];
    taskPlanDataCopy.forEach((topic: any, index: number) => {
      const partitionIds = Object.keys(data[topic.topicName]);
      const newReassignBrokerIdList = partitionIds.reduce((acc: Array<number>, cur: string) => {
        const ressignBrokerIdList = data[topic.topicName][cur].reassignBrokerIdList;
        if (ressignBrokerIdList.length !== topicNewReplicas[index]) {
          hasError.push(topic.topicName + ' Partition ' + cur);
        }
        acc.push(...ressignBrokerIdList);
        return acc;
      }, []);
      topic.reassignBrokerIdList = Array.from(new Set(newReassignBrokerIdList));
      topic.partitionPlanList.forEach((partition: any) => {
        partition.reassignBrokerIdList = data[topic.topicName][partition.partitionId].reassignBrokerIdList;
      });
    });
    if (hasError.length) {
      message.error(hasError.join(',') + '副本数与目标节点数不一致');
    } else {
      setTaskPlanData(taskPlanDataCopy);
    }
  };
  const checkRep = (_: any, value: any[]) => {
    if (value && value.length && topicNewReplicas.find((rep) => rep && rep > value.length)) {
      return Promise.reject('节点数低于Topic最大副本数');
    } else {
      return Promise.resolve();
    }
  };
  const disabledDate: RangePickerProps['disabledDate'] = (current) => {
    // 不能选择小于当前时间
    return current && current <= moment().add(-1, 'days').endOf('day');
  };
  const range = (start: number, end: number) => {
    const result = [];
    for (let i = start; i < end; i++) {
      result.push(i);
    }
    return result;
  };
  const disabledDateTime = (current: any) => {
    return {
      disabledHours: () => (current > moment() ? [] : range(0, moment().hour())),
      disabledMinutes: () => (current > moment() ? [] : range(0, moment().add(1, 'minute').minute())),
      // disabledSeconds: () => [55, 56],
    };
  };

  const nodeChange = (val: any) => {
    setSelectBrokerList(val);
  };

  useEffect(() => {
    if (!drawerVisible) return;
    onClickPreview();
  }, [selectBrokerList, needMovePartitions, moveDataTimeRanges]);

  useEffect(() => {
    if (topics.length === 0 || !drawerVisible) return;
    if (jobId) {
      setLoadingTopic(true);
      getJobsTaskData()
        .then((res: any) => {
          const jobData = (res && JSON.parse(res.jobData)) || {};
          const planTime = res?.planTime && moment(res.planTime, 'YYYY-MM-DD HH:mm:ss');
          const { topicPlanList = [], throttleUnitB = 0, jobDesc = '' } = jobData;
          let selectedBrokerList: any[] = [];
          const topicData = topicPlanList.map((topic: any) => {
            selectedBrokerList = topic.reassignBrokerIdList;
            return {
              ...topic,
              topicName: topic.topicName,
              latestDaysAvgBytesInList: topic.latestDaysAvgBytesInList || [],
              latestDaysMaxBytesInList: topic.latestDaysMaxBytesInList || [],
              partitionIdList: topic.partitionIdList,
              replicaNum: topic.presentReplicaNum,
              retentionMs: topic.originalRetentionTimeUnitMs,
              // newRetentionMs: topic.reassignRetentionTimeUnitMs,
            };
          });
          setTopicData(topicData);
          const newReplica = topicPlanList.map((t: any) => t.newReplicaNum || []);
          setTopicNewReplicas(newReplica);
          const needMovePartitions = topicPlanList.map((t: any) => t.partitionIdList || []);
          setNeedMovePartitions(needMovePartitions);
          const MoveDataTimeRanges = topicPlanList.map((t: any) => {
            const timeHour = t.reassignRetentionTimeUnitMs / 1000 / 60 / 60;
            return timeHour > 1 ? Math.floor(timeHour) : timeHour.toFixed(2);
          });
          setMoveDataTimeRanges(MoveDataTimeRanges);
          setSelectBrokerList(selectedBrokerList);
          form.setFieldsValue({
            brokerList: selectedBrokerList,
            throttle: throttleUnitB / 1024 / 1024,
            planTime,
            description: res?.jobDesc,
          });
        })
        .finally(() => {
          setLoadingTopic(false);
        });
    }
  }, [drawerVisible]);

  useEffect(() => {
    if (!drawerVisible) return;
    !jobId &&
      drawerVisible &&
      Utils.request(Api.getTopicMetaData(+routeParams.clusterId))
        .then((res: any) => {
          const topics = (res || []).map((item: any) => {
            return {
              label: item.topicName,
              value: item.topicName,
              partitionIdList: item.partitionIdList,
            };
          });
          setTopicMetaData(topics);
        })
        .catch((err) => {
          message.error(err);
        });
  }, [drawerVisible]);

  useEffect(() => {
    if (!drawerVisible) return;
    if (!jobId) {
      setLoadingTopic(true);
      drawerVisible &&
        getReassignmentList(topicSelectValue)
          .then((res: any[]) => {
            setTopicData(res);
            const newReplica = res.map((t) => t.replicaNum || []);
            setTopicNewReplicas(newReplica);
            const needMovePartitions = res.map((t) => t.partitionIdList || []);
            setNeedMovePartitions(needMovePartitions);
            const MoveDataTimeRanges = res.map((t) => {
              const timeHour = t.retentionMs / 1000 / 60 / 60;
              return timeHour > 1 ? Math.floor(timeHour) : timeHour.toFixed(2);
            });
            setMoveDataTimeRanges(MoveDataTimeRanges);
          })
          .finally(() => {
            setLoadingTopic(false);
          });
    }
  }, [topicSelectValue, drawerVisible]);

  // ---------- 新增穿梭框替换目标节点select
  useEffect(() => {
    if (!drawerVisible) return;
    setVisible(true);
    Utils.request(Api.getDashboardMetadata(routeParams.clusterId, MetricType.Broker)).then((res: any) => {
      const dataDe = res || [];
      const dataHandle = dataDe.map((item: any) => {
        return {
          ...item,
          key: item.brokerId,
          title: `${item.brokerId} (${item.host})`,
        };
      });
      setBrokerList(dataHandle);
    });
  }, [drawerVisible]);

  const addReassign = () => {
    if (selectBrokerList.length && topicNewReplicas.find((item) => item > selectBrokerList.length)) return;
    form.validateFields().then((e) => {
      const formData = form.getFieldsValue();
      const handledData = {
        creator: global.userInfo.userName,
        jobType: 0, // type 0 topic迁移 1 扩缩容 2集群均衡
        planTime: formData.planTime,
        jobStatus: jobId ? jobStatus : 2, //status 2 创建
        target: topicSelectValue.join(','),
        id: jobId || '',
        jobDesc: formData.description,
        jobData: JSON.stringify({
          clusterId: routeParams.clusterId,
          jobDesc: formData.description,
          throttleUnitB: formData.throttle * 1024 * 1024,
          topicPlanList: topicSelectValue.map((topic, index) => {
            return {
              clusterId: routeParams.clusterId,
              topicName: topic,
              partitionIdList: needMovePartitions[index],
              partitionNum: needMovePartitions[index].length,
              presentReplicaNum: topicNewReplicas[index],
              newReplicaNum: topicNewReplicas[index] || topic.replicaNum,
              originalBrokerIdList: taskPlanData[index].currentBrokerIdList,
              reassignBrokerIdList: taskPlanData[index].reassignBrokerIdList,
              originalRetentionTimeUnitMs: topicData[index].retentionMs,
              reassignRetentionTimeUnitMs: (moveDataTimeRanges[index] * 60 * 60 * 1000) / (moveDataTimeRangesType[index] || 1),
              latestDaysAvgBytesInList: topicData[index].latestDaysAvgBytesInList,
              latestDaysMaxBytesInList: topicData[index].latestDaysMaxBytesInList,
              partitionPlanList: taskPlanData[index].partitionPlanList,
            };
          }),
        }),
      };
      if (jobId) {
        Utils.put(Api.putJobsTaskData(routeParams.clusterId), handledData)
          .then(() => {
            message.success('迁移任务编辑成功');
            onDrawerClose();
            genData();
          })
          .catch((err: any) => {
            console.log(err, 'err');
          });
      } else {
        Utils.post(Api.createTask(routeParams.clusterId), handledData).then(() => {
          message.success('迁移任务创建成功');
          onDrawerClose();
        });
      }
    });
  };

  return (
    <Drawer
      push={false}
      title={jobNameMap[type]}
      width={1080}
      placement="right"
      onClose={onDrawerClose}
      visible={visible}
      className="topic-job-drawer"
      maskClosable={false}
      destroyOnClose
      extra={
        <Space>
          <Button
            size="small"
            style={{ marginRight: 8 }}
            onClick={(_) => {
              // setVisible(false);
              onDrawerClose();
            }}
          >
            取消
          </Button>
          <Button size="small" type="primary" onClick={addReassign}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <div className="wrap">
        <h4 className="title">{jobNameMap[type]}Topic</h4>

        {!jobId && (
          <Form form={form}>
            <Row>
              <Col span={12}>
                <Form.Item>
                  <Select
                    placeholder="请选择Topic，可多选"
                    mode="multiple"
                    maxTagCount={'responsive'}
                    allowClear
                    onChange={(v: any) => {
                      setTopicSelectValue(v);
                    }}
                    options={topicMetaData}
                    // 点击Tooltip会触发Select的下拉
                    // maxTagPlaceholder={(v) => {
                    //   const tooltipValue = v
                    //     .map((item) => {
                    //       return item.value;
                    //     })
                    //     .join('、');
                    //   return (
                    //     <Tooltip visible={true} placement="topLeft" key={tooltipValue} title={tooltipValue}>
                    //       <span>{'+' + v.length + '...'}</span>
                    //     </Tooltip>
                    //   );
                    // }}
                  ></Select>
                </Form.Item>
              </Col>
            </Row>
          </Form>
        )}

        <Table dataSource={topicData} columns={topicDataColumns} pagination={false} loading={loadingTopic} />
        <Form form={form} layout="vertical" className="task-form">
          <Row>
            <Col span={12}>
              <Form.Item name="brokerList" label="目标节点" rules={[{ required: true }, { validator: checkRep }]}>
                <Transfer
                  dataSource={brokerList}
                  showSearch
                  filterOption={(inputValue, option) => option.host.indexOf(inputValue) > -1}
                  targetKeys={selectBrokerList}
                  onChange={nodeChange}
                  render={(item) => item.title}
                  titles={['待选节点', '已选节点']}
                  customHeader
                  showSelectedCount
                  locale={{ itemUnit: '', itemsUnit: '' }}
                  suffix={<IconFont type="icon-fangdajing" />}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              {/* taskPlanData是传给组件的初始值
                点击预览任务计划，触发onClickPreview回调，发起请求获取taskPlanData
                组件内部改完点击每一行保存时，再通过onClickSavePreview回调向外派发数据 */}
              <PreviewTaskPlan
                taskPlanData={taskPlanData}
                onClickPreview={onClickPreview}
                onClickSavePreview={onClickSavePreview}
                brokerList={brokerList}
              ></PreviewTaskPlan>
            </Col>
          </Row>
          <h4 className="title">迁移任务配置</h4>
          <Row gutter={32} className="topic-execution-time">
            <Col span={12}>
              <Form.Item
                name="throttle"
                label="限流"
                rules={[
                  { required: true },
                  {
                    validator: (r: any, v: number) => {
                      if ((v || v === 0) && v <= 0) {
                        return Promise.reject('限流值不能小于或等于0');
                      }
                      return Promise.resolve();
                    },
                  },
                ]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  // formatter={(value) => `${value} MB/s`}
                  // parser={(value) => value.replace('MB/s', '')}
                  addonAfter="MB/S"
                  max={99999}
                ></InputNumber>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="planTime" label="任务执行时间" rules={[{ required: true }]}>
                <DatePicker showTime style={{ width: '100%' }} disabledDate={disabledDate} disabledTime={disabledDateTime} />
              </Form.Item>
            </Col>
          </Row>
          <h4 className="title">描述</h4>
          <Form.Item name="description" label="任务描述" rules={[{ required: true }]}>
            <TextArea placeholder="暂支持 String 格式" style={{ height: 110 }} />
          </Form.Item>
        </Form>
      </div>
    </Drawer>
  );
};
