package com.zkcm.contract.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @description： AES（Advanced Encryption Standard） 加密/解密
 * @author     ：lwl
 * @date       ：2019/10/23 14:25
 * @version:
 */
public class AESUtil {

    private static final String KEY = "zkcmkjyxgs202107";  // 密匙，必须16位
    private static final String OFFSET = "zkcmsuzhougongsi"; // 偏移量
    private static final String ENCODING = "UTF-8"; // 编码
    private static final String ALGORITHM = "AES"; //算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"; // 默认的加密算法，CBC模式
    private static final String CIPHER_ALGORITHM2 = "AES/CBC/NoPadding"; // 默认的加密算法，CBC模式

    /**
     *  AES加密
     * @param data
     * @return String
     * @author anson
     * @date   2019-8-24 18:43:07
     */
    public static String AESencrypt(String data) throws Exception
    {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes());//CBC模式偏移量IV
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
        return new Base64().encodeToString(encrypted);//加密后再使用BASE64做转码
    }

    /**
     * AES解密
     * @param data
     * @return String
     * @author anson
     * @date   2019-8-24 18:46:07
     */
    public static String AESdecrypt(String data) throws Exception
    {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes()); //CBC模式偏移量IV
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] buffer = new Base64().decode(data);//先用base64解码
        byte[] encrypted = cipher.doFinal(buffer);
        return new String(encrypted, ENCODING);
    }
    /**
     * 加密方法
     *
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            // 参数：算法/模式/补码方式
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM2);
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new Base64().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

    }

    /**
     * 解密方法
     *
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public static String desEncrypt(String data, String key, String iv) {
        try {
            byte[] encrypted1 = new Base64().decode(filter(data));

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

    }

    public static String encrypt(String data) {
        return encrypt(data, KEY, OFFSET);
    }

    public static String desEncrypt(String data) {
        return desEncrypt(data, KEY, OFFSET);
    }

    public static String filter(String value) {
        value = value.replace("%23", "#")
                .replace("%25", "%")
                .replace("%26", "&")
                .replace("%2B", "+")
                .replace("%2F", "//")
                .replace("%3F", "?");
        return value;
    }
    public static void main(String[] args) throws Exception {

        System.out.println("原始数据：" + 1);
        String s = AESencrypt( "1234qwer");
        System.out.println("加密后的数据："+s);
        System.out.println("解密后的数据"+ AESdecrypt(s));

        System.out.println(encrypt("1234qwer"));
        System.out.println(desEncrypt("B/IcmRndPw0WrV47OChAdw=="));
    }
}
