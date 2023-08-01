const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');

module.exports = {
  mode: 'development',
  plugins: [
    new MiniCssExtractPlugin(),
    new ReactRefreshWebpackPlugin({
      overlay: false,
    }),
  ],
  output: {
    path: '/',
    publicPath: '/',
    filename: '[name].js',
    chunkFilename: '[name].js',
    library: 'layout',
    libraryTarget: 'amd',
  },
  devServer: {
    host: 'localhost',
    port: 8000,
    hot: true,
    open: true,
    openPage: 'http://localhost:8000/',
    inline: true,
    historyApiFallback: true,
    publicPath: `http://localhost:8000/`,
    headers: {
      'cache-control': 'no-cache',
      pragma: 'no-cache',
      'Access-Control-Allow-Origin': '*',
    },
    proxy: {
      '/ks-km/api/v3': {
        changeOrigin: true,
        target: 'http://127.0.0.1/',
      },
      '/logi-security/api/v1': {
        changeOrigin: true,
        target: 'http://127.0.0.1/',
      },
    },
  },
};
