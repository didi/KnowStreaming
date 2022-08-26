import React from 'react';
import './index.less';

export default ({ title, children }) => {
  return (
    <div className="typical-list-card">
      <div className="typical-list-card-container">
        <div className="title">{title}</div>
        {children}
      </div>
    </div>
  );
};
