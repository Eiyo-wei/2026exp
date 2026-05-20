package com.example.consumer.loadbalancer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class RandomLoadBalancerConfig {
    @Bean
    @ConditionalOnProperty(name = "lab2.loadbalancer.strategy", havingValue = "random")
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            ObjectProvider<ServiceInstanceListSupplier> suppliers) {
        String serviceId = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(suppliers, serviceId);
    }

    @Bean
    @ConditionalOnProperty(name = "lab2.loadbalancer.strategy", havingValue = "round-robin", matchIfMissing = true)
    ReactorLoadBalancer<ServiceInstance> roundRobinLoadBalancer(
            Environment environment,
            ObjectProvider<ServiceInstanceListSupplier> suppliers) {
        String serviceId = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RoundRobinLoadBalancer(suppliers, serviceId);
    }
}
