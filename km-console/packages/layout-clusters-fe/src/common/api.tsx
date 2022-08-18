function getApi(path: string) {
  const prefix = '/api/uic';
  return `${prefix}${path}`;
}

function getOrderApi(path: string) {
  const prefix = '/api/ticket';
  return `${prefix}${path}`;
}

const api = {
  login: getApi('/auth/login'),
  logout: getApi('/auth/logout'),
  selftProfile: getApi('/self/profile'),
  selftPassword: getApi('/self/password'),
  selftToken: getApi('/self/token'),
  user: getApi('/user'),
  tenant: getApi('/tenant'),
  team: getApi('/team'),
  configs: getApi('/configs'),
  role: getApi('/role'),
  ops: getApi('/ops'),
  log: getApi('/log'),
  homeStatistics: getApi('/home/statistics'),
  project: getApi('/project'),
  projects: getApi('/projects'),
  queues: getOrderApi('/queues'),
  tickets: getOrderApi('/tickets'),
  template: getOrderApi('/templates'),
  upload: getOrderApi('/file/upload'),

  task: '/api/job-ce/task',
};

export default api;
