import React from 'react';
import pageNoLicense from '@src/assets/page-no-license.png';

export default () => {
  return (
    <div className="error-page">
      <img width={230} height={150} src={pageNoLicense} />
      <div className="title">license 限制</div>
    </div>
  );
};
