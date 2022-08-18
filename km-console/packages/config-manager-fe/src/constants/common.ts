export const defaultPagination = {
  current: 1,
  pageSize: 10,
  total: 0,
};

export const defaultPaginationConfig = {
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  showTotal: (total: number) => `共 ${total} 条`,
};

export const cellStyle = {
  overflow: 'hidden',
  whiteSpace: 'nowrap' as any,
  textOverflow: 'ellipsis',
  cursor: 'pointer',
  maxWidth: 150,
};

export const systemCipherKey = 'Szjx2022@666666$';

export const oneDayMillims = 24 * 60 * 60 * 1000;

export const classNamePrefix = 'bdp';

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const dateFormat = 'YYYY-MM-DD';

export const SMALL_DRAWER_WIDTH = 480;
export const MIDDLE_DRAWER_WIDTH = 728;
export const LARGE_DRAWER_WIDTH = 1080;
