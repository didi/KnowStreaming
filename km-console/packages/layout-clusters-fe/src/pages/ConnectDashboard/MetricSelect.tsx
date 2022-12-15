import React, { useState, useEffect, forwardRef, useImperativeHandle, useRef } from 'react';
import { Drawer, Button, Space, Divider, ProTable } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import { ITableColumnsType } from 'knowdesign/lib/extend/d-table';
import { cloneDeep } from 'lodash';
import { MetricType } from '@src/api';
import '../../components/ChartOperateBar/style/indicator-drawer.less';

export interface TreeTableDataSourceType {
  set?: boolean;
  children?: TreeTableData;
  [key: string]: any;
}

export interface TreeTableData {
  showHeader: boolean;
  rowKey: string;
  columns: ITableColumnsType[];
  dataSource: TreeTableDataSourceType[];
}

export interface CheckboxStatus {
  type: MetricType;
  key: string;
  status: boolean | 'indeterminate';
  children?: CheckboxStatus[];
}

export interface TreeTableProps {
  tree: TreeTableData;
  checkField: string;
  submitCallback: ([leafs, checkboxData]: [any[], CheckboxStatus]) => Promise<any>;
}

const TreeTableItem = (props: {
  treeData: TreeTableData;
  checkboxData: CheckboxStatus[];
  keyTrace: (string | number)[];
  onKeyChange: any;
}) => {
  const { treeData, checkboxData: data, keyTrace, onKeyChange } = props;
  return (
    <ProTable
      tableProps={{
        noPagination: true,
        showHeader: false,
        rowKey: treeData.rowKey,
        columns: treeData.columns,
        dataSource: treeData.dataSource,
        attrs: Object.assign(
          {
            showHeader: treeData.showHeader,
            rowSelection: {
              hideSelectAll: keyTrace.length !== 0,
              selectedRowKeys: data.filter((item) => item.status === true).map((item) => item.key),
              onChange: (keys: string[]) => {
                onKeyChange(keyTrace, keys);
              },
              getCheckboxProps: (record: TreeTableDataSourceType) => {
                return {
                  indeterminate: data.some((item) => item.key === record[treeData.rowKey] && item.status === 'indeterminate'),
                };
              },
            },
          },
          treeData.dataSource.some((item) => item.children)
            ? {
                expandable: {
                  childrenColumnName: 'notExist',
                  expandRowByClick: true,
                  expandedRowRender: (record: TreeTableDataSourceType) => {
                    return record?.children ? (
                      <TreeTableItem
                        keyTrace={[...keyTrace, record[treeData.rowKey]]}
                        treeData={record.children}
                        checkboxData={data.find((item) => record[treeData.rowKey] === item.key)?.children}
                        onKeyChange={onKeyChange}
                      />
                    ) : (
                      <></>
                    );
                  },
                  rowExpandable: (record: TreeTableDataSourceType) => {
                    return !!record?.children;
                  },
                  expandIcon: ({ expanded, onExpand, record }: { expanded: boolean; onExpand: any; record: TreeTableDataSourceType }) => {
                    return record?.children ? (
                      expanded ? (
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
                      )
                    ) : (
                      <></>
                    );
                  },
                },
              }
            : {}
        ),
      }}
    />
  );
};

export const TreeTable = forwardRef((props: TreeTableProps, ref) => {
  const [treeData, setTreeData] = useState(props.tree);
  const [checkboxData, setCheckboxData] = useState<CheckboxStatus>();

  // 根据链条查找指定选择框
  const findTargetCheckbox = (checkboxData: CheckboxStatus, keyTrace: (string | number)[]) => {
    if (keyTrace.length === 0) {
      return checkboxData;
    }

    let targetCheckbox: CheckboxStatus = checkboxData;
    keyTrace.forEach((key) => {
      Object.values(targetCheckbox?.children).some((checkbox) => {
        if (key === checkbox.key) {
          targetCheckbox = checkbox;
          return true;
        } else {
          return false;
        }
      });
    });
    return targetCheckbox;
  };

  const changeStatus = (checkbox: CheckboxStatus, type: CheckboxStatus['status']) => {
    checkbox.status = type;
    (checkbox?.children || []).forEach((c) => changeStatus(c, type));
  };

  // 更新节点状态
  const updateCheckStatus = (data: CheckboxStatus, keyTrace: (string | number)[], keys: string[]) => {
    // 1. 设置更新元素及其子元素的状态
    const targetCheckbox = findTargetCheckbox(data, keyTrace);
    (targetCheckbox?.children || []).forEach((checkbox) => {
      if (keys.includes(checkbox.key)) {
        changeStatus(checkbox, true);
      } else if (checkbox.status === true) {
        changeStatus(checkbox, false);
      }
    });

    // 2. 更新自身状态
    targetCheckbox.status = keys?.length ? (keys.length === targetCheckbox?.children?.length ? true : 'indeterminate') : false;

    // 3. 更新父级选中状态
    for (let i = 1; i < keyTrace.length; i++) {
      const newTrace = keyTrace.slice(0, keyTrace.length - i);
      const newCheckbox = findTargetCheckbox(data, newTrace);
      const trueLen = newCheckbox?.children.map((item) => item.status === true).filter((s) => s).length;
      newCheckbox.status = trueLen === 0 ? false : trueLen === newCheckbox?.children.length ? true : 'indeterminate';
    }
  };

  const onKeyChange = (keyTrace: (string | number)[], keys: string[]) => {
    const clonedCheckboxData = cloneDeep(checkboxData);
    updateCheckStatus(clonedCheckboxData, keyTrace, keys);
    setCheckboxData(clonedCheckboxData);
  };

  // 生成初始选中结构
  const renderInitCheckboxData = (
    tree: TreeTableData,
    curCheckboxData: CheckboxStatus,
    wholeCheckboxData: CheckboxStatus,
    keyTrace: (string | number)[],
    callbacks: (() => void)[]
  ) => {
    curCheckboxData.children = [];
    const selectedKeys: string[] = [];

    tree.dataSource.forEach((item) => {
      const data = {
        type: item.type,
        key: item[tree.rowKey],
        status: false,
      };
      curCheckboxData.children.push(data);
      if (item.children) {
        renderInitCheckboxData(item.children, data, wholeCheckboxData, [...keyTrace, item[tree.rowKey]], callbacks);
      } else if (item.set) {
        selectedKeys.push(item[tree.rowKey]);
      }
    });

    if (selectedKeys.length) {
      callbacks.push(() => updateCheckStatus(wholeCheckboxData, keyTrace, selectedKeys));
    }
  };

  // 获取叶子节点状态数组
  const getLeafNodes = (checkboxData: CheckboxStatus, arr: any[]) => {
    checkboxData?.children.forEach((item) => {
      if (item?.children) {
        getLeafNodes(item, arr);
      } else {
        arr.push({ ...item });
      }
    });
  };

  const getLeafCheckboxData = () => {
    const leafs: any[] = [];
    getLeafNodes(checkboxData, leafs);
    return [leafs, checkboxData];
  };

  // 初始化选中状态数据结构
  useEffect(() => {
    const initCheckbox: CheckboxStatus = {
      type: undefined,
      key: undefined,
      status: false,
    };
    const callbacks: (() => void)[] = [];
    renderInitCheckboxData(treeData, initCheckbox, initCheckbox, [], callbacks);
    callbacks.forEach((cb) => cb());
    setCheckboxData(initCheckbox);
  }, [treeData]);

  useEffect(() => {
    setTreeData(props.tree);
  }, [props.tree]);

  useImperativeHandle(ref, () => ({
    getLeafCheckboxData,
  }));

  return checkboxData ? (
    <TreeTableItem treeData={treeData} checkboxData={checkboxData?.children} keyTrace={[]} onKeyChange={onKeyChange} />
  ) : (
    <></>
  );
});

export const TreeTableDrawer = forwardRef((props: TreeTableProps, ref) => {
  const [visible, setVisible] = useState(false);
  const [confirmLoading, setConfirmLoading] = useState(false);
  const treeTableRef = useRef(null);

  const onSubmit = () => {
    setConfirmLoading(true);
    props
      .submitCallback(treeTableRef.current?.getLeafCheckboxData())
      .then(() => {
        setVisible(false);
      })
      .finally(() => {
        setConfirmLoading(false);
      });
  };

  useImperativeHandle(ref, () => ({
    open: () => setVisible(true),
  }));

  return (
    <Drawer
      className="indicator-drawer"
      title="指标筛选"
      width="868px"
      forceRender={true}
      onClose={() => setVisible(false)}
      visible={visible}
      maskClosable={false}
      destroyOnClose
      extra={
        <Space>
          <Button size="small" onClick={() => setVisible(false)}>
            取消
          </Button>
          <Button type="primary" size="small" loading={confirmLoading} onClick={onSubmit}>
            确定
          </Button>
          <Divider type="vertical" />
        </Space>
      }
    >
      <TreeTable ref={treeTableRef} {...props} />
    </Drawer>
  );
});
