package com.xiaoniu.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SignatureUtils {

    /**
     * 说明：根据包名获取已安装应用的签名
     */
    public static HashSet<String> getSignature(Context context, String packageName) {
        HashSet<String> result = new HashSet<String>();
        if (context == null || TextUtils.isEmpty(packageName)) {
            return result;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo != null) {
                for (int i = 0; i < packageInfo.signatures.length; i++) {
                    String str = parseSignature(packageInfo.signatures[i].toByteArray());
                    if (!TextUtils.isEmpty(str)) {
                        result.add(str);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 说明：根据apk文件获取apk签名
     */
    public static Set<String> getSignaturesFromApk(File file) {
        Set<String> signatures = new HashSet<String>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    Signature s = new Signature(c.getEncoded());
                    signatures.add(parseSignature(s.toByteArray()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return signatures;
    }

    public static String parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(cert.getEncoded());
            return bytesToHexString(md5.digest());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toLowerCase();
    }

    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        if (jarFile == null || readBuffer == null) {
            return null;
        }
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
