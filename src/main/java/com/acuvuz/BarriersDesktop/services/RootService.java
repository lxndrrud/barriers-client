package com.acuvuz.BarriersDesktop.services;

public final class RootService {
    private final String host;

    public String getHost() {
        return host;
    }

    public RootService() {
        this.host = "http://localhost:8081";
    }
}
