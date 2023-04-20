package com.yize.chatserver.config;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    private static final Logger LOGGER = LogManager.getLogger(WebAppConfigurer.class);
    @Value("${file.upload-path}")
    private String filePathPrefix;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  /*允许跨域访问的路劲*/
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

    /**
     * 增加图片转换器
     * @param converters HttpMessageConverter
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>>  converters) {
        try {
            converters.add(new BufferedImageHttpMessageConverter());
        }catch (Exception e){
            LOGGER.error(LOGGER, e);
        }

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            registry.addResourceHandler("/files/**").addResourceLocations("file://"+filePathPrefix+"//**//");
        }catch (Exception e){
            LOGGER.error(LOGGER, e);
        }

    }




}