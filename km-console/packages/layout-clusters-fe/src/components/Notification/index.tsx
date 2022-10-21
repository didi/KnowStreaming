import React from 'react';
import { notification } from 'knowdesign';
import { ArgsProps } from 'knowdesign/es/basic/notification';
import { IconFont } from '@knowdesign/icons';

notification.config({
  top: 16,
  duration: 3,
});

const open = (type: 'info' | 'success' | 'warning' | 'error', content: ArgsProps) => {
  notification[type]({
    icon: <IconFont type={`icon-${type}-circle`} />,
    ...content,
  });
};

const customNotification = {
  info(content: ArgsProps) {
    open('info', content);
  },
  success(content: ArgsProps) {
    open('success', content);
  },
  warning(content: ArgsProps) {
    open('warning', content);
  },
  error(content: ArgsProps) {
    open('error', content);
  },
};

export default customNotification;
