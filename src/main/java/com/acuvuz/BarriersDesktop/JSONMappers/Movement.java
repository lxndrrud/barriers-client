package com.acuvuz.BarriersDesktop.JSONMappers;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Movement {
    public int id;
    public int id_building;
    public String building_name;
    public int id_event;
    public String event_timestamp;
    public String event_name;
    public int id_student;
    public int id_employee;

    public String firstname;
    public String middlename;
    public String lastname;
    public String skud_card;
    public String type;

    public int getId() {
        return id;
    }

    public int getId_building() {
        return id_building;
    }

    public int getId_event() {
        return id_event;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_timestamp() {
        return event_timestamp;
    }

    public int getId_student() {
        return id_student;
    }

    public int getId_employee() {
        return id_employee;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public String getSkud_card() {
        return skud_card;
    }

    public String getType() {
        if (id_student != 0) return "Студент";
        else if (id_employee != 0 ) return "Сотрудник";
        else return "Ошибка определения!";
    }
}


