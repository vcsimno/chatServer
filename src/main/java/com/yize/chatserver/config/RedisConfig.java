/*
 * Copyright (c) 2022. yize.link
 * editor: yize
 * date:  2022/11/8
 *
 * @author yize<vcsimno@163.com>
 * 本开源由yize发布和开发，部分工具引用了其他优秀团队的开源工具包。
 */
package com.yize.chatserver.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.yize.chatserver.constants.RedisConstants;
import io.lettuce.core.ReadFrom;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.time.Duration;



@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.connect-timeout}")
    private String timeout;

    private RedisTemplate<String, String> red1;
    private RedisTemplate<String, String> red2;
    private RedisTemplate<String, String> red3;

    /**按照需求拉取redis*/
    public RedisTemplate<String, String> getResource(String index) {

        switch (index) {
            case RedisConstants.ONLINE: {
                return red1;
            }
            case RedisConstants.USERS: {
                return red3;
            }
            default:
                return red2;
        }
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        RedisSerializer<String> redisSerializer = new StringRedisSerializer(Charset.defaultCharset());
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //配置序列化（解决乱码的问题） 过期时间600秒
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * 建立Redis连接
     *
     * @param host     主机地址
     * @param port     端口
     * @param password 密码
     * @param timeout  超时时间
     * @return Redis会话
     */
    private RedisTemplate<String, String> build(String host, String port, String password, String timeout, int dbIndex) {
        //单机部署
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, Integer.parseInt(port));
        redisStandaloneConfiguration.setPassword(password);
        //集群部署
        /*
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(Arrays.asList(commonHost.split(";")));
        redisClusterConfiguration.setPassword(commonPassword);
         */
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(Long.parseLong(timeout)))
                .poolConfig(new GenericObjectPoolConfig())
                .build();
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);

        //如果要使pool参数生效,一定要关闭shareNativeConnection
        connectionFactory.setShareNativeConnection(false);
        connectionFactory.setDatabase(dbIndex);
        //必须初始化实例
        connectionFactory.afterPropertiesSet();

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisTemplate<String, String> commonRedisTemplate = new StringRedisTemplate();
        commonRedisTemplate.setConnectionFactory(connectionFactory);
        // key、hash的key 采用 String序列化方式 【可自行配置】
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        commonRedisTemplate.setKeySerializer(redisSerializer);

        commonRedisTemplate.setHashKeySerializer(redisSerializer);
        // value、hash的value 采用 String 序列化方式
        commonRedisTemplate.setValueSerializer(redisSerializer);
        commonRedisTemplate.setHashValueSerializer(redisSerializer);

        commonRedisTemplate.afterPropertiesSet();
        return commonRedisTemplate;
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer configurationBuilderCustomizer() {
        red1 = build(host, port, password, timeout, 0);
        red2 = build(host, port, password, timeout, 1);
        red3 = build(host, port, password, timeout, 2);
        return configBuilder -> configBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }

}