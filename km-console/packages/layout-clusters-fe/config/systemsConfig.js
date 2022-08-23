const feSystemsConfig = {
  systemsConfig: [
    {
      ident: 'config',
      development: {
        publicPath: 'http://localhost:8001/config/',
        index: 'http://localhost:8001/config/manifest.json',
      },
      production: { publicPath: `${process.env.PUBLIC_PATH}/config/`, index: `${process.env.PUBLIC_PATH}/config/manifest.json` },
    },
  ],
  feConfig: {
    title: 'Know Streaming',
    header: {
      mode: 'complicated',
      logo: '/static/logo-white.png',
      subTitle: '管理平台',
      theme: '',
      right_links: [],
    },
  },
};
export default feSystemsConfig;
