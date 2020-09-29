import React from 'react';
import './index.less';
import { fullScreen } from 'store/full-screen';
import { observer } from 'mobx-react';

@observer
export class FullScreen extends React.Component {

  public handleClose = (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if ((event.target as any).nodeName === 'SECTION') fullScreen.close();
  }

  public render() {
    if (!fullScreen.content) return null;
    return (
      <section className="full-screen-mark" onClick={this.handleClose}>
        <div className="full-screen-content">
          {fullScreen.content}
        </div>
      </section>
    );
  }
}
