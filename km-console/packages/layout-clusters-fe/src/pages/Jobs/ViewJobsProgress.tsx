import React, { useState, useEffect } from 'react';
import moment from 'moment';
import { useParams } from 'react-router-dom';
import { Button, Drawer, Utils, Descriptions, Tabs, Input, Spin, InputNumber } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import TaskDetails from './TeskDetails';
import NodeTraffic from './NodeTraffic';
import RebalancePlan from './RebalancePlan';
import { jobTypeEnum, runningStatusEnum } from './config';
import { timeFormat, getSizeAndUnit } from '../../constants/common';
import Api from '@src/api';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
const { request, post } = Utils;
const { TabPane } = Tabs;
export const ViewJobsProgress = (props: any) => {
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [detailData, setDetailData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [positionType, setPositionType] = useState<string>('TeskDetails');
  const [editFlowLimitStatus, setEditFlowLimitStatus] = useState<boolean>(false); // 修改限流状态
  const [flowLimit, setFlowLimit] = useState<any>(); // 修改限流数值
  const callback = (key: any) => {
    // setSearchValue('');
    // setSearchKeywords('');
    setPositionType(key);
  };

  // * 修改限流事件
  const editFlowLimit = () => {
    if (!flowLimit) {
      message.error('限流值不能为空');
      return;
    }
    if (flowLimit < 0) {
      message.error('限流值最小值为0');
      return;
    }
    const newSize = Utils.transMBToB(flowLimit);
    post(Api.getJobTraffic(urlParams.clusterId, props?.record?.id, newSize)).then((res) => {
      message.success('修改限流成功');
      getDetailData();
      setEditFlowLimitStatus(false);
    });
  };
  // * 获取详情和任务明细数据
  const getDetailData = () => {
    setLoading(true);
    props?.record &&
      request(Api.getJobDetail(urlParams.clusterId, props?.record?.id))
        .then((res: any) => {
          setDetailData(res || []);
          const newFlowLimit = Utils.transBToMB(res?.flowLimit as number);
          setFlowLimit(newFlowLimit);
          // setDetailData(res || mockData);
          setLoading(false);
        })
        .catch((err) => {
          setLoading(false);
        });
  };

  useEffect(() => {
    getDetailData();
  }, [props?.record]);

  return (
    <Drawer
      width={1080}
      title="查看进度"
      visible={props.visible}
      onClose={() => props.setVisble(false)}
      destroyOnClose={true}
      maskClosable={false}
    >
      <Spin spinning={loading}>
        <Descriptions
          style={{ fontSize: '13px' }}
          column={3}
          labelStyle={{
            width: '92px',
            textAlign: 'right',
            display: 'flex',
            justifyContent: 'end',
            color: '#74788D',
            fontSize: '13px',
          }}
          contentStyle={{ fontSize: '13px' }}
        >
          <Descriptions.Item label="任务类型">{jobTypeEnum[detailData?.jobType] || '-'}</Descriptions.Item>
          <Descriptions.Item label="运行状态">{runningStatusEnum[detailData?.jobStatus] || '-'}</Descriptions.Item>
          <Descriptions.Item label="限流">
            {/* 修改限流 */}
            <div>
              {editFlowLimitStatus ? (
                <div className="edit-flow-limit" style={{ display: 'flex', alignItems: 'center' }}>
                  <InputNumber
                    controls={false}
                    size="small"
                    defaultValue={Utils.transBToMB(detailData?.flowLimit)}
                    value={flowLimit}
                    onChange={(e: any) => setFlowLimit(e)}
                    min={0}
                    max={99999}
                    style={{ width: '150px' }}
                  />
                  <CheckOutlined style={{ margin: '0 10px', color: 'green' }} onClick={editFlowLimit} />
                  <CloseOutlined style={{ color: 'red' }} onClick={() => setEditFlowLimitStatus(false)} />
                </div>
              ) : (
                <span>
                  {detailData?.flowLimit ? Utils.transBToMB(detailData?.flowLimit) + ' MB/s ' : '-'}
                  {runningStatusEnum[detailData?.jobStatus] && runningStatusEnum[detailData?.jobStatus] !== 'Success' ? (
                    <IconFont style={{ fontSize: '14px' }} type="icon-bianji1" onClick={() => setEditFlowLimitStatus(true)} />
                  ) : null}
                </span>
              )}
            </div>
          </Descriptions.Item>
          <Descriptions.Item label="计划执行时间">
            {detailData?.planTime ? moment(detailData?.planTime).format(timeFormat) : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="实际执行时间">
            {detailData?.startTime ? moment(detailData?.startTime).format(timeFormat) : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="任务完成时间">
            {detailData?.endTime ? moment(detailData?.endTime).format(timeFormat) : '-'}
          </Descriptions.Item>
        </Descriptions>
      </Spin>
      <Tabs className={'custom_tabs_class'} defaultActiveKey="Configuration" onChange={callback} destroyInactiveTabPane>
        {props?.record?.jobType === 2 && (
          <TabPane tab="均衡计划" key="Rebalance">
            <RebalancePlan status={props?.record?.jobStatus} jobId={props?.record?.id} />
          </TabPane>
        )}
        {/* {
          <TabPane tab="均衡计划" key="Rebalance">
            <RebalancePlan jobId={props?.record?.id} />
          </TabPane>
        } */}
        <TabPane tab="任务明细" key="TeskDetails">
          {<TaskDetails detailData={detailData} jobId={props?.record?.id} />}
        </TabPane>
        <TabPane tab="节点流量情况" key="NodeTraffic">
          {<NodeTraffic jobId={props?.record?.id} />}
        </TabPane>
      </Tabs>
    </Drawer>
  );
};
