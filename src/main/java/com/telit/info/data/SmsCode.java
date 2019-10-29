package com.telit.info.data;

import com.telit.info.util.TimeUtils;

public class SmsCode {
	String code;
	long time;
	long last;
	
	public SmsCode(String code,long last){
		this.code = code;
		this.last = last;
		time = TimeUtils.nowLong();
	}
	
	public String check(String code) {
		long now = TimeUtils.nowLong();
		if (now > time + last * TimeUtils.MINUTE) {
			return "验证码已过期";
		}
		if (this.code.equals(code)) {
			return null;
		}
		return "验证码错误";
	}
}
