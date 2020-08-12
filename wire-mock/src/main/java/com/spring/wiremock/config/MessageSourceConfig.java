package com.spring.wiremock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MessageSourceConfig {
    private final Logger LOG = LoggerFactory.getLogger(MessageSourceConfig.class);

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(getPropertyList().stream().toArray(String[]::new));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    private List<String> getPropertyList() {
        List<String> fileList = new ArrayList<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:i18n/*.properties");
            Arrays.stream(resources).map(Resource::getFilename).map(file -> {
                StringBuilder fileNameWithFolder = new StringBuilder();
                fileNameWithFolder.append("i18n/");
                fileNameWithFolder.append(file.replace(".properties", ""));
                return fileNameWithFolder.toString();
            })
                    .forEach(fileList::add);
        } catch (IOException e) {
            LOG.error("Error in reading property files in resource folder");
        }
        return fileList;
    }
}