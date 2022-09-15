export type PermissionNode = {
  id: number;
  parentId: number;
  permissionName: string;
  has: boolean;
  leaf: boolean;
  childList: PermissionNode[];
};

export type UserProps = {
  id: number;
  userName: string;
  realName: string;
  email: string;
  phone: string;
  updateTime: number;
  roleList: {
    id: number;
    roleName: string;
  }[];
  deptList: {
    id: number;
    parentId: number;
    deptName: string;
  }[];
  permissionTreeV0: PermissionNode;
};

export type RoleProps = {
  id: number;
  roleCode: string;
  roleName: string;
  description: string;
  authedUserCnt: number;
  authedUsers: string[];
  lastReviser: string | null;
  createTime: string;
  updateTime: string;
  permissionTreeV0: PermissionNode;
};

export interface AssignUser {
  id: number;
  name: string;
  has: boolean;
}

export enum UserOperate {
  Add,
  Edit,
}

export enum RoleOperate {
  Add,
  Edit,
  View,
}

export interface FormItemPermission {
  id: number;
  name: string;
  essentialPermission: { label: string; value: number };
  options: { label: string; value: number }[];
}
