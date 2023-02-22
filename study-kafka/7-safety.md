---
title: 7.Kafka安全管控
order: 7
toc: menu
---

## 7.1 安全概述

在 0.9.0.0 版本中，Kafka 社区添加了许多功能，这些功能可以单独或一起使用，以提高 Kafka 集群的安全性。当前支持以下安全措施：

1. 使用 SSL 或 SASL 对来自客户端（生产者和消费者）、其他代理和工具的代理连接进行身份验证。Kafka 支持以下 SASL 机制：

- SASL/GSSAPI (Kerberos) - 从版本 0.9.0.0 开始
- SASL/PLAIN - 从版本 0.10.0.0 开始
- SASL/SCRAM-SHA-256 和 SASL/SCRAM-SHA-512 - 从版本 0.10.2.0 开始
- SASL/OAUTHBEARER - 从 2.0 版开始

2. 从 Broker 到 ZooKeeper 的连接身份验证
3. 使用 SSL 加密代理和客户端之间、代理之间或代理和工具之间传输的数据（请注意，启用 SSL 时会出现性能下降，其幅度取决于 CPU 类型和 JVM 实现。）
4. 客户端对读/写操作的授权
5. 授权可插拔，支持与外部授权服务集成

值得注意的是，安全性是可选的——支持非安全集群，以及经过身份验证、未经身份验证、加密和非加密客户端的混合。

## 7.2 使用 SSL 的加密和认证

Apache Kafka 允许客户端使用 SSL 进行流量加密和身份验证。默认情况下，SSL 处于禁用状态，但可以根据需要打开。以下段落详细解释了如何设置您自己的 PKI 基础设施、使用它来创建证书以及配置 Kafka 以使用这些基础设施。

**1.为每个 Kafka Broker 生成 SSL 密钥和证书**

部署一个或多个支持 SSL 的代理的第一步是为每个服务器生成一个公钥/私钥对。由于 Kafka 希望所有密钥和证书都存储在密钥库中，因此我们将使用 Java 的 keytool 命令来完成此任务。该工具支持两种不同的密钥库格式，Java 特定的 jks 格式，现在已被弃用，以及 PKCS12。PKCS12 是 Java 版本 9 的默认格式，以确保无论使用的 Java 版本如何，都使用此格式，所有以下命令都明确指定 PKCS12 格式。

```

keytool -keystore {keystorefile} -alias localhost -validity {validity} -genkey -keyalg RSA -storetype pkcs12

```

您需要在上述命令中指定两个参数：

1. keystorefile：存储此代理的密钥（以及后来的证书）的密钥库文件。密钥库文件包含此代理的私钥和公钥，因此需要保持安全。理想情况下，此步骤在将使用密钥的 Kafka 代理上运行，因为此密钥永远不应传输/离开它打算用于的服务器。
2. 有效期：密钥的有效时间，以天为单位。请注意，这与证书的有效期不同，证书的有效期将在签署证书中确定。您可以使用同一个密钥申请多个证书：如果您的密钥有效期为 10 年，但您的 CA 只会签署有效期为一年的证书，那么随着时间的推移，您可以使用同一个密钥和 10 个证书。

要获得可与刚刚创建的私钥一起使用的证书，需要创建证书签名请求。当由受信任的 CA 签名时，此签名请求会生成实际证书，然后可以将其安装在密钥库中并用于身份验证。
要生成证书签名请求，请对目前创建的所有服务器密钥库运行以下命令。

```sh

keytool -keystore server.keystore.jks -alias localhost -validity {validity} -genkey -keyalg RSA -destkeystoretype pkcs12 -ext SAN=DNS:{FQDN},IP:{IPADDRESS1}

```

此命令假定您要在证书中添加主机名信息，如果不是这种情况，您可以省略扩展参数`-ext SAN=DNS:{FQDN},IP:{IPADDRESS1}`。有关这方面的更多信息，请参阅下文。

**主机名验证**

主机名验证在启用时是检查您要连接的服务器提供的证书中的属性与该服务器的实际主机名或 IP 地址的过程，以确保您确实连接到正确的服务器。
此检查的主要原因是防止中间人攻击。对于 Kafka，此检查默认已禁用很长时间，但从 Kafka 2.0.0 开始，默认为客户端连接和代理间连接启用服务器的主机名验证。
可以通过设置`ssl.endpoint.identification.algorithm`为空字符串来禁用服务器主机名验证。

对于动态配置的 Broker Listener，可以使用以下命令禁用主机名验证`kafka-configs.sh`：

```sh
bin/kafka-configs.sh --bootstrap-server localhost:9093 --entity-type brokers --entity-name 0 --alter --add-config "listener.name.internal.ssl.endpoint.identification.algorithm="

```

**2. 创建自己的 CA**

在这一步之后，集群中的每台机器都有一个可以用于加密流量的公钥/私钥对和一个证书签名请求，这是创建证书的基础。要添加身份验证功能，此签名请求需要由受信任的机构签名，该机构将在此步骤中创建。

证书颁发机构 (CA) 负责签署证书。CA 的工作就像政府颁发护照一样——政府在每本护照上盖章（签名），使护照变得难以伪造。其他政府验证印章以确保护照是真实的。类似地，CA 对证书进行签名，并且加密保证签名证书在计算上难以伪造。

该步骤省略..

**3.签署证书**

**4.生产中的常见陷阱**

以上段落显示了创建您自己的 CA 并使用它为您的集群签署证书的过程。虽然对于沙盒、开发、测试和类似系统非常有用，但这通常不是为企业环境中的生产集群创建证书的正确过程。企业通常会运营自己的 CA，用户可以发送 CSR 以使用该 CA 进行签名，这样做的好处是用户无需负责维护 CA 的安全以及每个人都可以信任的中央权威。然而，它也剥夺了用户对证书签名过程的大量控制。

1. 扩展密钥使用
   证书可能包含一个扩展字段，用于控制证书的使用目的。如果此字段为空，则对使用没有限制，但如果此处指定了任何使用，则有效的 SSL 实现必须强制执行这些使用。
   Kafka 的相关用法是：
   客户端认证
   服务器认证
   Kafka 代理需要允许这两种用法，因为对于集群内通信，每个代理都将作为客户端和服务器对其他代理进行操作。企业 CA 拥有 web 服务器的签名配置文件并将其用于 Kafka 的情况并不少见，这将仅包含 serverAuth 使用值并导致 SSL 握手失败。
2. 中间证书
   出于安全原因，企业根 CA 通常处于脱机状态。为了实现日常使用，创建了所谓的中间 CA，然后用于签署最终证书。将证书导入由中间 CA 签名的密钥库时，必须提供到根 CA 的整个信任链。这可以通过简单地将证书文件放入一个组合证书文件中，然后使用 keytool 导入来完成 。
3. 未能复制扩展字段
   CA 运营商通常不愿从 CSR 复制和请求扩展字段，而是更愿意自己指定这些字段，因为这使得恶意方更难获得具有潜在误导或欺诈值的证书。建议仔细检查签名证书，这些证书是否包含所有请求的 SAN 字段以启用正确的主机名验证。以下命令可用于将证书详细信息打印到控制台，应与最初请求的内容进行比较：

```
openssl x509 -in certificate.crt -text -noout

```

**5.配置 Kafka 代理**

Kafka Brokers 支持侦听多个端口上的连接。我们需要在 server.properties 中配置以下属性，该属性必须有一个或多个逗号分隔值：

```
listener
```

如果没有为代理间通信启用 SSL（请参阅下文了解如何启用它），则 PLAINTEXT 和 SSL 端口都是必需的。

```
listeners=PLAINTEXT://host.name:port,SSL://host.name:port

```

在代理端需要以下 SSL 配置

```
ssl.keystore.location=/var/private/ssl/server.keystore.jks
ssl.keystore.password=test1234
ssl.key.password=test1234
ssl.truststore.location=/var/private/ssl/server.truststore.jks
ssl.truststore.password=test1234
```

注意： ssl.truststore.password 在技术上是可选的，但强烈推荐。如果未设置密码，则仍可访问信任库，但禁用完整性检查。值得考虑的可选设置：

1. ssl.client.auth=none ("required" => 需要客户端身份验证，"requested" => 请求客户端身份验证并且没有证书的客户端仍然可以连接。不鼓励使用“requested”，因为它提供了一种错误的感觉安全和错误配置的客户端仍然会成功连接。）
2. ssl.cipher.suites（可选）。密码套件是身份验证、加密、MAC 和密钥交换算法的命名组合，用于协商使用 TLS 或 SSL 网络协议的网络连接的安全设置。（默认为空列表）
3. ssl.enabled.protocols=TLSv1.2,TLSv1.1,TLSv1（列出您将从客户端接受的 SSL 协议。请注意，不推荐使用 SSL 以支持 TLS，并且不建议在生产中使用 SSL）
4. ssl.keystore.type=JKS
5. ssl.truststore.type=JKS
6. ssl.secure.random.implementation=SHA1PRNG

如果要为 Broker 之间间通信启用 SSL，请将以下内容添加到 server.properties 文件（默认为 PLAINTEXT）

```
security.inter.broker.protocol=SSL

```

启动代理后，您应该能够在 server.log 中看到

```
with addresses: PLAINTEXT -> EndPoint(xxx.xxx.xx.xx,9092,PLAINTEXT),SSL -> EndPoint(xxx.xxx.xx.xx,9093,SSL)

```

要快速检查服务器密钥库和信任库是否设置正确，您可以运行以下命令

```
openssl s_client -debug -connect localhost:9093 -tls1

```

（注意：TLSv1 应该列在 ssl.enabled.protocols 下）
在此命令的输出中，您应该看到服务器的证书：

```
-----BEGIN CERTIFICATE-----
{variable sized random bytes}
-----END CERTIFICATE-----
subject=/C=US/ST=CA/L=Santa Clara/O=org/OU=org/CN=Sriharsha Chintalapani
issuer=/C=US/ST=CA/L=Santa Clara/O=org/OU=org/CN=kafka/emailAddress=test@test.com
```

如果证书未显示或有任何其他错误消息，则您的密钥库设置不正确。

**6.配置 Kafka 客户端**

仅新的 Kafka 生产者和消费者支持 SSL，不支持旧 API。生产者和消费者的 SSL 配置相同。
如果代理中不需要客户端身份验证，那么以下是一个最小配置示例：

```sh

security.protocol=SSL
ssl.truststore.location=/var/private/ssl/client.truststore.jks
ssl.truststore.password=test1234
```

注意：` ssl.truststore.password` 在技术上是可选的，但强烈推荐。如果未设置密码，则仍可访问信任库，但禁用完整性检查。如果需要客户端身份验证，则必须像步骤 1 中一样创建密钥库，并且还必须配置以下内容：

```sh
ssl.keystore.location=/var/private/ssl/client.keystore.jks
ssl.keystore.password=test1234
ssl.key.password=test1234

```

根据我们的要求和代理配置，可能还需要其他配置设置：

1. ssl.provider（可选）。用于 SSL 连接的安全提供程序的名称。默认值是 JVM 的默认安全提供程序。
2. ssl.cipher.suites（可选）。密码套件是身份验证、加密、MAC 和密钥交换算法的命名组合，用于协商使用 TLS 或 SSL 网络协议的网络连接的安全设置。
3. ssl.enabled.protocols=TLSv1.2,TLSv1.1,TLSv1。它应该至少列出一个在代理端配置的协议
4. ssl.truststore.type=JKS
5. ssl.keystore.type=JKS

使用控制台生产者和控制台消费者的示例：

```
kafka-console-producer.sh --bootstrap-server localhost:9093 --topic test --producer.config client-ssl.properties
kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic test --consumer.config client-ssl.properties
```

## 7.3 使用 SASL 进行身份验证

<font size=5 ><b> 1.JAAS 配置 </b></font>

Kafka 使用 Java 身份验证和授权服务 ( JAAS ) 进行 SASL 配置。

**1.Kafka 代理的 JAAS 配置**

KafkaServer 是每个 KafkaServer/Broker 使用的 JAAS 文件中的部分名称。本节为代理提供 SASL 配置选项，包括代理为进行代理间通信而建立的任何 SASL 客户端连接。如果多个侦听器配置为使用 SASL，则部分名称可以以小写的侦听器名称为前缀，后跟句点，例如 sasl_ssl.KafkaServer。

客户端部分用于验证与 zookeeper 的 SASL 连接。它还允许代理在 zookeeper 节点上设置 SASL ACL，锁定这些节点，以便只有代理可以修改它。所有代理都必须具有相同的主体名称。如果您想使用 Client 以外的部分名称，请将系统属性 zookeeper.sasl.clientconfig 设置为适当的名称（例如，-Dzookeeper.sasl.clientconfig=ZkClient）。

ZooKeeper 默认使用“zookeeper”作为服务名称。如果要更改此设置，请将系统属性 zookeeper.sasl.client.username 设置为适当的名称（例如，-Dzookeeper.sasl.client.username=zk）。

代理还可以使用代理配置属性来配置 JAAS sasl.jaas.config。属性名称必须以包括 SASL 机制的侦听器前缀作为前缀，即 listener.name.{listenerName}.{saslMechanism}.sasl.jaas.config. 配置值中只能指定一个登录模块。如果在侦听器上配置了多个机制，则必须使用侦听器和机制前缀为每个机制提供配置。例如，

```sh
listener.name.sasl_ssl.scram-sha-256.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required \
    username="admin" \
    password="admin-secret";
listener.name.sasl_ssl.plain.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
    username="admin" \
    password="admin-secret" \
    user_admin="admin-secret" \
    user_alice="alice-secret";
```

如果在不同级别定义 JAAS 配置，则使用的优先顺序为：

- 代理配置属性 listener.name.{listenerName}.{saslMechanism}.sasl.jaas.config
- {listenerName}.KafkaServer 静态 JAAS 配置部分
- KafkaServer 静态 JAAS 配置部分

请注意，ZooKeeper JAAS 配置只能使用静态 JAAS 配置进行配置。

有关代理配置的示例，请参阅[GSSAPI (Kerberos)](https://kafka.apache.org/27/documentation.html#security_sasl_kerberos_brokerconfig)、 [PLAIN](https://kafka.apache.org/27/documentation.html#security_sasl_plain_brokerconfig)、 [SCRAM](https://kafka.apache.org/27/documentation.html#security_sasl_scram_brokerconfig)或 [OAUTHBEARER](https://kafka.apache.org/27/documentation.html#security_sasl_oauthbearer_brokerconfig)。

**2.Kafka 客户端的 JAAS 配置**

客户端可以使用客户端配置属性 [sasl.jaas.config](https://kafka.apache.org/27/documentation.html#security_client_dynamicjaas) 或使用 类似于代理的 [静态 JAAS 配置文件来配置 JAAS](https://kafka.apache.org/27/documentation.html#security_client_staticjaas)。

1. 使用客户端配置属性的 JAAS 配置
   客户端可以将 JAAS 配置指定为生产者或消费者属性，而无需创建物理配置文件。此模式还允许同一 JVM 中的不同生产者和消费者通过为每个客户端指定不同的属性来使用不同的凭据。如果同时指定了静态 JAAS 配置系统属性 java.security.auth.login.config 和客户端属性 sasl.jaas.config ，则将使用客户端属性。
   有关示例配置，请参阅[GSSAPI (Kerberos)](https://kafka.apache.org/27/documentation.html#security_sasl_kerberos_brokerconfig)、 [PLAIN](https://kafka.apache.org/27/documentation.html#security_sasl_plain_brokerconfig)、 [SCRAM](https://kafka.apache.org/27/documentation.html#security_sasl_scram_brokerconfig)或 [OAUTHBEARER](https://kafka.apache.org/27/documentation.html#security_sasl_oauthbearer_brokerconfig)。

2. 使用静态配置文件的 JAAS 配置
   要使用静态 JAAS 配置文件在客户端上配置 SASL 身份验证：
   ①. 添加一个 JAAS 配置文件，其中包含一个名为 KafkaClient 的客户端登录部分。在 KafkaClient 中为所选机制配置登录模块，如设置 GSSAPI (Kerberos)、 PLAIN、 SCRAM 或 OUTHBEARER 的示例中所述。例如，GSSAPI 凭证可以配置为：
   ```
   KafkaClient {
       com.sun.security.auth.module.Krb5LoginModule required
       useKeyTab=true
       storeKey=true
       keyTab="/etc/security/keytabs/kafka_client.keytab"
       principal="kafka-client-1@EXAMPLE.COM";
   };
   ```
   ②.将 JAAS 配置文件位置作为 JVM 参数传递给每个客户端 JVM。例如：

```
-Djava.security.auth.login.config=/etc/kafka/kafka_client_jaas.conf
```

<font size=5 ><b> 2. SASL 配置 </b></font>

SASL 可以与 PLAINTEXT 或 SSL 一起用作传输层，分别使用安全协议 SASL_PLAINTEXT 或 SASL_SSL。如果使用 SASL_SSL，则[还必须配置 SSL](https://kafka.apache.org/27/documentation.html#security_ssl)。

**1.SASL 机制**

Kafka 支持以下 SASL 机制：

- GSSAPI (Kerberos)
- PLAIN
- SCRAM-SHA-256
- SCRAM-SHA-512
- OAUTHBEARER

**2.Kafka 代理的 SASL 配置**

1. 在 server.properties 中配置 SASL 端口，方法是向 listeners 参数添加 SASL_PLAINTEXT 或 SASL_SSL 中的至少一个，该参数包含一个或多个逗号分隔值：

   ```

   listeners=SASL_PLAINTEXT://host.name:port

   ```

   如果您只配置 SASL 端口（或者如果您希望 Kafka 代理使用 SASL 相互验证），请确保为代理间通信设置相同的 SASL 协议：

   ```
   security.inter.broker.protocol=SASL_PLAINTEXT（或 SASL_SSL）

   ```

2. 选择一种或多种 [受支持的机制](https://kafka.apache.org/27/documentation.html#security_sasl_mechanism) 以在代理中启用，然后按照步骤为该机制配置 SASL。要在代理中启用多种机制，请按照 此处的步骤操作。

3. Kafka 客户端的 SASL 配置

   仅新的 Java Kafka 生产者和消费者支持 SASL 身份验证，不支持旧 API。

   要在客户端上配置 SASL 身份验证，请选择在代理中为客户端身份验证启用的 SASL [机制](https://kafka.apache.org/27/documentation.html#security_sasl_mechanism)，然后按照步骤为所选机制配置 SASL。

<font size=5 ><b> 3. 使用 SASL/Kerberos 进行身份验证 </b></font>

**1. 先决条件**

1. Kerberos
   如果您的组织已经在使用 Kerberos 服务器（例如，通过使用 Active Directory），则无需仅为 Kafka 安装新服务器。否则，您将需要安装一个，您的 Linux 供应商可能有 Kerberos 软件包以及如何安装和配置它的简短指南（[Ubuntu](https://help.ubuntu.com/community/Kerberos)、[Redhat](https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Linux/6/html/Managing_Smart_Cards/installing-kerberos.html)）。请注意，如果您使用的是 Oracle Java，则需要为您的 Java 版本下载 JCE 策略文件并将它们复制到 $JAVA_HOME/jre/lib/security。

2. 创建 Kerberos 主体

   如果您使用组织的 Kerberos 或 Active Directory 服务器，请向您的 Kerberos 管理员询问集群中每个 Kafka 代理以及将使用 Kerberos 身份验证（通过客户端和工具）访问 Kafka 的每个操作系统用户的主体。
   如果您已经安装了自己的 Kerberos，则需要使用以下命令自己创建这些主体：

   ```

   sudo /usr/sbin/kadmin.local -q 'addprinc -randkey kafka/{hostname}@{REALM}'
   sudo /usr/sbin/kadmin.local -q "ktadd -k /etc/security/keytabs/{keytabname}.keytab kafka/{hostname}@{REALM}"

   ```

3. 确保可以使用主机名访问所有主机- Kerberos 要求您的所有主机都可以使用它们的 FQDN 进行解析。

**2.配置 Kafka Broker**

1. 在每个 Kafka 代理的配置目录中添加一个经过适当修改的 JAAS 文件，类似于下面的文件，我们将其命名为 kafka_server_jaas.conf （请注意，每个代理都应该有自己的密钥表）：

   ```

   KafkaServer {
       com.sun.security.auth.module.Krb5LoginModule required
       useKeyTab=true
       storeKey=true
       keyTab="/etc/security/keytabs/kafka_server.keytab"
       principal="kafka/kafka1.hostname.com@EXAMPLE.COM";
   };

   // Zookeeper client authentication
   Client {
       com.sun.security.auth.module.Krb5LoginModule required
       useKeyTab=true
       storeKey=true
       keyTab="/etc/security/keytabs/kafka_server.keytab"
       principal="kafka/kafka1.hostname.com@EXAMPLE.COM";
   };

   ```

JAAS 文件中的 KafkaServer 部分告诉代理要使用哪个主体以及存储该主体的密钥表的位置。它允许代理使用本节中指定的密钥表登录。有关 Zookeeper SASL 配置的更多详细信息，请参阅[注释](https://kafka.apache.org/27/documentation.html#security_jaas_broker)。

2. 将 JAAS 和可选的 krb5 文件位置作为 JVM 参数传递给每个 Kafka 代理（请参阅[此处](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jgss/tutorials/KerberosReq.html)了解更多详细信息）：

   ```
   -Djava.security.krb5.conf=/etc/kafka/krb5.conf
   -Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf
   ```

3. 确保启动 kafka 代理的操作系统用户可以读取 JAAS 文件中配置的密钥表。

4. 在 server.properties 中配置 SASL 端口和 SASL 机制。例如：

   ```
   listeners=SASL_PLAINTEXT://host.name:port
   security.inter.broker.protocol=SASL_PLAINTEXT
   sasl.mechanism.inter.broker.protocol=GSSAPI
   sasl.enabled.mechanisms=GSSAPI
   ```

   我们还必须在 server.properties 中配置服务名称，该名称应与 kafka 代理的主体名称匹配。在上面的例子中，principal 是
   “kafka/kafka1.hostname.com@EXAMPLE.com”，所以：

   ```
   sasl.kerberos.service.name=kafka

   ```

**3.配置 Kafka 客户端**

在客户端上配置 SASL 身份验证：

1. 客户端（生产者、消费者、连接工作人员等）将使用自己的主体（通常与运行客户端的用户同名）向集群进行身份验证，因此根据需要获取或创建这些主体。然后为每个客户端配置 JAAS 配置属性。通过指定不同的主体，JVM 中的不同客户端可以作为不同的用户运行。producer.properties 或 consumer.properties 中的属性 sasl.jaas.config 描述了像 producer 和 consumer 这样的客户端如何连接到 Kafka Broker。以下是使用 keytab 的客户端的示例配置（推荐用于长时间运行的进程）：

   ```
   sasl.jaas.config=com.sun.security.auth.module.Krb5LoginModule required \
       useKeyTab=true \
       storeKey=true  \
       keyTab="/etc/security/keytabs/kafka_client.keytab" \
       principal="kafka-client-1@EXAMPLE.COM";
   ```

   对于像 kafka-console-consumer 或 kafka-console-producer 这样的命令行实用程序，kinit 可以与“useTicketCache=true”一起使用，如下所示：

   ```
   sasl.jaas.config=com.sun.security.auth.module.Krb5LoginModule required \
     useTicketCache=true;
   ```

   客户端的 JAAS 配置也可以指定为类似于[此处](https://kafka.apache.org/27/documentation.html#security_client_staticjaas)描述的代理的 JVM 参数。客户端使用名为 KafkaClient 的登录部分 。此选项仅允许一个用户用于来自 JVM 的所有客户端连接。

2. 确保启动 kafka 客户端的操作系统用户可以读取 JAAS 配置中配置的密钥表。
3. 可选择将 krb5 文件位置作为 JVM 参数传递给每个客户端 JVM（
   ```
   -Djava.security.krb5.conf=/etc/kafka/krb5.conf
   ```
4. 在 producer.properties 或 consumer.properties 中配置以下属性：
   ```
   security.protocol=SASL_PLAINTEXT (or SASL_SSL)
   sasl.mechanism=GSSAPI
   sasl.kerberos.service.name=kafka
   ```

<font size=5 ><b> 4.使用 SASL/PLAIN 进行身份验证 </b></font>

SASL/PLAIN 是一种简单的用户名/密码认证机制，通常与 TLS 一起用于加密以实现安全认证。Kafka 支持 SASL/PLAIN 的默认实现，可以按照[此处](https://kafka.apache.org/27/documentation.html#security_sasl_plain_production)所述进行扩展以供生产使用。

Principal 用户名用作配置 ACL 等 的身份验证。

**1. 配置 Kafka 代理**

1. 在每个 Kafka 代理的配置目录中添加一个经过适当修改的 JAAS 文件，类似于下面的文件，我们将其命名为 kafka_server_jaas.conf 以用于本示例：

   ```
   KafkaServer {
       org.apache.kafka.common.security.plain.PlainLoginModule required
       username="admin"
       password="admin-secret"
       user_admin="admin-secret"
       user_alice="alice-secret";
   };
   ```

   此配置定义了两个用户（admin 和 alice）。KafkaServer 部分 中的属性用户名和密码由代理用于启动与其他代理的连接。在本例中， admin 是代理间通信的用户。属性集 user_userName 定义了连接到代理的所有用户的密码，代理使用这些属性验证所有客户端连接，包括来自其他代理的连接。

2. 将 JAAS 配置文件位置作为 JVM 参数传递给每个 Kafka 代理：

   ```
   -Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf

   ```

3. 在 server.properties 中配置 SASL 端口和 SASL 机制。例如
   ```
   listeners=SASL_SSL://host.name:port
   security.inter.broker.protocol=SASL_SSL
   sasl.mechanism.inter.broker.protocol=PLAIN
   sasl.enabled.mechanisms=PLAIN
   ```

**2. 配置 Kafka 客户端**

在客户端上配置 SASL 身份验证：

1. 在 producer.properties 或 consumer.properties 中为每个客户端配置 JAAS 配置属性。登录模块描述了生产者和消费者等客户端如何连接到 Kafka 代理。以下是 PLAIN 机制的客户端的示例配置：

   ```
   sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
     username="alice" \
     password="alice-secret";
   ```

   客户端使用选项用户名和密码来配置用户以进行客户端连接。在此示例中，客户端以用户 alice 的身份连接到代理。JVM 中的不同客户端可以通过在 sasl.jaas.config.

   客户端的 JAAS 配置也可以指定为类似于此处描述的代理的 JVM 参数。客户端使用名为 KafkaClient 的登录部分 。此选项仅允许一个用户用于来自 JVM 的所有客户端连接。

2. 在 producer.properties 或 consumer.properties 中配置以下属性：

   ```
   security.protocol=SASL_SSL
   sasl.mechanism=PLAIN
   ```

3. 在生产中使用 SASL/PLAIN

-     SASL/PLAIN 应仅与 SSL 作为传输层一起使用，以确保在未加密的情况下不会在线路上传输明文密码。
- Kafka 中 SASL/PLAIN 的默认实现在 JAAS 配置文件中指定用户名和密码，如下 所示。从 Kafka 版本 2.0 开始，您可以通过配置自己的回调处理程序来避免在磁盘上存储明确的密码，这些回调处理程序使用配置选项从外部源获取用户名和密码， sasl.server.callback.handler.class 并且 sasl.client.callback.handler.class.
- 在生产系统中，外部认证服务器可以实现密码认证。从 Kafka 2.0 版开始，您可以插入自己的回调处理程序，这些处理程序使用外部身份验证服务器通过配置 sasl.server.callback.handler.class.

<font size=5 ><b> 5. 使用 SASL/SCRAM 进行身份验证 </b></font>

Salted Challenge Response Authentication Mechanism (SCRAM) 是一系列 SASL 机制，可解决执行用户名/密码验证的传统机制（如 PLAIN 和 DIGEST-MD5）的安全问题。该机制在 RFC 5802 中定义。Kafka 支持 SCRAM-SHA-256 和 SCRAM-SHA-512，可与 TLS 一起使用以执行安全身份验证。用户名用作 PrincipalACL 等配置的身份验证。Kafka 中的默认 SCRAM 实现将 SCRAM 凭据存储在 Zookeeper 中，适用于 Zookeeper 在专用网络上的 Kafka 安装。有关更多详细信息，请参阅安全注意事项 。

<font size=5 ><b> 6. 使用 SASL/OAUTHBEARER 进行身份验证 </b></font>

<font size=5 ><b> 7. 在代理中启用多个 SASL 机制 </b></font>

[在代理中启用多个 SASL 机制](https://kafka.apache.org/27/documentation.html#security_overview)

<font size=5 ><b> 8. 在正在运行的集群中修改 SASL 机制 </b></font>

可以使用以下顺序在正在运行的集群中修改 SASL 机制：

1. 通过将机制添加到每个代理的 server.properties 中的 sasl.enabled.mechanisms 来启用新的 SASL 机制。更新 JAAS 配置文件以包括此处所述的两种机制。增量反弹集群节点。
2. 使用新机制重新启动客户端。
3. 要更改代理间通信的机制（如果需要），请将 server.properties 中的 sasl.mechanism.inter.broker.protocol 设置为新机制并再次增量反弹集群。
4. 要删除旧机制（如果需要），请从 server.properties 中的 sasl.enabled.mechanisms 中删除旧机制，并从 JAAS 配置文件中删除旧机制的条目。再次增量反弹集群。

## 7.4 授权和 ACL

[授权和 ACL](https://kafka.apache.org/27/documentation.html#security_authz)

## 7.5 在运行的集群中加入安全特性

您可以通过前面讨论的一种或多种支持的协议来保护正在运行的集群。这是分阶段完成的：

- 增量反弹集群节点以打开额外的安全端口。
- 使用安全端口而不是 PLAINTEXT 端口重新启动客户端（假设您正在保护客户端-代理连接）。
- 再次增量反弹集群以启用代理到代理的安全性（如果需要）
- 关闭 PLAINTEXT 端口的最终增量反弹

配置 SSL 和 SASL 的具体步骤在 7.2 和 7.3 节中描述。按照以下步骤启用所需协议的安全性。

安全实现允许您为代理-客户端和代理-代理通信配置不同的协议。这些必须在单独的退回中启用。PLAINTEXT 端口必须始终保持打开状态，以便代理和/或客户端可以继续通信。
当执行增量反弹时，通过 SIGTERM 干净地停止代理。在移动到下一个节点之前，等待重新启动的副本返回 ISR 列表也是一种很好的做法。
例如，假设我们希望使用 SSL 加密代理-客户端和代理-代理通信。在第一次增量反弹中，每个节点上都会打开一个 SSL 端口：

```
listeners=PLAINTEXT://broker1:9091,SSL://broker1:9092

```

然后我们重新启动客户端，将它们的配置更改为指向新打开的安全端口：

```
bootstrap.servers = [broker1:9092,...]
security.protocol = SSL
...etc
```

在第二次增量服务器反弹中，我们指示 Kafka 使用 SSL 作为代理-代理协议（它将使用相同的 SSL 端口）：

```
listeners=PLAINTEXT://broker1:9091,SSL://broker1:9092
security.inter.broker.protocol=SSL
```

在最后的反弹中，我们通过关闭 PLAINTEXT 端口来保护集群：

```
listeners=SSL://broker1:9092
security.inter.broker.protocol=SSL

```

或者，我们可以选择打开多个端口，以便可以使用不同的协议进行代理-代理和代理-客户端通信。假设我们希望始终使用 SSL 加密（即用于代理-代理和代理-客户端通信），但我们还想将 SASL 身份验证添加到代理-客户端连接。我们将通过在第一次反弹期间打开两个额外的端口来实现这一点：

```
listeners=PLAINTEXT://broker1:9091,SSL://broker1:9092,SASL_SSL://broker1:9093

```

然后我们将重新启动客户端，将它们的配置更改为指向新打开的 SASL 和 SSL 安全端口：

```
bootstrap.servers = [broker1:9093,...]
security.protocol = SASL_SSL
...etc
```

第二个服务器反弹将通过我们之前在端口 9092 上打开的 SSL 端口切换集群以使用加密的代理-代理通信

```
listeners=PLAINTEXT://broker1:9091,SSL://broker1:9092,SASL_SSL://broker1:9093
security.inter.broker.protocol=SSL
```

最后的反弹通过关闭 PLAINTEXT 端口来保护集群。

```
listeners=SSL://broker1:9092,SASL_SSL://broker1:9093
security.inter.broker.protocol=SSL
```

ZooKeeper 可以独立于 Kafka 集群进行保护。执行此操作的步骤在第 7.6.2 节中介绍。

## 7.6 Zookeeper 认证

[Zookeeper 认证](https://kafka.apache.org/27/documentation.html#zk_authz)

## 7.7 ZooKeeper 加密

使用双向 TLS 的 ZooKeeper 连接是加密的。从 ZooKeeper 版本 3.5.7（随 Kafka 版本 2.5 发布的版本）开始 ZooKeeper 支持服务器端配置 ssl.clientAuth（不区分大小写：需要/需要/无是有效选项，默认为需要），并设置此 ZooKeeper 中的 value 为 none 允许客户端通过 TLS 加密的连接进行连接，而无需提供自己的证书。这是一个示例（部分）Kafka Broker 配置，用于仅使用 TLS 加密连接到 ZooKeeper。这些配置在上面的 Broker Configs 中进行了描述。

```
# connect to the ZooKeeper port configured for TLS
zookeeper.connect=zk1:2182,zk2:2182,zk3:2182
# required to use TLS to ZooKeeper (default is false)
zookeeper.ssl.client.enable=true
# required to use TLS to ZooKeeper
zookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNetty
# define trust stores to use TLS to ZooKeeper; ignored unless zookeeper.ssl.client.enable=true
# no need to set keystore information assuming ssl.clientAuth=none on ZooKeeper
zookeeper.ssl.truststore.location=/path/to/kafka/truststore.jks
zookeeper.ssl.truststore.password=kafka-ts-passwd
# tell broker to create ACLs on znodes (if using SASL authentication, otherwise do not set this)
zookeeper.set.acl=true
```
