package com.gordon.rpc.invocation;

import com.gordon.rpc.annotation.SRpcConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j public class SRpcConsumerFactoryPostProcessor
        implements BeanClassLoaderAware, EnvironmentAware, BeanFactoryPostProcessor, ApplicationContextAware {

    private ClassLoader classLoader;
    private ConfigurableEnvironment environment;
    private ApplicationContext context;
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap();

    public SRpcConsumerFactoryPostProcessor() {
    }

    @Override public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        postProcessRpcConsumerBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
    }

    private void postProcessRpcConsumerBeanFactory(ConfigurableListableBeanFactory beanFactory,
            BeanDefinitionRegistry beanDefinitionRegistry) {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        int len = beanDefinitionNames.length;
        for (int i = 0; i < len; i++) {
            String beanDefinitionName = beanDefinitionNames[i];
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        parseField(field);
                    }
                });
            }

        }

        Iterator<Map.Entry<String, BeanDefinition>> it = beanDefinitions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BeanDefinition> entry = it.next();
            if (context.containsBean(entry.getKey())) {
                throw new IllegalArgumentException("Spring context already has a bean named " + entry.getKey());
            }
            beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), entry.getValue());
            log.info("register OrcRpcConsumerBean definition: {}", entry.getKey());
        }

    }

    private void parseField(Field field) {
        // 获取所有RpcConsumer注解
        SRpcConsumer sRpcConsumer = field.getAnnotation(SRpcConsumer.class);
        if (sRpcConsumer != null) {
            // 使用field的类型和OrcRpcConsumer注解一起生成BeanDefinition
            SRpcConsumerBeanDefinitionBuilder beanDefinitionBuilder = new SRpcConsumerBeanDefinitionBuilder(
                    field.getType(), sRpcConsumer);
            BeanDefinition beanDefinition = beanDefinitionBuilder.build();
            beanDefinitions.put(field.getName(), beanDefinition);
        }
    }

    @Override public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
