import * as React from 'react';
import Url from 'lib/url-parser';
import { getTicketBycode } from 'lib/api';
import { IUser } from 'types/base-type';
import { urlPrefix } from 'constants/left-menu';

export class SSOCallBack extends React.Component<any> {
  constructor(public code: string, public jumpto: string) {
    super({});
    const url = Url();
    this.jumpto = url.search.jumpto;
    this.code = url.search.code;
  }

  public componentDidMount() {
    getTicketBycode(this.code).then((data: IUser) => {
      const userData = data || {} as IUser;
      (window as any).taotieCommandQueue = (window as any).taotieCommandQueue || [];
      (window as any).taotieCommandQueue.push({command: 'setCookieUserNameForTaotie', parameter: userData.username});

      if (this.jumpto) {
        window.location.href = decodeURIComponent(this.jumpto);
      } else {
        window.location.href = `${urlPrefix}`;
      }
    });
  }
  public render() {
    return (
      <div>正在登录...</div>
    );
  }
}
