## 使用说明

### 依赖安装：

```
npm install
```

### 启动：

```
npm run start
```

### 构建：

```
npm run build
```

构建后的代码默认会存放到项目根路径下 `km-rest/src/main/resources/templates/config` 文件夹里

## 目录结构

- config: 开发 & 构建配置
  - theme.js: antd 主题配置
  - d1-webpack.base.js: webpack 基础配置
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
