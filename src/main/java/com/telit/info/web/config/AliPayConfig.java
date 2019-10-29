package com.telit.info.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class AliPayConfig {
	@Value("${ali.appId}")
	private String aliAppId;
	@Value("${ali.payPrivateKey}")
	private String aliPayPrivateKey;
	@Value("${ali.payPublicKey}")
	private String aliPayPublicKey;
	@Value("${ali.bckMoneyUrl}")
	private String aliBckMoneyUrl;
}
