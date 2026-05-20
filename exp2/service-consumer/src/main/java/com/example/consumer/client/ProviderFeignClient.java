package com.example.consumer.client;

import com.example.consumer.model.Book;
import com.example.consumer.model.ProviderResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-provider", path = "/api/books")
public interface ProviderFeignClient {
    @GetMapping
    ProviderResponse<List<Book>> list();

    @GetMapping("/instance")
    ProviderResponse<String> instance();

    @PostMapping
    ProviderResponse<Book> create(@RequestBody Book book);

    @PutMapping("/{id}")
    ProviderResponse<Book> update(@PathVariable("id") Long id, @RequestBody Book book);

    @DeleteMapping("/{id}")
    ProviderResponse<String> delete(@PathVariable("id") Long id);
}
