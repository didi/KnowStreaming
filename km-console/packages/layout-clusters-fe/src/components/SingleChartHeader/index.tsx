import React, { useEffect, useState } from 'react';
import { Tooltip, Select, IconFont, Utils, Divider } from 'knowdesign';
import moment from 'moment';
import { DRangeTime } from 'knowdesign';
import IndicatorDrawer from './IndicatorDrawer';
import NodeScope from './NodeScope';

import './style/index.less';
import { MetricType } from 'src/api';

export interface Inode {
  name: string;
  desc: string;
}

export interface KsHeaderOptions {
  rangeTime: [number, number];
  isRelativeRangeTime: boolean;
  isAutoReload: boolean;
  gridNum?: number;
  scopeData?: {
    isTop: boolean;
    data: number | number[];
  };
}
export interface IindicatorSelectModule {
  metricType?: MetricType;
  hide?: boolean;
  drawerTitle?: string;
  selectedRows: (string | number)[];
  checkboxProps?: (record: any) => { [props: string]: any };
  tableData?: Inode[];
  submitCallback?: (value: (string | number)[]) => Promise<any>;
}

export interface IfilterData {
  hostName?: string;
  logCollectTaskId?: string | number;
  pathId?: string | number;
  agent?: string;
}

export interface IcustomScope {
  label: string;
  value: string | number;
}

export interface InodeScopeModule {
  customScopeList: IcustomScope[];
  scopeName?: string;
  showSearch?: boolean;
  searchPlaceholder?: string;
  change?: () => void;
}
interface PropsType {
  indicatorSelectModule?: IindicatorSelectModule;
  hideNodeScope?: boolean;
  hideGridSelect?: boolean;
  nodeScopeModule?: InodeScopeModule;
  onChange: (options: KsHeaderOptions) => void;
}

// 列布局选项
const GRID_SIZE_OPTIONS = [
  {
    label: '3列',
    value: 8,
  },
  {
    label: '2列',
    value: 12,
  },
  {
    label: '1列',
    value: 24,
  },
];

const SingleChartHeader = ({
  indicatorSelectModule,
  nodeScopeModule = {
    customScopeList: [],
  },
  hideNodeScope = false,
  hideGridSelect = false,
  onChange: onChangeCallback,
}: PropsType): JSX.Element => {
  const [gridNum, setGridNum] = useState<number>(GRID_SIZE_OPTIONS[1].value);
  const [rangeTime, setRangeTime] = useState<[number, number]>(() => {
    const curTimeStamp = moment().valueOf();
    return [curTimeStamp - 60 * 60 * 1000, curTimeStamp];
  });
  const [isRelativeRangeTime, setIsRelativeRangeTime] = useState(true);
  const [isAutoReload, setIsAutoReload] = useState(false);
  const [indicatorDrawerVisible, setIndicatorDrawerVisible] = useState(false);

  const [scopeData, setScopeData] = useState<{
    isTop: boolean;
    data: any;
  }>({
    isTop: true,
    data: 5,
  });

  useEffect(() => {
    onChangeCallback({
      rangeTime,
      scopeData,
      gridNum,
      isRelativeRangeTime,
      isAutoReload,
    });
    setIsAutoReload(false);
  }, [rangeTime, scopeData, gridNum, isRelativeRangeTime]);

  // 当时间范围为相对时间时，每隔 1 分钟刷新一次时间
  useEffect(() => {
    let relativeTimer: number;
    if (isRelativeRangeTime) {
      relativeTimer = window.setInterval(() => {
        setIsAutoReload(true);
        reloadRangeTime();
      }, 1 * 60 * 1000);
    }

    return () => {
      relativeTimer && window.clearInterval(relativeTimer);
    };
  }, [isRelativeRangeTime, rangeTime]);

  const sizeChange = (value: number) => {
    setGridNum(value);
  };

  const timeChange = (curRangeTime: [number, number], isRelative: boolean) => {
    setRangeTime([...curRangeTime]);
    setIsRelativeRangeTime(isRelative);
  };

  const reloadRangeTime = () => {
    const timeLen = rangeTime[1] - rangeTime[0] || 0;
    const curTimeStamp = moment().valueOf();
    setRangeTime([curTimeStamp - timeLen, curTimeStamp]);
  };

  const openIndicatorDrawer = () => {
    setIndicatorDrawerVisible(true);
  };

  const closeIndicatorDrawer = () => {
    setIndicatorDrawerVisible(false);
  };

  const nodeScopeChange = (data: any, isTop?: any) => {
    setScopeData({
      isTop,
      data,
    });
  };

  return (
    <>
      <div className="ks-chart-container">
        <div className="ks-chart-container-header">
          <div className="header-left">
            <div className="icon-box" onClick={reloadRangeTime}>
              <IconFont className="icon" type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" style={{ height: 20, top: 0 }} />
            <DRangeTime timeChange={timeChange} rangeTimeArr={rangeTime} />
          </div>
          <div className="header-right">
            {!hideNodeScope && <NodeScope nodeScopeModule={nodeScopeModule} change={nodeScopeChange} />}
            {!hideGridSelect && (
              <Select className="grid-select" style={{ width: 70 }} value={gridNum} options={GRID_SIZE_OPTIONS} onChange={sizeChange} />
            )}
            <Divider type="vertical" style={{ height: 20, top: 0 }} />
            <Tooltip title="点击指标筛选，可选择指标" placement="bottomRight">
              <div className="icon-box" onClick={openIndicatorDrawer}>
                <IconFont className="icon" type="icon-shezhi1" />
              </div>
            </Tooltip>
          </div>
        </div>
      </div>
      {!indicatorSelectModule?.hide && (
        <IndicatorDrawer visible={indicatorDrawerVisible} onClose={closeIndicatorDrawer} indicatorSelectModule={indicatorSelectModule} />
      )}
    </>
  );
};

export default SingleChartHeader;
