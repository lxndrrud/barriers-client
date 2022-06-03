package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.Employee;
import com.acuvuz.BarriersDesktop.JSONMappers.Movement;
import com.acuvuz.BarriersDesktop.JSONMappers.MovementWithUser;
import com.acuvuz.BarriersDesktop.JSONMappers.Student;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.acuvuz.BarriersDesktop.utils.DateTimeParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class MainController {
    public Button updateButton;
    public Button personalMovementsButton;
    public DatePicker fromDate;
    public TextField fromHour;
    public TextField fromMinute;


    public DatePicker toDate;
    public TextField toHour;
    public TextField toMinute;

    public TableView movementsTableView;

    private final MovementService movementService;


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


    public void onPersonalMovementsButtonClick() {
        try {
            var datesArray = DateTimeParser.parseMovementInterval(
                    fromDate, fromHour, fromMinute,
                    toDate, toHour, toMinute
            );

            Student student = null;
            Employee employee = null;
            Movement[] movements = null;
            var selectedMovement = (MovementWithUser) movementsTableView.getSelectionModel()
                    .getSelectedItem();
            if (selectedMovement.id_student != 0 ) {
                student = movementService.getStudentInfo(selectedMovement.id_student);
            }
            else if (selectedMovement.id_employee != 0) {
                employee = movementService.getEmployeeInfo(selectedMovement.id_employee);
            }
            movements = movementService.getMovementsForUser(selectedMovement,
                    datesArray.get(0), datesArray.get(1));
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(MainApplication.class
                    .getResource("/FXML/UserModalWindow.fxml"));
            //AnchorPane page = (AnchorPane) loader.load();
            Stage modalStage = new Stage();

            // Create the dialog Stage.
            Scene scene = new Scene(loader.load(), 800, 600);
            modalStage.setTitle("Информация о человеке");
            modalStage.setScene(scene);

            var controller = (UserModalController) loader.getController();

            if (student != null) {
                controller.setStudent(student);
            }
            else if (employee != null) {
                controller.setEmployee(employee);
            }

            controller.setMovements(movements);
            modalStage.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public MainController() {
        movementService = new MovementService();
    }
}
