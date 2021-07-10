import { observable, action } from 'mobx';
import { IUploadFile, IConfigInfo } from 'types/base-type';
import { getFileList, addFile, deleteFile, modfiyFile, getFileType, getConfigFiles } from 'lib/api';

interface IEnum {
  code: number;
  message: string;
  suffix?: string;
}
interface IUploadFileType {
  fileEnum: IEnum[];
  storageEnum: IEnum[];
}

export class Version {
  @observable
  public loading: boolean = false;

  @observable
  public fileSuffix: string = '';

  @observable
  public acceptFileType: string = '';

  @observable
  public configFiles: string = '';

  @observable
  public fileList: IUploadFile[] = [];

  @observable
  public storageTypes: IConfigInfo[] = [];

  @observable
  public currentFileType: number = 0;

  @observable
  public fileTypeList: IEnum[] = [];

  @observable
  public acceptFileMap = {} as {
    [key: number]: string,
  };

  @action.bound
  public setFileList(data: IUploadFile[]) {
    this.fileList = (data || []).map((item, index) => {
      return {
        ...item,
        configType: this.acceptFileMap[item.fileType] || '',
        key: index,
      };
    });
    this.setLoading(false);
  }

  @action.bound
  public setConfigFiles(data: string) {
    this.configFiles = data;
  }

  @action.bound
  public setFileTypeList(data: IUploadFileType) {
    const fileTypeList = data && data.fileEnum || [];
    const storageEnum = data && data.storageEnum || [];
    this.fileTypeList = fileTypeList.map((item, index) => {
      if (item.suffix) {
        this.acceptFileMap[item.code] = item.message;
      }

      if (index === 0) { // 初始化文件上传类型
        this.acceptFileType = item.suffix;
      }

      return {
        ...item,
        label: item.message,
        value: item.code,
      };
    });

    this.storageTypes = storageEnum.map(item => {
      return {
        ...item,
        label: item.message,
        value: item.message,
      };
    });
    this.setLoading(false);
  }

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setAcceptFileType(type: number) {
    this.fileSuffix = this.fileTypeList[type].suffix;
    this.currentFileType = type;
    this.acceptFileType = this.acceptFileMap[type] || '';
  }

  public async getFileTypeList() {
    this.setLoading(true);
    getFileType().then(this.setFileTypeList);
  }

  public getFileList() {
    this.setLoading(true);
    getFileList().then(this.setFileList);
  }

  public deleteFile(id: number) {
    deleteFile(id).then(() => this.getFileList());
  }

  public modfiyFile(params: IUploadFile) {
    modfiyFile(params).then(() => this.getFileList());
  }

  public addFile(params: IUploadFile) {
    params.clusterId = params.clusterId || -1;
    addFile(params).then(() => this.getFileList());
  }

  public getConfigFiles(fileId: number) {
    getConfigFiles(fileId).then(this.setConfigFiles);
  }
}

export const version = new Version();
