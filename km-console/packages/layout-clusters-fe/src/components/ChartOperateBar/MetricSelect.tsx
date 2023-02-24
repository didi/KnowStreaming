import React, { useState, useEffect, forwardRef, useImperativeHandle, useRef } from 'react';
import { Drawer, Button, Space, Divider, AppContainer, ProTable, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { arrayMoveImmutable } from 'array-move';
import './style/indicator-drawer.less';
import { useLocation, useParams } from 'react-router-dom';
import api, { MetricType } from '@src/api';
import { MetricInfo, resolveMetricsRank } from '@src/constants/chartConfig';

export interface Inode {
  name: string;
  desc: string;
}

export interface MetricSelectProps extends React.HTMLAttributes<HTMLDivElement> {
  metricType: MetricType;
  hide?: boolean;
  drawerTitle?: string;
  selectedRows: (string | number)[];
  checkboxProps?: (record: any) => { [props: string]: any };
  tableData?: Inode[];
  submitCallback?: (value: (string | number)[]) => Promise<any>;
}

interface SelectedMetrics {
  [category: string]: string[];
}

type CategoryData = {
  category: string;
  metrics: {
    name: string;
    unit: string;
    desc: string;
  }[];
};

export const expandedRowColumns = [
  {
    title: '指标名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '单位',
    dataIndex: 'unit',
    key: 'unit',
  },
  {
    title: '描述',
    dataIndex: 'desc',
    key: 'desc',
  },
];

const ExpandedRow = ({ metrics, category, selectedMetrics, selectedMetricChange }: any) => {
  return (
    <div>
      <ProTable
        tableProps={{
          showHeader: false,
          noPagination: true,
          rowKey: 'name',
          columns: expandedRowColumns,
          dataSource: metrics,
          attrs: {
            rowSelection: {
              hideSelectAll: true,
              selectedRowKeys: selectedMetrics,
              onChange: (keys: string[]) => {
                selectedMetricChange(category, keys);
              },
            },
          },
        }}
      />
    </div>
  );
};

export const MetricSelect = forwardRef((metricSelect: MetricSelectProps, ref) => {
  const [global] = AppContainer.useGlobalValue();
  const { pathname } = useLocation();
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const [categoryData, setCategoryData] = useState<CategoryData[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [childrenSelectedRowKeys, setChildrenSelectedRowKeys] = useState<SelectedMetrics>({});
  const [visible, setVisible] = useState<boolean>(false);

  const columns = [
    {
      title: `${
        pathname.endsWith('/broker')
          ? 'Broker'
          : pathname.endsWith('/topic')
          ? 'Topic'
          : pathname.endsWith('/replication')
          ? 'MM2'
          : 'Cluster'
      } Metrics`,
      dataIndex: 'category',
      key: 'category',
    },
  ];

  const formateTableData = () => {
    const tableData = metricSelect.tableData;
    const categoryData: {
      [category: string]: {
        name: string;
        unit: string;
        desc: string;
      }[];
    } = {};

    tableData.forEach(({ name, desc }) => {
      const metricDefine = global.getMetricDefine(metricSelect?.metricType, name);
      const returnData = {
        name,
        desc,
        unit: metricDefine?.unit,
      };
      if (metricDefine?.category) {
        if (!categoryData[metricDefine.category]) {
          categoryData[metricDefine.category] = [returnData];
        } else {
          categoryData[metricDefine.category].push(returnData);
        }
      }
    });

    const result = Object.entries(categoryData).map(([category, data]) => ({
      category,
      metrics: data.sort((a, b) => Number(a.name > b.name) - 0.5),
    }));
    setCategoryData(result);
  };

  const formateSelectedKeys = () => {
    const newKeys = metricSelect?.selectedRows;
    const result: SelectedMetrics = {};
    const selectedCategories: string[] = [];

    newKeys?.forEach((name: string) => {
      const metricDefine = global.getMetricDefine(metricSelect?.metricType, name);
      if (metricDefine) {
        if (!result[metricDefine.category]) {
          result[metricDefine.category] = [name];
        } else {
          result[metricDefine.category].push(name);
        }
      }
    });

    Object.entries(result).forEach(([curCategory, metrics]) => {
      if (metrics.length) {
        selectedCategories.push(curCategory);
      }
    });

    setSelectedCategories(selectedCategories);

    setChildrenSelectedRowKeys((cur) => ({
      ...cur,
      ...result,
    }));
  };

  const rowChange = (newCategorys: string[]) => {
    newCategorys.sort();
    const prevCategories = selectedCategories;
    const addCategories: string[] = [];
    const delCategories: string[] = [];
    // 需要选中的指标
    newCategorys.forEach((name) => !prevCategories.includes(name) && addCategories.push(name));
    // 取消选中的指标
    prevCategories.forEach((name) => !newCategorys.includes(name) && delCategories.push(name));

    const changedCategories: SelectedMetrics = {};
    // 1. 选中，即选中所有子项
    addCategories.forEach((curCategory) => {
      let childrenData: string[] = [];
      categoryData.some(({ category, metrics }) => {
        if (curCategory === category) {
          childrenData = metrics.map(({ name }) => name);
          return true;
        }
        return false;
      });
      changedCategories[curCategory] = childrenData;
    });
    // 2. 取消选中，取消选中所有子项
    delCategories.forEach((curCategory) => {
      changedCategories[curCategory] = [];
    });

    setSelectedCategories(newCategorys);
    setChildrenSelectedRowKeys((cur) => ({
      ...cur,
      ...changedCategories,
    }));
  };

  const childrenRowChange = (curCategory: string, keys: string[]) => {
    // 更新选中的指标项
    setChildrenSelectedRowKeys((cur) => ({
      ...cur,
      [curCategory]: keys,
    }));
    // 更新父级选中状态
    if (keys.length === 0) {
      const i = selectedCategories.indexOf(curCategory);
      if (i !== -1) {
        const newCategories = [...selectedCategories];
        newCategories.splice(i, 1);
        setSelectedCategories(newCategories);
      }
    } else {
      const curCategoryData = categoryData.find((value) => value.category === curCategory);
      if (curCategoryData.metrics.length === keys.length) {
        setSelectedCategories([...selectedCategories, curCategory]);
      }
    }
  };

  const submitRowKeys = () => {
    setConfirmLoading(true);

    const allRowKeys: string[] = [];
    Object.entries(childrenSelectedRowKeys).forEach(([, arr]) => allRowKeys.push(...arr));

    metricSelect.submitCallback(allRowKeys).then(
      () => {
        setConfirmLoading(false);
        setVisible(false);
      },
      () => {
        setConfirmLoading(false);
      }
    );
  };

  const rowSelection = {
    selectedRowKeys: selectedCategories,
    onChange: rowChange,
    // getCheckboxProps: (record: any) => metricSelect.checkboxProps && metricSelect.checkboxProps(record),
    getCheckboxProps: (record: CategoryData) => {
      const isAllSelected = record.metrics.length === childrenSelectedRowKeys[record.category]?.length;
      const isNotCheck = !childrenSelectedRowKeys[record.category] || childrenSelectedRowKeys[record.category]?.length === 0;
      return {
        indeterminate: !isNotCheck && !isAllSelected,
      };
    },
  };

  useEffect(formateTableData, [metricSelect.tableData]);

  useEffect(() => {
    visible && formateSelectedKeys();
  }, [visible, metricSelect.selectedRows]);

  useImperativeHandle(
    ref,
    () => ({
      open: () => setVisible(true),
    }),
    []
  );

  return (
    <>
      <Drawer
        className="indicator-drawer"
        title={metricSelect.drawerTitle || '指标筛选'}
        width="868px"
        forceRender={true}
        onClose={() => setVisible(false)}
        visible={visible}
        maskClosable={false}
        extra={
          <Space>
            <Button size="small" onClick={() => setVisible(false)}>
              取消
            </Button>
            <Button
              type="primary"
              size="small"
              disabled={Object.entries(childrenSelectedRowKeys).every(([, arr]) => !arr.length)}
              loading={confirmLoading}
              onClick={submitRowKeys}
            >
              确定
            </Button>
            <Divider type="vertical" />
          </Space>
        }
      >
        <ProTable
          tableProps={{
            showHeader: false,
            rowKey: 'category',
            columns: columns,
            dataSource: categoryData,
            noPagination: true,
            attrs: {
              rowSelection: rowSelection,
              expandable: {
                expandRowByClick: true,
                expandedRowRender: (record: CategoryData) => (
                  <ExpandedRow
                    metrics={record.metrics}
                    category={record.category}
                    selectedMetrics={childrenSelectedRowKeys[record.category] || []}
                    selectedMetricChange={childrenRowChange}
                  />
                ),
                expandIcon: ({ expanded, onExpand, record }: any) => {
                  return expanded ? (
                    <IconFont
                      style={{ fontSize: '16px' }}
                      type="icon-xia"
                      onClick={(e: any) => {
                        onExpand(record, e);
                      }}
                    />
                  ) : (
                    <IconFont
                      style={{ fontSize: '16px' }}
                      type="icon-jiantou_1"
                      onClick={(e: any) => {
                        onExpand(record, e);
                      }}
                    />
                  );
                },
              },
            },
          }}
        />
      </Drawer>
    </>
  );
});

interface MetricsFilterProps {
  metricType: MetricType;
  onSelectChange: (list: (string | number)[], rankList: string[]) => void;
}

const MetricsFilter = forwardRef((props: MetricsFilterProps, ref) => {
  const { metricType, onSelectChange } = props;
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [metricsList, setMetricsList] = useState<MetricInfo[]>([]); // 指标列表
  const [metricRankList, setMetricRankList] = useState<string[]>([]);
  const [selectedMetricNames, setSelectedMetricNames] = useState<(string | number)[]>(undefined); // 默认选中的指标的列表
  const metricSelectRef = useRef(null);

  // 更新指标
  const setMetricList = (metricDetailDTOList: { metric: string; rank: number; set: boolean }[]) => {
    return Utils.request(api.getDashboardMetricList(clusterId, metricType), {
      method: 'POST',
      data: {
        metricDetailDTOList,
      },
    });
  };

  // 图表展示顺序变更
  const rankChange = (oldIndex: number, newIndex: number) => {
    const newList = arrayMoveImmutable(metricRankList, oldIndex, newIndex);
    setMetricRankList(newList);
    setMetricList(newList.map((metric, rank) => ({ metric, rank, set: metricsList.find(({ name }) => metric === name)?.set || false })));
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
    Utils.request(api.getDashboardMetricList(clusterId, metricType)).then((res: MetricInfo[] | null) => {
      if (!res) return;
      const supportMetrics = res.filter((metric) => metric.support);
      const selectedMetrics = supportMetrics.filter((metric) => metric.set).map((metric) => metric.name);
      updateRank([...supportMetrics]);
      setMetricsList(supportMetrics);
      setSelectedMetricNames(selectedMetrics);
    });
  };

  // 指标选中项更新回调
  const metricSelectCallback = (newMetricNames: (string | number)[]) => {
    const updateMetrics: { metric: string; set: boolean; rank: number }[] = [];
    // 需要选中的指标
    newMetricNames.forEach(
      (name) =>
        !selectedMetricNames.includes(name) &&
        updateMetrics.push({ metric: name as string, set: true, rank: metricsList.find(({ name: metric }) => metric === name)?.rank })
    );
    // 取消选中的指标
    selectedMetricNames.forEach(
      (name) =>
        !newMetricNames.includes(name) &&
        updateMetrics.push({ metric: name as string, set: false, rank: metricsList.find(({ name: metric }) => metric === name)?.rank })
    );

    const requestPromise = Object.keys(updateMetrics).length ? setMetricList(updateMetrics) : Promise.resolve();
    requestPromise.then(
      () => getMetricList(),
      () => getMetricList()
    );

    return requestPromise;
  };

  useEffect(() => {
    onSelectChange(selectedMetricNames, metricRankList);
  }, [selectedMetricNames, metricRankList]);

  useEffect(() => {
    getMetricList();
  }, []);

  useImperativeHandle(ref, () => ({
    rankChange,
    open: () => metricSelectRef.current?.open(),
  }));

  return (
    <MetricSelect
      ref={metricSelectRef}
      metricType={metricType}
      tableData={metricsList}
      selectedRows={selectedMetricNames}
      submitCallback={metricSelectCallback}
    />
  );
});

export default MetricsFilter;
