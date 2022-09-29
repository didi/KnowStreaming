import React, { useState } from 'react';
import { Input } from 'knowdesign';
import { IconFont } from '@knowdesign/icons';
import './style/index.less';

interface IObjectProps {
  [propName: string]: any;
}
interface ISearchInputProps {
  onSearch: (value?: string) => unknown;
  iconType?: string;
  attrs?: IObjectProps;
}

const SearchInput: React.FC<ISearchInputProps> = (props: ISearchInputProps) => {
  const { onSearch, iconType = 'icon-fangdajing', attrs } = props;
  const [changeVal, setChangeVal] = useState<string>('');

  const onChange = (e: any) => {
    if (e.target.value === '' || e.target.value === null || e.target.value === undefined) {
      onSearch('');
    }
    attrs?.onChange && attrs?.onChange(e.target.value);
    setChangeVal(e.target.value);
  };

  return (
    <Input.Search
      {...attrs}
      suffix={<IconFont type={iconType} onClick={() => onSearch(changeVal)} />}
      onChange={onChange}
      className={'dcloud-clustom-input-serach ' + `${attrs?.className}`}
      onSearch={(val: string) => {
        onSearch(val);
      }}
    />
  );
};

export default SearchInput;
