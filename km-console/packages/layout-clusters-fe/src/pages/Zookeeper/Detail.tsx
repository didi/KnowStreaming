import React, { useState, useEffect } from 'react';
import { Drawer, Utils, AppContainer, Spin, Empty } from 'knowdesign';
import ZKDetailMenu from './Sider';
import Api from '@src/api';
import ZKInfo from './Info';
import ZKData from './Data';
const { request } = Utils;
import './index.less';
import { DataNode } from './config';

const ZookeeperDetail = ({ visible, setVisible }: { visible: boolean; setVisible: (visible: boolean) => void }) => {
  const [global] = AppContainer.useGlobalValue();
  // const { visible, setVisible } = props;
  const [detailLoading, setDetailLoading] = useState(true);
  const [isDetail, setIsDetail] = useState(true);
  const [detailInfoLoading, setDetailInfoLoading] = useState(false);
  const [pathList, setPathList] = useState([]);
  const [node, setNode] = useState<any>({});
  const [idenKey, setIdenKey] = useState([]);
  const [siderWidth, setSiderWidth] = useState(200);
  const [startPageX, setStartPageX] = useState(0);
  const [dragging, setDragging] = useState(false);
  const [detailTreeData, setDetailTreeData] = useState([]);

  const onClose = () => {
    setVisible(false);
    setPathList([]);
    setNode({});
    setIdenKey([]);
  };

  const siderMouseDown = (e: { pageX: number }) => {
    setStartPageX(e.pageX);
    setDragging(true);
  };

  const siderMouseMove = (e: { pageX: number }) => {
    const currentSiderWidth = siderWidth + e.pageX - startPageX;

    if (currentSiderWidth < 200) {
      setSiderWidth(200);
    } else if (currentSiderWidth > 320) {
      setSiderWidth(320);
    } else {
      setSiderWidth(currentSiderWidth);
    }

    setStartPageX(e.pageX);
  };

  const siderMouseUp = () => {
    setDragging(false);
  };

  const rootClick = () => {
    setPathList([]);
    setIdenKey([]);
  };

  useEffect(() => {
    // 第一次加载不触发详情信息的loading
    !detailLoading && setDetailInfoLoading(true);
    visible &&
      request(Api.getZookeeperNodeData(+global?.clusterInfo?.id), {
        params: { path: '/' + pathList.map((item: DataNode) => item.title).join('/') },
      })
        .then((res) => {
          setNode(res || {});
        })
        .catch(() => {
          setNode({});
        })
        .finally(() => {
          setDetailInfoLoading(false);
        });
  }, [pathList]);

  useEffect(() => {
    setDetailLoading(true);
    visible &&
      request(Api.getZookeeperNodeChildren(+global?.clusterInfo?.id), { params: { path: '/', keyword: '' } })
        // zkDetailInfo()
        .then((res: string[]) => {
          const newData =
            res && res.length > 0
              ? res.map((item: string, index: number) => {
                  return {
                    title: item,
                    key: `${index}`,
                  };
                })
              : [];
          if (newData.length > 0) {
            setIsDetail(false);
            setDetailTreeData(newData);
          }
        })
        .finally(() => {
          setDetailLoading(false);
        });
  }, [visible]);

  return (
    <Drawer
      push={false}
      title={'Zookeeper 详情'}
      width={1080}
      placement="right"
      onClose={onClose}
      visible={visible}
      className="zookeeper-detail-drawer"
      destroyOnClose
      maskClosable={false}
    >
      <Spin spinning={detailLoading}>
        {isDetail ? (
          <div className="zk-detail-empty">
            <Empty image={Empty.PRESENTED_IMAGE_CUSTOM} description="暂无数据" />
          </div>
        ) : (
          <div className="zk-detail-layout">
            <div className="zk-detail-layout-left" style={{ width: siderWidth + 'px' }}>
              <div className="zk-detail-layout-left-title">目录结构</div>
              <div className="zk-detail-layout-left-content">
                {visible && (
                  <ZKDetailMenu
                    setDetailInfoLoading={setDetailInfoLoading}
                    detailTreeData={detailTreeData}
                    setPathList={setPathList}
                    setIdenKey={setIdenKey}
                    idenKey={idenKey}
                  />
                )}
              </div>
            </div>
            <div className="zk-detail-layout-resizer" style={{ left: siderWidth + 'px' }} onMouseDown={siderMouseDown}>
              {dragging && <div className="resize-mask" onMouseMove={siderMouseMove} onMouseUp={siderMouseUp} />}
            </div>
            <div className="zk-detail-layout-right">
              <div className="zk-detail-layout-right-title">详细信息</div>
              <div className="zk-detail-layout-right-content">
                {visible && (
                  <Spin spinning={detailInfoLoading}>
                    <div className="zk-detail-layout-right-content-countheight">
                      <div className="zk-detail-layout-right-content-path">
                        <span onClick={rootClick}>{node.namespace}</span>
                        {pathList.length > 0 &&
                          pathList.map((item, index) => {
                            if (item.key === idenKey[0]) {
                              return (
                                <span key={index}>
                                  {' '}
                                  /
                                  <a key={index} onClick={() => setIdenKey([item.key])}>
                                    {item.title}
                                  </a>
                                </span>
                              );
                            }
                            return (
                              <span key={index}>
                                {' '}
                                /
                                <span key={index} onClick={() => setIdenKey([item.key])}>
                                  {item.title}
                                </span>
                              </span>
                            );
                          })}
                      </div>
                      {/* <div>{'/' + pathList.map((item: any) => item.title).join(' / ') || '/'}</div> */}
                      <div className="zk-detail-layout-right-content-info">
                        <ZKInfo siderWidth={siderWidth} nodeInfo={node?.stat || {}} />
                      </div>
                      <ZKData nodeData={node?.data || ''} />
                    </div>
                  </Spin>
                )}
              </div>
            </div>
          </div>
        )}
      </Spin>
    </Drawer>
  );
};

export default ZookeeperDetail;
