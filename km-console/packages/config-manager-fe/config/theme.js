const themeConfig = {
  primaryColor: '#556ee6',
  theme: {
    'primary-color': '#556ee6',
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
