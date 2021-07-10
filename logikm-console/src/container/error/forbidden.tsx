import * as React from 'react';
import './index.less';

export class ForbiddenPage extends React.Component {
  public render() {
    return (
      <>
      <div className="forbidden">
        <span>您暂无权限查看当前页面</span>
      </div>
      </>
    );
  }
}
