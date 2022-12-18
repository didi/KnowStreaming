import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import api, { MetricType } from '@src/api';
import { arrayMoveImmutable } from 'array-move';
import { expandedRowColumns } from '@src/components/ChartOperateBar/MetricSelect';
import { TreeTableDataSourceType, TreeTableDrawer, TreeTableData, CheckboxStatus } from './MetricSelect';
import { MetricInfo } from '@src/constants/chartConfig';
import { forwardRef } from 'react';
import { AppContainer, Utils } from 'knowdesign';
import { useParams } from 'react-router-dom';

interface MetricsFilterProps {
  metricType: MetricType[];
  onSelectChange: (list: MetricInfo[]) => void;
  onRankChange: (rankList: string[]) => void;
}

// 处理图表排序
const resolveMetricsRank = (metricList: MetricInfo[]) => {
  const isRanked = metricList.some(({ rank }) => rank !== null);
  const listInfo: { [key: string]: { metric: string; rank: number; set: boolean }[] } = {};
  let shouldUpdate = false;
  let sortedList: MetricInfo[] = [];

  if (isRanked) {
    const rankedMetrics = metricList.filter(({ rank }) => rank !== null).sort((a, b) => a.rank - b.rank);
    const unRankedMetrics = metricList.filter(({ rank }) => rank === null);
    // 如果有新增/删除指标的情况，需要触发更新
    if (unRankedMetrics.length || rankedMetrics.some(({ rank }, i) => rank !== i)) {
      shouldUpdate = true;
    }
    sortedList = [...rankedMetrics, ...unRankedMetrics.sort((a, b) => Number(a.name > b.name) - 0.5)];
  } else {
    shouldUpdate = true;
    // 按字母先后顺序初始化指标排序
    sortedList = metricList.sort((a, b) => a.type - b.type).sort((a, b) => (a.type !== b.type ? 1 : Number(a.name > b.name) - 0.5));
  }

  sortedList.forEach((metric, rank) => {
    !listInfo[metric.type] && (listInfo[metric.type] = []);
    listInfo[metric.type].push({ metric: metric.name, rank, set: metricList.find(({ name }) => metric.name === name)?.set || false });
  });

  return {
    list: sortedList.map(({ name }) => name),
    listInfo,
    shouldUpdate,
  };
};

const MetricsFilter = forwardRef((props: MetricsFilterProps, ref) => {
  const [global] = AppContainer.useGlobalValue();
  const { metricType, onSelectChange, onRankChange } = props;
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [metricsList, setMetricsList] = useState<MetricInfo[]>([]); // 指标列表
  const [metricRankList, setMetricRankList] = useState<string[]>([]);
  const [tree, setTree] = useState<TreeTableData>();
  const metricSelectRef = useRef(null);

  // 更新指标
  const setMetricList = (list: { [key: string]: { metric: string; rank: number; set: boolean }[] }) => {
    const reqArr: Promise<any>[] = [];
    Object.entries(list).forEach(([type, metricDetailDTOList]) => {
      reqArr.push(
        Utils.request(api.getDashboardMetricList(clusterId, type as unknown as MetricType), {
          method: 'POST',
          data: {
            metricDetailDTOList,
          },
        })
      );
    });
    return Promise.all(reqArr);
  };

  // TODO: 图表展示顺序变更
  const rankChange = (oldIndex: number, newIndex: number) => {
    const newList = arrayMoveImmutable(metricRankList, oldIndex, newIndex);
    setMetricRankList(newList);
    const updates: { [key: string]: { metric: string; rank: number; set: boolean }[] } = {};
    newList.forEach((metric, rank) => {
      const targetMetric = metricsList.find(({ name }) => metric === name);
      if (targetMetric) {
        const info = { metric, rank, set: targetMetric?.set || false };
        updates[targetMetric.type] ? updates[targetMetric.type].push(info) : (updates[targetMetric.type] = [info]);
      }
    });
    setMetricList(updates);
  };

  // 更新 rank
  const updateRank = (metricList: MetricInfo[]) => {
    const { list, listInfo, shouldUpdate } = resolveMetricsRank(metricList);
    setMetricRankList(list);
    if (shouldUpdate) {
      setMetricList(listInfo);
    }
  };

  // 获取指标列表
  const getMetricList = () => {
    Promise.all(metricType.map((type) => Utils.request(api.getDashboardMetricList(clusterId, type)))).then((list) => {
      let allSupportMetrics: MetricInfo[] = [];

      const treeData: TreeTableData = {
        showHeader: true,
        rowKey: 'category',
        columns: [{ title: '分类', dataIndex: 'category' }],
        dataSource: [],
      };

      (list as unknown as (MetricInfo[] | null)[]).forEach((res, i) => {
        if (!res) return;
        const supportMetrics = res.filter((metric) => metric.support);
        allSupportMetrics = allSupportMetrics.concat(supportMetrics);

        // 处理结构
        const data: TreeTableDataSourceType = {
          category: metricType[i] === MetricType.Connect ? 'Connect Cluster' : 'Connector',
        };
        const categoryData: {
          [category: string]: {
            name: string;
            unit: string;
            desc: string;
          }[];
        } = {};
        supportMetrics.forEach(({ name, desc, set }) => {
          const metricDefine = global.getMetricDefine(metricType[i], name);
          const returnData = {
            type: metricType[i],
            set,
            name,
            desc,
            unit: metricDefine?.unit,
          };
          if (metricDefine.category) {
            if (!categoryData[metricDefine.category]) {
              categoryData[metricDefine.category] = [returnData];
            } else {
              categoryData[metricDefine.category].push(returnData);
            }
          } else {
            if (!categoryData['Other']) {
              categoryData['Other'] = [returnData];
            } else {
              categoryData['Other'].push(returnData);
            }
          }
        });

        if (Object.keys(categoryData).length > 1) {
          const returnData: TreeTableData = {
            showHeader: false,
            rowKey: 'category',
            columns: [{ title: '分类', dataIndex: 'category' }],
            dataSource: [],
          };
          Object.entries(categoryData).forEach(([category, data]) => {
            returnData.dataSource.push({
              category,
              children: {
                showHeader: true,
                rowKey: 'name',
                columns: expandedRowColumns,
                dataSource: data,
              },
            });
          });
          data.children = returnData;
        } else {
          data.children = {
            showHeader: true,
            rowKey: 'name',
            columns: expandedRowColumns,
            dataSource: Object.values(categoryData)[0] || [],
          };
        }
        treeData.dataSource.push(data);
      });
      setTree(treeData);

      updateRank([...allSupportMetrics]);
      setMetricsList(allSupportMetrics);
    });
  };

  // TODO: 指标选中项更新回调
  const metricSelectCallback = ([newMetrics]: [CheckboxStatus[], any]) => {
    const updateMetrics: { [key: string]: { metric: string; set: boolean; rank: number }[] } = {};

    newMetrics.forEach((metric) => {
      const oldMetric = metricsList.find(({ type, name }) => type === metric.type && name === metric.key);

      if (oldMetric) {
        if (oldMetric.set !== metric.status) {
          if (updateMetrics[metric.type]) {
            updateMetrics[metric.type].push({ metric: oldMetric.name, set: !oldMetric.set, rank: oldMetric.rank });
          } else {
            updateMetrics[metric.type] = [{ metric: oldMetric.name, set: !oldMetric.set, rank: oldMetric.rank }];
          }
        }
      }
    });

    const requestPromise = Object.keys(updateMetrics).length ? setMetricList(updateMetrics) : Promise.resolve();
    requestPromise.then(
      () => getMetricList(),
      () => getMetricList()
    );
    return requestPromise;
  };

  useEffect(() => {
    onSelectChange(metricsList);
  }, [metricsList]);

  useEffect(() => {
    onRankChange(metricRankList);
  }, [metricRankList]);

  useEffect(() => {
    getMetricList();
  }, []);

  useImperativeHandle(ref, () => ({
    rankChange,
    open: () => metricSelectRef.current?.open(),
  }));

  return tree && <TreeTableDrawer ref={metricSelectRef} tree={tree} checkField="set" submitCallback={metricSelectCallback} />;
});

export default MetricsFilter;
