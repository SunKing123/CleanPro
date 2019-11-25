package com.xiaoniu.cleanking.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GZipUtils {
    /**
     * 解压缩
     * @param compressed
     * @return
     * @throws IOException
     */
    public static String decompress(final byte[] compressed) throws IOException {
        final StringBuilder outStr = new StringBuilder();
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }
        } else {
            outStr.append(compressed);
        }
        return outStr.toString();
    }

    public static boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    /**
     * 解压数据
     * @param content
     * @return
     */
    public static String decompress(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        String decompress = "";
        byte[] decode = Base64.decode(content.getBytes(), Base64.DEFAULT);
        try {
            decompress = GZipUtils.decompress(decode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompress;
    }
}
