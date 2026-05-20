package com.example.consumer.model;

public record ProviderResponse<T>(String service, int port, T data) {
}
