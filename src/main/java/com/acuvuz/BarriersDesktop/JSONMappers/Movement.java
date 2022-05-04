package com.acuvuz.BarriersDesktop.JSONMappers;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Movement {
    public int id;
    public int id_building;
    public int id_event;
    public String event_timestamp;
    public String event_name;
    public int id_student;
    public int id_employee;

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
}


