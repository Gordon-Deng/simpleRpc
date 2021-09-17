package com.gordon.rpc.config;

import com.gordon.rpc.invocation.SRpcConsumerFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = {"s.rpc.enabled"},
    matchIfMissing = true
)
public class SRpcConsumerAutoConfiguration {

    public SRpcConsumerAutoConfiguration() {

    }

    @Bean
    public static BeanFactoryPostProcessor orcRpcConsumerPostProcessor() {
        return new SRpcConsumerFactoryPostProcessor();
    }

}
