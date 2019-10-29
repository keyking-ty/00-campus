package com.telit.info.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
//import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;

@Configuration
@EnableSwagger2
public class Swagger2Configuration {
	
	@Bean
    public Docket buildDocket(){
		return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInf())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.telit.info.web.controller.app"))
                .paths(PathSelectors.any())
                .build();
	}
	
	private ApiInfo buildApiInf(){
        return new ApiInfoBuilder()
                    .title("00校园接口")
                    .description("springboot swagger2")
                    .build();
    }
}
