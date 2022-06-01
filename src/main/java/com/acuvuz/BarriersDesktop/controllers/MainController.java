package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.MovementWithUser;
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

import java.util.ArrayList;

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

    public String parseMovementIntervalFromElements(DatePicker datePicker, TextField hourField, TextField minuteField) {
        LocalDate localDate = datePicker.getValue();
        String resultString = "";

        if (localDate != null) {
            resultString = localDate.getDayOfMonth() + "." +
                    localDate.getMonthValue() + "." +
                    localDate.getYear() + "T";
        }
        else {
            resultString =  new SimpleDateFormat("dd.MM.yyyy").format(Timestamp.from(Instant.now())) + "T";
        }

        if (hourField.getText().length() != 0) {
            if (hourField.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле часов фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += fromHour.getText() + ":";
        }
        else {
            resultString += new SimpleDateFormat("HH").format(Timestamp.from(Instant.now())) + ":";

        }

        if (minuteField.getText().length() != 0) {
            if (minuteField.getText().matches("\\D")) {
                createAlertModalWindow("Ошибка!", "Ошибка в поле минут фильтра 'От'!",
                        "Вы ввели нечисловое значение!");
            }

            resultString += minuteField.getText().toString();
        }
        else {
            resultString += new SimpleDateFormat("mm").format(Timestamp.from(Instant.now()));
        }


        return resultString;
    }
    public ArrayList<String> parseMovementInterval() {

        String fromString = parseMovementIntervalFromElements(fromDate, fromHour, fromMinute);
        String toString =  parseMovementIntervalFromElements(toDate, toHour, toMinute);

        var result = new ArrayList<String>();
        result.add(fromString);
        result.add(toString);

        return result;
    }


    public void onUpdateButtonClick() {
        var datesArray = parseMovementInterval();
        MovementWithUser[] movementWithUsers = this.movementService.getAll(datesArray.get(0), datesArray.get(1));
        //var movementList = movementsTableView.getItems();
        movementsTableView.getItems().removeAll();
        ObservableList<MovementWithUser> movementsList = FXCollections.observableArrayList(movementWithUsers);
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
