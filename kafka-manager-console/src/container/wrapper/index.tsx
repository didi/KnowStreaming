import * as React from 'react';
import { observer } from 'mobx-react';
import { wrapper } from 'store/wrapper';
import { XFormWrapper } from 'component/x-form-wrapper';

import './index.less';

@observer
export default class AllWrapperInOne extends React.Component {
  public changeXFormWrapperlVisible(visible: boolean) {
    wrapper.setXFormWrapper({ ...wrapper.xFormWrapper, visible });
  }

  public componentWillUnmount() {
    wrapper.setXFormWrapperRef(null);
    wrapper.setXFormWrapper(null);
  }

  public render() {
    return wrapper.xFormWrapper && wrapper.xFormWrapper.visible ?
      (
        <XFormWrapper
          ref={form => wrapper.setXFormWrapperRef(form)}
          onChangeVisible={this.changeXFormWrapperlVisible}
          {...wrapper.xFormWrapper}
        />
      ) : null;
  }
}
