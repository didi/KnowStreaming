const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');
const pkgJson = require('../package');

module.exports = {
  mode: 'development',
  plugins: [
    new MiniCssExtractPlugin(),
    new ReactRefreshWebpackPlugin({
      overlay: false,
    }),
  ],
  devServer: {
    host: '127.0.0.1',
    port: pkgJson.port,
    hot: true,
    open: false,
    publicPath: `http://localhost:${pkgJson.port}/${pkgJson.ident}/`,
    inline: true,
    disableHostCheck: true,
    historyApiFallback: true,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  },
  output: {
    path: '/',
    publicPath: `http://localhost:${pkgJson.port}/${pkgJson.ident}/`,
    library: pkgJson.ident,
    libraryTarget: 'amd',
    filename: '[name].js',
    chunkFilename: '[name].js',
  },
  devtool: 'cheap-module-eval-source-map',
};
