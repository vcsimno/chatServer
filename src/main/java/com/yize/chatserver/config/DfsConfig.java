/*
 * Copyright (c)  $toda.year yize.link
 * editor: yize
 * date:
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */

package com.yize.chatserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dfs")
public class DfsConfig
{
    /** 路径*/
    private String path;

    //生产环境建议用nginx绑定域名映射
    /** 域名*/
    private String domain;
    public static final String RESOURCE_PREFIX = "/profile";
}