package com.telit.info.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.telit.info.util.CommonUtil;
import com.telit.info.util.HttpUtil;
import com.telit.info.util.JsonUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
public class SmsSender {
	
	@Value("${sms.urlPath}")
	private String urlPath;//api接口地址
	@Value("${sms.regAccount}")
	private String regAccount;//注册账号
	@Value("${sms.regPwd}")
	private String regPwd;//注册密码
	@Value("${sms.marketAccount}")
	private String marketAccount;//营销账号
	@Value("${sms.marketPwd}")
	private String marketPwd;//营销密码
	
	public boolean sendMsgToPhone(String phone,String msg) {
		SmsSendRequest request = new SmsSendRequest(regAccount,regPwd,msg,phone);
		String data = JsonUtil.encodeToStr(request);
		log.info(data);
		String response = HttpUtil.doJsonPost(urlPath,data);
		if (CommonUtil.isNotEmpty(response)) {
			log.info(response);
			SmsSendResponse smsSingleResponse = JsonUtil.decodeToObj(response,SmsSendResponse.class);
			if ("0".equals(smsSingleResponse.getCode())) {
				return true;
			}
		}
		//{"account":"M250501_M1512555","msg":"测试电短信","password":"qSQVJD2LwO6d75","phone":"17521636635","report":"true"}
		return false;
	}
}
