package com.fanpage.ten_cm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /images/.. 로 시작하는 주소로 요청이 오면
        registry.addResourceHandler("/images/**")
                // 실제 컴퓨터의 이 폴더에서 파일을 읽어온다! (반드시 끝에 / 를 붙여주세요)
                .addResourceLocations("file:///D:/image/");
    }
}