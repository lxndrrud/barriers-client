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

    public TextField fullnameTextField;
    public TextField cardTextField;
    public TextField typeTextField;
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
                false,
                fromDate, fromHour, fromMinute,
                toDate, toHour, toMinute
        );

        Movement[] movements = this.movementService.getMovementsForUser(
                student != null ? student.student.id : 0,
                employee != null ? employee.employee.id : 0,
                datesArray.get(0), datesArray.get(1));
        //var movementList = movementsTableView.getItems();
        movementsTableView.getItems().removeAll();
        ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
        movementsTableView.setItems(movementsList);
    }

    public void setStudent(Student student) {
        this.student = student;
        fullnameTextField.setText(
                student.student.lastname + " " +  student.student.firstname + " "
                        + student.student.middlename
        );
        cardTextField.setText(student.student.skud_card);
        typeTextField.setText("Студент");
        var scrollPane = new ScrollPane();
        var vbox = new VBox();
        for (var group: student.groups) {
            var textField = new TextField(
                    group.title + " - " + group.course+ " курс - "+ group.department_title);
            textField.setPrefWidth(330);
            textField.setEditable(false);
            vbox.getChildren().add(textField);
        }
        scrollPane.setContent(vbox);
        groupsPane.setContent(scrollPane);
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
        fullnameTextField.setText(
                employee.employee.lastname + " " + employee.employee.firstname + " "
                + employee.employee.middlename
        );
        cardTextField.setText(employee.employee.skud_card);
        typeTextField.setText("Сотрудник");
        var scrollPane = new ScrollPane();
        var vbox = new VBox();
        for (var position: employee.positions) {
            var textField = new TextField(
                    position.title + " - " + position.department_title + " - " + position.date_drop);
            textField.setPrefWidth(330);
            textField.setEditable(false);
            vbox.getChildren().add(textField);
        }
        scrollPane.setContent(vbox);
        positionsPane.setContent(scrollPane);
    }

    public void setGuest() {
        fullnameTextField.setText("Гость");
        cardTextField.setText("Отсутствует");
        typeTextField.setText("Гость");
    }

    public void setMovements(Movement[] movements) {
        this.movements = movements;
        movementsTableView.getItems().removeAll();
        ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
        movementsTableView.setItems(movementsList);
    }
}
