import * as React from 'react';
import ClipboardJS from 'clipboard';
import {
  message,
} from 'component/antd';

const triggerEvent = (eventName: string, element: Element) => {
  let event;
  const ele = element || document;

  event = document.createEvent('HTMLEvents');
  event.initEvent(eventName, true, true);
  ele.dispatchEvent(event);
};

export class Clipboard extends React.Component<any> {
  public state = {
    text: '',
  };

  private clipboard: any = null;
  private dom: Element = null;

  public componentDidMount() {
    const clipboard = this.clipboard = new ClipboardJS('.___clipboard', {
      text(trigger: Element) {
        return trigger.getAttribute('data-text');
      },
    });

    clipboard.on('success', (e: any) => {
      message.success('复制成功!');
      e.clearSelection();
    });

    clipboard.on('error', (e: any) => {
      message.error('复制失败！' + e);
    });
  }

  public componentWillUnmount() {
    this.clipboard.destroy();
  }

  public copy(text: string) {
    this.setState({ text });
    setTimeout(() => triggerEvent('click', this.dom), 0);
  }

  public render() {
    return (
      <div className="___clipboard" data-text={this.state.text} ref={dom => this.dom = dom} />
    );
  }
}
