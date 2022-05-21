package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import javafx.util.converter.NumberStringConverter;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MainController {
    public Button updateButton;
    public Button personalMovementsButton;
    public TextField fromTextField;
    public TextField toTextField;

    public DatePicker fromDate;
    public TextField fromHour;
    public TextField fromMinute;


    public DatePicker toDate;
    public TextField toHour;
    public TextField toMinute;

    public TableView movementsTableView;

    private final MovementService movementService;
    private final SerialPortController portController;

    public void createAlertModalWindow(String title, String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public ArrayList<String> parseMovementInterval() {
        LocalDate fromLocalDate = fromDate.getValue();
        LocalDate toLocalDate = toDate.getValue();

        String fromString = "";
        String toString = "";

        if (fromLocalDate != null) {
            fromString = fromLocalDate.getDayOfMonth() + "." +
                    fromLocalDate.getMonthValue() + "." +
                    fromLocalDate.getYear() + "T";
        }
        else {
            fromString =  new SimpleDateFormat("dd.MM.yyyy").format(Timestamp.from(Instant.now())) + "T";
        }



        if (fromHour.getText().length() != 0) {
            if (fromHour.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле часов фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            fromString += fromHour.getText() + ":";
        }
        else {
            fromString += new SimpleDateFormat("HH").format(Timestamp.from(Instant.now())) + ":";

        }
        if (fromMinute.getText().length() != 0) {
            if (fromMinute.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле минут фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            fromString += fromMinute.getText().toString();
        }
        else {
            fromString += new SimpleDateFormat("mm").format(Timestamp.from(Instant.now()));
        }

        if (toLocalDate != null) {
            toString = toLocalDate.getDayOfMonth() + "." +
                    toLocalDate.getMonthValue() + "." +
                    toLocalDate.getYear() + "T";
        }
        else {
            toString =  new SimpleDateFormat("dd.MM.yyyy").format(Timestamp.from(Instant.now())) + "T";
        }

        if (toHour.getText().length() != 0) {
            if (toHour.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле часов фильтра 'До'!",
                        "Вы ввели нечисловое значение!");
            }

            toString += toHour.getText().toString();
        } else {
            toString += new SimpleDateFormat("HH").format(Timestamp.from(Instant.now())) + ":";

        }

        if (toMinute.getText().length() != 0) {
            if (toMinute.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле минут фильтра 'До'!",
                        "Вы ввели нечисловое значение!");
            }

            toString += toMinute.getText().toString();
        }
        else {
            toString += new SimpleDateFormat("mm").format(Timestamp.from(Instant.now()));

        }

        var result = new ArrayList<String>();
        result.add(fromString);
        result.add(toString);

        return result;
    }


    public void onUpdateButtonClick() {
        var datesArray = parseMovementInterval();
        Movement[] movements = this.movementService.getAll(datesArray.get(0), datesArray.get(1));
        //var movementList = movementsTableView.getItems();
        movementsTableView.getItems().removeAll();
        ObservableList<Movement> movementsList = FXCollections.observableArrayList(movements);
        movementsTableView.setItems(movementsList);
    }

    public void onTurnOffPortButtonClick() {
        portController.closePort();
    }

    public void onPersonalMovementsButtonClick() {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/FXML/UserModalWindow.fxml"));
            //AnchorPane page = (AnchorPane) loader.load();
            Stage modalStage = new Stage();

            // Create the dialog Stage.
            Scene scene = new Scene(loader.load(), 800, 600);
            modalStage.setTitle("Информация о человеке");
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public MainController() {
        movementService = new MovementService();
        portController = SerialPortController.getInstance();
    }
}
