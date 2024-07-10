package com.Y_LAB.homework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

@EnableWebMvc
@Configuration
@ComponentScan("com.Y_LAB.homework")
@EnableAspectJAutoProxy
public class AppConfig implements WebMvcConfigurer {

    /**
     * Контекст приложения Spring.
     */
    private final ApplicationContext applicationContext;

    /**
     * Создает новый экземпляр {@link AppConfig}.
     *
     * @param applicationContext контекст приложения
     */
    @Autowired
    public AppConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Настраивает конфигуратор для подстановки значений свойств.
     *
     * @return конфигуратор для подстановки значений свойств
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(loadProperties());
        return configurer;
    }

    /**
     * Загружает свойства из файла application.yml.
     *
     * @return загруженные свойства
     */
    private static Properties loadProperties() {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource("application.yml"));
        return yamlPropertiesFactoryBean.getObject();
    }

    /**
     * Добавляет перехватчик запросов для авторизации пользователей.
     *
     * @param registry реестр перехватчиков
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AuthorizationConfig authorizationInterceptor = applicationContext.getBean(AuthorizationConfig.class);
        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
    }

}
