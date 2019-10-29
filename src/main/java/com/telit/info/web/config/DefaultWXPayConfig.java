package com.telit.info.web.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.wxpay.sdk.IWXPayDomain;
import com.wxpay.sdk.WXPayConfig;

@Component
public class DefaultWXPayConfig extends WXPayConfig {
	@Resource
	private WechatPayConfig payConfig;
	
	IWXPayDomain domain;
	
	@Override
	public String getAppID() {
		return payConfig.getWxAppId();
	}

	@Override
	public String getMchID() {
		return payConfig.getWxMchId();
	}

	@Override
	public String getKey() {
		return payConfig.getWxAppkey();
	}

	@Override
	public InputStream getCertStream() {
		try {
			return new FileInputStream("sadadadad");
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public IWXPayDomain getWXPayDomain() {
		if (domain == null) {
			domain = new IWXPayDomain() {
				@Override
				public void report(String domain, long elapsedTimeMillis, Exception ex) {
					
				}
				@Override
				public DomainInfo getDomain(WXPayConfig config) {
					DomainInfo info = new DomainInfo(payConfig.getWxDomain(),true);
					return info;
				}
			};
		}
		return domain;
	}
}
