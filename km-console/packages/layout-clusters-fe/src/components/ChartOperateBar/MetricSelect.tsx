import React, { useState, useEffect, forwardRef, useImperativeHandle } from 'react';
import { Drawer, Button, Space, Divider, AppContainer, ProTable, Utils } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { MetricSelect } from './index';
import './style/indicator-drawer.less';
import { useLocation } from 'react-router-dom';

interface PropsType extends React.HTMLAttributes<HTMLDivElement> {
  metricSelect: MetricSelect;
}

interface MetricInfo {
  name: string;
  unit: string;
  desc: string;
}

interface SelectedMetrics {
  [category: string]: string[];
}

type CategoryData = {
  category: string;
  metrics: MetricInfo[];
};

const expandedRowColumns = [
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
    <div
      style={{
        position: 'relative',
        padding: '12px 16px',
        margin: '0 7px',
        border: '1px solid #EFF2F7',
        borderRadius: '8px',
        backgroundColor: '#ffffff',
      }}
    >
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

const MetricSelect = forwardRef(({ metricSelect }: PropsType, ref) => {
  const [global] = AppContainer.useGlobalValue();
  const { pathname } = useLocation();
  const [confirmLoading, setConfirmLoading] = useState<boolean>(false);
  const [categoryData, setCategoryData] = useState<CategoryData[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [childrenSelectedRowKeys, setChildrenSelectedRowKeys] = useState<SelectedMetrics>({});
  const [visible, setVisible] = useState<boolean>(false);

  const columns = [
    {
      title: `${pathname.endsWith('/broker') ? 'Broker' : pathname.endsWith('/topic') ? 'Topic' : 'Cluster'} Metrics`,
      dataIndex: 'category',
      key: 'category',
    },
  ];

  const formateTableData = () => {
    const tableData = metricSelect.tableData;
    const categoryData: {
      [category: string]: MetricInfo[];
    } = {};

    tableData.forEach(({ name, desc }) => {
      const metricDefine = global.getMetricDefine(metricSelect?.metricType, name);
      const returnData = {
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
      }
    });

    const result = Object.entries(categoryData).map(([category, data]) => ({
      category,
      metrics: data.sort((a, b) => Number(a.name > b.name) - 0.5),
    }));
    setCategoryData(result);
  };

  const formateSelectedKeys = () => {
    const newKeys = metricSelect.selectedRows;
    const result: SelectedMetrics = {};
    const selectedCategories: string[] = [];

    newKeys.forEach((name: string) => {
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

export default MetricSelect;
