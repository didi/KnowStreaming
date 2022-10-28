import React from 'react';
import page404 from '@src/assets/page404.png';

export default () => {
  return (
    <div className="error-page">
      <img width={230} height={150} src={page404} />
      <div className="title">很抱歉，页面走丢了～</div>
      <div className="desc">请检查页面地址是否正确或刷新页面</div>
      <a className="link" href="/">
        返回首页
      </a>
    </div>
  );
};
