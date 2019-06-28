package com.installment.mall.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DES3Utils {

    // 定义加密算法，DESede即3DES
    private static final String Algorithm = "DESede";

    //    static{
//        System.loadLibrary("deskey");
//    }

    /**
     * @return 秘钥
     */
    public static String getKey(){
        //return "pFUKJ0n0yJyfIaDfxEVIbyme";
        return "Fwxq1v0Fucxd1FathVKZYsrK";
    }
    private DES3Utils() {
    }
    
    // MD5
    public static String MD5Encoding(String source) throws NoSuchAlgorithmException {
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        byte[] input = source.getBytes();
        mdInst.update(input);
        byte[] output = mdInst.digest();

        int i = 0;

        StringBuilder buf = new StringBuilder();

        for (int offset = 0; offset < output.length; offset++) {
            i = output[offset];

            if (i < 0) {
                i += 256;
            }

            if (i < 16) {
                buf.append('0');
            }

            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }
    /**
     * 加密方法
     *
     * @param src
     *            源数据的字节数组
     * @return
     */
    public static String encryptMode(String src) {
        try {
            byte [] targetSrc = src.getBytes() ;
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(build3DesKey(getKey()),
                    Algorithm);
            // 实例化Cipher
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return Base64Util.encode(cipher.doFinal(targetSrc));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密函数
     *
     * @param src
     *            密文的字节数组
     * @return
     */
    public static String decryptMode(String src ,String key) {
        try {
            byte [] targetSrc = Base64Util.decode(src) ;
            SecretKey deskey = new SecretKeySpec(build3DesKey(key),
                    Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(targetSrc),Charset.forName("UTF-8"));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 解密函数
     * @param src
     *            密文的字节数组
     * @return
     */
    public static String decryptMode(String src) {
        try {
            byte [] targetSrc = Base64Util.decode(src) ;
            SecretKey deskey = new SecretKeySpec(build3DesKey(getKey()),
                    Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(targetSrc),Charset.forName("UTF-8"));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
     /**
     * 生成MD5数据签名
     * @param jsonStr
     *             数据体的JSON格式的字符串,不能是JSONARRAY类型字符串
     * @return
     * @throws NoSuchAlgorithmException
     */
    @SuppressWarnings("unchecked")
    public static String getSign(String jsonStr) {
        // 判断需要生成的签名字符串是否为空
        if (jsonStr != null && jsonStr.trim().length() > 0) {
            // 将JSON格式字符串转换成TREEMAP进行属性KEY值升序排列
            TreeMap<String, Object> jsonMap = JSONObject.parseObject(jsonStr, TreeMap.class);
            // 瘵签名数据进午签明前格式接装key=value且用&连接
            if (jsonMap != null && jsonMap.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String key : jsonMap.keySet()) {
                    sb.append(key).append("=").append(jsonMap.get(key)).append("&");
                }
                String md5Sign = sb.substring(0, sb.length() - 1);
                // 进行MD5签名
                try {
                    return MD5Encoding(md5Sign);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("数据签名发生异常 : " + e.getMessage());
                }
            }
        }
        return null;
    }
    
    public static String buildEncryptRequestParams(String jsonStr){

        org.json.JSONObject jsonObject;
        if(TextUtils.isEmpty(jsonStr)){
            jsonObject = new org.json.JSONObject();
        }else{
            jsonObject = JSONUtils.build(jsonStr);
        }
//        if (!TextUtils.isEmpty(userToken)){
//            JSONUtils.put(jsonObject , "loginToken" , userToken);
//        }
        JSONUtils.put(jsonObject , "timestamp" , new Date().getTime());
        String sign = getSign(jsonObject.toString());
        org.json.JSONObject result = new org.json.JSONObject();
        JSONUtils.put(result , "data" , jsonObject);
        JSONUtils.put(result , "sign" , sign);
        return encryptMode(result.toString());
        
    }
     /**
     * 对数据进行验签
     *             原签名字符串
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static boolean checkSign(String JsonData, String sign) throws Exception {
        if (JsonData == null) {
            throw new RuntimeException("签名内容不能为空");
        }
        if (sign == null) {
            throw new RuntimeException("原签名内容不能为空");
        }
        TreeMap<String, Object> testMap = JSONObject.parseObject(JsonData, TreeMap.class);
        String checkData = testMap.get("data").toString();
        TreeMap<String, Object> tescheckDataMap = JSONObject.parseObject(checkData, TreeMap.class);
        // 对签名的数据体做拼接
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : tescheckDataMap.keySet()) {
            stringBuilder.append(key).append("=").append(tescheckDataMap.get(key)).append("&");
        }
        String checkMd5Sign = stringBuilder.substring(0, stringBuilder.length() - 1);
        //进行MD5加密，并与原签名进行比对
        String signStr = MD5Encoding(checkMd5Sign);
        return TextUtils.equals(signStr, sign);
    }
    /**
     * 根据字符串生成密钥24位的字节数组
     *
     * @param keyStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr)
            throws UnsupportedEncodingException {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes(StandardCharsets.UTF_8);

        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
}
