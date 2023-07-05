import { AppContainer, Form, Tabs, Utils } from 'knowdesign';
import message from '@src/components/Message';
import * as React from 'react';
import ConfigForm from './component/ConfigFrom';
import TestResult from '../TestingConsumer/component/Result';
import API from '../../api';
import { getFormConfig, getTableColumns, tabsConfig } from './config';
import './index.less';
import { useParams } from 'react-router-dom';

const { TabPane } = Tabs;

const ProduceClientTest = () => {
  const [global] = AppContainer.useGlobalValue();
  const [form] = Form.useForm();
  const customFormRef: any = React.createRef();
  const [configInfo, setConfigInfo] = React.useState({});
  const [activeKey, setActiveKey] = React.useState(tabsConfig[0].name);
  const [tableData, setTableData] = React.useState([]);
  const [topicMetaData, setTopicMetaData] = React.useState([]);
  const [running, setRunning] = React.useState(false);
  const [isKeyOn, setIsKeyOn] = React.useState(true);
  const { clusterId } = useParams<{ clusterId: string }>();

  const currentInterval = React.useRef(null);

  let currentMsgNum = 0;
  let startTime = 0;

  React.useEffect(() => {
    Utils.request(API.getTopicMetaData(+clusterId))
      .then((res: any) => {
        const filterRes = res.filter((item: any) => item.type !== 1);
        const topics = (filterRes || []).map((item: any) => {
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

    return () => {
      clearInterval();
    };
  }, []);

  const runProduceClient = (values: any, isFirst = false) => {
    // 记录每一次请求发送的条数
    currentMsgNum = currentMsgNum ? currentMsgNum + values.chunks : values.chunks;

    const params = {
      clientProperties: {
        acks: values.acks,
        'compression.type': values.compressionType,
      },
      clusterId,
      partitionIdList: values.frocePartition,
      recordCount: values.chunks,
      recordHeader: values.recordHeader,
      recordKey: isKeyOn ? values.key || '' : undefined,
      recordOperate: isFirst,
      recordValue: values.value,
      topicName: values.topicName,
    };

    Utils.post(API.postClientProducer, params)
      .then((res: any) => {
        if (isFirst) {
          startTime = new Date().getTime();
        }
        setTableData(res || []);
        if (values.producerMode === 'timed') {
          if (values.interval && !currentInterval.current) {
            // const randomValue = Math.random() < 0.5 ? -1 : 1;
            // const random = +values.jitter ? +values.interval + randomValue * (Math.random() * +values.jitter) : values.interval;
            const random = values.interval;

            currentInterval.current = window.setInterval(() => {
              runProduceClient(values);
            }, random);
          }
          // 第一次接口返回到现在的时间大于运行时间 当前发送条数大于总条数停止请求
          const currentTime = new Date().getTime();
          // if (currentTime - startTime >= +values.elapsed || currentMsgNum >= values.messageProduced) {
          if (currentTime - startTime >= +values.elapsed) {
            clearInterval();
            setRunning(false);
          }
        } else {
          // manual 方式
          setRunning(false);
        }
      })
      .catch((err) => {
        // manual 方式
        if (values.producerMode !== 'timed') {
          setRunning(false);
        }
      });
  };

  const run = async () => {
    form
      .validateFields()
      .then((values) => {
        values.elapsed = values.elapsed * 60 * 1000;
        const data = customFormRef.current.getTableData();
        values.recordHeader = {};
        for (const item of data) {
          values.recordHeader[item.key] = item.value;
        }
        // 点击按钮重新定时器清空
        clearInterval();
        setRunning(true);
        runProduceClient(values, true);
      })
      .catch((error) => {
        const { values } = error;
        if (!values.chunks || (values.producerMode === 'timed' && (!values.interval || !values.elapsed))) {
          setActiveKey('Flow');
        }
        if (!values.topicName || !values.value) {
          setActiveKey('Data');
        }
      });
  };

  const clearInterval = () => {
    currentInterval.current && window.clearInterval(currentInterval.current);
    currentInterval.current = null;
  };

  const stop = () => {
    currentInterval.current && window.clearInterval(currentInterval.current);
    setRunning(false);
  };

  const onHandleValuesChange = (value: any, allValues: any) => {
    Object.keys(value).forEach((key) => {
      let changeValue: any = null;
      let partitionIdList = [];
      switch (key) {
        case 'producerMode':
          changeValue = value[key];
          setConfigInfo({
            ...configInfo,
            needTimeOption: changeValue === 'timed',
          });
          break;
        case 'interval':
          changeValue = value[key];
          setConfigInfo({
            ...configInfo,
            maxJitter: +value[key] - 1,
          });
          break;
        case 'topicName':
          changeValue = value[key] as string;
          AppContainer.eventBus.emit('ProduceTopicChange', value[key]);
          partitionIdList = topicMetaData.find((item) => item.label === changeValue)?.partitionIdList || [];
          partitionIdList = partitionIdList.map((item: number) => ({
            label: item,
            value: item,
          }));
          setConfigInfo({
            ...configInfo,
            partitionIdList,
          });
          break;
      }
    });
  };

  const clearForm = () => {
    setConfigInfo({});
    clearInterval();
    setTableData([]);
    form.resetFields();
    AppContainer.eventBus.emit('ProduceTopicChange', '');
    setIsKeyOn(true);
    customFormRef.current.resetTable();
  };

  const onTabChange = (key: string) => {
    setActiveKey(key);
  };

  const onKeySwitchChange = (checked: boolean) => {
    setIsKeyOn(checked);
  };

  const TabsContent = (
    <div className="form-tabs">
      <Tabs defaultActiveKey={tabsConfig[0].name} activeKey={activeKey} onChange={onTabChange}>
        {tabsConfig.map(({ name, control }) =>
          !control || (global.isShowControl && global.isShowControl(control)) ? <TabPane tab={name} key={name} /> : <></>
        )}
      </Tabs>
    </div>
  );
  return (
    <>
      <div className="client-test-panel">
        <ConfigForm
          title="生产配置"
          customContent={TabsContent}
          customFormRef={customFormRef}
          activeKey={activeKey}
          formConfig={getFormConfig({
            topicMetaData,
            activeKey,
            configInfo,
            form,
            onKeySwitchChange,
            isKeyOn,
            isShowControl: global.isShowControl,
          })}
          formData={{}}
          form={form}
          onHandleValuesChange={onHandleValuesChange}
          clearForm={clearForm}
          submit={run}
          running={running}
          stop={stop}
        />
        <TestResult
          showProcessList={false}
          tableProps={{
            showHeader: false,
            scroll: { y: 600 },
            columns: getTableColumns(),
            dataSource: tableData,
            pagination: false,
          }}
          showCardList={false}
        />
      </div>
    </>
  );
};

export default ProduceClientTest;
