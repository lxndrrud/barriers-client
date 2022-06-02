package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Employee;
import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.MovementWithUser;
import com.acuvuz.BarriersDesktop.JSONMappers.Student;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.acuvuz.BarriersDesktop.utils.DateTimeParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserModalController {
    final MovementService movementService;

    Student student;
    Employee employee;
    Movement[] movements;

    public Button updateButton;

    public Label lastnameLabel;
    public Label firstnameLabel;
    public Label middlenameLabel;
    public Label cardLabel;

    public TitledPane positionsPane;
    public TitledPane groupsPane;

    public TableView movementsTableView;

    public Label studentFlagLabel;
    public Label employeeFlagLabel;

    public DatePicker fromDate;
    public TextField fromHour;
    public TextField fromMinute;


    public DatePicker toDate;
    public TextField toHour;
    public TextField toMinute;



    public UserModalController() {
        movementService = new MovementService();
    }

    public void onUpdateButtonClick() {
        var datesArray = DateTimeParser.parseMovementInterval(
                fromDate, fromHour, fromMinute,
                toDate, toHour, toMinute
        );
        MovementWithUser[] movementWithUsers = this.movementService.getAll(datesArray.get(0), datesArray.get(1));
        //var movementList = movementsTableView.getItems();
        movementsTableView.getItems().removeAll();
        ObservableList<MovementWithUser> movementsList = FXCollections.observableArrayList(movementWithUsers);
        movementsTableView.setItems(movementsList);
    }

    public void setStudent(Student student) {
        this.student = student;
        firstnameLabel.setText(student.student.firstname);
        middlenameLabel.setText(student.student.middlename);
        lastnameLabel.setText(student.student.lastname);
        cardLabel.setText(student.student.skud_card);
        employeeFlagLabel.setText("Нет");
        studentFlagLabel.setText("Да");
        var vbox = new VBox();
        for (var group: student.groups) {
            vbox.getChildren().add(new Label(
                    group.title + " - " + group.course+ " курс - "+ group.department_title));
        }
        groupsPane.setContent(vbox);
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
        firstnameLabel.setText(employee.employee.firstname);
        middlenameLabel.setText(employee.employee.middlename);
        lastnameLabel.setText(employee.employee.lastname);
        cardLabel.setText(employee.employee.skud_card);
        employeeFlagLabel.setText("Да");
        studentFlagLabel.setText("Нет");
        var vbox = new VBox();
        for (var position: employee.positions) {
            vbox.getChildren().add(new Label(
                    position.title + " - " + position.department_title + " - " + position.date_drop));
        }
        positionsPane.setContent(vbox);
    }

    public void setMovements(Movement[] movements) {
        this.movements = movements;
        movementsTableView.getItems().removeAll();
        ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
        movementsTableView.setItems(movementsList);
    }
}
