package com.example.provider.model;

public record ProviderResponse<T>(String service, int port, T data) {
}
