package com.example.consumer.controller;

import com.example.consumer.client.ProviderFeignClient;
import com.example.consumer.model.Book;
import com.example.consumer.model.ProviderResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    private static final String PROVIDER_URL = "http://service-provider/api/books";

    private final RestTemplate restTemplate;
    private final ProviderFeignClient feignClient;

    public ConsumerController(RestTemplate restTemplate, ProviderFeignClient feignClient) {
        this.restTemplate = restTemplate;
        this.feignClient = feignClient;
    }

    @GetMapping("/rest/books")
    public ProviderResponse<List<Book>> restList() {
        return restTemplate.exchange(PROVIDER_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<ProviderResponse<List<Book>>>() {
                }).getBody();
    }

    @PostMapping("/rest/books")
    public ProviderResponse<Book> restCreate(@RequestBody Book book) {
        return restTemplate.exchange(PROVIDER_URL, HttpMethod.POST, new HttpEntity<>(book),
                new ParameterizedTypeReference<ProviderResponse<Book>>() {
                }).getBody();
    }

    @PutMapping("/rest/books/{id}")
    public ProviderResponse<Book> restUpdate(@PathVariable Long id, @RequestBody Book book) {
        return restTemplate.exchange(PROVIDER_URL + "/{id}", HttpMethod.PUT, new HttpEntity<>(book),
                new ParameterizedTypeReference<ProviderResponse<Book>>() {
                }, id).getBody();
    }

    @DeleteMapping("/rest/books/{id}")
    public ProviderResponse<String> restDelete(@PathVariable Long id) {
        return restTemplate.exchange(PROVIDER_URL + "/{id}", HttpMethod.DELETE, null,
                new ParameterizedTypeReference<ProviderResponse<String>>() {
                }, id).getBody();
    }

    @GetMapping("/feign/books")
    public ProviderResponse<List<Book>> feignList() {
        return feignClient.list();
    }

    @PostMapping("/feign/books")
    public ProviderResponse<Book> feignCreate(@RequestBody Book book) {
        return feignClient.create(book);
    }

    @PutMapping("/feign/books/{id}")
    public ProviderResponse<Book> feignUpdate(@PathVariable Long id, @RequestBody Book book) {
        return feignClient.update(id, book);
    }

    @DeleteMapping("/feign/books/{id}")
    public ProviderResponse<String> feignDelete(@PathVariable Long id) {
        return feignClient.delete(id);
    }

    @GetMapping("/loadbalancer/rest")
    public Map<String, Object> restLoadBalancer(@RequestParam(defaultValue = "10") int times) {
        List<Integer> ports = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            ports.add(restTemplate.exchange(PROVIDER_URL + "/instance", HttpMethod.GET, null,
                    new ParameterizedTypeReference<ProviderResponse<String>>() {
                    }).getBody().port());
        }
        return Map.of("strategy", "round-robin by default, random when lab2.loadbalancer.strategy=random", "ports", ports);
    }

    @GetMapping("/loadbalancer/feign")
    public Map<String, Object> feignLoadBalancer(@RequestParam(defaultValue = "10") int times) {
        List<Integer> ports = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            ports.add(feignClient.instance().port());
        }
        return Map.of("strategy", "same client-side LoadBalancer used by OpenFeign", "ports", ports);
    }
}
