package com.zkcm.hydrobiologicasinica.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author ：lwl
 * @description： AES（Advanced Encryption Standard） 加密/解密
 * @date ：2019/10/23 14:25
 * @version:
 */
public class AESUtil {

    private static final String KEY = "zkcmkjyxgs201907";  // 密匙，必须16位
    private static final String OFFSET = "zkcmsuzhougongsi"; // 偏移量
    private static final String ENCODING = "UTF-8"; // 编码
    private static final String ALGORITHM = "AES"; //算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"; // 默认的加密算法，CBC模式

    /**
     * AES加密
     *
     * @param data
     * @return String
     * @author anson
     * @date 2019-8-24 18:43:07
     */
    public static String AESencrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes());//CBC模式偏移量IV
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
        return new Base64().encodeToString(encrypted);//加密后再使用BASE64做转码
    }

    /**
     * AES解密
     *
     * @param data
     * @return String
     * @author anson
     * @date 2019-8-24 18:46:07
     */
    public static String AESdecrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes()); //CBC模式偏移量IV
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] buffer = new Base64().decode(data);//先用base64解码
        byte[] encrypted = cipher.doFinal(buffer);
        return new String(encrypted, ENCODING);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("原始数据：" + 1);
        String s = AESencrypt(1 + "");
        System.out.println("加密后的数据：" + s);
        System.out.println("解密后的数据" + AESdecrypt(s));
    }
}
