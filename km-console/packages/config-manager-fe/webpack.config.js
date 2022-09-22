const path = require('path');
require('dotenv').config({ path: path.resolve(process.cwd(), '../../.env') });
const merge = require('webpack-merge');
const devMode = process.env.NODE_ENV === 'development';
const commonConfig = require('./config/webpack.common');
const devConfig = require('./config/webpack.dev');
const prodConfig = require('./config/webpack.prod');

module.exports = merge(commonConfig, devMode ? devConfig : prodConfig);
