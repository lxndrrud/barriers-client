package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.DTO.ParsedPortData;
import com.acuvuz.BarriersDesktop.JSONMappers.*;
import com.acuvuz.BarriersDesktop.utils.DotenvProvider;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MovementService {
    private final DotenvProvider dotenvProvider;

    public MovementService() {
        this.dotenvProvider = new DotenvProvider();
    }

    public MovementWithUser[] getAll(int id_building, String from, String to) throws Exception {
        var link = this.dotenvProvider.getHost() + "/movements";
        try {
            var client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(link);
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .addParameter("id_building", String.valueOf(id_building))
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                MovementWithUser[] result = gson.fromJson(data, MovementWithUser[].class);
                client.close();
                return result;
            } else {
                client.close();
                throw new Exception(response.getEntity().getContent().toString());
            }
        } catch (Exception e ) {
            throw e;
        }
    }

    public void createMovementAction(ParsedPortData parsedPortData) throws IOException {
        sendCreateMovementActionRequest(parsedPortData.getReader(), parsedPortData.getCode());
    }

    public void createFailMovementAction(ParsedPortData parsedPortData) throws IOException {
        sendCreateMovementActionRequest("fail", parsedPortData.getCode());
    }

    private void sendCreateMovementActionRequest(String event, String skudCard) throws IOException {
        try {
            var client = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost(this.dotenvProvider.getHost() + "/movements/action");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_building", Integer.toString(this.dotenvProvider.getIdBuilding())));
            params.add(new BasicNameValuePair("event", event));
            params.add(new BasicNameValuePair("skud_card", skudCard));

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = client.execute(httpPost);

            /*
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(data);
             */

            client.close();

        } catch (Exception e ) {
            throw e;
        }
    }

    public Movement[] getMovementsForUser(int idStudent, int idEmployee, String from, String to, Integer idBuilding) throws Exception {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.dotenvProvider.getHost() + "/movements/user");
            var uriBuilder = new URIBuilder(httpGet.getURI());
            if (idStudent != 0) {
                uriBuilder
                        .addParameter("id_student", Integer.toString(idStudent))
                        .addParameter("from", from)
                        .addParameter("to", to)
                        .addParameter("id_building", idBuilding.toString())
                        .build();
            }
            else if (idEmployee != 0) {
                uriBuilder
                        .addParameter("id_employee", Integer.toString(idEmployee))
                        .addParameter("from", from)
                        .addParameter("to", to)
                        .addParameter("id_building", idBuilding.toString())
                        .build();
            }

            httpGet.setURI(uriBuilder.build());
            HttpResponse response = client.execute(httpGet);

            int returnCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (returnCode == 200) {
                Gson gson = new Gson();
                // Пропарсить сущность и вернуть её
                Movement[] movements = gson.fromJson(data, Movement[].class);
                client.close();
                return movements;
            }
            else {
                client.close();
                throw new Exception(response.getEntity().getContent().toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
