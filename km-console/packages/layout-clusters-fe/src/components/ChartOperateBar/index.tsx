import React, { useEffect, useRef, useState } from 'react';
import { Select, Divider, Button } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import moment from 'moment';
import { DRangeTime } from 'knowdesign';
import NodeSelect from './NodeSelect';
import './style/index.less';

export interface KsHeaderOptions {
  rangeTime: [number, number];
  isRelativeRangeTime: boolean;
  isAutoReload: boolean;
  gridNum?: number;
  scopeData?: {
    isTop: boolean;
    data: any;
  };
}

export interface IfilterData {
  hostName?: string;
  logCollectTaskId?: string | number;
  pathId?: string | number;
  agent?: string;
}

interface PropsType {
  hideNodeScope?: boolean;
  hideGridSelect?: boolean;
  nodeSelect?: {
    name?: string;
    customContent?: React.ReactElement<any>;
  };
  onChange: (options: KsHeaderOptions) => void;
  openMetricFilter: () => void;
}

interface ScopeData {
  isTop: boolean;
  data: any;
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

const MetricOperateBar = ({
  nodeSelect = {},
  hideNodeScope = false,
  hideGridSelect = false,
  onChange: onChangeCallback,
  openMetricFilter,
}: PropsType): JSX.Element => {
  const [gridNum, setGridNum] = useState<number>(GRID_SIZE_OPTIONS[1].value);
  const [rangeTime, setRangeTime] = useState<[number, number]>(() => {
    const curTimeStamp = moment().valueOf();
    return [curTimeStamp - 60 * 60 * 1000, curTimeStamp];
  });
  const [isRelativeRangeTime, setIsRelativeRangeTime] = useState(true);
  const [isAutoReload, setIsAutoReload] = useState(false);
  const [scopeData, setScopeData] = useState<ScopeData>({
    isTop: true,
    data: 5,
  });

  const sizeChange = (value: number) => setGridNum(value);

  const timeChange = (curRangeTime: [number, number], isRelative: boolean) => {
    setRangeTime([...curRangeTime]);
    setIsRelativeRangeTime(isRelative);
  };

  const reloadRangeTime = () => {
    if (isRelativeRangeTime) {
      const timeLen = rangeTime[1] - rangeTime[0] || 0;
      const curTimeStamp = moment().valueOf();
      setRangeTime([curTimeStamp - timeLen, curTimeStamp]);
    } else {
      setRangeTime([...rangeTime]);
    }
  };

  const nodeScopeChange = (data: any, isTop?: any) => {
    setScopeData({
      isTop,
      data,
    });
  };

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

  return (
    <>
      <div className="ks-chart-container">
        <div className="ks-chart-container-header">
          <div className="header-left">
            {/* 刷新 */}
            <div className="icon-box" onClick={reloadRangeTime}>
              <IconFont className="icon" type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" style={{ height: 20, top: 0 }} />
            {/* 时间选择 */}
            <DRangeTime timeChange={timeChange} rangeTimeArr={rangeTime} />
          </div>
          <div className="header-right">
            {/* 节点范围 */}
            {!hideNodeScope && (
              <NodeSelect name={nodeSelect.name || ''} onChange={nodeScopeChange}>
                {nodeSelect.customContent}
              </NodeSelect>
            )}
            {/* 分栏 */}
            {!hideGridSelect && (
              <Select className="grid-select" style={{ width: 70 }} value={gridNum} options={GRID_SIZE_OPTIONS} onChange={sizeChange} />
            )}
            {(!hideNodeScope || !hideGridSelect) && <Divider type="vertical" style={{ height: 20, top: 0 }} />}
            <Button type="primary" onClick={() => openMetricFilter()}>
              指标筛选
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default MetricOperateBar;
