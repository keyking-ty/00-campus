package com.telit.info.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsUtil {
	private static String mtUrl = "http://esms.10690007.net/sms/mt";
	private static String spid = "101088";
	private static String sppassword = "q5kyxSky";
	private final static int charsetCode = 15;
	private static String charset = "GB2312";

	public static boolean sendSmsToOne(String phone, String content) {
		// 操作命令、SP编号、SP密码，必填参数
		String command = "MT_REQUEST";
		// sp服务代码，可选参数，默认为 00
		// String spsc = "00";
		// 源号码，可选参数
		// String sa = "12345";
		// 下行内容以及编码格式，必填参数
		String sm = encodeHexStr(charsetCode, content);// 下行内容进行Hex编码，此处dc设为15，即使用GBK编码格式
		// 组成url字符串
		String smsUrl = mtUrl + "?command=" + command + "&spid=" + spid + "&sppassword=" + sppassword + "&da=" + phone
				+ "&sm=" + sm + "&dc=" + charsetCode;
		String resStr = HttpUtil.doGet(smsUrl, charset);
		// 解析响应字符串
		Map<String, String> map = parseResStr(resStr);
		//System.out.println(map);
		return "000".equals(map.get("mterrcode"));
	}

	public static boolean sendSmsToMore(List<String> phones, String content) {
		if (phones == null || phones.size() == 0) {
			return false;
		}
		// 操作命令、SP编号、SP密码，必填参数
		String command = "MULTI_MT_REQUEST";
		// sp服务代码，可选参数，默认为 00
		// String spsc = "00";
		// 源号码，可选参数
		// String sa = "10657109053657";
		// 目标号码组，必填参数
		String das = phones.get(0);
		for (int i = 1; i < phones.size(); i++) {
			das += "," + phones.get(i);
		}
		// 下行内容以及编码格式，必填参数
		String sm = encodeHexStr(charsetCode, content);// 下行内容进行Hex编码，此处dc设为15，即使用GBK编码格式
		// 组成url字符串
		String smsUrl = mtUrl + "?command=" + command + "&spid=" + spid + "&sppassword=" + sppassword + "&das=" + das
				+ "&sm=" + sm + "&dc=" + charsetCode;
		// 发送http请求，并接收http响应
		String resStr = HttpUtil.doGet(smsUrl.toString(), charset);
		// 解析响应字符串
		Map<String, String> map = parseResStr(resStr);
		//System.out.println(map);
		return "000".equals(map.get("mterrcode"));
	}

	/**
	 * 将 短信下行 请求响应字符串解析到一个HashMap中
	 * 
	 * @param resStr
	 * @return
	 */
	public static Map<String, String> parseResStr(String resStr) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] ps = resStr.split("&");
			for (int i = 0; i < ps.length; i++) {
				int ix = ps[i].indexOf("=");
				if (ix != -1) {
					map.put(ps[i].substring(0, ix), ps[i].substring(ix + 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Hex编码字符组
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * 将普通字符串转换成Hex编码字符串
	 * 
	 * @param dataCoding 编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
	 * @param realStr    普通字符串
	 * @return Hex编码字符串
	 */
	public static String encodeHexStr(int dataCoding, String realStr) {
		String hexStr = null;
		if (realStr != null) {
			byte[] data = null;
			try {
				if (dataCoding == 15) {
					data = realStr.getBytes("GBK");
				} else if ((dataCoding & 0x0C) == 0x08) {
					data = realStr.getBytes("UnicodeBigUnmarked");
				} else {
					data = realStr.getBytes("ISO8859-1");
				}
			} catch (UnsupportedEncodingException e) {
				System.out.println(e.toString());
			}
			if (data != null) {
				int len = data.length;
				char[] out = new char[len << 1];
				// two characters form the hex value.
				for (int i = 0, j = 0; i < len; i++) {
					out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
					out[j++] = DIGITS[0x0F & data[i]];
				}
				hexStr = new String(out);
			}
		}
		return hexStr;
	}

	/**
	 * 将Hex编码字符串还原成普通字符串
	 * 
	 * @param dataCoding 反编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
	 * @param hexStr     Hex编码字符串
	 * @return 普通字符串
	 */
	public static String decodeHexStr(int dataCoding, String hexStr) {
		String realStr = null;
		if (hexStr != null) {
			char[] data = hexStr.toCharArray();
			int len = data.length;
			if ((len & 0x01) != 0) {
				throw new RuntimeException("Odd number of characters.");
			}
			byte[] out = new byte[len >> 1];
			for (int i = 0, j = 0; j < len; i++) {
				int f = Character.digit(data[j], 16) << 4;
				if (f == -1) {
					throw new RuntimeException("Illegal hexadecimal charcter " + data[j] + " at index " + j);
				}
				j++;
				f = f | Character.digit(data[j], 16);
				if (f == -1) {
					throw new RuntimeException("Illegal hexadecimal charcter " + data[j] + " at index " + j);
				}
				j++;
				out[i] = (byte) (f & 0xFF);
			}
			try {
				if (dataCoding == 15) {
					realStr = new String(out, "GBK");
				} else if ((dataCoding & 0x0C) == 0x08) {
					realStr = new String(out, "UnicodeBigUnmarked");
				} else {
					realStr = new String(out, "ISO8859-1");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return realStr;
	}
}
