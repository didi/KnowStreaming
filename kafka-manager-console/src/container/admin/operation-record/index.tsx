import * as React from 'react';
import { observer } from 'mobx-react';
import { SearchAndFilterContainer } from 'container/search-filter';
import { IXFormWrapper, IMetaData, IRegister } from 'types/base-type';
import { admin } from 'store/admin';
import { customPagination, cellStyle } from 'constants/table';
import { Table, Tooltip } from 'component/antd';
import { timeFormat } from 'constants/strategy';
import { SearchFormComponent } from '../searchForm';
import { getJarFuncForm, operateList, getOperateColumns } from './config'
import moment = require('moment');
import { tableFilter } from 'lib/utils';

@observer
export class OperationRecord extends SearchAndFilterContainer {
  public state: any = {
    searchKey: '',
    filteredInfo: null,
    sortedInfo: null,
  };

  public getData<T extends IMetaData>(origin: T[]) {
    let data: T[] = origin;
    let { searchKey } = this.state;
    searchKey = (searchKey + '').trim().toLowerCase();

    data = searchKey ? origin.filter((item: IMetaData) =>
      (item.clusterName !== undefined && item.clusterName !== null) && item.clusterName.toLowerCase().includes(searchKey as string),
    ) : origin;
    return data;
  };

  public searchForm = (params: any) => {
    // this.props.setFuncSubValue(params)
    // getSystemFuncList(params).then(res => {
    //   this.props.setSysFuncList(res.data)
    //   this.props.setPagination(res.pagination)
    // })
    const { operator, moduleId } = params || {}
    operator ? admin.getOperationRecordData(params) : admin.getOperationRecordData({ moduleId })
    // getJarList(params).then(res => {
    //   this.props.setJarList(res.data)
    //   this.props.setPagination(res.pagination)
    // })
  }

  public clearAll = () => {
    this.setState({
      filteredInfo: null,
      sortedInfo: null,
    });
  };

  public setHandleChange = (pagination: any, filters: any, sorter: any) => {
    this.setState({
      filteredInfo: filters,
      sortedInfo: sorter,
    });
  }

  public renderOperationRecordList() {
    let { sortedInfo, filteredInfo } = this.state;
    sortedInfo = sortedInfo || {};
    filteredInfo = filteredInfo || {};
    const operatingTime = Object.assign({
      title: '操作时间',
      dataIndex: 'modifyTime',
      key: 'modifyTime',
      align: 'center',
      sorter: (a: any, b: any) => a.modifyTime - b.modifyTime,
      render: (t: number) => moment(t).format(timeFormat),
      width: '15%',
      sortOrder: sortedInfo.columnKey === 'modifyTime' && sortedInfo.order,
    });

    const operatingPractice = Object.assign({
      title: '行为',
      dataIndex: 'operate',
      key: 'operate',
      align: 'center',
      width: '12%',
      filters: tableFilter<any>(this.getData(admin.oRList), 'operateId', operateList),
      // filteredValue: filteredInfo.operate || null,
      onFilter: (value: any, record: any) => {
        return record.operateId === value
      }
    }, this.renderColumnsFilter('modifyTime'))

    const columns = getOperateColumns()
    columns.splice(0, 0, operatingTime);
    columns.splice(3, 0, operatingPractice);
    return (
      <>
        <div className="container">
          <div className="table-operation-panel">
            <SearchFormComponent
              formMap={getJarFuncForm()}
              onSubmit={(params: any) => this.searchForm(params)}
              clearAll={() => this.clearAll()}
              isReset={true}
            />
          </div>
          <div className="table-wrapper">
            <Table
              rowKey="key"
              loading={admin.loading}
              dataSource={this.getData(admin.oRList)}
              columns={columns}
              pagination={customPagination}
              bordered
              onChange={this.setHandleChange}
            />
          </div>
        </div>
      </>
    )
  };

  componentDidMount() {
    admin.getOperationRecordData({ moduleId: 0 });
  }

  render() {
    return <div>
      {
        this.renderOperationRecordList()
      }
    </div>
  }
}