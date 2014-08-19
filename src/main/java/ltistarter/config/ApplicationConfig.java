/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltistarter.config;

import ltistarter.model.ConfigEntity;
import ltistarter.repository.ConfigRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Allows for easy access to the application configuration,
 * merges config settings from spring and local application config
 */
@Component
public class ApplicationConfig implements ApplicationContextAware {

    final static Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    private volatile static ApplicationContext context;
    private volatile static ApplicationConfig config;

    @Autowired
    ConfigurableEnvironment env;

    @Resource(name = "defaultConversionService")
    @SuppressWarnings("SpringJavaAutowiringInspection")
    ConversionService conversionService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    ConfigRepository configRepository;

    @PostConstruct
    public void init() {
        log.info("INIT");
        //log.info("profiles active: " + ArrayUtils.toString(env.getActiveProfiles()));
        //log.info("profiles default: " + ArrayUtils.toString(env.getDefaultProfiles()));
        env.setActiveProfiles("dev", "testing");
        config = this;
        log.info("Config INIT: profiles active: " + ArrayUtils.toString(env.getActiveProfiles()));
    }

    @PreDestroy
    public void shutdown() {
        context = null;
        config = null;
        log.info("DESTROY");
    }

    // DELEGATED from the spring Environment (easier config access)

    public ConfigurableEnvironment getEnvironment() {
        return env;
    }

    /**
     * Return whether the given property key is available for resolution, i.e.,
     * the value for the given key is not {@code null}.
     */
    public boolean containsProperty(String key) {
        assert key != null;
        boolean contains = env.containsProperty(key);
        if (!contains) {
            ConfigEntity ce = configRepository.findByName(key);
            contains = (ce != null);
        }
        return contains;
    }

    /**
     * Return the property value associated with the given key, or
     * {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param defaultValue the default value to return if no value is found
     */
    public String getProperty(String key, String defaultValue) {
        return getProperty(key, String.class, defaultValue);
    }

    /**
     * Return the property value associated with the given key, or
     * {@code defaultValue} if the key cannot be resolved.
     *
     * @param key          the property name to resolve
     * @param targetType   the expected type of the property value
     * @param defaultValue the default value to return if no value is found
     */
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        assert key != null;
        assert targetType != null;
        T property = env.getProperty(key, targetType, defaultValue);
        // check for database override
        ConfigEntity ce = configRepository.findByName(key);
        if (ce != null) {
            try {
                property = conversionService.convert(ce.getValue(), targetType);
            } catch (Exception e) {
                property = defaultValue;
                log.warn("Failed to convert config (" + ce.getValue() + ") into a (" + targetType + "), using default (" + defaultValue + "): " + e);
            }
        }
        return property;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * @return the current service instance the spring application context (only populated after init)
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * @return the current service instance of the config object (only populated after init)
     */
    public static ApplicationConfig getInstance() {
        return config;
    }

}
