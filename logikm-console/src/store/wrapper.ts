import { observable, action } from 'mobx';
import { IXFormWrapper } from 'types/base-type';

class Wrapper {
  @observable
  public ref: any = null;

  @observable
  public xFormWrapper: IXFormWrapper = {} as IXFormWrapper;

  @action.bound
  public close() {
    this.xFormWrapper = {
      ...this.xFormWrapper,
      visible: false,
    };
  }

  @action.bound
  public open(xFormWrapper: IXFormWrapper) {
    this.setXFormWrapper({
      ...xFormWrapper,
      visible: true,
    });
  }


  @action.bound
  public setXFormWrapper(xFormWrapper: IXFormWrapper) {
    this.xFormWrapper = xFormWrapper;
  }

  @action.bound
  public setXFormWrapperRef(ref: any) {
    this.ref = ref;
  }

}

export const wrapper = new Wrapper();
