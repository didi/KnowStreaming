import React from 'react';
import { IconFont } from '@knowdesign/icons';
import { message } from 'knowdesign';
import { ArgsProps, ConfigOnClose } from 'knowdesign/es/basic/message';

type ConfigContent = React.ReactNode;
type ConfigDuration = number | (() => void);
type JointContent = ConfigContent | ArgsProps;

message.config({
  top: 16,
});

function isArgsProps(content: JointContent): content is ArgsProps {
  return Object.prototype.toString.call(content) === '[object Object]' && !!(content as ArgsProps).content;
}

const openMessage = (
  type: 'info' | 'success' | 'warning' | 'error',
  content: JointContent,
  duration?: ConfigDuration,
  onClose?: ConfigOnClose
) => {
  if (isArgsProps(content)) {
    message[type]({
      icon: <IconFont type={`icon-${type}-circle`} />,
      ...content,
    });
  } else {
    message[type]({
      icon: <IconFont type={`icon-${type}-circle`} />,
      content,
      duration,
      onClose,
    });
  }
};

const customMessage = {
  info(content: JointContent, duration?: ConfigDuration, onClose?: ConfigOnClose) {
    openMessage('info', content, duration, onClose);
  },
  success(content: JointContent, duration?: ConfigDuration, onClose?: ConfigOnClose) {
    openMessage('success', content, duration, onClose);
  },
  warning(content: JointContent, duration?: ConfigDuration, onClose?: ConfigOnClose) {
    openMessage('warning', content, duration, onClose);
  },
  error(content: JointContent, duration?: ConfigDuration, onClose?: ConfigOnClose) {
    openMessage('error', content, duration, onClose);
  },
};

export default customMessage;
