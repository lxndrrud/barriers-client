package com.acuvuz.BarriersDesktop.services;

import com.acuvuz.BarriersDesktop.JSONMappers.Employee;
import com.acuvuz.BarriersDesktop.JSONMappers.Student;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class UserService {
    private final RootService root;
    public UserService() {
        this.root = RootService.getInstance();
    }

    public User sendSkudCardInfo(String code) {
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

    public Student getStudentInfo(int id_student) throws Exception {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/users/student");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("id_student", Integer.toString(id_student))
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            client.close();
            int returnCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (returnCode == 200) {
                Gson gson = new Gson();
                // ???????????????????? ???????????????? ?? ?????????????? ????
                Student student = gson.fromJson(data, Student.class);
                client.close();
                return student;
            }
            else {
                throw new Exception(response.getStatusLine().getStatusCode()
                        + response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            System.out.println("???????????? ?????? ?????????????? ???? ????????????");
            throw e;
        }
    }

    public Employee getEmployeeInfo(int id_employee) throws Exception {
        var client = HttpClientBuilder.create().build();
        try {
            HttpGet httpGet = new HttpGet(this.root.getHost() + "/users/employee");
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("id_employee", Integer.toString(id_employee))
                    .build();
            httpGet.setURI(uri);
            HttpResponse response = client.execute(httpGet);

            client.close();
            int returnCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (returnCode == 200) {
                Gson gson = new Gson();
                // ???????????????????? ???????????????? ?? ?????????????? ????
                Employee employee = gson.fromJson(data, Employee.class);
                client.close();
                return employee;
            }
            else {
                throw new Exception(response.getStatusLine().getStatusCode()
                        + response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            System.out.println("???????????? ?????? ?????????????? ???? ????????????");
            throw e;
        }
    }
}
