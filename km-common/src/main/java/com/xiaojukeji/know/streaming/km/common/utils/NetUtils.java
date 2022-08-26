package com.xiaojukeji.know.streaming.km.common.utils;

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
    private static String hostCache = null;
    private static String macCache = null;

    public static String localMac() {
        if (!ValidateUtils.isBlank(macCache)) {
            return macCache;
        }

        initLocalCache();
        return macCache;
    }

    public static String localIp() {
        if (!ValidateUtils.isBlank(ipCache)) {
            return ipCache;
        }

        initLocalCache();
        return ipCache;
    }

    public static String localHost() {
        if (!ValidateUtils.isNull(hostCache)) {
            return hostCache;
        }

        initLocalCache();
        return hostCache;
    }

    public static Boolean hostLegal(String hostname) {
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

    private static void initLocalCache() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress == null) {
                return;
            }

            hostCache = inetAddress.getHostName();
            ipCache = inetAddress.getHostAddress();

            //获取网卡，获取地址
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }

                //字节转换为整数
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }

            macCache = sb.toString();
        } catch (Exception e) {
            // ignore
        }
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
            // ignore
        }
        return null;
    }
}