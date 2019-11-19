package com.comm.jksdk.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * @Created with IDEA
 * @author:DaiHang
 * @Date:2019/10/11
 * @Time:13:47
 */
public final class DesUtils {
    private static final String DES = "DES";
    private static final String KEY = "GEEK-ADS";

    private DesUtils() {}

    /**
     * 加密
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
        return cipher.doFinal(src);
    }

    /**
     * 解密
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        return cipher.doFinal(src);
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String temp = "";
        for (int n = 0; n < b.length; n++) {
            temp = (Integer.toHexString(b[n] & 0XFF));
            if (temp.length() == 1)
                hs = hs + "0" + temp;
            else
                hs = hs + temp;
        }
        return hs.toUpperCase();

    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("length not even");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    private static String decode(String src, String key) {
        String decryptStr = "";
        try {
            byte[] decrypt = decrypt(hex2byte(src.getBytes()), key.getBytes());
            decryptStr = new String(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptStr;
    }

    private static String encode(String src, String key){
        byte[] bytes = null;
        String encryptStr = "";
        try {
            bytes = encrypt(src.getBytes(), key.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (bytes != null)
            encryptStr = byte2hex(bytes);
        return encryptStr;
    }

    /**
     * 解密
     */
    public static String decode(String src) {
        return decode(src, KEY);
    }

    /**
     * 加密
     */
    public static String encode(String src) {
        return encode(src, KEY);
    }


//    public static void main(String[] args) {
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("bid",10);
////        jsonObject.put("marketName","jinritoutiao_1");
////        jsonObject.put("osSystem",1);
////        jsonObject.put("productName",13);
////        jsonObject.put("sdkVersion",1);
////        jsonObject.put("ts","1570600114881");
////        jsonObject.put("userActive","1570600114525");
////        jsonObject.put("versionCode",1);
////        JSONArray array = new JSONArray();
////        array.add(JSON.parse("{\"adPosition\": \"test_ad_code\",\"adVersion\": \"5\",\"productId\": 1}"));
////        jsonObject.put("positionInfos",array);
////        String encodeSS = encode(JSON.toJSONString(jsonObject));
////        System.out.println(encodeSS);
//
//        String decodeSS = decode("739D1E4030957CB46F5A7B85EAF4617CE07378016E9DEB442F2F1C14514BCDF74A8A9EF1268FA9321109A0C1D311E13194301FBCC3979A22E25156DF196A4E4C340E08DF873454A8F8E878EC9A95C8033F240B5070DF11C76A84470C65591097501082ACFF41A68C525C69DC9927F85DBE2C3A398535B1ABDB24703FAE146C822EFD316EC22BE1AC8DFBF953FC2B3980983CC16B18F9E07BCC9004B939B4E37478731A913058FC1F7F77E852B64CA8CE8ED0F58DB9AEFD423588B976C8A6508112D33F284E5C1FBE6DACEFB590F67720C6B9438E0880FE6E2336395A332B77D65E2465043243FC8C169F7A48DB7B6CD9169AE4AEDA35643D98552F669B9559060071897EF084F05026A1299E350B79759FD8F206B90E64FB109BF4FC31AD7DA4F32687C0C14DB37B708692B43A51D5EDC33275F566CC8D2FD05095D2D36DD62D23FBE48E2843FBDC502BEF726D027B65B91179099527D8BB2336395A332B77D65E2465043243FC8C169F7A48DB7B6CD9169AE4AEDA35643D992B9FE799490A4F0071897EF084F05026A1299E350B797528AFB2E24C490F301837DA3184043DDD4AA09518A638B922EFFA139E0863B049A7C60029152213341F2998B8F9CB11B464FDDFEE2F3CF98C");
//        System.out.println(decodeSS);
//    }
}