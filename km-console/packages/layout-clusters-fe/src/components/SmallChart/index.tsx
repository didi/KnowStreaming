import React from 'react';
import { SingleChart, Spin } from 'knowdesign';

const CHART_CONFIG = {
  grid: {
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  xAxis: {
    show: false,
    type: 'category',
    axisLabel: {
      show: false,
    },
  },
  yAxis: {
    type: 'value',
    show: false,
    axisLabel: {
      show: false,
    },
  },
  legend: {
    show: false,
  },
  toolBox: {
    show: false,
  },
  tooltip: {
    show: false,
  },
};

const LINE_CONFIG = {
  lineStyle: {
    color: '#556EE6',
    width: 1,
    smooth: 0.25,
  },
  areaStyle: {
    color: {
      type: 'linear',
      x: 0,
      y: 0,
      x2: 0,
      y2: 1,
      colorStops: [
        {
          offset: 0,
          color: 'rgba(178,191,255,0.24)',
        },
        {
          offset: 1,
          color: 'rgba(142,167,244,0.04)', // 100% 处的颜色
        },
      ],
      global: false, // 缺省为 false
    },
  },
};

const SmallChart = (props: {
  width: string | number;
  height: string | number;
  loading?: boolean;
  chartData: {
    name: string;
    data: [number | string, number | string];
  };
}) => {
  const { chartData, loading = false, width, height } = props;

  if (loading) {
    return <Spin spinning={loading} style={{ width, height }} />;
  }

  if (!chartData || !chartData?.data?.length) {
    return <div style={{ width, height }}></div>;
  }

  return (
    <SingleChart
      wrapStyle={{
        width,
        height,
        zIndex: 0,
        minWidth: '82px',
      }}
      option={CHART_CONFIG}
      chartTypeProp="line"
      propChartData={[chartData]}
      seriesCallback={(lines: any) => {
        return lines.map((line: any) => {
          line.data.sort((a: any, b: any) => a[0] - b[0]);
          return {
            ...line,
            ...LINE_CONFIG,
          };
        });
      }}
    />
  );
};

export default SmallChart;
