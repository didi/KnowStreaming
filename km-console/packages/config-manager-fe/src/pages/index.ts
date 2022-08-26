import Setting from './ConfigManage';
import User from './UserManage';
import OperationLog from './OperationLog';
import HomePage from './HomePage';
import CommonConfig from './CommonConfig';

export const pageRoutes = [
  {
    path: '',
    exact: false,
    component: HomePage,
    commonRoute: CommonConfig,
    children: [
      {
        path: 'setting',
        exact: true,
        component: Setting,
      },
      {
        path: 'user',
        exact: true,
        component: User,
      },
      {
        path: 'operation-log',
        exact: true,
        component: OperationLog,
      },
    ],
  },
];
