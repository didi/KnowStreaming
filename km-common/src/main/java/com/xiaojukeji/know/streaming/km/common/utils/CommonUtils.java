package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: D10865
 * @description:
 * @date: Create on 2019/1/18 上午11:48
 * @modified By D10865
 */
public class CommonUtils {

    private static final ILog LOGGER = LogFactory.getLog(CommonUtils.class);

    private static final String REGEX = ",";

    private CommonUtils() {}

    /**
     * 获取MD5值
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes( StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取文件的md5
     * @param file
     * @return
     */
    public static String getMD5(MultipartFile file) {
        try {
            //获取文件的byte信息
            byte[] uploadBytes = file.getBytes();
            // 拿到一个MD5转换器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            LOGGER.error("method=getMD5||msg=获取文件的md5失败:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 保留指定的小数位 四舍五入
     * @param data    需要转换的数据
     * @param decimal 保留小数的位数 默认是2
     * @return
     */
    public static double formatDouble(double data, int decimal) {
        if (decimal < 0){decimal = 2;}

        BigDecimal b = BigDecimal.valueOf(data);
        return b.setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String toHex(byte[] bytes) {

        final char[] hexDigits = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(hexDigits[(bytes[i] >> 4) & 0x0f]);
            ret.append(hexDigits[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    public static String strList2String(List<String> strList) {
        if (strList == null || strList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String elem: strList) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static String intList2String(List<Integer> intList) {
        if (intList == null || intList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Integer elem: intList) {
            if (elem == null) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static String longList2String(List<Long> longList) {
        if (longList == null || longList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Long elem: longList) {
            if (elem == null) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static String intSet2String(Set<Integer> intSet) {
        if (intSet == null || intSet.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Integer elem: intSet) {
            if (elem == null) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static List<String> string2StrList(String str) {
        if (!StringUtils.hasText(str)) {
            return new ArrayList<>();
        }
        List<String> strList = new ArrayList<>();
        for (String elem: str.split(REGEX)) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            strList.add(elem);
        }
        return strList;
    }

    public static Long monitorTimestamp2min(Long timestamp){
        return timestamp - timestamp % 60000;
    }

    /**
     * 字符串追加
     *
     * @param items
     * @return
     */
    public static String strConcat(List<String> items) {
        StringBuilder stringBuilder = new StringBuilder(128);
        boolean isFirstItem = true;

        for (String item : items) {
            if (isFirstItem) {
                stringBuilder.append( String.format("\"%s\"", item));
                isFirstItem = false;
            } else {
                stringBuilder.append(",").append( String.format("\"%s\"", item));
            }
        }

        return stringBuilder.toString();
    }
    /**
     * 判断是否为合法IP
     * @return the ip
     */
    public static boolean checkIp(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }

        String rexp1 = "^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])\\.";
        String rexp2 = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])\\.";
        String rexp3 = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";

        Pattern pat = Pattern.compile(rexp1 + rexp2 + rexp2 + rexp3);
        Matcher mat = pat.matcher(addr);

        return mat.find();

    }

    /**
     * 生成固定长度的随机字符串
     */
    public static String randomString(int length) {
        char[] value = new char[length];
        for (int i = 0; i < length; i++) {
            value[i] = randomWritableChar();
        }
        return new String(value);
    }

    /**
     * 随机生成单个随机字符
     */
    public static char randomWritableChar() {
        Random random = new Random();
        return (char) (33 + random.nextInt(94));
    }

    public static List<Integer> string2IntList(String str) {
        if (!StringUtils.hasText(str)) {
            return new ArrayList<>();
        }
        List<Integer> intList = new ArrayList<>();
        for (String elem :str.split(REGEX)) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            intList.add(Integer.valueOf(elem));
        }
        return intList;
    }

    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }

        return true;
    }

    public static String getWorkerId(String url){
        try {
            URI uri = new URI(url);
            return uri.getHost() + ":" + uri.getPort();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 校验两个list的第一个元素是否相等,以","分隔元素。
     * @param str1
     * @param str2
     * @return
     */
    public static boolean checkFirstElementIsEquals(String str1, String str2) {
        if (ValidateUtils.anyBlank(str1, str2)) {
            return false;
        }
        Integer targetLeader = CommonUtils.string2IntList(str1).get(0);
        Integer originalLeader = CommonUtils.string2IntList(str2).get(0);
        return originalLeader.equals(targetLeader);
    }
}
