package com.example.consumer.loadbalancer;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

public class LowestPortFirstLoadBalancer implements ReactorLoadBalancer<ServiceInstance> {
    private final ObjectProvider<ServiceInstanceListSupplier> suppliers;
    private final String serviceId;

    public LowestPortFirstLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> suppliers, String serviceId) {
        this.suppliers = suppliers;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = suppliers.getIfAvailable();
        if (supplier == null) {
            return Mono.just(new EmptyResponse());
        }
        return supplier.get(request)
                .next()
                .map(this::chooseLowestPort);
    }

    private Response<ServiceInstance> chooseLowestPort(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return new EmptyResponse();
        }
        ServiceInstance selected = instances.stream()
                .min(Comparator.comparingInt(ServiceInstance::getPort))
                .orElseThrow();
        return new DefaultResponse(selected);
    }

    @Override
    public String toString() {
        return "LowestPortFirstLoadBalancer{serviceId='" + serviceId + "'}";
    }
}
