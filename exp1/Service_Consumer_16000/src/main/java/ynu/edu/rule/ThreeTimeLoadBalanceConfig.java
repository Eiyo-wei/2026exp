package ynu.edu.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ynu.edu.rule.detail.ThreeTimeLoadBalancer;

public class ThreeTimeLoadBalanceConfig {
    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment env, LoadBalancerClientFactory lbf) {
        String name = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new ThreeTimeLoadBalancer(lbf.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
