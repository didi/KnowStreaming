import { PaginationConfig } from 'component/antd';

export const pagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  showTotal: (total) => `共 ${total} 条`,
};

export const customPagination: PaginationConfig = {
  position: 'bottom',
  showQuickJumper: true,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`,
};

export const cellStyle = {
  overflow: 'hidden',
  whiteSpace: 'nowrap',
  textOverflow: 'ellipsis',
  // cursor: 'pointer',
};

export const searchProps = {
  optionFilterProp: 'children',
  showSearch: true,
  filterOption: (input: any, option: any) => {
    if ( typeof option.props.children === 'object' ) {
      const { props } =  option.props.children as any;
      return (props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
    return (option.props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
  },
};
