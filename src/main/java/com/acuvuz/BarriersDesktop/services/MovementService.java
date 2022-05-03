package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovementService {
    private final RootService root;

    public MovementService() {
        this.root = new RootService();
    }

    public Movement[] getAll(String from, String to) {
        var link = this.root.getHost() + "/movements";

        try {
            var client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(link);
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Movement[] result = gson.fromJson(data, Movement[].class);
                client.close();
                return result;
            } else {
                client.close();
                return new Movement[]{};
            }
        } catch (Exception e ) {
            return new Movement[]{};
        }
    }
    public Movement[] getAllOld(String from, String to) {
        try {
            // Create a neat value object to hold the URL
            URL url = new URL(String.format(this.root.getHost() + "/movements?from=%s&to=%s", from, to));

            // Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // This line makes the request
            InputStream responseStream = connection.getInputStream();

            Gson gson = new Gson();

            return gson.fromJson(new BufferedReader(new InputStreamReader(responseStream)), Movement[].class);

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Ошибка в поиске перемещений!");
            return new Movement[]{};
        }
    }

    public User sendSkudCardInfo(String portData) {
        System.out.println(portData);

        var variables = portData.split(";");
        String code = variables[0].split("=")[1];
        String reader = variables[1].split("=")[1];
        try {
            var client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/users/skudCard");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("skud_card", code)
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                User user = gson.fromJson(data, User.class);
                client.close();
                return user;
            }
            client.close();
            return new User();
        } catch (Exception e) {
            System.out.println(e);
            return new User();
        }
    }
}
