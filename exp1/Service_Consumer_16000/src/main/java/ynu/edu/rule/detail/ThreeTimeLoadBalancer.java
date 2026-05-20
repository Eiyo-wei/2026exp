package ynu.edu.rule.detail;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;

public class ThreeTimeLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private int instance_call_count = 0;//已经被调用的次数
    private int instance_index = 0; //当前服务实例下标
    private String service_id; //存储服务实例名称
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplier;

    public ThreeTimeLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplier, String serviceId) {
        this.serviceInstanceListSupplier = serviceInstanceListSupplier;
        this.service_id = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplier.getIfAvailable();
        return supplier.get().next().map(this::getInstanceResponse);
    }


    public Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        int size = instances.size();
        ServiceInstance instance = null;
        while (instance == null) {
            if (this.instance_call_count < 3) {
                instance = instances.get(this.instance_index);
                this.instance_call_count++;
            } else {
                this.instance_index++;
                this.instance_call_count = 0;
                if (this.instance_index >= size) {
                    this.instance_index = 0;
                }
            }
        }
        return new DefaultResponse(instance);

    }

}
