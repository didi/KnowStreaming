## YML文件MYSQL密码加密存储手册

### 1、本地部署加密

**第一步：生成密文**

在本地仓库中找到jasypt-1.9.3.jar，默认在org/jasypt/jasypt/1.9.3中，使用`java -cp`生成密文。

```bash
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=mysql密码 password=加密的salt algorithm=PBEWithMD5AndDES
```

```bash
## 得到密文
DYbVDLg5D0WRcJSCUGWjiw==
```

**第二步：配置jasypt**

在YML文件中配置jasypt，例如

```yaml
jasypt:
  encryptor:
    algorithm: PBEWithMD5AndDES
    iv-generator-classname: org.jasypt.iv.NoIvGenerator
```

**第三步：配置密文**

使用密文替换YML文件中的明文密码为ENC(密文），例如[application.yml](https://github.com/didi/KnowStreaming/blob/master/km-rest/src/main/resources/application.yml)中MYSQL密码。

```yaml
know-streaming: 
  username: root
  password: ENC(DYbVDLg5D0WRcJSCUGWjiw==)
```

**第四步：配置加密的salt（选择其一）**

- 配置在YML文件中（不推荐）

```yaml
jasypt:
  encryptor:
    password: salt
```

- 配置程序启动时的命令行参数

```bash
java -jar xxx.jar --jasypt.encryptor.password=salt
```

- 配置程序启动时的环境变量

```bash
export JASYPT_PASSWORD=salt
java -jar xxx.jar --jasypt.encryptor.password=${JASYPT_PASSWORD}
```

## 2、容器部署加密

利用docker swarm 提供的 secret 机制加密存储密码，使用docker swarm来管理密码。

### 2.1、secret加密存储

**第一步：初始化docker swarm**

```bash
docker swarm init
```

**第二步：创建密钥**

```bash
echo "admin2022_" | docker secret create mysql_password -

# 输出密钥
f964wi4gg946hu78quxsh2ge9
```

**第三步：使用密钥**

```yaml
# mysql用户密码
SERVER_MYSQL_USER: root
SERVER_MYSQL_PASSWORD: mysql_password

knowstreaming-mysql:
    # root 用户密码
    MYSQL_ROOT_PASSWORD: mysql_password
secrets:
  mysql_password:
    external: true
```

### 2.2、使用密钥文件加密

**第一步：创建密钥**

```bash
echo "admin2022_" > password
```

**第二步：使用密钥**

```yaml
# mysql用户密码
SERVER_MYSQL_USER: root
SERVER_MYSQL_PASSWORD: mysql_password
secrets:
  mysql_password:
    file: ./password
```
