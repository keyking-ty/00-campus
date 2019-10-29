package com.telit.info.util;

import java.security.MessageDigest;

public class MD5 {

	public final static String getMD5(String s) {
		return getMD5(CommonUtil.DEFAULT_CHARSET,s);
	}
	
	public final static String getMD5(String charset,String s) {
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(s.getBytes(charset));
			byte[] md = mdTemp.digest();
		    StringBuffer buf = new StringBuffer("");  
		    for (int i = 0 ; i < md.length; i++) {  
		        int v  = md[i];  
		        if (v < 0) {
		        	v += 256;
		        }
		        if (v < 16) {
		        	buf.append("0");
		        }
		        buf.append(Integer.toHexString(v));
		    }  
			return buf.toString();
		} catch (Exception e) {
			return null;
		}
	}
}