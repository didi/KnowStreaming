package com.xiaojukeji.know.streaming.km.common.utils.zookeeper;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.parser.FourLetterWordDataParser;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import org.apache.zookeeper.common.ClientX509Util;
import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.common.X509Util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

public class FourLetterWordUtil {
    private static final ILog LOGGER = LogFactory.getLog(FourLetterWordUtil.class);

    public static final String MonitorCmd = "mntr";
    public static final String ConfigCmd = "conf";
    public static final String ServerCmd = "srvr";

    private static final Set<String> supportedCommands = new HashSet<>();

    public static <T> Result<T> executeFourLetterCmd(Long clusterPhyId,
                                                     String host,
                                                     int port,
                                                     boolean secure,
                                                     int timeout,
                                                     FourLetterWordDataParser<T> dataParser) {
        try {
            if (!supportedCommands.contains(dataParser.getCmd())) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, String.format("ZK %s命令暂未进行支持", dataParser.getCmd()));
            }

            String cmdData = send4LetterWord(host, port, dataParser.getCmd(), secure, timeout);
            if (cmdData.contains("not executed because it is not in the whitelist.")) {
                return Result.buildFromRSAndMsg(ResultStatus.ZK_FOUR_LETTER_CMD_FORBIDDEN, cmdData);
            }
            if (ValidateUtils.isBlank(cmdData)) {
                return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, cmdData);
            }

            return Result.buildSuc(dataParser.parseAndInitData(clusterPhyId, host, port, cmdData));
        } catch (Exception e) {
            LOGGER.error(
                    "method=executeFourLetterCmd||clusterPhyId={}||host={}||port={}||cmd={}||secure={}||timeout={}||errMsg=exception!",
                    clusterPhyId, host, port, dataParser.getCmd(), secure, timeout, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }


    /**************************************************** private method ****************************************************/

    private static String send4LetterWord(
            String host,
            int port,
            String cmd,
            boolean secure,
            int timeout) throws IOException, X509Exception.SSLContextException {
        long startTime = System.currentTimeMillis();

        LOGGER.info("connecting to {} {}", host, port);

        Socket socket = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        try {
            InetSocketAddress hostaddress = host != null
                    ? new InetSocketAddress(host, port)
                    : new InetSocketAddress(InetAddress.getByName(null), port);
            if (secure) {
                LOGGER.info("using secure socket");
                try (X509Util x509Util = new ClientX509Util()) {
                    SSLContext sslContext = x509Util.getDefaultSSLContext();
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    SSLSocket sslSock = (SSLSocket) socketFactory.createSocket();
                    sslSock.connect(hostaddress, timeout);
                    sslSock.startHandshake();
                    socket = sslSock;
                }
            } else {
                socket = new Socket();
                socket.connect(hostaddress, timeout);
            }
            socket.setSoTimeout(timeout);

            outputStream = socket.getOutputStream();
            outputStream.write(cmd.getBytes());
            outputStream.flush();

            // 等待InputStream有数据
            while (System.currentTimeMillis() - startTime <= timeout && socket.getInputStream().available() <= 0) {
                BackoffUtils.backoff(10);
            }

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (SocketTimeoutException e) {
            throw new IOException("Exception while executing four letter word: " + cmd, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error(
                            "method=send4LetterWord||clusterPhyId={}||host={}||port={}||cmd={}||secure={}||timeout={}||errMsg=exception!",
                            host, port, cmd, secure, timeout, e
                    );
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error(
                            "method=send4LetterWord||host={}||port={}||cmd={}||secure={}||timeout={}||errMsg=exception!",
                            host, port, cmd, secure, timeout, e
                    );
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error(
                            "method=send4LetterWord||host={}||port={}||cmd={}||secure={}||timeout={}||errMsg=exception!",
                            host, port, cmd, secure, timeout, e
                    );
                }
            }
        }
    }

    static {
        supportedCommands.add(MonitorCmd);
        supportedCommands.add(ConfigCmd);
        supportedCommands.add(ServerCmd);
    }

}
