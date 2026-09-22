package com.miniboot.autoconfigure;

import com.miniboot.annotation.ConfigurationProperties;
import com.miniboot.bind.ConfigurationPropertiesBinder;
import com.miniboot.env.Environment;
import com.miniioccontainer.aop.MiniAopInterceptor;
import com.miniioccontainer.context.MiniApplicationContext;

import java.util.Map;

/**
 * Binds {@link ConfigurationProperties} onto IoC beans after the context is refreshed.
 */
public class ConfigurationPropertiesAutoConfiguration implements AutoConfiguration {

    @Override
    public void configure(AutoConfigurationContext context) {
        Environment environment = context.getEnvironment();
        if (environment == null) {
            System.out.println("[MiniBoot] Skip ConfigurationProperties binding (no Environment)");
            return;
        }
        MiniApplicationContext applicationContext = context.getApplicationContext();
        Map<String, Object> beans = applicationContext.getBeansOfType(Object.class);
        int bound = 0;
        for (Object bean : beans.values()) {
            Object target = MiniAopInterceptor.unwrap(bean);
            if (!target.getClass().isAnnotationPresent(ConfigurationProperties.class)) {
                continue;
            }
            ConfigurationPropertiesBinder.bind(environment, target);
            bound++;
            System.out.println("[MiniBoot] Bound @ConfigurationProperties: " + target);
        }
        System.out.println("[MiniBoot] AutoConfig applied: ConfigurationProperties (" + bound + " bean(s))");
    }
}
