package com.yize.chatserver.config;

import cn.hutool.core.io.resource.ClassPathResource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.InputStreamResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class LoadYaml implements ApplicationContextInitializer {

    public LoadYaml(ConfigurableApplicationContext configurableApplicationContext){
        initialize(configurableApplicationContext);
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            //PropertySource<?> propertySource = loader.load("externalConfiguration", new InputStreamResource(Files.newInputStream(Paths.get("/home/conf.yml")))).get(0);
            PropertySource<?> propertySource = loader.load("externalConfiguration", new InputStreamResource(Files.newInputStream(Paths.get(new ClassPathResource("conf.yml").getAbsolutePath())))).get(0);
            configurableApplicationContext.getEnvironment().getPropertySources().addFirst(propertySource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
