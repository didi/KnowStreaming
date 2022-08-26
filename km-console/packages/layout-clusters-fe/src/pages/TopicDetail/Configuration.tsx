import React, { useState, useEffect } from 'react';
import { AppContainer, Checkbox, ProTable, Utils } from 'knowdesign';
import { useParams } from 'react-router-dom';
import Api from '@src/api';
import { getTopicConfigurationColmns } from './config';
import { ConfigurationEdit } from './ConfigurationEdit';
import { ClustersPermissionMap } from '../CommonConfig';
const { request } = Utils;

const BrokerConfiguration = (props: any) => {
  const { hashData } = props;
  const urlParams = useParams<any>(); // 获取地址栏参数
  const [global] = AppContainer.useGlobalValue();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  // const [filterType, setFilterType] = useState<number>(0); // 多选框的筛选结果 filterType
  const [checkedBoxList, setCheckedBoxList] = useState<string[]>([]); // 多选框的选中的列表
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });
  const [editVisible, setEditVisible] = useState(false);
  const [record, setRecord] = useState(null); // 获取当前点击行的数据；
  const [readOnlyVisible, setReadOnlyVisible] = useState(null);
  const [readOnlyRecord, setReadOnlyRecord] = useState(null);

  // 请求接口获取数据
  const genData = async ({ pageNo, pageSize }: any) => {
    if (urlParams?.clusterId === undefined || hashData?.topicName === undefined) return;

    setLoading(true);
    // const params = dealTableRequestParams({
    //   searchKeywords: props.searchKeywords ? props.searchKeywords : '',
    //   pageNo,
    //   pageSize,
    // });
    const params = {
      searchKeywords: props.searchKeywords ? props.searchKeywords.slice(0, 128) : undefined,
      pageNo,
      pageSize,
      preciseFilterDTOList:
        checkedBoxList.length > 0
          ? checkedBoxList.map((item) => {
              return {
                fieldName: item,
                fieldValueList: [item === 'readOnly' ? false : true],
                include: true,
              };
            })
          : undefined,
    };

    request(Api.getTopicConfigs(hashData?.topicName, urlParams?.clusterId), { data: params, method: 'POST' })
      .then((res: any) => {
        setPagination({
          current: res.pagination?.pageNo,
          pageSize: res.pagination?.pageSize,
          total: res.pagination?.total,
        });
        setData(res?.bizData || []);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const onTableChange = (pagination: any, filters: any, sorter: any) => {
    // setPagination(pagination);
    // const asc = sorter?.order && sorter?.order === 'ascend' ? true : false;
    // const sortColumn = sorter.field && toLine(sorter.field);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize });
  };

  // 多选配置
  const checkedBoxOptions = [
    // { label: 'Hide read-only', value: 'readOnly' },
    { label: 'Show Overrides Only', value: 'override' },
  ];

  const checkedBoxChange = (e: any) => {
    // 通过checked转换filterType
    // const newfilterType =
    //   e.includes('readOnly') && e.includes('override')
    //     ? 0
    //     : e.includes('readOnly') && !e.includes('override')
    //       ? 1
    //       : !e.includes('readOnly') && e.includes('override')
    //         ? 2
    //         : 3;

    // setFilterType(newfilterType);
    setCheckedBoxList(e);
    // 调用接口
  };

  const setEditOp = (record: any) => {
    setEditVisible(true);
    setRecord(record);
  };

  useEffect(() => {
    props.positionType === 'Configuration' &&
      genData({
        pageNo: 1,
        pageSize: pagination.pageSize,
        // sorter: defaultSorter
      });
  }, [props.searchKeywords, checkedBoxList]);

  return (
    <>
      <div className={'detail-header-cases'} style={{ padding: '0 0 12px' }}>
        <Checkbox.Group options={checkedBoxOptions} value={checkedBoxList} onChange={checkedBoxChange} />
      </div>
      <ProTable
        showQueryForm={false}
        tableProps={{
          showHeader: false,
          rowKey: 'path',
          loading: loading,
          columns: getTopicConfigurationColmns({
            setEditOp,
            readOnlyRecord,
            readOnlyVisible,
            allowEdit: global.hasPermission && global.hasPermission(ClustersPermissionMap.TOPIC_CHANGE_CONFIG),
          }),
          dataSource: data,
          paginationProps: { ...pagination },
          attrs: {
            // className: 'frameless-table', // 纯无边框表格类名
            bordered: false,
            onChange: onTableChange,
            onRow: (record: any) => {
              if (!!record?.readOnly) {
                return {
                  onMouseEnter: () => {
                    setReadOnlyVisible(true);
                    setReadOnlyRecord(record);
                  },
                  onMouseLeave: () => {
                    setReadOnlyVisible(false);
                    setReadOnlyRecord(null);
                  },
                };
              }
              return {};
            },
          },
        }}
      />
      <ConfigurationEdit record={record} hashData={hashData} visible={editVisible} setVisible={setEditVisible} genData={genData} />
    </>
  );
};

export default BrokerConfiguration;
