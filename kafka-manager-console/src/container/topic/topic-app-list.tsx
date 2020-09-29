import { Button } from 'component/antd';
import { observer } from 'mobx-react';
import * as React from 'react';
import 'styles/table-filter.less';
import { app } from 'store/app';
import { CommonAppList } from 'container/app/app-list';
import { showEditModal } from 'container/modal';
import Url from 'lib/url-parser';

@observer
export class TopicAppList extends CommonAppList {
  public static defaultProps = {
    from: 'topic',
  };

  constructor(defaultProps: any) {
    super(defaultProps);
  }

  public componentDidMount() {
    if (!app.data.length) {
      app.getAppList();
    }

    if (Url().search.hasOwnProperty('application')) {
      showEditModal();
    }
  }

  public renderTable() {
    return this.renderTableList(this.getData(app.data));
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入应用名称或者负责人')}
        <li className="right-btn-1">
          <Button type="primary" onClick={() => showEditModal()}>应用申请</Button>
        </li>
      </ul>
    );
  }

  public applyApp() {
    showEditModal();
  }

  public render() {
    return (
      <div className="container">
        <div className="table-operation-panel">
          {this.renderOperationPanel()}
        </div>
        <div className="table-wrapper">
          {this.renderTable()}
        </div>
      </div>
    );
  }
}
