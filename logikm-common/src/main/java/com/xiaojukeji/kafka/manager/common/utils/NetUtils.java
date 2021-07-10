package com.xiaojukeji.kafka.manager.common.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author zengqiao
 * @date 20/6/8
 */
public class NetUtils {
    private static String ipCache = null;
    private static String hostnameCache = null;

    public static String localIp() {
        if (!ValidateUtils.isBlank(ipCache)) {
            return ipCache;
        }

        InetAddress inetAddress = getInetAddress();
        hostnameCache = inetAddress.getHostName();
        ipCache = inetAddress.getHostAddress();
        return ipCache;
    }

    public static String localHostname() {
        if (!ValidateUtils.isNull(hostnameCache)) {
            return hostnameCache;
        }
        localIp();
        return hostnameCache;
    }

    public static Boolean hostnameLegal(String hostname) {
        if (ValidateUtils.isExistBlank(hostname)) {
            return false;
        }

        hostname = hostname.trim();
        try {
            InetAddress.getByName(hostname);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":") == -1) {
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}