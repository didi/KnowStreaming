import * as React from 'react';
import { observer } from 'mobx-react';
import { version } from 'store/version';
import Url from 'lib/url-parser';
import './error/index.less';

@observer
export class ConfigureInfoPage extends React.Component {
  public fileId: number;

  constructor(props: any) {
    super(props);
    const url = Url();
    this.fileId = Number(url.search.fileId);
  }

  public componentDidMount() {
    version.getConfigFiles(this.fileId);
  }

  public render() {
    return (
      <>
      <div className="config-info">
        {version.configFiles}
      </div>
      </>
    );
  }
}
