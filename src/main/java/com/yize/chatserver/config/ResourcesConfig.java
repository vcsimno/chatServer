/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

import static com.yize.chatserver.config.DfsConfig.RESOURCE_PREFIX;


@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    @Resource
    private DfsConfig dfsConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* 本地文件上传路径 */
        registry.addResourceHandler(RESOURCE_PREFIX + "/**")
            .addResourceLocations("file:" + dfsConfig.getPath() + "/");
    }
}