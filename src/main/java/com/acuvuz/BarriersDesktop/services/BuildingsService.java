package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.JSONMappers.Building;
import com.acuvuz.BarriersDesktop.utils.DotenvProvider;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class BuildingsService {
    private final DotenvProvider dotenvProvider;
    public BuildingsService() {
        this.dotenvProvider = new DotenvProvider();
    }

    public Building[] GetAll() {
        var link = this.dotenvProvider.getHost() + "/buildings";
        try {
            var client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(link);
            URI uri = new URIBuilder(httpGet.getURI()).build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Building[] result = gson.fromJson(data, Building[].class);
                client.close();
                return result;
            } else {
                client.close();
                return new Building[]{};
            }
        } catch (Exception e) {
            return new Building[]{};
        }
    }
}
