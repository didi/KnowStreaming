const themeConfig = {
  primaryColor: '#5664FF',
  theme: {
    'primary-color': '#5664FF',
    'border-radius-base': '2px',
    'border-radius-sm': '2px',
    'font-size-base': '12px',
    'font-family': 'Helvetica Neue, Helvetica, Arial, PingFang SC, Heiti SC, Hiragino Sans GB, Microsoft YaHei, sans-serif',
    'font-family-bold':
      'HelveticaNeue-Medium, Helvetica Medium, PingFangSC-Medium, STHeitiSC-Medium, Microsoft YaHei Bold, Arial, sans-serif',
  },
};

module.exports = {
  'prefix-cls': 'layout',
  ...themeConfig.theme,
};
