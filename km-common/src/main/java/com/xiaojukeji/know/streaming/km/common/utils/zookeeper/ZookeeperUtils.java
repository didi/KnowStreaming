package com.xiaojukeji.know.streaming.km.common.utils.zookeeper;

import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.common.NetUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.zookeeper.common.StringUtils.split;

public class ZookeeperUtils {
    private static final int DEFAULT_PORT = 2181;

    /**
     * 解析ZK地址
     * @see ConnectStringParser
     */
    public static List<Tuple<String, Integer>> connectStringParser(String connectString) {
        List<Tuple<String, Integer>> ipPortList = new ArrayList<>();

        if (connectString == null) {
            return ipPortList;
        }

        // parse out chroot, if any
        int off = connectString.indexOf('/');
        if (off >= 0) {
            connectString = connectString.substring(0, off);
        }

        List<String> hostsList = split(connectString, ",");
        for (String host : hostsList) {
            int port = DEFAULT_PORT;
            String[] hostAndPort = NetUtils.getIPV6HostAndPort(host);
            if (hostAndPort.length != 0) {
                host = hostAndPort[0];
                if (hostAndPort.length == 2) {
                    port = Integer.parseInt(hostAndPort[1]);
                }
            } else {
                int pidx = host.lastIndexOf(':');
                if (pidx >= 0) {
                    // otherwise : is at the end of the string, ignore
                    if (pidx < host.length() - 1) {
                        port = Integer.parseInt(host.substring(pidx + 1));
                    }
                    host = host.substring(0, pidx);
                }
            }

            ipPortList.add(new Tuple<>(host, port));
        }

        return ipPortList;
    }

    public static String getNamespace(String zookeeperAddress) {
        int index = zookeeperAddress.indexOf('/');
        String namespace = "/";
        if (index != -1) {
            namespace = zookeeperAddress.substring(index);
        }
        return namespace;
    }


}
