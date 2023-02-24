import React, { useEffect, useMemo, useRef, useState } from 'react';
import { DataNode } from 'knowdesign/lib/basic/tree';
import { useParams } from 'react-router-dom';
import { Tree, Input, Utils, Button } from 'knowdesign';
import api from '@src/api';
import type { ConnectCluster } from '../Connect/AddConnector';

export interface Connector {
  connectClusterId: number;
  connectClusterName: string;
  connectorName: string;
}

interface SelectContentProps {
  title: string;
  scopeList: {
    connectClusters: ConnectCluster[];
    connectors: Connector[];
  };
  isTop?: boolean;
  visibleChange?: (v: boolean) => void;
  onChange?: (list: any, inputValue: string) => void;
}

interface CheckedNodes {
  connectClusters: number[];
  connectors: { connectClusterId: number; connectorName: string }[];
}

interface CheckedKeysProps {
  checked: React.Key[];
  halfChecked: React.Key[];
}

const getParentKey = (key: React.Key, tree: DataNode[]): React.Key => {
  let parentKey: React.Key;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};

const SelectContent = (props: SelectContentProps) => {
  const { isTop, visibleChange, onChange } = props;
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [treeData, setTreeData] = useState<DataNode[]>([]);
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [checkedKeys, setCheckedKeys] = useState<CheckedKeysProps>({
    checked: [],
    halfChecked: [],
  });
  const [checkedNodes, setCheckedNodes] = useState<CheckedNodes>({
    connectClusters: [],
    connectors: [],
  });
  const defaultChecked = useRef<CheckedKeysProps>({
    checked: [],
    halfChecked: [],
  });

  const onExpand = (newExpandedKeys: string[]) => {
    setExpandedKeys(newExpandedKeys);
    setAutoExpandParent(false);
  };

  const onCheck = (keys: CheckedKeysProps, { checkedNodes }: { checkedNodes: DataNode[] }) => {
    const returnData: CheckedNodes = {
      connectClusters: [],
      connectors: [],
    };
    checkedNodes.map((node) => {
      if (node.children) {
        returnData.connectClusters.push(node.key as number);
      } else {
        const [id, ...rest] = (node.key as string).split(':');
        returnData.connectors.push({
          connectClusterId: Number(id),
          connectorName: rest.join(':'),
        });
      }
    });
    setCheckedNodes(returnData);
    setCheckedKeys(
      keys as {
        checked: any[];
        halfChecked: any[];
      }
    );
  };

  const onSubmit = () => {
    onChange(checkedNodes, `${checkedNodes.connectClusters.length + checkedNodes.connectors.length}项`);
  };

  const onCancel = () => {
    visibleChange(false);
    setCheckedKeys(defaultChecked.current);
  };

  const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    let newExpandedKeys: React.Key[] = [];
    treeData.forEach((item) => {
      if (String(item.title).indexOf(value) > -1) {
        newExpandedKeys.push(getParentKey(item.key, treeData));
      }
      item.children?.forEach((item) => {
        if (String(item.title).indexOf(value) > -1) {
          newExpandedKeys.push(getParentKey(item.key, treeData));
        }
      });
    });
    newExpandedKeys = newExpandedKeys.filter((item, i, self) => item && self.indexOf(item) === i);
    setExpandedKeys(newExpandedKeys as React.Key[]);
    setSearchValue(value);
    setAutoExpandParent(true);
  };

  const filterTreeData = useMemo(() => {
    const loop = (data: DataNode[]): DataNode[] =>
      data.map((item) => {
        const strTitle = item.title as string;
        const index = strTitle.indexOf(searchValue);
        const beforeStr = strTitle.substring(0, index);
        const afterStr = strTitle.slice(index + searchValue.length);
        const title =
          index > -1 ? (
            <span>
              {beforeStr}
              <span className="site-tree-search-value">{searchValue}</span>
              {afterStr}
            </span>
          ) : (
            <span>{strTitle}</span>
          );
        if (item.children) {
          return { title, key: item.key, children: loop(item.children) };
        }

        return {
          title,
          key: item.key,
        };
      });
    return loop(treeData);
  }, [treeData, searchValue]);

  // 获取节点范围列表
  const getScopeList = async () => {
    const clustersMap: { [key: string]: DataNode } = {};
    props.scopeList.connectClusters.forEach((connectCluster) => {
      clustersMap[connectCluster.id] = {
        title: connectCluster.name,
        key: connectCluster.id,
        children: [],
      };
    });
    props.scopeList.connectors.forEach((connector) => {
      const targetConnectCluster = clustersMap[connector.connectClusterId];
      if (targetConnectCluster) {
        targetConnectCluster.children.push({
          title: connector.connectorName,
          key: `${connector.connectClusterId}:${connector.connectorName}`,
        });
      }
    });
    setTreeData(Object.values(clustersMap));
  };

  useEffect(() => {
    if (isTop) {
      setCheckedKeys({
        checked: [],
        halfChecked: [],
      });
      setCheckedNodes({
        connectClusters: [],
        connectors: [],
      });
      defaultChecked.current = {
        checked: [],
        halfChecked: [],
      };
    }
  }, [isTop]);

  useEffect(() => {
    getScopeList();
  }, [props.scopeList]);

  return (
    <>
      <h6 className="time_title">{props.title}</h6>
      <div className="custom-scope dashboard-custom-scope">
        <div style={{ padding: '0 16px 12px 16px' }}>
          <Input size="small" placeholder="请输入内容筛选" onChange={onInputChange} />
        </div>
        <Tree
          className="connect-dashboard-option-tree"
          checkable
          checkStrictly
          treeData={filterTreeData}
          checkedKeys={checkedKeys}
          expandedKeys={expandedKeys}
          autoExpandParent={autoExpandParent}
          onExpand={onExpand}
          onCheck={onCheck}
        />
        <div className="btn-con">
          <Button type="primary" size="small" className="btn-sure" onClick={onSubmit} disabled={checkedKeys.checked.length === 0}>
            确定
          </Button>
          <Button size="small" onClick={onCancel}>
            取消
          </Button>
        </div>
      </div>
    </>
  );
};

export default SelectContent;
