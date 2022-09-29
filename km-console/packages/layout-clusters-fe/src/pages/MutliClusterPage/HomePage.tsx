import React, { useEffect, useRef, useState } from 'react';
import { Slider, Input, Select, Checkbox, Button, Utils, Spin, AppContainer } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import API from '@src/api';
import TourGuide, { MultiPageSteps } from '@src/components/TourGuide';
import './index.less';
import { healthSorceList, sortFieldList, sortTypes, statusFilters } from './config';
import ClusterList from './List';
import AccessClusters from './AccessCluster';
import CustomCheckGroup from './CustomCheckGroup';
import { ClustersPermissionMap } from '../CommonConfig';

const CheckboxGroup = Checkbox.Group;
const { Option } = Select;

interface ClustersState {
  liveCount: number;
  downCount: number;
  total: number;
}

export interface SearchParams {
  healthScoreRange?: [number, number];
  checkedKafkaVersions?: string[];
  sortInfo?: {
    sortField: string;
    sortType: string;
  };
  keywords?: string;
  clusterStatus?: number[];
  isReloadAll?: boolean;
}

// 未接入集群默认页
const DefaultPage = (props: { setVisible: (visible: boolean) => void }) => {
  return (
    <div className="empty-page">
      <div className="title">Kafka 多集群管理</div>
      <div className="img">
        <div className="img-card-1" />
        <div className="img-card-2" />
        <div className="img-card-3" />
      </div>
      <div>
        <Button className="header-filter-top-button" type="primary" onClick={() => props.setVisible(true)}>
          <span>
            <IconFont type="icon-jiahao" />
            <span className="text">接入集群</span>
          </span>
        </Button>
      </div>
    </div>
  );
};

// 加载状态
const LoadingState = () => {
  return (
    <div style={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <Spin spinning={true} />
    </div>
  );
};

const MultiClusterPage = () => {
  const [global] = AppContainer.useGlobalValue();
  const [pageLoading, setPageLoading] = useState(true);
  const [accessClusterVisible, setAccessClusterVisible] = React.useState(false);
  const [curClusterInfo, setCurClusterInfo] = useState<any>({});
  const [kafkaVersions, setKafkaVersions] = React.useState<string[]>([]);
  const [existKafkaVersion, setExistKafkaVersion] = React.useState<string[]>([]);
  const [stateInfo, setStateInfo] = React.useState<ClustersState>({
    downCount: 0,
    liveCount: 0,
    total: 0,
  });
  // TODO: 首次进入因 searchParams 状态变化导致获取两次列表数据的问题
  const [searchParams, setSearchParams] = React.useState<SearchParams>({
    keywords: '',
    checkedKafkaVersions: [],
    healthScoreRange: [0, 100],
    sortInfo: {
      sortField: 'HealthScore',
      sortType: 'asc',
    },
    clusterStatus: [0, 1],
    // 是否拉取当前所有数据
    isReloadAll: false,
  });

  const searchKeyword = useRef('');
  const isReload = useRef(false);

  // 获取集群状态
  const getPhyClusterState = () => {
    Utils.request(API.phyClusterState)
      .then((res: any) => {
        setStateInfo(res);
      })
      .finally(() => {
        setPageLoading(false);
      });
  };

  // 获取 kafka 全部版本
  const getSupportKafkaVersion = () => {
    Utils.request(API.supportKafkaVersion).then((res) => {
      setKafkaVersions(Object.keys(res || {}));
    });
  };

  const updateSearchParams = (params: SearchParams) => {
    setSearchParams((curParams) => ({ ...curParams, isReloadAll: false, ...params }));
  };

  const searchParamsChangeFunc = {
    // 健康分改变
    onSilderChange: (value: [number, number]) =>
      updateSearchParams({
        healthScoreRange: value,
      }),
    // 排序信息改变
    onSortInfoChange: (type: string, value: string) =>
      updateSearchParams({
        sortInfo: {
          ...searchParams.sortInfo,
          [type]: value,
        },
      }),
    // Live / Down 筛选
    onClusterStatusChange: (list: number[]) =>
      updateSearchParams({
        clusterStatus: list,
      }),
    // 集群名称搜索项改变
    onInputChange: () =>
      updateSearchParams({
        keywords: searchKeyword.current,
      }),
    // 集群版本筛选
    onChangeCheckGroup: (list: string[]) => {
      updateSearchParams({
        checkedKafkaVersions: list,
        isReloadAll: isReload.current,
      });
      isReload.current = false;
    },
  };

  // 获取当前接入集群的 kafka 版本
  const getExistKafkaVersion = (isReloadAll = false) => {
    isReload.current = isReloadAll;
    Utils.request(API.getClustersVersion).then((versions: string[]) => {
      if (!Array.isArray(versions)) {
        versions = [];
      }
      setExistKafkaVersion(versions.sort().reverse() || []);
    });
  };

  // 接入/编辑集群
  const showAccessCluster = (clusterInfo: any = {}) => {
    setCurClusterInfo(clusterInfo);
    setAccessClusterVisible(true);
  };
  // 接入/编辑集群回调
  const afterAccessCluster = () => {
    getPhyClusterState();
    getExistKafkaVersion(true);
  };

  useEffect(() => {
    getPhyClusterState();
    getSupportKafkaVersion();
    getExistKafkaVersion();
  }, []);

  return (
    <>
      {pageLoading ? (
        <LoadingState />
      ) : !stateInfo?.total ? (
        <DefaultPage setVisible={setAccessClusterVisible} />
      ) : (
        <>
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
                          onPressEnter={searchParamsChangeFunc.onInputChange}
                          onChange={(e) => (searchKeyword.current = e.target.value)}
                          allowClear
                          bordered={false}
                          placeholder="请输入ClusterName进行搜索"
                          suffix={<IconFont className="icon" type="icon-fangdajing" onClick={searchParamsChangeFunc.onInputChange} />}
                        />
                      </div>
                      {global.hasPermission && global.hasPermission(ClustersPermissionMap.CLUSTER_ADD) ? (
                        <>
                          <div className="header-filter-top-divider"></div>
                          <Button className="header-filter-top-button" type="primary" onClick={() => showAccessCluster()}>
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
                        <h3 className="header-filter-bottom-item-title">版本分布</h3>
                        <div className="header-filter-bottom-item-content flex">
                          {existKafkaVersion.length ? (
                            <CustomCheckGroup
                              kafkaVersions={existKafkaVersion}
                              onChangeCheckGroup={searchParamsChangeFunc.onChangeCheckGroup}
                            />
                          ) : null}
                        </div>
                      </div>
                      <div className="header-filter-bottom-item header-filter-bottom-item-slider">
                        <h3 className="header-filter-bottom-item-title title-right">健康分</h3>
                        <div className="header-filter-bottom-item-content">
                          <Slider
                            range
                            step={20}
                            defaultValue={[0, 100]}
                            marks={healthSorceList}
                            onAfterChange={searchParamsChangeFunc.onSilderChange}
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="multi-cluster-filter">
                  <div className="multi-cluster-filter-select">
                    <Select
                      onChange={(value) => searchParamsChangeFunc.onSortInfoChange('sortField', value)}
                      defaultValue="HealthScore"
                      style={{ width: 170, marginRight: 12 }}
                    >
                      {sortFieldList.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.label}
                        </Option>
                      ))}
                    </Select>
                    <Select
                      onChange={(value) => searchParamsChangeFunc.onSortInfoChange('sortType', value)}
                      defaultValue="asc"
                      style={{ width: 170 }}
                    >
                      {sortTypes.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.label}
                        </Option>
                      ))}
                    </Select>
                  </div>
                  <div className="multi-cluster-filter-checkbox">
                    <CheckboxGroup
                      options={statusFilters}
                      value={searchParams.clusterStatus}
                      onChange={searchParamsChangeFunc.onClusterStatusChange}
                    />
                  </div>
                </div>
              </div>
            </div>
            <div className="multi-cluster-page-dashboard">
              <ClusterList
                searchParams={searchParams}
                showAccessCluster={showAccessCluster}
                getPhyClusterState={getPhyClusterState}
                getExistKafkaVersion={getExistKafkaVersion}
              />
            </div>
          </div>
          {/* 引导页 */}
          <TourGuide guide={MultiPageSteps} run={true} />
        </>
      )}

      <AccessClusters
        clusterInfo={curClusterInfo}
        kafkaVersion={kafkaVersions}
        visible={accessClusterVisible}
        setVisible={setAccessClusterVisible}
        afterSubmitSuccess={afterAccessCluster}
      />
    </>
  );
};

export default MultiClusterPage;
