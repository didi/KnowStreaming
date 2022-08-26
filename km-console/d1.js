const d1Config = require('./d1.json');
d1Config.appConfig.webpackChain = function (config) {
  // config.devServer.port(10000);
};
module.exports = d1Config;
