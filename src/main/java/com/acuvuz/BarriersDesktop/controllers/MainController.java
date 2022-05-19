package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MainController {
    public Button updateButton;
    public Button personalMovementsButton;
    public TextField fromTextField;
    public TextField toTextField;

    public Spinner fromDay;
    public ComboBox<Month> fromMonth;
    public Spinner fromYear;
    public Spinner fromHour;
    public Spinner fromMinute;
    public Spinner toDay;
    public ComboBox<Month> toMonth;
    public Spinner toYear;
    public Spinner toHour;
    public Spinner toMinute;

    public TableView movementsTableView;

    private final MovementService movementService;
    private final SerialPortController portController;




    public void onUpdateButtonClick() {
        Movement[] movements = this.movementService.getAll(fromTextField.getText(), toTextField.getText());
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

    public void setUpMonths() {
        /*
        ArrayList<String> monthList = new ArrayList<>();
        for (var month: Month.values()) {
            monthList.add(month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")));
        }

        fromMonth.setItems(FXCollections.observableArrayList(monthList));
        toMonth.setItems(FXCollections.observableArrayList(monthList));

         */
        ArrayList<Month> list = new ArrayList<>();
        for (var month: Month.values()) {
            list.add(month.)
        }

        fromMonth.setItems(FXCollections.observableArrayList(Month.values()));
        toMonth.setItems(FXCollections.observableArrayList(Month.values()));

    }

    public MainController() {
        movementService = new MovementService();
        portController = SerialPortController.getInstance();
    }
}
