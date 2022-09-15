## 前提

正常情况下，您应该通过 [本地源码启动手册](https://github.com/didi/KnowStreaming/blob/master/docs/dev_guide/%E6%9C%AC%E5%9C%B0%E6%BA%90%E7%A0%81%E5%90%AF%E5%8A%A8%E6%89%8B%E5%86%8C.md) 来打包工程。如果您有需要在本地独立启动前端服务，请参考以下手册。

在进行以下的步骤之前，首先确保您已经安装了 `node`。如已安装，可以通过在终端执行 `node -v` 来获取到 node 版本，项目推荐使用 `node v12` 版本运行。

另外，`windows` 用户请在 `git bash` 下运行下面的命令。

## 一、安装项目依赖（必须）

1. 安装 lerna（可选，安装后可以直接通过 lerna 的全局指令管理项目，如果不了解 lerna 可以不安装）

```
npm install -g lerna
```

2. 安装项目依赖

```
npm run i
```

我们默认保留了 `package-lock.json` 文件，以防止可能的依赖包自动升级导致的问题。依赖默认会通过 `https://registry.npmjs.org` 服务下载，如果您无法连通该服务器，请删除当前目录及 `packages/*` 子目录下的 `package-lock.json` 后，在当前目录下使用 `node v12` 版本执行命令 `npm run i`。

## 二、启动项目

```
npm run start
```

该指令会启动 `packages` 目录下的所有应用，如果需要单独启动应用，其查看下方 QA。

多集群管理应用会启动在 http://localhost:8000，系统管理应用会占用 http://localhost:8001。
请确认 `8000` 和 `8001` 端口没有被其他应用占用。

后端本地服务启动在 http://localhost:8080，请求通过 webpack dev server 代理访问 8080 端口，需要启动后端服务后才能正常请求接口。

如果启动失败，可以参见另外一种本地启动方式 [本地源码启动手册](https://github.com/didi/KnowStreaming/blob/master/docs/dev_guide/%E6%9C%AC%E5%9C%B0%E6%BA%90%E7%A0%81%E5%90%AF%E5%8A%A8%E6%89%8B%E5%86%8C.md)

## 三、构建项目

```
npm run build

```

项目构建成功后，会存放到 km-rest/src/main/resources/tamplates 目录下。

## 目录结构

- packages
  - layout-clusters-fe: 基座应用 & 多集群管理（其余应用启动需要首先启动该应用）
  - config-manager-fe: 子应用 - 系统管理
- ...

## 常见问题

Q: 在 `km-console` 目录下执行 `npm run start` 时看不到应用构建和热加载过程？如何启动单个应用？

A: 需要到具体的应用中执行 `npm run start`，例如 `cd packages/layout-clusters-fe` 后，执行 `npm run start`。
