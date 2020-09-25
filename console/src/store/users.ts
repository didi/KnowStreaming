import { observable, action } from 'mobx';
import { getUsers } from 'lib/api';

export interface IUserDetail {
  password: number;
  role: number;
  username: string;
}

export class Users {
  public roleMap = ['普通用户', '运维人员', '管理员'];
  public filterRole = this.roleMap.map(i => ({ text: i, value: i }));

  @observable
  public userData: IUserDetail[] = [];

  @action.bound
  public setUserData(data: []) {
    this.userData = data.map((d: any) => {
      d.roleName = this.roleMap[d.role];
      return d;
    });
  }

  public mapRole(role: number) {
    return this.roleMap[role];
  }

  public getUsers() {
    getUsers().then(this.setUserData);
  }
}

export const users = new Users();
