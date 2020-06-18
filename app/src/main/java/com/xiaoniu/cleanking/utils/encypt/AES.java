package com.xiaoniu.cleanking.utils.encypt;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static final String _KEY = "api.JHuan.com)!^";
    private static byte[] AESKeys = {0x41, 0x72, 0x65, 0x79, 0x6F, 0x75, 0x6D, 0x79, 0x53, 0x6E, 0x6F, 0x77, 0x6D, 0x61, 0x6E, 0x3F};

    /**
     * AES 加密
     * @param sSrc  要加密的字符
     * @return  加密后的字符
     */
    public static String encrypt(String sSrc){
        String encryptStr;
        try {
            byte[] key = _KEY.getBytes(StandardCharsets.UTF_8);
            byte[] iv = AESKeys;
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// "算法/模式/补码方式"
            IvParameterSpec _iv = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, _iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            encryptStr = new String(Base64.encode(encrypted,Base64.NO_WRAP), StandardCharsets.UTF_8);
        }catch (Exception e){
            encryptStr = "";
        }
        return encryptStr;
    }


    /**
     * AES 解密
     * @param sSrc  要解密的字符
     * @return  解密后的字符
     */
    public static String decrypt(String sSrc){
        String decryptStr;
        try {
            byte[] key = _KEY.getBytes(StandardCharsets.UTF_8);
            byte[] iv = AESKeys;
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec _iv = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, _iv);
            byte[] encrypted = Base64.decode(sSrc.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP);// 先用base64解码
            byte[] original = cipher.doFinal(encrypted);
            decryptStr = new String(original, StandardCharsets.UTF_8);
        }catch (Exception e){
            decryptStr = "";
        }
        return decryptStr;
    }

}
