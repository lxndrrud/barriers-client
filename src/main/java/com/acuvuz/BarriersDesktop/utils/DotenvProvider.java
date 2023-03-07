package com.acuvuz.BarriersDesktop.utils;

import io.github.cdimascio.dotenv.Dotenv;

public final class DotenvProvider {
    private final String host;
    private final String photoHost;
    private final int idBuilding;
    private String barrier1Port;
    private String barrier2Port;

    public String getHost() {
        return host;
    }

    public int getIdBuilding() {
        return idBuilding;
    }

    public String getPhotoHost() { return photoHost; }

    public String getBarrier1Port() {
        return barrier1Port;
    }

    public String getBarrier2Port() {
        return barrier2Port;
    }

    public DotenvProvider() {
        Dotenv dotenv = Dotenv.load();
        this.host = dotenv.get("SERVER_HOST");
        this.idBuilding = Integer.parseInt(dotenv.get("ID_BUILDING"));
        this.photoHost = dotenv.get("PHOTO_HOST");
        this.barrier1Port = dotenv.get("BARRIER_1_PORT");
        this.barrier2Port = dotenv.get("BARRIER_2_PORT");
    }
}
