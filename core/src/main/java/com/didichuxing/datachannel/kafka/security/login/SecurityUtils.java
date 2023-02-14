package com.didichuxing.datachannel.kafka.security.login;

import kafka.server.KafkaConfig;
import org.apache.kafka.common.config.types.Password;
import scala.Option;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SecurityUtils {
    private static String ENCRYPT_KEY = "123456";
    private static String ENCRYPT_ALGORITHM = "DES";
    private static boolean isSec = false;

    public static void start(KafkaConfig config) {
        // ALGORITHM = config.passwordEncoderCipherAlgorithm();
        Option<String> secret = config.passwordEncoderSecret().map(Password::value);
        if (secret.isDefined()) {
            isSec = true;
            ENCRYPT_KEY = secret.get();
        }
    }

    public static String encrypt(String data) {
        return Base64.getEncoder().encodeToString(des(data.getBytes(), Cipher.ENCRYPT_MODE));
    }

    public static String decrypt(String data) {
        if (!isSec) {
            return data;
        }
        return new String(des(Base64.getDecoder().decode(data), Cipher.DECRYPT_MODE));
    }

    private static byte[] des(byte[] data, int mode) {
        try {
            byte[] key = Arrays.copyOf(ENCRYPT_KEY.getBytes(), 8);
            DESKeySpec desKeySpec = new DESKeySpec(key);

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(mode, secretKey, new SecureRandom());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String p = encrypt("1qaz2wsx");
        System.out.println(p);
        System.out.println(decrypt(p));
    }
}
