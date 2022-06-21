package com.acuvuz.BarriersDesktop.JSONMappers;

import com.acuvuz.BarriersDesktop.utils.DateTimeParser;

import java.util.Arrays;

public class MovementWithUser {
    public Movement movement;
    public User user;

    public int getId() {
        return movement.getId();
    }

    public int getId_building() {
        return movement.id_building;
    }

    public int getId_event() {
        return movement.id_event;
    }

    public String getEvent_name() {
        return movement.event_name;
    }

    public String getEvent_timestamp() {
        return DateTimeParser.parseTimestamp(movement.event_timestamp);
    }

    public int getId_student() {
        return movement.id_student;
    }

    public int getId_employee() {
        return movement.id_employee;
    }

    public String getBuilding_name() {
        return movement.building_name;
    }

    public String getFirstname() {
        return user.firstname;
    }

    public String getMiddlename() {
        return user.middlename;
    }

    public String getLastname() {
        return user.lastname;
    }

    public String getSkud_card() {
        return user.skud_card;
    }

    public String getType() {
        if (movement.id_student != 0) return "Студент";
        else if (movement.id_employee != 0 ) return "Сотрудник";
        else return "Гость";
    }
}


