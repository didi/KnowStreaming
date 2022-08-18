import React from 'react';
// TODO: 菜单配置接口有点乱，看是否可以归类下，以及是否可以去掉一些非必要的属性
export interface MenuConfItem {
  key?: string;
  name: string | React.ReactNode;
  path: string;
  icon?: string;
  children?: MenuConfItem[];
  visible?: boolean;
  rootVisible?: boolean;
  to?: string;
  divider?: boolean;
  target?: string;
  getQuery?: (query: any) => any;
}

export interface TreeNode {
  id: number;
  pid: number;
  name: string;
  path: string;
  type: number;
  leaf: number;
  children?: TreeNode[];
  icon_color?: string;
  icon_char?: string;
  cate?: string;
  note?: string;
  selectable?: boolean;
}

export interface ResponseDat {
  list: any[];
  total: number;
}

export interface Response {
  err: string;
  dat: any | ResponseDat;
}

export interface UserProfile {
  id: number;
  username: string;
  dispname: string;
  email: string;
  phone: string;
  im: string;
  isroot: boolean;
}

export interface Tenant {
  id: number;
  ident: string;
  name: string;
  note: string;
}

export interface Team {
  id: number;
  ident: string;
  name: string;
  note: string;
  mgmt: number;
}

export interface Role {
  id: number;
  name: string;
  note: string;
  cate: 'global' | 'local';
  operations: string[];
}

export interface Order {
  id: number;
  title: string;
  levels: number;
  cc: string;
  content: string;
  scheduleStartTime: string;
  status: string;
  creator: string;
}
