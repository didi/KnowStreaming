import { Collapse, Divider, Spin, Utils } from 'knowdesign';
import * as React from 'react';
import API from '../../api';
import { Link, useParams } from 'react-router-dom';
import InfiniteScroll from 'react-infinite-scroll-component';
import moment from 'moment';
import { timeFormat } from '../../constants/common';
import { DownOutlined } from '@ant-design/icons';
import { renderToolTipValue } from './config';
import RenderEmpty from '@src/components/RenderEmpty';

const { Panel } = Collapse;

interface ILog {
  clusterPhyId: number;
  createTime: number;
  operateTime: string;
  resName: string;
  resTypeCode: number;
  resTypeName: string;
  updateTime: number;
}

const ChangeLog = () => {
  const { clusterId } = useParams<{ clusterId: string }>();
  const [data, setData] = React.useState<ILog[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [pagination, setPagination] = React.useState({
    pageNo: 0,
    pageSize: 10,
    total: 0,
  });

  const getChangeLog = (pageSize = 10) => {
    const promise = Utils.request(API.getClusterChangeLog(+clusterId), {
      params: {
        pageNo: pagination.pageNo + 1,
        pageSize,
      },
    });
    promise.then((res: any) => {
      setData((cur) => cur.concat(res?.bizData));
      setPagination(res?.pagination);
    });
    return promise;
  };

  React.useEffect(() => {
    getChangeLog(20).then(
      () => setLoading(false),
      () => setLoading(false)
    );
  }, []);

  const getHref = (item: any) => {
    if (item.resTypeName.toLowerCase().includes('topic')) return `/cluster/${clusterId}/topic/list#topicName=${item.resName}`;
    if (item.resTypeName.toLowerCase().includes('broker')) return `/cluster/${clusterId}/broker/list#brokerId=${item.resName}`;
    return '';
  };

  return (
    <>
      <div className="change-log-panel">
        <div className="title">历史变更记录</div>
        {!loading && !data.length ? (
          <RenderEmpty message="暂无配置记录" />
        ) : (
          <div id="changelog-scroll-box">
            <Spin spinning={loading} style={{ paddingLeft: '42%', marginTop: 100 }} />
            <InfiniteScroll
              dataLength={data.length}
              next={getChangeLog as any}
              hasMore={data.length < pagination.total}
              loader={<Spin style={{ paddingLeft: '42%', paddingTop: 10 }} spinning={true} />}
              endMessage={
                !pagination.total ? (
                  ''
                ) : (
                  <Divider className="load-completed-tip" plain>
                    加载完成 共 {pagination.total} 条
                  </Divider>
                )
              }
              scrollableTarget="changelog-scroll-box"
            >
              <Collapse defaultActiveKey={['log-0']} accordion>
                {data.map((item, index) => {
                  return (
                    <Panel
                      header={
                        <>
                          <div className="header">
                            <div className="label">{renderToolTipValue(`[${item.resTypeName}] ${item.resName}`, 24)}</div>
                            <span className="icon">
                              <DownOutlined />
                            </span>
                          </div>
                          <div className="header-time">{moment(item.updateTime).format(timeFormat)}</div>
                        </>
                      }
                      key={`log-${index}`}
                      showArrow={false}
                    >
                      <div className="log-item">
                        <span>名称</span>
                        <div className="value">
                          {getHref(item) ? (
                            <Link to={getHref(item)}>{renderToolTipValue(item.resName, 18)}</Link>
                          ) : (
                            renderToolTipValue(item.resName, 18)
                          )}
                        </div>
                      </div>
                      <Divider />
                      <div className="log-item">
                        <span>时间</span>
                        <span className="value">{moment(item.updateTime).format(timeFormat)}</span>
                      </div>
                      <Divider />
                      <div className="log-item">
                        <span>内容</span>
                        <span className="value">{'修改配置'}</span>
                      </div>
                      <Divider />
                      <div className="log-item">
                        <span>类型</span>
                        <span className="value">{item.resTypeName}</span>
                      </div>
                    </Panel>
                  );
                })}
              </Collapse>
            </InfiniteScroll>
          </div>
        )}
      </div>
    </>
  );
};

export default ChangeLog;
