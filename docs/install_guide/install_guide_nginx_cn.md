---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 

## nginx配置-安装手册

# 一、独立部署

请参考参考：[kafka-manager 安装手册](install_guide_cn.md)

# 二、nginx配置

## 1、独立部署配置

```
    #nginx 根目录访问配置如下 
    location / {
        proxy_pass  http://ip:port;
    }  
```

## 2、前后端分离&配置多个静态资源

以下配置解决`nginx代理多个静态资源`，实现项目前后端分离，版本更新迭代。

### 1、源码下载

根据所需版本下载对应代码，下载地址：[Github 下载地址](https://github.com/didi/Logi-KafkaManager)

### 2、修改webpack.config.js 配置文件

修改`kafka-manager-console`模块 `webpack.config.js`
以下所有<font color='red'>xxxx</font>为nginx代理路径和打包静态文件加载前缀,<font color='red'>xxxx</font>可根据需求自行更改。

```
    cd kafka-manager-console
    vi webpack.config.js

    # publicPath默认打包方式根目录下，修改为nginx代理访问路径。 
    let publicPath = '/xxxx';
```

### 3、打包

```
    
    npm cache clean --force && npm install
    
```

ps：如果打包过程中报错，运行`npm install clipboard@2.0.6`，相反请忽略！

### 4、部署

#### 1、前段静态文件部署

静态资源 `../kafka-manager-web/src/main/resources/templates`

上传到指定目录，目前以`root目录`做demo

#### 2、上传jar包并启动，请参考：[kafka-manager 安装手册](install_guide_cn.md)

#### 3、修改nginx 配置

```
    location /xxxx {
    #   静态文件存放位置
        alias   /root/templates;
        try_files $uri $uri/ /xxxx/index.html;
        index  index.html;
    }

    location /api {
       proxy_pass  http://ip:port;
    }
    #后代端口建议使用/api，如果冲突可以使用以下配置
    #location /api/v2 {
    #   proxy_pass  http://ip:port;
    #}
    #location /api/v1 {
    #   proxy_pass  http://ip:port;
    #}
```







