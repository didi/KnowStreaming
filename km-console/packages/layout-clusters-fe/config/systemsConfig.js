const feSystemsConfig = {
  systemsConfig: [
    {
      ident: 'config',
      development: {
        publicPath: 'http://localhost:8001/config/',
        index: 'http://localhost:8001/config/manifest.json',
      },
      production: { publicPath: '/config/', index: '/config/manifest.json' },
      // production: {
      //   publicPath: '//img-ys011.didistatic.com/static/bp_fe_daily/bigdata_cloud_KnowStreaming_FE/gn/config/',
      //   index: '//img-ys011.didistatic.com/static/bp_fe_daily/bigdata_cloud_KnowStreaming_FE/gn/config/manifest.json',
      // },
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
module.exports = feSystemsConfig;
