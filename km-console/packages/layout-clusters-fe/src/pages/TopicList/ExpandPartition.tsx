import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { AppContainer, Button, Divider, Drawer, Form, InputNumber, SingleChart, Space, Spin, Tooltip, Utils } from 'knowdesign';
import notification from '@src/components/Notification';
import Api, { MetricType } from '@src/api/index';
import { getBasicChartConfig, getDataUnit } from '@src/constants/chartConfig';
import { formatChartData, OriginMetricData } from '@src/constants/chartConfig';

const ExpandPartition = (props: { record: any; onConfirm: () => void }) => {
  const [global] = AppContainer.useGlobalValue();
  const { record, onConfirm } = props;
  const [form] = Form.useForm();
  const routeParams = useParams<{
    clusterId: string;
  }>();
  const [expandPartitionsVisible, setExpandPartitionsVisible] = useState(false);
  const [topicMetricData, setTopicMetricData] = useState<undefined | any>();
  const [minByteInOut, setMinByteInOut] = useState<number>(0);
  const [expandToNum, setExpandToNum] = useState<number>(record.partitionNum);
  const [loading, setLoading] = useState<boolean>(false);
  const confirm = () => {
    const params = {
      clusterId: Number(routeParams.clusterId),
      incPartitionNum: form.getFieldValue('incPartitionNum') - record.partitionNum,
      topicName: record.topicName,
    };
    Utils.post(Api.expandPartitions(), params).then((data) => {
      if (data === null) {
        notification.success({
          message: '扩分区成功',
        });
        setExpandPartitionsVisible(false);
        onConfirm && onConfirm();
      } else {
        notification.error({
          message: '扩分区失败',
        });
      }
    });
  };
  useEffect(() => {
    if (!expandPartitionsVisible) return;
    const [startStamp, endStamp] = [Date.now() - 24 * 3600 * 1000, Date.now()];

    setLoading(true);
    const metricParams = {
      aggType: 'avg',
      endTime: Math.round(endStamp),
      metricsNames: ['BytesIn', 'BytesOut'],
      startTime: Math.round(startStamp),
      topNu: 0,
      topics: [record.topicName],
    };
    Utils.post(Api.getTopicsMetrics(Number(routeParams.clusterId)), metricParams).then((data: any) => {
      const minByteInOut = Math.min(
        ...data.map((metricItem: any) =>
          Math.min(
            ...metricItem.metricLines.map((lineItem: any) => Math.min(...lineItem.metricPoints.map((point: any) => Number(point.value))))
          )
        )
      );
      const empiricalMinValue = 10 * 1024 * record.partitionNum;

      const lines = data.map((metric: OriginMetricData) => {
        const child = metric.metricLines[0];
        child.name = metric.metricName;
        return child;
      });
      const formatedData = formatChartData(
        [
          {
            metricName: 'BytesIn',
            metricLines: lines,
          },
        ],
        global?.getMetricDefine || {},
        MetricType.Topic,
        [startStamp, endStamp]
      );

      setMinByteInOut(minByteInOut < empiricalMinValue ? empiricalMinValue : minByteInOut);
      setTopicMetricData(formatedData[0]);
      setLoading(false);
    });

    form.setFieldsValue({
      incPartitionNum: record.partitionNum,
    });
  }, [expandPartitionsVisible]);
  const formattedMinBytesInOut = (v: number) => {
    const [unit, size] = getDataUnit['Memory'](v);
    return `${(v / size).toFixed(2)}${unit}/s`;
  };
  return (
    <>
      <Button
        type="link"
        onClick={(_) => {
          setExpandPartitionsVisible(true);
        }}
      >
        扩分区
      </Button>
      <Drawer
        className="expand-partition-drawer"
        title="扩分区"
        width={1080}
        visible={expandPartitionsVisible}
        maskClosable={false}
        extra={
          <Space>
            <Button
              size="small"
              style={{ marginRight: 8 }}
              onClick={() => {
                setExpandPartitionsVisible(false);
              }}
            >
              取消
            </Button>
            <Button size="small" type="primary" onClick={confirm}>
              确定
            </Button>
            <Divider type="vertical" />
          </Space>
        }
        onClose={(_) => {
          setExpandPartitionsVisible(false);
        }}
      >
        <div className="brief-info">
          <div className="item">
            <span className="field">Topic名称 :</span>
            <Tooltip title={record.topicName}>
              <span className="val">{record.topicName}</span>
            </Tooltip>
          </div>
          <div className="item">
            <span className="field desc-field">描述 :</span>
            <Tooltip title={record.description || '-'}>
              <span className="desc-val">{record.description || '-'}</span>
            </Tooltip>
          </div>
        </div>
        <Spin spinning={loading}>
          <div className="flow-chart" style={{ height: 247 }}>
            {topicMetricData && (
              <SingleChart
                showHeader={false}
                wrapStyle={{
                  width: '100%',
                  height: 268,
                  marginTop: 10,
                }}
                chartTypeProp="line"
                propChartData={topicMetricData}
                option={getBasicChartConfig({
                  title: {
                    text: `{unit|单位: ${topicMetricData.metricUnit}}`,
                    top: 0,
                    left: -6,
                  },
                  xAxis: {
                    type: 'time',
                  },
                  legend: {
                    left: 'center',
                  },
                  grid: {
                    left: 0,
                    right: 20,
                    top: 36,
                  },
                  tooltip: {
                    customWidth: 160,
                  },
                })}
                seriesCallback={(data: any) => {
                  return data.metricLines.map((line: any) => {
                    return {
                      ...line,
                      areaStyle: {
                        opacity: 0.06,
                      },
                      lineStyle: {
                        width: 1,
                      },
                      smooth: 0.25,
                      symbol: 'emptyCircle',
                      symbolSize: 4,
                      emphasis: {
                        disabled: true,
                      },
                    };
                  });
                }}
              />
            )}
          </div>
        </Spin>
        <div className="operation">
          <div className="partition-input">
            <div className="cur">
              <div className="num-txt">当前分区数：</div>
              <InputNumber className="num-input" disabled value={record.partitionNum} size="small"></InputNumber>
              <span className="remark">
                当前分区数预计最低可支持 {minByteInOut === Infinity ? '-' : formattedMinBytesInOut(minByteInOut)} 的消息写入速率
              </span>
            </div>
            <div className="expand-to">
              <div className="num-txt">增加至：</div>
              <Form form={form}>
                <Form.Item name="incPartitionNum" style={{ marginBottom: 0 }}>
                  <InputNumber
                    className="num-input"
                    size="small"
                    min={record.partitionNum}
                    onChange={(v) => {
                      setExpandToNum(Number(v));
                    }}
                  ></InputNumber>
                </Form.Item>
              </Form>
              <span className="remark">
                扩分区后预计最低可支持{' '}
                {minByteInOut === Infinity
                  ? '-'
                  : formattedMinBytesInOut(minByteInOut * (expandToNum ? expandToNum / record.partitionNum : 1))}{' '}
                的消息写入速率
              </span>
            </div>
          </div>
        </div>
      </Drawer>
    </>
  );
};

export default ExpandPartition;
