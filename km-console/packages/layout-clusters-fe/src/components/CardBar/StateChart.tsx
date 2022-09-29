import React, { useRef, useEffect } from 'react';
import * as echarts from 'echarts';

type EChartsOption = echarts.EChartsOption;

const EchartsExample = (props: any) => {
  const lineRef = useRef<any>(null);
  const myChartRef = useRef<any>(null);

  useEffect(() => {
    initChart();
  }, []);

  /**
   * 初始化
   */
  const initChart = () => {
    myChartRef.current = echarts.init(lineRef.current);
    // let data = [];
    // const data = props;

    const option: any = {
      xAxis: {
        type: 'category',
        data: ['bigNu', 'betweenNu', 'smallNu'],
        show: false,
        nameLocationm: 'start',
        axisLabel: {
          show: false,
        },
      },
      yAxis: {
        type: 'value',
        show: false,
        // boundaryGap: false,
        // splitNumber: 10,
        // max: 10,
        min: 0,
        axisLabel: {
          show: false,
        },
        // offset: 40,
      },
      grid: {
        top: 0,
        right: 0,
        left: 0,
        bottom: 0,
      },
      series: [
        {
          data: props?.data,
          type: 'bar',
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180, 180, 180, 0.2)',
          },
          itemStyle: {
            normal: {
              color: (params: any) => {
                // 定义一个颜色数组colorList
                const colorList = ['#FF7066', '#00C0A2', '#FF7066'];
                return colorList[params.dataIndex];
              },
            },
          },
          barWidth: '12px',
        },
      ],
    };
    myChartRef.current && myChartRef.current.setOption(option, true);
  };

  return <div ref={lineRef} style={{ width: '100%', height: '100%' }}></div>;
};

export default EchartsExample;
