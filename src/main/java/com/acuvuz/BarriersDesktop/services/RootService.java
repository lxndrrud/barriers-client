package com.acuvuz.BarriersDesktop.services;

public final class RootService {
    private final String host;
    private final int idBuilding;

    public String getHost() {
        return host;
    }

    public int getIdBuilding() {
        return idBuilding;
    }

    public RootService() {
        this.host = "http://localhost:8081";
        this.idBuilding = 1;
    }
}
