import * as ReactDOM from 'react-dom';
import { Provider } from 'mobx-react';
import * as React from 'react';
import Router from './router';
import 'styles/style.less';

const renderApp = () => {
  ReactDOM.render(
    <Provider>
      <Router />
    </Provider>,
    document.getElementById('root'),
  );
};

renderApp();
