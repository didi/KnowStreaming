import React, { useState } from 'react';
import { message } from 'knowdesign';
import { HeartTwoTone } from '@ant-design/icons';

const AccessClusterConfig = () => {
  const [count, setCount] = useState<number>(1);

  const setErgeModal = () => {
    if (count >= 50) {
      message.success({
        content: 'Erge',
        icon: <HeartTwoTone />,
      });
      setCount(1);
    } else {
      setCount(count + 1);
    }
  };

  return <div className="multi-cluster-erge" onClick={setErgeModal} />;
};

export default AccessClusterConfig;
