package com.example.provider.controller;

import com.example.provider.model.Book;
import com.example.provider.model.ProviderResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(3);

    @Value("${server.port}")
    private int port;

    public BookController() {
        books.put(1L, new Book(1L, "Spring Cloud 实战", "Provider-" + System.currentTimeMillis()));
        books.put(2L, new Book(2L, "微服务架构", "haha"));
    }

    @GetMapping
    public ProviderResponse<List<Book>> list() {
        return response(new ArrayList<>(books.values()));
    }

    @GetMapping("/instance")
    public ProviderResponse<String> instance(HttpServletRequest request) {
        return response("Handled by provider instance on port " + port + ", uri=" + request.getRequestURI());
    }

    @PostMapping
    public ProviderResponse<Book> create(@RequestBody Book request) {
        long id = nextId.getAndIncrement();
        Book created = new Book(id, request.title(), request.author());
        books.put(id, created);
        return response(created);
    }

    @PutMapping("/{id}")
    public ProviderResponse<Book> update(@PathVariable Long id, @RequestBody Book request) {
        Book updated = new Book(id, request.title(), request.author());
        books.put(id, updated);
        return response(updated);
    }

    @DeleteMapping("/{id}")
    public ProviderResponse<String> delete(@PathVariable Long id) {
        Book removed = books.remove(id);
        String message = removed == null ? "book " + id + " not found" : "deleted book " + id;
        return response(message);
    }

    private <T> ProviderResponse<T> response(T data) {
        return new ProviderResponse<>("service-provider", port, data);
    }
}
