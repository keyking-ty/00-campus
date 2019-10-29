package com.telit.info.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class WechatPayConfig {
	@Value("${wx.appId}")
	private String wxAppId;
	@Value("${wx.appSecret}")
	private String wxAppSecret;
	@Value("${wx.mchId}")
	private String wxMchId;
	@Value("${wx.appkey}")
	private String wxAppkey;
	@Value("${wx.domain}")
	private String wxDomain;
}
