package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.services.MovementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainController {
    public Button updateButton;
    public TextField fromTextField;
    public TextField toTextField;

    public TableView movementsTableView;

    private MovementService movementService;
    private SerialPortController portController;



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

    public MainController() {
        movementService = new MovementService();
        portController = SerialPortController.getInstance();
    }
}
