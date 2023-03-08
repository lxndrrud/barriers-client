package com.acuvuz.BarriersDesktop.JSONMappers;

import com.acuvuz.BarriersDesktop.utils.DateTimeParser;

public class Movement {
    final DateTimeParser dateTimeParser;
    public int id;
    public int id_building;
    public String building_name;
    public int id_event;
    public String event_timestamp;
    public String event_name;
    public int id_student;
    public int id_employee;

    public int getId() {
        return id;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_timestamp() {
        return dateTimeParser.parseTimestamp(event_timestamp);
    }

    public Movement() {
        dateTimeParser = new DateTimeParser();
    }

}
