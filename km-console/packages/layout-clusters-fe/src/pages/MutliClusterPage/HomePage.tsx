import React, { useEffect, useMemo, useRef, useState, useReducer } from 'react';
import { Slider, Input, Select, Checkbox, Button, Utils, Spin, IconFont, AppContainer } from 'knowdesign';
import API from '../../api';
import TourGuide, { MultiPageSteps } from '@src/components/TourGuide';
import './index.less';
import { healthSorceList, linesMetric, pointsMetric, sortFieldList, sortTypes, statusFilters } from './config';
import { oneDayMillims } from '../../constants/common';
import ListScroll from './List';
import AccessClusters from './AccessCluster';
import CustomCheckGroup from './CustomCheckGroup';
import { ClustersPermissionMap } from '../CommonConfig';

const CheckboxGroup = Checkbox.Group;
const { Option } = Select;

const MultiClusterPage = () => {
  const [run, setRun] = useState<boolean>(false);
  const [global] = AppContainer.useGlobalValue();
  const [statusList, setStatusList] = React.useState([1, 0]);
  const [kafkaVersion, setKafkaVersion] = React.useState({});
  const [visible, setVisible] = React.useState(false);
  const [list, setList] = useState<[]>([]);
  const [healthScoreRange, setHealthScoreRange] = React.useState([0, 100]);
  const [checkedKafkaVersion, setCheckedKafkaVersion] = React.useState({});
  const [sortInfo, setSortInfo] = React.useState({
    sortField: 'HealthScore',
    sortType: 'asc',
  });
  const [clusterLoading, setClusterLoading] = useState(true);
  const [pageLoading, setPageLoading] = useState(true);
  const [isReload, setIsReload] = useState(false);
  const [versionLoading, setVersionLoading] = useState(true);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [stateInfo, setStateInfo] = React.useState({
    downCount: 0,
    liveCount: 0,
    total: 0,
  });
  const [pagination, setPagination] = useState({
    pageNo: 1,
    pageSize: 10,
    total: 0,
  });

  const searchKeyword = useRef('');

  const getPhyClustersDashbord = (pageNo: number, pageSize: number) => {
    const endTime = new Date().getTime();
    const startTime = endTime - oneDayMillims;
    const params = {
      metricLines: {
        endTime,
        metricsNames: linesMetric,
        startTime,
      },
      latestMetricNames: pointsMetric,
      pageNo: pageNo || 1,
      pageSize: pageSize || 10,
      preciseFilterDTOList: [
        {
          fieldName: 'kafkaVersion',
          fieldValueList: checkedKafkaVersion,
        },
      ],
      rangeFilterDTOList: [
        {
          fieldMaxValue: healthScoreRange[1],
          fieldMinValue: healthScoreRange[0],
          fieldName: 'HealthScore',
        },
      ],
      searchKeywords,
      ...sortInfo,
    };

    if (statusList.length === 1) {
      params.preciseFilterDTOList.push({
        fieldName: 'Alive',
        fieldValueList: statusList,
      });
    }
    return Utils.post(API.phyClustersDashbord, params);
  };

  const getSupportKafkaVersion = () => {
    setVersionLoading(true);
    Utils.request(API.supportKafkaVersion)
      .then((res) => {
        setKafkaVersion(res || {});
        setVersionLoading(false);
        setCheckedKafkaVersion(res ? Object.keys(res) : []);
      })
      .catch((err) => {
        setVersionLoading(false);
      });
  };

  const getPhyClusterState = () => {
    Utils.request(API.phyClusterState)
      .then((res: any) => {
        setStateInfo(res);
      })
      .finally(() => {
        setPageLoading(false);
      });
  };

  useEffect(() => {
    getPhyClusterState();
    getSupportKafkaVersion();
  }, []);

  useEffect(() => {
    if (!pageLoading && stateInfo.total) {
      setRun(true);
    }
  }, [pageLoading, stateInfo]);

  useEffect(() => {
    if (versionLoading) return;
    setClusterLoading(true);
    getPhyClustersDashbord(pagination.pageNo, pagination.pageSize)
      .then((res: any) => {
        setPagination(res.pagination);
        setList(res?.bizData || []);
        return res;
      })
      .finally(() => {
        setClusterLoading(false);
      });
  }, [sortInfo, checkedKafkaVersion, healthScoreRange, statusList, searchKeywords, isReload]);

  const onSilderChange = (value: number[]) => {
    setHealthScoreRange(value);
  };

  const onSelectChange = (type: string, value: string) => {
    setSortInfo({
      ...sortInfo,
      [type]: value,
    });
  };

  const onStatusChange = (list: []) => {
    setStatusList(list);
  };

  const onInputChange = (e: any) => {
    const { value } = e.target;
    setSearchKeywords(value.trim());
  };

  const onChangeCheckGroup = (list: []) => {
    setCheckedKafkaVersion(list);
  };

  const afterSubmitSuccessAccessClusters = () => {
    getPhyClusterState();
    setIsReload(!isReload);
  };

  const renderEmpty = () => {
    return (
      <div className="empty-page">
        <div className="title">Kafka 多集群管理</div>
        <div className="img">
          <div className="img-card-1" />
          <div className="img-card-2" />
          <div className="img-card-3" />
        </div>
        <div>
          <Button className="header-filter-top-button" type="primary" onClick={() => setVisible(true)}>
            <span>
              <IconFont type="icon-jiahao" />
              <span className="text">接入集群</span>
            </span>
          </Button>
        </div>
      </div>
    );
  };

  const renderLoading = () => {
    return (
      <div style={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <Spin spinning={true} />
      </div>
    );
  };

  const renderContent = () => {
    return (
      <div className="multi-cluster-page" id="scrollableDiv">
        <div className="multi-cluster-page-fixed">
          <div className="content-container">
            <div className="multi-cluster-header">
              <div className="cluster-header-card">
                <div className="cluster-header-card-bg-left"></div>
                <div className="cluster-header-card-bg-right"></div>
                <h5 className="header-card-title">
                  Clusters<span className="chinese-text"> 总数</span>
                </h5>
                <div className="header-card-total">{stateInfo.total}</div>
                <div className="header-card-info">
                  <div className="card-info-item card-info-item-live">
                    <div>
                      live
                      <span className="info-item-value">
                        <em>{stateInfo.liveCount}</em>
                      </span>
                    </div>
                  </div>
                  <div className="card-info-item card-info-item-down">
                    <div>
                      down
                      <span className="info-item-value">
                        <em>{stateInfo.downCount}</em>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div className="cluster-header-filter">
                <div className="header-filter-top">
                  <div className="header-filter-top-input">
                    <Input
                      onPressEnter={onInputChange}
                      onChange={(e) => (searchKeyword.current = e.target.value)}
                      allowClear
                      bordered={false}
                      placeholder="请输入ClusterName进行搜索"
                      suffix={<IconFont className="icon" type="icon-fangdajing" onClick={() => setSearchKeywords(searchKeyword.current)} />}
                    />
                  </div>
                  {global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTER_ADD) ? (
                    <>
                      <div className="header-filter-top-divider"></div>
                      <Button className="header-filter-top-button" type="primary" onClick={() => setVisible(true)}>
                        <IconFont type="icon-jiahao" />
                        <span className="text">接入集群</span>
                      </Button>
                    </>
                  ) : (
                    <></>
                  )}
                </div>

                <div className="header-filter-bottom">
                  <div className="header-filter-bottom-item header-filter-bottom-item-checkbox">
                    <h3 className="header-filter-bottom-item-title">版本选择</h3>
                    <div className="header-filter-bottom-item-content flex">
                      {Object.keys(kafkaVersion).length ? (
                        <CustomCheckGroup kafkaVersion={Object.keys(kafkaVersion)} onChangeCheckGroup={onChangeCheckGroup} />
                      ) : null}
                    </div>
                  </div>
                  <div className="header-filter-bottom-item header-filter-bottom-item-slider">
                    <h3 className="header-filter-bottom-item-title title-right">健康分</h3>
                    <div className="header-filter-bottom-item-content">
                      <Slider range step={20} defaultValue={[0, 100]} marks={healthSorceList} onAfterChange={onSilderChange} />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className="multi-cluster-filter">
              <div className="multi-cluster-filter-select">
                <Select
                  onChange={(value) => onSelectChange('sortField', value)}
                  defaultValue="HealthScore"
                  style={{ width: 170, marginRight: 12 }}
                >
                  {sortFieldList.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.label}
                    </Option>
                  ))}
                </Select>
                <Select onChange={(value) => onSelectChange('sortType', value)} defaultValue="asc" style={{ width: 170 }}>
                  {sortTypes.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.label}
                    </Option>
                  ))}
                </Select>
              </div>
              <div className="multi-cluster-filter-checkbox">
                <CheckboxGroup options={statusFilters} value={statusList} onChange={onStatusChange} />
              </div>
            </div>
            <div className="test-modal-23"></div>
          </div>
        </div>
        <Spin spinning={clusterLoading}>{renderList}</Spin>
      </div>
    );
  };

  const renderList = useMemo(() => {
    return <ListScroll list={list} pagination={pagination} loadMoreData={getPhyClustersDashbord} getPhyClusterState={getPhyClusterState} />;
  }, [list, pagination]);

  return (
    <>
      <TourGuide guide={MultiPageSteps} run={run} />
      {pageLoading ? renderLoading() : stateInfo.total ? renderContent() : renderEmpty()}
      <AccessClusters
        visible={visible}
        setVisible={setVisible}
        kafkaVersion={Object.keys(kafkaVersion)}
        afterSubmitSuccess={afterSubmitSuccessAccessClusters}
      />
    </>
  );
};

export default MultiClusterPage;
