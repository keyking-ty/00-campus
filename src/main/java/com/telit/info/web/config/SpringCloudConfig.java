package com.telit.info.web.config;

import java.nio.charset.StandardCharsets;

//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

//@EnableDiscoveryClient//开启发现服务功能
//@Configuration
public class SpringCloudConfig {

	/**
	 * 调用服务模版
	 * 主要是定义一个bean RestTemplate对象； springcloud消费者，服务提供者之间的交互是http rest方式，比dubbo rpc方式更加灵活方便点；
	 * @return
	 */
	//@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return restTemplate;
	}
}
