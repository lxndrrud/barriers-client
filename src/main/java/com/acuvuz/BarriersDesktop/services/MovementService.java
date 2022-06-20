package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.DTO.ParsedPortData;
import com.acuvuz.BarriersDesktop.JSONMappers.*;
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


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MovementService {
    private final RootService root;

    public MovementService() {
        this.root = RootService.getInstance();
    }

    public MovementWithUser[] getAll(int id_building, String from, String to) {
        var link = this.root.getHost() + "/movements";

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
                return new MovementWithUser[]{};
            }
        } catch (Exception e ) {
            return new MovementWithUser[]{};
        }
    }

    public int createMovementAction(ParsedPortData parsedPortData) {
        return sendCreateMovementActionRequest(parsedPortData.getReader(), parsedPortData.getCode());
    }

    public int createFailMovementAction(ParsedPortData parsedPortData) {
        return sendCreateMovementActionRequest("fail", parsedPortData.getCode());
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

    public Movement[] getMovementsForUser(int idStudent, int idEmployee, String from, String to) {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/movements/user");
            var uriBuilder = new URIBuilder(httpGet.getURI());
            if (idStudent != 0) {
                uriBuilder
                        .addParameter("id_student", Integer.toString(idStudent))
                        .addParameter("from", from)
                        .addParameter("to", to)
                        .build();
            }
            else if (idEmployee != 0) {
                uriBuilder
                        .addParameter("id_employee", Integer.toString(idEmployee))
                        .addParameter("from", from)
                        .addParameter("to", to)
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
                var exception = new Exception(response.getStatusLine().getStatusCode()
                        + response.getStatusLine().getReasonPhrase());
                client.close();
                throw exception;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Ошибка при запросе на сервер");
            return new Movement[]{};
        }
    }

    /*
    private int getEmployeeInfoAndMovements(int id_employee, String from, String to) {
        try {
            getEmployeeInfo(id_employee);
            return 0;
        } catch (Exception e ) {
            System.out.println(e);
            return 1;
        }
    }

    private int getStudentInfoAndMovements(int id_student, String from, String to) {
        try {
            getStudentInfo(id_student);
            return 0;
        } catch (Exception e ) {
            System.out.println(e);
            return 1;
        }
    }

     */

    /*
    public static void main(String[] args) {
        var movements = new MovementService();
        try {
            var result1 = movements.getStudentInfo(1);
            System.out.println(result1.student.firstname);
            System.out.println(result1.student.middlename);
            System.out.println(result1.student.lastname);
            System.out.println(result1.student.skud_card);
            for (var item: result1.groups) {
                System.out.println(item.id);
                System.out.println(item.title);
                System.out.println(item.department_title);
            }

            var result2 = movements.getEmployeeInfo(1);
            System.out.println(result2.employee.firstname);
            System.out.println(result2.employee.middlename);
            System.out.println(result2.employee.lastname);
            System.out.println(result2.employee.skud_card);
            for (var item: result2.positions) {
                System.out.println(item.id);
                System.out.println(item.title);
                System.out.println(item.department_title);
                System.out.println(item.date_drop);
            }

            var all = movements
                    .getAll(1, "14.04.2022T15:00", "");

            int i=0;
            for (var move: all) {
                var get = movements.getMovementsForUser(move.id_student, move.id_employee, "14.04.2022T15:00", "");
                for (var getted: get) {
                    System.out.println(getted.building_name);
                    System.out.println(getted.event_name);
                    System.out.println(getted.event_timestamp);
                    System.out.println(getted.id_student);
                    System.out.println(getted.id_employee);
                    System.out.println("");
                    System.out.println("");
                }
            }


        } catch (Exception e) {
            System.out.println("Обосрался!");
        }
    }



    private void sendPersonalMovementsRequest(MovementWithUser movementWithUser, String from, String to) {
        try {
            if (movementWithUser.id_employee != 0) {
                var employeeInfo = getEmployeeInfo(movementWithUser.id_employee);
                //getEmployeeInfoAndMovements(movementWithUser.id_employee, from, to);

            }
            else if (movementWithUser.id_student != 0) {
                var studentInfo = getStudentInfo(movementWithUser.id_student);
                //returnCode = getStudentInfoAndMovements(movementWithUser.id_student, from, to);
            }
            var movements = getMovementsForUser(1, 0, from, to);


        } catch (Exception e ) {
            System.out.println("Ошибка при запросе на сервер");
        }
    }


     */



}
