import { observer } from 'mobx-react';
import * as React from 'react';
import 'styles/table-filter.less';
import { app } from 'store/app';
import { CommonAppList } from 'container/app/app-list';

@observer
export class AdminAppList extends CommonAppList {
  public static defaultProps = {
    from: 'admin',
  };

  constructor(defaultProps: any) {
    super(defaultProps);
  }

  public componentDidMount() {
    if (!app.adminAppData.length) {
      app.getAdminAppList();
    }
  }

  public renderTable() {
    return this.renderTableList(this.getData(app.adminAppData));
  }

  public renderOperationPanel() {
    return (
      <ul>
        {this.renderSearch('', '请输入应用名称或者负责人')}
      </ul>
    );
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
