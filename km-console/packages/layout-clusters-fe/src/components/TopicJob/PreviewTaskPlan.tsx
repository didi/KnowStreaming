import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { Button, Divider, Drawer, Select, Space, Table, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import Api, { MetricType } from '@src/api/index';

const { Option } = Select;

export default (props: any) => {
  const { taskPlanData, onClickPreview, onClickSavePreview, brokerList = [] } = props;
  const routeParams = useParams<{ clusterId: string }>();
  const [visible, setVisible] = useState(false);
  // const [brokerList, setBrokerList] = useState([]);
  // 存储每个topic的各个partition的编辑状态以及编辑后的目标BrokerID列表
  const [reassignBrokerIdListEditStatusMap, setReassignBrokerIdListEditStatusMap] = useState<{
    [key: string]: {
      [key: number]: {
        currentBrokerIdList: Array<number>;
        reassignBrokerIdList: Array<number>;
        isEdit: boolean;
      };
    };
  }>({});
  const onClose = () => {
    setVisible(false);
  };
  const setCurTopicPartitionEditStatus = (record: any, v: any, status: boolean) => {
    let reassignBrokerIdListEditStatusMapCopy = JSON.parse(JSON.stringify(reassignBrokerIdListEditStatusMap));
    reassignBrokerIdListEditStatusMapCopy[record.topicName][v].isEdit = status;
    setReassignBrokerIdListEditStatusMap(reassignBrokerIdListEditStatusMapCopy);
  };
  const columns = [
    {
      title: 'Topic',
      dataIndex: 'topicName',
    },
    {
      title: '源BrokerID',
      dataIndex: 'currentBrokerIdList',
      render: (a: any) => {
        return a.join(',');
      },
    },
    {
      title: '目标BrokerID',
      dataIndex: 'reassignBrokerIdList',
      render: (a: any) => {
        return a.join(',');
      },
    },
  ];
  useEffect(() => {
    let mapObj = taskPlanData.reduce((acc: any, cur: any) => {
      acc[cur.topicName] = cur.partitionPlanList.reduce((pacc: any, pcur: any) => {
        pacc[pcur.partitionId] = {
          currentBrokerIdList: pcur.currentBrokerIdList,
          reassignBrokerIdList: pcur.reassignBrokerIdList,
          isEdit: false,
        };
        return pacc;
      }, {});
      return acc;
    }, {});
    setReassignBrokerIdListEditStatusMap(mapObj);
  }, [taskPlanData]);

  return (
    <div>
      <Button
        style={{ marginTop: 20 }}
        type="link"
        onClick={() => {
          setVisible(true);
          onClickPreview && onClickPreview(taskPlanData);
        }}
      >
        预览任务计划
      </Button>
      <Drawer
        title={
          <Space size={0}>
            <Button
              className="drawer-title-left-button"
              type="text"
              size="small"
              icon={<IconFont type="icon-fanhui1" />}
              onClick={onClose}
            />
            <Divider type="vertical" />
            <span style={{ paddingLeft: '5px' }}>任务计划</span>
          </Space>
        }
        width={600}
        placement="right"
        onClose={onClose}
        visible={visible}
        className="preview-task-plan-drawer"
        maskClosable={false}
        destroyOnClose
        // closeIcon={<ArrowLeftOutlined />}
      >
        <Table
          rowKey={'topicName'}
          dataSource={taskPlanData}
          columns={columns}
          pagination={false}
          expandable={{
            expandedRowRender: (topicRecord) => {
              let partitionPlanList = topicRecord.partitionPlanList;
              const columns = [
                {
                  title: 'Partition',
                  dataIndex: 'partitionId',
                },
                {
                  title: '源BrokerID',
                  dataIndex: 'currentBrokerIdList',
                  render: (a: any) => {
                    return a.join(',');
                  },
                },
                {
                  title: '目标BrokerID',
                  dataIndex: 'reassignBrokerIdList',
                  render: (a: any, partitionRecord: any) => {
                    let isEdit = reassignBrokerIdListEditStatusMap[topicRecord.topicName][partitionRecord.partitionId].isEdit;
                    let reassignBrokerIdList =
                      reassignBrokerIdListEditStatusMap[topicRecord.topicName][partitionRecord.partitionId].reassignBrokerIdList;
                    return isEdit ? (
                      <Select
                        value={reassignBrokerIdList}
                        mode="multiple"
                        maxTagCount={'responsive'}
                        allowClear
                        onChange={(selBrokerIds) => {
                          let reassignBrokerIdListEditStatusMapCopy = JSON.parse(JSON.stringify(reassignBrokerIdListEditStatusMap));
                          reassignBrokerIdListEditStatusMapCopy[topicRecord.topicName][partitionRecord.partitionId].reassignBrokerIdList =
                            selBrokerIds;
                          setReassignBrokerIdListEditStatusMap(reassignBrokerIdListEditStatusMapCopy);
                        }}
                        style={{ width: '122px' }}
                      >
                        {brokerList.length > 0 &&
                          brokerList?.map((broker: any) => (
                            <Option key={Math.random()} value={broker.brokerId}>
                              {broker.brokerId}
                            </Option>
                          ))}
                      </Select>
                    ) : (
                      a.join(',')
                    );
                  },
                },
                {
                  title: '操作',
                  dataIndex: 'partitionId',
                  render: (v: any) => {
                    let isEdit = reassignBrokerIdListEditStatusMap[topicRecord.topicName][v].isEdit;
                    return isEdit ? (
                      <>
                        <Button
                          type="link"
                          onClick={() => {
                            onClickSavePreview && onClickSavePreview(reassignBrokerIdListEditStatusMap);
                          }}
                        >
                          保存
                        </Button>
                        <Button
                          type="link"
                          onClick={() => {
                            setCurTopicPartitionEditStatus(topicRecord, v, false);
                          }}
                        >
                          取消
                        </Button>
                      </>
                    ) : (
                      <>
                        <Button
                          type="link"
                          onClick={() => {
                            setCurTopicPartitionEditStatus(topicRecord, v, true);
                          }}
                        >
                          编辑
                        </Button>
                      </>
                    );
                  },
                },
              ];
              return (
                <div className="partition-task-plan-list">
                  <Table
                    rowKey={'partitionId'}
                    dataSource={partitionPlanList}
                    columns={columns}
                    pagination={{
                      simple: true,
                    }}
                  ></Table>
                </div>
              );
            },
          }}
        ></Table>
      </Drawer>
    </div>
  );
};
