package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.google.gson.Gson;
import com.sun.net.httpserver.Request;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public User sendSkudCardInfo(String portData) {
        System.out.println(portData);

        var variables = portData.split(";");
        System.out.println(variables);
        String code = variables[0].split("=")[1];
        //String reader = variables[1].split("=")[1];
        try {
            var client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/users/skudCard");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("skud_card", code)
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (response.getStatusLine().getStatusCode() == 200) {
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

    public int createMovementAction(String portData) {

        // Парсинг данных с порта
        var variables = portData.split(";");
        String code = variables[0].split("=")[1];
        String reader = variables[1].split("=")[1].trim();
        System.out.println("Code: '" + code + "'");
        System.out.println("Reader: '" + reader + "'");

        return sendCreateMovementActionRequest(reader, code);
    }

    public int createFailMovementAction(String portData) {
        // Парсинг данных с порта
        var variables = portData.split(";");
        String code = variables[0].split("=")[1];
        System.out.println("Code: '" + code + "'");

        return sendCreateMovementActionRequest("fail", code);
    }

    private int sendCreateMovementActionRequest(String event, String skudCard) {
        try {
            var client = HttpClientBuilder.create().build();

            HttpPost httpPost = new HttpPost(this.root.getHost() + "/movements/action");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id_building", Integer.toString(this.root.getIdBuilding())));
            params.add(new BasicNameValuePair("event", event));
            params.add(new BasicNameValuePair("skud_card", skudCard));

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = client.execute(httpPost);

            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(data);

            client.close();
            int returnCode = response.getStatusLine().getStatusCode();

            return returnCode;
        } catch (Exception e ) {
            System.out.println("Ошибка при запросе на сервер");
            return 500;
        }
    }

    private String getEmployeeMovementsEndpoint() {
        return "/movements/employee";
    }

    private int getStudentInfoAndMovements(Movement movement, String from, String to) {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/movements/student");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("id_student", Integer.toString(movement.id_student))
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            client.close();
            int returnCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (returnCode == 200) {
                Gson gson = new Gson();
                // Пропарсить сущность и вернуть её
                User user = gson.fromJson(data, User.class);
                client.close();
                return returnCode;
            }
            return returnCode;
        } catch (Exception e) {
            System.out.println("Ошибка при запросе на сервер");
            return 500;
        }
    }

    private int getEmployeeInfoAndMovements(Movement movement, String from, String to) {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/movements/employee");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("id_student", Integer.toString(movement.id_student))
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            client.close();
            int returnCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (returnCode == 200) {
                Gson gson = new Gson();
                // Пропарсить сущность и вернуть её
                User user = gson.fromJson(data, User.class);
                client.close();
                return returnCode;
            }
            return returnCode;
        } catch (Exception e) {
            System.out.println("Ошибка при запросе на сервер");
            return 500;
        }
    }

    private int sendPersonalMovementsRequest(Movement movement, String from, String to) {
        try {
            int returnCode;
            if (movement.id_employee != 0)
                returnCode = getStudentInfoAndMovements(movement, from, to);
            else if (movement.id_student != 0)
                returnCode = getEmployeeInfoAndMovements(movement, from, to);
            else
                returnCode = 0;


            return returnCode;
        } catch (Exception e ) {
            System.out.println("Ошибка при запросе на сервер");
            return 500;
        }
    }




}
