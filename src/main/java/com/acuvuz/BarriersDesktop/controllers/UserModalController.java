package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Building;
import com.acuvuz.BarriersDesktop.JSONMappers.Employee;
import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.Student;
import com.acuvuz.BarriersDesktop.services.BuildingsService;
import com.acuvuz.BarriersDesktop.utils.AlertModalCreator;
import com.acuvuz.BarriersDesktop.utils.DateTimeParser;
import com.acuvuz.BarriersDesktop.utils.DotenvProvider;
import com.acuvuz.BarriersDesktop.services.MovementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class UserModalController {
    final MovementService movementService;
    final BuildingsService buildingsService;
    final AlertModalCreator alertModalCreator;
    final DotenvProvider dotenvProvider;
    final DateTimeParser dateTimeParser;

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

    public DatePicker fromDate;
    public TextField fromHour;
    public TextField fromMinute;


    public DatePicker toDate;
    public TextField toHour;
    public TextField toMinute;
    public ComboBox buildingsComboBox;
    public ImageView userPhoto;



    public UserModalController() {
        movementService = new MovementService();
        buildingsService = new BuildingsService();
        dotenvProvider = new DotenvProvider();
        alertModalCreator = new AlertModalCreator();
        dateTimeParser = new DateTimeParser();
    }

    public void loadBuildings() {
        try {
            buildingsComboBox.getItems().removeAll();
            var buildingsList = FXCollections
                    .observableArrayList( buildingsService.GetAll());
            int index = 0;
            for (var b: buildingsList) {
                if (b.id == dotenvProvider.getIdBuilding()) {
                    break;
                }
                index++;
            }
            buildingsComboBox.setItems(buildingsList);
            buildingsComboBox.getSelectionModel().select(index);
        } catch (Exception e) {
            alertModalCreator.createAlertModalWindow("Ошибка", "Ошибка получения информации о зданиях",
                    e.getMessage());
        }
    }


    public void updateMovements() {
        var thread = new Thread(() -> {
            try {
                var datesArray = dateTimeParser.parseMovementInterval(
                        false,
                        fromDate, fromHour, fromMinute,
                        toDate, toHour, toMinute
                );
                int idBuilding = ((Building) buildingsComboBox.getSelectionModel()
                        .getSelectedItem()).id;

                Movement[] movements = this.movementService.getMovementsForUser(
                        student != null ? student.student.id : 0,
                        employee != null ? employee.employee.id : 0,
                        datesArray.get(0), datesArray.get(1), idBuilding);
                //var movementList = movementsTableView.getItems();
                movementsTableView.getItems().removeAll();
                ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
                movementsTableView.setItems(movementsList);
            } catch (Exception e) {
                alertModalCreator.createAlertModalWindow("Ошибка",
                        "Ошибка получения информации о перемещениях",
                        e.getMessage());
            }
        });
        thread.start();
    }

    public void onUpdateButtonClick() { updateMovements(); }

    public void setStudent(Student student) {
        var thread = new Thread(() -> {
            try {
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
            } catch (Exception e) {
                alertModalCreator.createAlertModalWindow("Ошибка",
                        "Ошибка получения информации о студенте",
                        e.getMessage());
            }

            var thread2 = new Thread(() -> {
                try {
                    userPhoto.setImage(new Image(dotenvProvider.getPhotoHost() + "/" + student.student.photo_path));
                } catch (Exception e) {
                    alertModalCreator.createAlertModalWindow("Ошибка",
                            "Ошибка получения фото студента",
                            e.getMessage());
                }
            });
            thread2.start();
        });
        thread.start();

    }
    public void setEmployee(Employee employee) {
        var thread = new Thread(() -> {
            try {
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
                    var dateDrop = (position.date_drop != null) ? position.date_drop : "Текущая должность";
                    var textField = new TextField(
                            position.title + " - " + position.department_title + " - " + dateDrop);
                    textField.setPrefWidth(330);
                    textField.setEditable(false);
                    vbox.getChildren().add(textField);
                }
                scrollPane.setContent(vbox);
                positionsPane.setContent(scrollPane);
            } catch (Exception e) {
                alertModalCreator.createAlertModalWindow("Ошибка",
                        "Ошибка получения информации о сотруднике",
                        e.getMessage());
            }

            var thread2 = new Thread(() -> {
                try {
                    userPhoto.setImage(new Image(dotenvProvider.getPhotoHost() + "/" + employee.employee.photo_path));
                } catch (Exception e) {
                    alertModalCreator.createAlertModalWindow("Ошибка",
                            "Ошибка получения фото сотрудника",
                            e.getMessage());
                }
            });
            thread2.start();
        });
        thread.start();
    }

    public void setMovements(Movement[] movements) {
        this.movements = movements;
        movementsTableView.getItems().removeAll();
        ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
        movementsTableView.setItems(movementsList);
    }

    public void initDates() {
        fromDate.setValue(LocalDate.now());
        fromHour.setText("00");
        fromMinute.setText("00");
        toDate.setValue(LocalDate.now().plusDays(1));
        toHour.setText("00");
        toMinute.setText("00");
    }
}
