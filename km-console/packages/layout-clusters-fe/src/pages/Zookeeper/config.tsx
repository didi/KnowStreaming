import React from 'react';
import { Tag } from 'knowdesign';
export interface DetailMenuType {
  detailTreeData: any;
  setDetailInfoLoading: (loading: boolean) => void;
  setPathList: (pathList: DataNode[]) => void;
  setIdenKey: (idenKey: string[]) => void;
  idenKey: string[];
}
export interface DataNode {
  title: string;
  key: string;
  isLeaf?: boolean;
  children?: DataNode[];
}

// 角色
const roleType: any = {
  leader: 'Leader',
  follower: 'Follower',
  ovsever: 'Obsever',
};

export const updateTreeData = (list: DataNode[], key: React.Key, children: DataNode[]): DataNode[] => {
  return list.map((node) => {
    if (node.key === key) {
      return {
        ...node,
        children,
      };
    }
    if (node.children) {
      return {
        ...node,
        children: updateTreeData(node.children, key, children),
      };
    }
    return node;
  });
};

export const getZookeeperColumns = (arg?: any) => {
  const columns = [
    {
      title: 'Host',
      dataIndex: 'host',
      key: 'host',
      width: 200,
      render: (t: string, r: any) => {
        return (
          <span>
            {t}
            {r?.status ? <Tag className="tag-success">Live</Tag> : <Tag className="tag-error">Down</Tag>}
          </span>
        );
      },
    },
    {
      title: 'Port',
      dataIndex: 'port',
      key: 'port',
      width: 200,
    },
    {
      title: 'Version',
      dataIndex: 'version',
      key: 'version',
      width: 200,
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
      width: 200,
      render(t: string, r: any) {
        return (
          <Tag
            style={{
              background: t === 'leader' ? 'rgba(85,110,230,0.10)' : t === 'follower' ? 'rgba(0,192,162,0.10)' : '#fff3e4',
              color: t === 'leader' ? '#556EE6' : t === 'follower' ? '#00C0A2' : '#F58342',
              padding: '3px 6px',
            }}
          >
            {roleType[t]}
          </Tag>
        );
      },
    },
  ];
  return columns;
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
};

export const getPathByKey = (curKey: string, data: DataNode[]) => {
  /** 存放搜索到的树节点到顶部节点的路径节点 */
  let result: any[] = [];

  const traverse = (curKey: string, path: any[], data: DataNode[]) => {
    // 树为空时，不执行函数
    if (data.length === 0) {
      return;
    }

    // 遍历存放树的数组
    for (const item of data) {
      // 遍历的数组元素存入path参数数组中
      path.push(item);
      // 如果目的节点的id值等于当前遍历元素的节点id值
      if (item.key === curKey) {
        // 把获取到的节点路径数组path赋值到result数组
        result = JSON.parse(JSON.stringify(path));
        return;
      }

      // 当前元素的children是数组
      const children = Array.isArray(item.children) ? item.children : [];
      // 递归遍历子数组内容
      traverse(curKey, path, children);
      // 利用回溯思想，当没有在当前叶树找到目的节点，依次删除存入到的path数组路径
      path.pop();
    }
  };
  traverse(curKey, [], data);
  // 返回找到的树节点路径
  return result;
};
