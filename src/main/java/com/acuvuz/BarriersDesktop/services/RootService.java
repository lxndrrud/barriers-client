package com.acuvuz.BarriersDesktop.services;

import io.github.cdimascio.dotenv.Dotenv;

public final class RootService {
    private static RootService instance;
    public static RootService getInstance() {
        if (instance != null) return instance;
        instance = new RootService();
        return instance;
    }
    private final String host;
    private final int idBuilding;

    public String getHost() {
        return host;
    }

    public int getIdBuilding() {
        return idBuilding;
    }

    private RootService() {
        Dotenv dotenv = Dotenv.load();
        this.host = dotenv.get("SERVER_HOST");
        this.idBuilding = Integer.parseInt(dotenv.get("ID_BUILDING"));
    }
}
