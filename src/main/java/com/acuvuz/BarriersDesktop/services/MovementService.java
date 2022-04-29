package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MovementService {
    private final RootService root;

    public MovementService() {
        this.root = new RootService();
    }
    public Movement[] getAll(String from, String to) {
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

    public User sendSkudCardInfo(String skudCardInfo) {
        System.out.println(skudCardInfo);
        var splitted = new ArrayList<>(Arrays.asList(skudCardInfo.split("@")));
        var code = new ArrayList<>(Arrays.asList(splitted.get(0).split("="))).get(1);
        var reader = new ArrayList<>(Arrays.asList(splitted.get(1).split("="))).get(1);
        try {
            URL url = new URL(String.format(this.root.getHost() + "/users/skudCard?skud_card=%s", code));

            // Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // This line makes the request
            InputStream responseStream = connection.getInputStream();

            Gson gson = new Gson();

            return gson.fromJson(new BufferedReader(new InputStreamReader(responseStream)), User.class);
        } catch (Exception e) {
            System.out.println(e);
            return new User();
        }
    }
}
