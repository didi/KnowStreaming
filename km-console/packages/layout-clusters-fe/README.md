## 使用说明

### 依赖安装（如在 km-console 目录下执行 npm run i 安装过依赖，这步可以省略）：

```
npm install
```

注意，这种方式只会安装当前应用的依赖。如果您不了解，推荐在 km-console 目录下执行 npm run i 安装依赖。

### 启动：

```
npm run start
```

启动后访问地址为 http://localhost:8000

### 构建：

```
npm run build
```

构建后的代码默认会存放到项目根路径下 `km-rest/src/main/resources/templates/layout` 文件夹里

## 目录结构

- config: 开发 & 构建配置
  - theme.js: antd 主题配置
  - registerApps.js: SPA 注册
  - systemsConfig.js: 子应用配置
  - d1-webpack.base.js: webpack 基础配置
  - CoverHtmlWebpackPlugin.js: 输出 html 内容
  - CountComponentWebpackPlugin.js: 计算 knowdesign 组件引用次数
  - webpackConfigResolveAlias.js: 文件路径别名配置
- src：源代码所在目录
  - @types: TypeScript 全局类型声明
  - api: 请求定义
  - assets：全局资源 img、css
  - components：公共组件
  - constants: 全局配置、通用方法
  - locales: 国际化语言
  - pages: 路由匹配的页面组件
  - app.tsx: 菜单、路由配置组件
  - index.html: 单页
  - index.tsx: 入口文件
- tsconfig.json: TypeScript 配置
- webpack.config.js: webpack 配置入口
