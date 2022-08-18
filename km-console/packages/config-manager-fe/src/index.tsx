import React from 'react';
import ReactDOM from 'react-dom';
import singleSpaReact from 'single-spa-react';
import App from './app';

function domElementGetter() {
  let el = document.getElementById('ks-layout-container');
  if (!el) {
    el = document.createElement('div');
    el.id = 'ks-layout-container';
    document.body.appendChild(el);
  }

  return el;
}

const reactLifecycles = singleSpaReact({
  React,
  ReactDOM,
  rootComponent: App,
  domElementGetter,
});

export const bootstrap = [reactLifecycles.bootstrap];

export const mount = [reactLifecycles.mount];

export const unmount = [reactLifecycles.unmount];
