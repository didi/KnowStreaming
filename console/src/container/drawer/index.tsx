import * as React from 'react';
import { observer } from 'mobx-react';
import { drawer } from 'store/drawer';

import ResetOffset from './reset-offset';
import TopicSample from './topic-sample';

@observer
export default class AllDrawerInOne extends React.Component {
  public render() {
    if (!drawer.id) return null;
    return (
      <>
      {drawer.id === 'showResetOffset' ? <ResetOffset /> : null}
      {drawer.id === 'showTopicSample' ? <TopicSample /> : null}
      </>
    );
  }
}
