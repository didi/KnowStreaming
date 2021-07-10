import { ILabelValue, IBill } from 'types/base-type';

export const getBillBarOption = (data: IBill[]) => {
  const xData: string[] = [];
  const yData: number[] = [];

  if (data) {
    data.forEach(item => {
      xData.push(item.gmtMonth);
      yData.push(item.cost);
    });
  }
  return {
    color: ['#3398DB'],
    tooltip: {
      trigger: 'axis',
      axisPointer: {            // 坐标轴指示器，坐标轴触发有效
        type: 'line',        // 默认为直线，可选为：'line' | 'shadow'
      },
    },
    xAxis: [
      {
        type: 'category',
        data: xData,
        axisTick: {
          alignWithLabel: true,
        },
      },
    ],
    yAxis: [
      {
        type: 'value',
      },
    ],
    grid: {
      left: '1%',
      right: '1%',
      bottom: '3%',
      top: '40px',
      containLabel: true,
    },
    series: yData.length ? [
      {
        name: '金额',
        type: 'bar',
        barWidth: '60%',
        data: yData,
      },
    ] : null,
  };
};

export const getPieChartOption = (data: ILabelValue[], legend: string[]) => {
  const color = legend.length > 2 ? ['#4BD863', '#3399ff', '#F19736', '#F04844', '#999999'] : ['#F28E61', '#7082A6'];
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: '35%',
      data: legend,
    },
    color,
    series: data && data.length ? [
      {
        name: '',
        type: 'pie',
        radius: ['50%', '35%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center',
        },
        emphasis: {
          label: {
            // show: true,
            show: false,
            fontSize: '16',
            fontWeight: 'bold',
          },
        },
        labelLine: {
          show: false,
        },
        data,
      },
    ] : [],
  };
};
