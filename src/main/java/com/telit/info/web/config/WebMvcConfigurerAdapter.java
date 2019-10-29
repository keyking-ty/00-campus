package com.telit.info.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfigurerAdapter implements WebMvcConfigurer  {
   /**
      * 以前要访问一个页面需要先创建个Controller控制类，在写方法跳转到页面
      * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/toLogin就跳转到login.html页面了
   */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
        registry.addViewController("/tologin").setViewName("login");
        registry.addViewController("/welcome").setViewName("welcome");
        registry.addViewController("/errordeal").setViewName("error/errordealogin");
        registry.addViewController("/unauthorized").setViewName("error/unauthorized");
        registry.addViewController("/main").setViewName("main");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

