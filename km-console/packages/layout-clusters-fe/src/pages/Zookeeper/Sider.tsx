import React, { useEffect, useState, useRef } from 'react';
import { Tree, SearchInput, AppContainer, Utils } from 'knowdesign';
import { DataNode, DetailMenuType, getPathByKey, updateTreeData } from './config';
import Api from '@src/api';

const { request } = Utils;

const ZKDetailMenu = (props: DetailMenuType) => {
  const { detailTreeData, setDetailInfoLoading, setPathList, setIdenKey, idenKey } = props;
  const [global] = AppContainer.useGlobalValue();
  const [treeData, setTreeData] = useState<DataNode[]>(detailTreeData);
  const [autoExpandParent, setAutoExpandParent] = useState<boolean>(true);
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [loadedKeys, setLoadedKeys] = useState<string[]>([]);
  const [childrenClose, setChildrenClose] = useState<boolean>(false);
  // const [searchValue, setSearchValue] = useState<string>('');

  // const treeRef = useRef();

  // 处理参数
  const getParams = (key: string, searchValue?: string) => {
    const path =
      '/' +
      getPathByKey(key, treeData)
        .map((item: any) => item.title)
        .join('/');
    return {
      path,
      keyword: searchValue ? searchValue : '',
    };
  };

  const onSelect = (selectedKeys: string[]) => {
    // 控制右侧详情内容的Loading
    setDetailInfoLoading(true);
    setIdenKey(selectedKeys);
  };

  const onLoadData = ({ key, children = null }: any) => {
    return new Promise<void>((resolve, reject) => {
      // 节点关闭，在展开要重新发送请求，以保证节点的准确性
      if (children && !childrenClose) {
        resolve();
        return;
      }
      request(Api.getZookeeperNodeChildren(+global?.clusterInfo?.id), { params: getParams(key) })
        .then((res: string[]) => {
          const newData =
            res && res.length > 0
              ? res.map((item: string, index: number) => {
                  return {
                    title: item,
                    key: `${key}-${index}`,
                  };
                })
              : [
                  {
                    title: '暂无子节点',
                    key: `${key}-${1}`,
                    disabled: true,
                    selectable: false,
                    isLeaf: true,
                  },
                ];
          setAutoExpandParent(true);
          setTreeData((origin: DataNode[]) => updateTreeData(origin, key, newData));
        })
        .finally(() => {
          return resolve();
        });
    });
  };

  const searchChange = (e: string) => {
    if (idenKey[0] && idenKey[0].length > 0) {
      request(Api.getZookeeperNodeChildren(+global?.clusterInfo?.id), { params: getParams(idenKey[0], e) }).then((res: string[]) => {
        const newData =
          res && res.length > 0
            ? res.map((item: string, index: number) => {
                return {
                  title: item,
                  key: `${idenKey[0]}-${index}`,
                };
              })
            : [
                // 如果查询不到节点或者所查询的父节点下没有子节点人为插入一个节点
                {
                  title: e ? '未搜索到相关节点' : '暂无子节点',
                  key: `${idenKey[0]}-${0}`,
                  disabled: true,
                  selectable: false,
                  isLeaf: true,
                },
              ];

        setTreeData((origin: DataNode[]) => {
          return updateTreeData(origin, idenKey[0], newData);
        });
        // 筛选打开的节点中非选中节点的其他节点（排除选中节点以及选中节点的叶子节点）
        const filterExpandedKeys = expandedKeys.filter((item) => item.slice(0, idenKey[0].length) !== idenKey[0]);
        // 将当前选中的节点再次合并
        const newExpandedKeys = [...filterExpandedKeys, ...idenKey];
        setExpandedKeys(newExpandedKeys);
        setLoadedKeys(newExpandedKeys);
        setAutoExpandParent(true);
      });
    }
  };

  // 展开收起
  const onExpand = (keys: any, arg: any) => {
    const { node, expanded } = arg;
    let filterExpandedKeys = keys;
    let newLoadKeys = loadedKeys;
    setChildrenClose(false);
    if (!expanded) {
      idenKey[0] !== node.key && (idenKey[0] as string)?.slice(0, node.key.length) === node.key && setIdenKey([]);
      filterExpandedKeys = keys.filter((item: string) => {
        return item !== node.key && item.slice(0, node.key.length) !== node.key;
      });
      newLoadKeys = loadedKeys.filter((i: string) => i.slice(0, node.key.length) !== node.key);
      setChildrenClose(true);
    }
    setAutoExpandParent(false);
    setExpandedKeys(filterExpandedKeys);
    setLoadedKeys(newLoadKeys);
  };

  const onLoad = (loadedKeys: string[]) => {
    setLoadedKeys(loadedKeys);
  };

  useEffect(() => {
    // treeRef?.current?.scrollTo({ key: idenKey[0] });
    const pathKey = getPathByKey(idenKey[0], treeData);
    setPathList(pathKey);
  }, [idenKey]);

  useEffect(() => {
    if (detailTreeData.length > 0) {
      setTreeData(detailTreeData);
    }
  }, [detailTreeData]);

  return (
    <>
      <SearchInput
        onSearch={searchChange}
        attrs={{
          placeholder: '选中节点后进行搜索',
          //   onChange: searchChange,
          size: 'small',
          style: {
            marginBottom: '15px',
          },
          // value: searchValue,
          // onChange: (value: string) => setSearchValue(value),
        }}
      />
      <div className="zk-detail-layout-left-content-text">
        <Tree
          // ref={treeRef}
          // height={300}
          onLoad={onLoad}
          loadData={onLoadData}
          loadedKeys={loadedKeys}
          expandedKeys={expandedKeys}
          selectedKeys={idenKey}
          onExpand={onExpand}
          autoExpandParent={autoExpandParent}
          onSelect={onSelect}
          treeData={treeData}
        />
      </div>
    </>
  );
};

export default ZKDetailMenu;
