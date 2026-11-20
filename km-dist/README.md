
# 安装包说明

- bin：可执行脚本
  - startup.sh：启动脚本
  - shutdown.sh：停止脚本


- conf：配置
  - application.yml：配置文件
  - logback-spring.xml：日志配置
  - start-out.logrotate：logs/start.out 的 logrotate 轮转配置（可选启用，见下文）


- helm：安装KnowStreaming的Helm相关文件


- init：初始化相关资源
  - sql：初始化DB的相关SQL；
  - template：初始化ES索引的索引模版
  

- libs：Java的libs
  - ks-km.jar：KnowStreaming的Jar包


- LICENSE：开源协议


## start.out 日志轮转（Linux）

`bin/startup.sh` 将 Java 的标准输出和标准错误追加到 `logs/start.out`。每次启动都会写入时间和启动命令。已有实例也可以单独启用以下配置，运行中的 Java 进程无需重启。

1. 确认 Linux 主机已安装 logrotate，并在 `conf/start-out.logrotate` 中填写实际日志文件的绝对路径。下面以安装目录 `/data/KnowStreaming` 为例。
2. 以能读写日志文件和日志目录的服务账号执行以下命令。为这个实例使用单独的状态文件。按 `command -v` 的输出替换示例中的可执行文件路径。

```sh
command -v logrotate
mkdir -p "$HOME/.local/state/knowstreaming" && chmod 700 "$HOME/.local/state/knowstreaming"
/usr/sbin/logrotate -d -s "$HOME/.local/state/knowstreaming/start-out.status" "/data/KnowStreaming/conf/start-out.logrotate"
```

3. 预演通过后，在同一账号的 `crontab -e` 中添加以下一行，并确认该文件只由这一项任务轮转。保存配置文件后需要启用调度才会生效。

```cron
*/5 * * * * /usr/sbin/logrotate -s "$HOME/.local/state/knowstreaming/start-out.status" "/data/KnowStreaming/conf/start-out.logrotate"
```

任务每 5 分钟检查一次，按天归档。检查时超过 `maxsize 100M` 会提前分卷，两次检查之间文件仍可超过阈值。默认压缩归档，在轮转时清理超过 `maxage 7` 天的文件，最多保留 `rotate 14` 份。高日志量时，14 份可能覆盖不足 7 天。以上参数均可按需调整。

`copytruncate` 让原进程继续写同一个文件，仍可用 `tail -f logs/start.out` 跟踪。复制与截短之间可能丢失少量输出。首次处理已有大文件时，请预留足以容纳一份完整副本的空间。
