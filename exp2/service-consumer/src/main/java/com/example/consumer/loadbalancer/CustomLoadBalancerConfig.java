package com.example.consumer.loadbalancer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class CustomLoadBalancerConfig {
    @Bean
    ReactorLoadBalancer<ServiceInstance> lowestPortFirstLoadBalancer(
            Environment environment,
            ObjectProvider<ServiceInstanceListSupplier> suppliers) {
        String serviceId = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new LowestPortFirstLoadBalancer(suppliers, serviceId);
    }
}
