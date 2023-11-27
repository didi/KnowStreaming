import React, { useEffect, useRef, useState } from 'react';
import { Button, Form, Input, Select, Modal, ProTable, AppContainer, DKSBreadcrumb, Utils, Divider } from 'knowdesign';
import message from '@src/components/Message';
import { IconFont } from '@knowdesign/icons';
import ACLsCardBar from '@src/components/CardBar/ACLsCardBar';
import api from '@src/api';
import { tableHeaderPrefix } from '@src/constants/common';
import { useParams } from 'react-router-dom';
import AddACLDrawer, {
  ACL_OPERATION,
  ACL_PERMISSION_TYPE,
  ACL_PATTERN_TYPE,
  ACL_RESOURCE_TYPE,
  RESOURCE_TO_OPERATIONS_MAP,
  RESOURCE_MAP_KEYS,
} from './EditDrawer';
import { ClustersPermissionMap } from '../CommonConfig';
import './index.less';

const { confirm } = Modal;

export type ACLsProps = {
  kafkaUser: string;
  resourceType: ACL_RESOURCE_TYPE;
  resourceName: string;
  resourcePatternType: ACL_PATTERN_TYPE;
  aclPermissionType: ACL_PATTERN_TYPE;
  aclOperation: ACL_OPERATION;
  aclClientHost: string;
};

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
};

const SecurityACLs = (): JSX.Element => {
  const [global] = AppContainer.useGlobalValue();
  const { clusterId } = useParams<{
    clusterId: string;
  }>();
  const [loading, setLoading] = useState<boolean>(true);
  const [data, setData] = useState<ACLsProps[]>([]);
  const [pagination, setPagination] = useState<any>(defaultPagination);
  const [form] = Form.useForm();
  const editDrawerRef = useRef(null);

  const getACLs = (query = {}) => {
    const formData = form.getFieldsValue();
    const queryData = {
      // 模糊查询
      fuzzySearchDTOList: [] as { fieldName: string; fieldValue: string }[],
      // 精确查询
      preciseFilterDTOList: [] as { fieldName: string; fieldValueList: (string | number)[] }[],
    };
    Object.entries(formData)
      .filter((i) => i[1])
      .forEach(([fieldName, fieldValue]: [string, any]) => {
        if (fieldName === 'resourceType') {
          queryData.preciseFilterDTOList.push({
            fieldName,
            fieldValueList: fieldValue.map((type: string) => ACL_RESOURCE_TYPE[type as RESOURCE_MAP_KEYS]),
          });
        } else {
          queryData.fuzzySearchDTOList.push({
            fieldName,
            fieldValue,
          });
        }
      });
    const queryParams = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      ...queryData,
      ...query,
    };

    setLoading(true);
    Utils.request(api.getACLs(clusterId), {
      method: 'POST',
      data: queryParams,
    }).then(
      (res: any) => {
        const { pageNo, pageSize, total } = res.pagination;
        const pages = Math.ceil(total / pageSize);
        if (pageNo > pages && pages !== 0) {
          getACLs({ pageNo: pages });
          return false;
        }

        setPagination({
          ...pagination,
          current: pageNo,
          pageSize,
          total,
        });
        setData(res.bizData);
        setLoading(false);
        return true;
      },
      () => setLoading(false)
    );
  };

  const columns = () => {
    const baseColumns: any = [
      {
        title: 'Principal',
        dataIndex: 'kafkaUser',
      },
      {
        title: 'Permission',
        dataIndex: 'aclPermissionType',
        render(type: number) {
          return ACL_PERMISSION_TYPE[type];
        },
      },
      {
        title: 'Pattern Type',
        dataIndex: 'resourcePatternType',
        width: 180,
        render(type: number) {
          return ACL_PATTERN_TYPE[type];
        },
      },
      {
        title: 'Operations',
        dataIndex: 'aclOperation',
        render(type: number) {
          return ACL_OPERATION[type];
        },
      },
      {
        title: 'Resource',
        dataIndex: 'resourceType',
        render(type: number, record: ACLsProps) {
          return `${ACL_RESOURCE_TYPE[type]} ${record.resourceName}`;
        },
      },
      {
        title: 'Host',
        dataIndex: 'aclClientHost',
      },
    ];
    if (global.hasPermission && global.hasPermission(ClustersPermissionMap.SECURITY_ACL_DELETE)) {
      baseColumns.push({
        title: '操作',
        dataIndex: '',
        width: 120,
        render(record: ACLsProps) {
          return (
            <>
              <Button type="link" size="small" style={{ paddingLeft: 0 }} onClick={() => onDelete(record)}>
                删除
              </Button>
            </>
          );
        },
      });
    }

    return baseColumns;
  };

  const onDelete = (record: ACLsProps) => {
    confirm({
      title: '确定删除 ACL 吗?',
      okText: '删除',
      okType: 'primary',
      centered: true,
      okButtonProps: {
        size: 'small',
        danger: true,
      },
      cancelButtonProps: {
        size: 'small',
      },
      onOk() {
        return Utils.request(api.delACLs, {
          method: 'DELETE',
          data: { ...record, clusterId: Number(clusterId) },
        }).then((_) => {
          message.success('删除成功');
          getACLs();
        });
      },
    });
  };

  const onTableChange = (curPagination: any) => {
    getACLs({ pageNo: curPagination.current, pageSize: curPagination.pageSize });
  };

  useEffect(() => {
    getACLs();
  }, []);

  return (
    <div className="security-acls-page">
      <DKSBreadcrumb
        breadcrumbs={[
          { label: '多集群管理', aHref: '/' },
          { label: global?.clusterInfo?.name, aHref: `/cluster/${global?.clusterInfo?.id}` },
          { label: 'ACLs', aHref: `` },
        ]}
      />
      <div className="card-bar">
        <ACLsCardBar />
      </div>
      <div className="security-acls-page-list custom-table-content">
        <div className={tableHeaderPrefix}>
          <div className={`${tableHeaderPrefix}-left`}>
            <div className={`${tableHeaderPrefix}-left-refresh`} onClick={() => getACLs()}>
              <IconFont className={`${tableHeaderPrefix}-left-refresh-icon`} type="icon-shuaxin1" />
            </div>
            <Divider type="vertical" className={`${tableHeaderPrefix}-divider`} />
            <Form form={form} layout="inline" onFinish={() => getACLs({ page: 1 })}>
              <Form.Item name="kafkaUser">
                <Input placeholder="请输入 Principal" />
              </Form.Item>
              <Form.Item name="resourceType">
                <Select
                  placeholder="选择 ResourceType"
                  options={Object.keys(RESOURCE_TO_OPERATIONS_MAP).map((key) => ({ label: key, value: key }))}
                  mode="multiple"
                  maxTagCount="responsive"
                  allowClear
                  style={{ width: 200 }}
                />
              </Form.Item>
              <Form.Item name="resourceName">
                <Input placeholder="请输入 Resource" />
              </Form.Item>
              <Form.Item>
                <Button type="primary" ghost htmlType="submit">
                  查询
                </Button>
              </Form.Item>
            </Form>
          </div>
          {global.hasPermission && global.hasPermission(ClustersPermissionMap.SECURITY_ACL_ADD) ? (
            <div className={`${tableHeaderPrefix}-right`}>
              <Button
                type="primary"
                // icon={<PlusOutlined />}
                onClick={() => editDrawerRef.current.onOpen(true, getACLs)}
              >
                新增ACL
              </Button>
            </div>
          ) : (
            <></>
          )}
        </div>
        <ProTable
          tableProps={{
            showHeader: false,
            loading,
            rowKey: 'id',
            dataSource: data,
            paginationProps: pagination,
            columns: columns() as any,
            lineFillColor: true,
            attrs: {
              onChange: onTableChange,
              scroll: { y: 'calc(100vh - 400px)' },
            },
          }}
        />
      </div>

      {/* 新增 ACL 抽屉 */}
      <AddACLDrawer ref={editDrawerRef} />
    </div>
  );
};

export default SecurityACLs;
