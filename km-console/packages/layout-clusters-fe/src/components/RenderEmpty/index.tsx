import React from 'react';

const RenderEmpty = (props: { height?: string | number; message: string }) => {
  const { height = 200, message } = props;
  return (
    <>
      <div className="empty-panel" style={{ height }}>
        <div className="img" />
        <div className="text">{message}</div>
      </div>
    </>
  );
};

export default RenderEmpty;
