import { observable, action } from 'mobx';
import { IUser, IStaff } from 'types/base-type';
import { getAccount, getUserList, addUser, modfiyUser, deleteUser } from 'lib/api';
import { roleMap } from 'constants/status-map';
import { setCookie, getCookie } from 'lib/utils';
export class Users {
  @observable
  public loading: boolean = false;

  @observable
  public currentUser: IUser = {
    role: Number(getCookie('role')),
    chineseName: getCookie('chineseName'),
  } as IUser;

  @observable
  public userData: IUser[] = [];

  @observable
  public staff: IStaff[] = [];

  @observable
  public newPassWord: any = null;

  @action.bound
  public setAccount(data: IUser) {
    setCookie([{ key: 'role', value: `${data.role}`, time: 1 }]);
    setCookie([{ key: 'username', value: `${data.username}`, time: 1 }]);
    data.chineseName = getCookie('chineseName');
    this.currentUser = data;
  }

  @action.bound
  public setUserData(data: IUser[]) {
    this.userData = data.map((d: IUser, index: number) => {
      d.roleName = roleMap[d.role];
      d.key = index;
      return d;
    });
    this.setLoading(false);
  }

  @action.bound
  public setLoading(value: boolean) {
    this.loading = value;
  }

  @action.bound
  public setNewPassWord(value: boolean) {
    this.newPassWord = value;
  }

  public getAccount() {
    getAccount().then(this.setAccount);
  }

  public getUserList() {
    this.setLoading(true);
    getUserList().then(this.setUserData);
  }

  public deleteUser(username: string) {
    deleteUser(username).then(() => this.getUserList());
  }

  public modfiyUser(params: IUser) {
    return modfiyUser(params).then(() => this.getUserList());
  }

  public addUser(params: IUser) {
    return addUser(params).then(() => this.getUserList());
  }
}

export const users = new Users();
