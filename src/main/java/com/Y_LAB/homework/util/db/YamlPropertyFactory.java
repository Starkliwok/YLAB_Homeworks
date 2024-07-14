package com.Y_LAB.homework.util.db;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Properties;

public class YamlPropertyFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) {
        Properties propertiesFromYaml = loadYamlIntoProperties(resource.getResource());
        return new PropertiesPropertySource(Objects.requireNonNull(resource.getResource().getFilename()), propertiesFromYaml);
    }

    private Properties loadYamlIntoProperties(Resource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}