## 安装项目依赖

- 安装 lerna

```
npm install -g lerna
```

- 安装项目依赖

```
npm run i
```

## 启动项目

```
npm run start
```

### 环境信息

http://localhost:port

## 构建项目

```
npm run build

```

## 目录结构

- packages
  - layout-clusters-fe: 基座应用 & 多集群管理
  - config-manager-fe: 子应用 - 系统管理
- tool: 启动 & 打包脚本
- ...

## 常见问题

Q: 执行 `npm run start` 时看不到应用构建和热加载过程？
A: 需要到具体的应用中执行 `npm run start`，例如 `cd packages/layout-clusters-fe` 后，执行 `npm run start`。
