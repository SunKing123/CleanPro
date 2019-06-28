package com.installment.mall.utils.encypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5EncryptUtil {
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 字符串MD5加密
	 * 
	 * @param text
	 *            明文字符串
	 * @return 加密后字符串
	 */
	public static String md5Encrypt(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(text.getBytes(StandardCharsets.UTF_8));
			byte[] encrybytes = digest.digest();
			String md5Str = new String(encodeHex(encrybytes));
			return md5Str;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return null;
	}

	public static void main(String args[]) {
		System.out.println(md5Encrypt("agentId=103"));
	}

	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param hex
	 *            String 十六进制
	 * @return String 转换后的字符串
	 */
	public static String hex2bin(String hex) {
		String digital = "0123456789ABCDEF";
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return new String(bytes);
	}
}
