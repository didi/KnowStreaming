/* eslint-disable no-case-declarations */
import { DownloadOutlined } from '@ant-design/icons';
import { AppContainer, Divider, Tooltip, Utils } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import * as React from 'react';
import moment from 'moment';
import { timeFormat } from '../../constants/common';
import ConfigForm from './component/ConfigForm';
import TestResult from './component/Result';
import { getFormConfig, getTableColumns, startFromMap } from './config';
import './index.less';
import API from '../../api';
import { useParams } from 'react-router-dom';

interface IPartition {
  consumedOffset: number;
  logEndOffset: number;
  partitionId: number;
  recordCount: number;
  recordSizeUnitB: number;
}

const ConsumeClientTest = () => {
  const formRef: any = React.createRef();
  const [global] = AppContainer.useGlobalValue();
  const [configInfo, setConfigInfo] = React.useState({});
  const [topicMetaData, setTopicMetaData] = React.useState([]);
  const [partitionProcess, setPartitionProcess] = React.useState([]);
  const partitionProcessRef = React.useRef<any[]>([]);
  const [tableData, setTableData] = React.useState([]);
  const [tableDataRes, setTableDataRes] = React.useState([]);
  const recordCountCur = React.useRef(0);
  const recordSizeCur = React.useRef(0);
  const [isStop, setIsStop] = React.useState(true);
  const { clusterId } = useParams<{ clusterId: string }>();
  const isStopStatus = React.useRef(true);
  const curPartitionList = React.useRef<IPartition[]>([]);
  const lastPartitionList = React.useRef<IPartition[]>([]);
  const [topicName, setTopicName] = React.useState<string>('');
  const [partitionLists, setPartitionLists] = React.useState([]);
  const [consumerGroupList, setConsumerGroupList] = React.useState([]);

  React.useEffect(() => {
    Utils.request(API.getTopicMetaData(+clusterId))
      .then((res: any) => {
        const topics = (res || []).map((item: any) => {
          return {
            label: item.topicName,
            value: item.topicName,
          };
        });
        setTopicMetaData(topics);
      })
      .catch((err) => {
        message.error(err);
      });
    return () => {
      stop();
    };
  }, []);

  React.useEffect(() => {
    if (topicName) {
      Utils.request(API.getConsumerGroup(topicName, +clusterId))
        .then((res: any) => {
          const consumers = (res || []).map((item: any) => {
            return {
              label: item.groupName,
              value: item.groupName,
            };
          });
          setConsumerGroupList(consumers);
        })
        .catch((err) => {
          message.error(err);
        });

      Utils.request(API.getTopicsMetaData(topicName, +clusterId))
        .then((res: any) => {
          const partitionLists = (res?.partitionIdList || []).map((item: any) => {
            return {
              label: item,
              value: item,
            };
          });
          setPartitionLists(partitionLists);
        })
        .catch((err) => {
          message.error(err);
        });
    }
  }, [topicName]);

  React.useEffect(() => {
    // TODO: 临时修改，tableData 长度为 0 时，直接退出
    if (tableData.length !== 0) {
      const res = tableData.map((item, index) => {
        //Partition,offset,Timestamp,key,value,Header
        return {
          ...item,
          Partition: item.Partition,
          offset: item.offset,
          Timestamp: item.Timestamp,
          key: index + '_' + item.offset,
          keydata: item.key,
        };
      });
      setTableDataRes(res);
    }
  }, [tableData]);

  const onHandleValuesChange = (value: any, allValues: any) => {
    Object.keys(value).forEach((key) => {
      let changeValue = null;
      switch (key) {
        case 'topic':
          AppContainer.eventBus.emit('ConsumeTopicChange', value[key]);
          setTopicName(value[key]);
          break;
        case 'start':
          changeValue = value[key][1];
          setConfigInfo({
            ...configInfo,
            needStartFromDate: changeValue === 'a specific date',
            needConsumerGroup: changeValue === 'a consumer group',
            needOffset: changeValue === 'latest x offsets' || changeValue === 'an offset',
            needOffsetMax: changeValue === 'latest x offsets',
            needPartitionList: changeValue === 'an offset',
          });
          break;
        case 'until':
          changeValue = value[key];
          setConfigInfo({
            ...configInfo,
            needMsgNum: changeValue === 'number of messages' || changeValue === 'number of messages per partition',
            needMsgSize: changeValue === 'max size per partition' || changeValue === 'max size',
            needUntilDate: changeValue === 'timestamp',
          });
          break;
        case 'filter':
          changeValue = value[key];
          setConfigInfo({
            ...configInfo,
            needFilterKeyValue: changeValue === 1 || changeValue === 2,
            needFilterSize: changeValue === 3 || changeValue === 4 || changeValue === 5,
          });
          break;
      }
    });
  };

  const loopUntilTask = (values: any, res: any) => {
    const { until, untilDate, untilMsgNum, unitMsgSize } = values;
    const currentTime = new Date().getTime();
    const partitionConsumedList: IPartition[] = res.partitionConsumedList || [];
    lastPartitionList.current = res.partitionConsumedList || [];
    const _partitionList = curPartitionList.current.length ? Array.from(curPartitionList.current) : Array.from(partitionConsumedList);

    for (const item of partitionConsumedList) {
      const index = _partitionList.findIndex((row) => row.partitionId === item.partitionId);
      if (index < 0) {
        _partitionList.push({
          ...item,
        }); // 不存在则加入
      } else {
        // 存在则累加
        _partitionList[index].recordCount = _partitionList[index].recordCount + item.recordCount;
        _partitionList[index].recordSizeUnitB = _partitionList[index].recordSizeUnitB + item.recordSizeUnitB;
        _partitionList.splice(index, 1, item);
      }
    }
    const processList = _partitionList.map((row) => ({
      label: `P${row.partitionId}`,
      totalNumber: row.logEndOffset,
      currentNumber: row.consumedOffset,
      key: row.partitionId,
    }));
    setPartitionProcess(processList as any); // 用于进度条渲染
    partitionProcessRef.current = processList;

    curPartitionList.current = _partitionList;
    if (!isStopStatus.current) {
      switch (until) {
        case 'timestamp':
          setIsStop(currentTime >= untilDate);
          isStopStatus.current = currentTime >= untilDate;
          break;
        case 'number of messages':
          setIsStop(+recordCountCur.current >= untilMsgNum);
          isStopStatus.current = +recordCountCur.current >= untilMsgNum;
          break;
        case 'number of messages per partition': // 所有分区都达到了设定值
          // 过滤出消费数量不足设定值的partition
          const filtersPartition = _partitionList.filter((item: any) => item.recordCount < untilMsgNum);
          curPartitionList.current = filtersPartition; // 用作下一次请求的入参
          setIsStop(filtersPartition.length < 1);
          isStopStatus.current = filtersPartition.length < 1;
          break;
        case 'max size':
          setIsStop(+recordSizeCur.current >= unitMsgSize);
          isStopStatus.current = +recordSizeCur.current >= unitMsgSize;
          break;
        case 'max size per partition':
          // 过滤出消费size不足设定值的partition
          const filters = partitionConsumedList.filter((item: any) => item.recordSizeUnitB < unitMsgSize);
          setIsStop(filters.length < 1);
          isStopStatus.current = filters.length < 1;
          curPartitionList.current = filters;
          break;
      }
    }
  };

  const runClientConsumer = (values: any, params: any = {}, isFirst = false) => {
    if (isStopStatus.current && !isFirst) return;
    const offsetLists = [
      {
        offset: values?.offset,
        partitionId: values?.partitionId,
      },
    ];
    const _params = {
      clusterId,
      maxDurationUnitMs: 5000, // 前端超时时间为10s，这里接口超时设置8s
      clientProperties: {},
      maxRecords: 10,
      topicName: values.topic,
      recordOperate: isFirst,
      filter: {
        filterCompareKey: values.filterKey,
        filterCompareSizeUnitB: values.filterSize,
        filterCompareValue: values.filterValue,
        filterType: values.filter,
      },
      startFrom: {
        startFromType: isFirst ? startFromMap[values.start[1]].type : 3,
        consumerGroup: startFromMap[values.start[1]].type === 4 ? values.consumerGroup : undefined,
        latestMinusX: startFromMap[values.start[1]].type === 5 ? values.offset : undefined,
        offsetList: isFirst
          ? startFromMap[values.start[1]].type === 3
            ? offsetLists
            : undefined
          : lastPartitionList.current?.map((item) => ({
              offset: item.consumedOffset,
              partitionId: item.partitionId,
            })),
        timestampUnitMs:
          values.start[1] === 'a specific date'
            ? startFromMap[values.start[1]].getDate(values.startDate)
            : startFromMap[values.start[1]].getDate && startFromMap[values.start[1]].getDate(),
      },
      ...params,
    };

    if (!isStopStatus.current) {
      Utils.post(API.postClientConsumer, _params)
        .then((res: any) => {
          // 累计
          recordCountCur.current = recordCountCur.current + res.totalRecordCount;
          recordSizeCur.current = recordSizeCur.current + res.totalRecordSizeUnitB;

          setTableData(res.recordList || []); // TODO:这里累加是不是需要按partition进行累加
          loopUntilTask(values, res);
          if (!isStopStatus.current) {
            runClientConsumer(values, {
              partitionConsumedList: curPartitionList.current,
            });
          }
        })
        .catch((error) => {
          setIsStop(true);
          isStopStatus.current = true;
        });
    }
  };

  const initValues = () => {
    curPartitionList.current = [];
    lastPartitionList.current = [];
    setPartitionProcess([]);
    partitionProcessRef.current = [];
    // setRecordCount(0);
    recordCountCur.current = 0;
    // setRecordSize(0);
    recordSizeCur.current = 0;
  };

  const clearForm = () => {
    initValues();
    setTableData([]);
    setIsStop(true);
    isStopStatus.current = true;
    setConfigInfo({});
    AppContainer.eventBus.emit('ConsumeTopicChange', '');
    formRef.current.resetFields();
  };

  const run = async () => {
    const values = await formRef.current.validateFields();
    initValues();
    setIsStop(false);
    isStopStatus.current = false;
    setTableDataRes([]);
    runClientConsumer(values, {}, true);
  };

  const stop = () => {
    setIsStop(true);
    isStopStatus.current = true;
  };

  const download = () => {
    let str = `Partition,offset,Timestamp,key,value,Header,\n`;
    const tableRes: any[] = tableData.map((item, index) => {
      return {
        Partition: item.partitionId,
        offset: item.offset,
        Timestamp: moment(item.timestampUnitMs).format(timeFormat),
        key: item.key || '-',
        value: item.value,
        Header: item.headerList?.join('、'),
      };
    });
    // 增加\t为了不让表格显示科学计数法或者其他格式
    for (let i = 0; i < tableRes.length; i++) {
      // for(let item in tableRes[i]){
      //   str += `${tableRes[i][item] + '\t'}${i < (Object.keys(tableRes[i]).length-1) ? ',' : ''}`;
      // }
      const keyArrs = Object.keys(tableRes[i]);
      for (let j = 0; j < keyArrs.length; j++) {
        const item = keyArrs[j];
        str += `${tableRes[i][item] + '\t'},`;
      }
      str += '\n';
    }

    let uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);

    var link = document.createElement('a');
    link.href = uri;

    link.download = `consume-client-test${new Date().getTime()}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const TableHeaderInfo = (
    <div className="consume-table-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
      <div>
        <span className="title">消费详情</span>
      </div>
      <div>
        <span className="info">
          消息条数 <span className="num">{recordCountCur.current}</span> 条
        </span>
        <span className="info" style={{ marginLeft: 10 }}>
          消息大小 <span className="num">{Utils.getSizeAndUnit(recordSizeCur.current).value}</span>{' '}
          {Utils.getSizeAndUnit(recordSizeCur.current, 'B').unit}
        </span>
        <Divider type="vertical" />
        <span className="info info-download" onClick={download}>
          <DownloadOutlined style={{ marginRight: 6 }} size={12} color="#74788D" />
          {/* <IconFont type="icon-xiazai" /> */}
          <span>下载</span>
        </span>
      </div>
    </div>
  );

  const processTitle = () => {
    return (
      <>
        <span className="">
          offset{' '}
          <Tooltip title="每个partition消费的offset信息">
            <IconFont type="icon-zhushi" />
          </Tooltip>
        </span>
      </>
    );
  };

  return (
    <>
      <div className="client-test-panel">
        <ConfigForm
          title="消费配置"
          formConfig={getFormConfig(topicMetaData, configInfo, partitionLists, consumerGroupList)}
          formData={{}}
          formRef={formRef}
          onHandleValuesChange={onHandleValuesChange}
          clearForm={clearForm}
          submit={run}
          running={!isStop}
          stop={stop}
        />
        <TestResult
          showProcessList={true}
          processTitle={processTitle()}
          processList={partitionProcess}
          tableTitle={TableHeaderInfo}
          tableProps={{
            columns: getTableColumns({ isShowControl: global.isShowControl }),
            dataSource: tableDataRes,
            lineFillColor: true,
            rowKey: 'offset',
            showHeader: false,
            attrs: {
              id: 'timestampUnitMs',
            },
          }}
          showCardList={false}
        />
      </div>
    </>
  );
};

export default ConsumeClientTest;
