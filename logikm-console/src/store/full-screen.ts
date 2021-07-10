import { observable, action } from 'mobx';

class FullScreen {
  @observable
  public content: JSX.Element = null;

  @action.bound
  public show(dom: JSX.Element) {
    this.content = dom;
  }

  @action.bound
  public close() {
    this.content = null;
  }

}

export const fullScreen = new FullScreen();
