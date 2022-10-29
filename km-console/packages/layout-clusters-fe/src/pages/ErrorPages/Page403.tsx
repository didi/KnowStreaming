import React from 'react';
import page403 from '@src/assets/page403.png';

export default () => {
  return (
    <div className="error-page">
      <img width={230} height={150} src={page403} />
      <div className="title">抱歉，您没有权限访问该页面～</div>
      <a className="link" href="/">
        返回首页
      </a>
    </div>
  );
};
