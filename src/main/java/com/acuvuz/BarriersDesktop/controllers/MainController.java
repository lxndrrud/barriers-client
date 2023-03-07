package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.*;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.BuildingsService;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.acuvuz.BarriersDesktop.utils.DotenvProvider;
import com.acuvuz.BarriersDesktop.services.UserService;
import com.acuvuz.BarriersDesktop.utils.DateTimeParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.time.LocalDate;


public class MainController {
    public Button updateButton;
    public Button personalMovementsButton;
    public ComboBox buildingsComboBox;
    public DatePicker fromDate;
    public TextField fromHour;
    public TextField fromMinute;


    public DatePicker toDate;
    public TextField toHour;
    public TextField toMinute;

    public TableView movementsTableView;

    // LP = LastPerson (последний вошедший человек)
    public TextField fullnameLPTextField;
    public TextField typeLPTextField;

    public ImageView lastPersonPhoto;

    private final MovementService movementService;
    private final BuildingsService buildingsService;
    private final UserService userService;
    private final DotenvProvider dotenvProvider;

    private SerialPortController barrier1PortController;
    private SerialPortController barrier2PortController;

    public void setBarrier1PortController(SerialPortController barrier1PortController) {
        this.barrier1PortController = barrier1PortController;
    }

    public void setBarrier2PortController(SerialPortController barrier2PortController) {
        this.barrier2PortController = barrier2PortController;
    }

    public void onUpdateButtonClick() {
        updateMovements();
    }

    public void onUnlockBarrier1ButtonClick() {
        barrier1PortController.unlockBarrier();
    }
    public void onLockBarrier1ButtonClick() {
        barrier1PortController.lockBarrier();
    }

    public void onUnlockBarrier2ButtonClick() {
        barrier2PortController.unlockBarrier();
    }

    public void onLockBarrier2ButtonClick() {
        barrier2PortController.lockBarrier();
    }

    public void onOpenPort1ButtonClick() {
        barrier1PortController.run();
    }

    public void onClosePort1ButtonClick() {
        barrier1PortController.closePort();
    }

    public void onOpenPort2ButtonClick() { barrier2PortController.run(); }

    public void onClosePort2ButtonClick() {
        barrier2PortController.closePort();
    }

    public void updateMovements() {
        var datesArray = DateTimeParser.parseMovementInterval(
                true,
                fromDate, fromHour, fromMinute,
                toDate, toHour, toMinute
        );
        int id_building = ((Building) buildingsComboBox.getSelectionModel()
                .getSelectedItem()).id;
        MovementWithUser[] movementWithUsers = this.movementService.getAll(id_building,
                datesArray.get(0), datesArray.get(1));
        //var movementList = movementsTableView.getItems();
        movementsTableView.getItems().removeAll();
        ObservableList<MovementWithUser> movementsList = FXCollections
                .observableArrayList(movementWithUsers);
        movementsTableView.setItems(movementsList);
    }

    public void setLastPersonInfo(User user) {
        // Еще что-то нужно сделать с фоткой
        fullnameLPTextField.setText(
                user.lastname + " " + user.firstname
                + " " +user.middlename
        );
        typeLPTextField.setText(user.type);
        var thread = new Thread(() -> {
            lastPersonPhoto.setImage(new Image(dotenvProvider.getPhotoHost() + "/" + user.photo_path));
        });
        thread.start();
    }


    public void onPersonalMovementsButtonClick() {
        try {
            var datesArray = DateTimeParser.parseMovementInterval(
                    false,
                    fromDate, fromHour, fromMinute,
                    toDate, toHour, toMinute
            );

            Student student = null;
            Employee employee = null;
            var selectedMovement = (MovementWithUser) movementsTableView.getSelectionModel()
                    .getSelectedItem();
            if (selectedMovement == null ) return;
            if (selectedMovement.getId_student() != 0 ) {
                student = userService.getStudentInfo(selectedMovement.getId_student());
            }
            else if (selectedMovement.getId_employee() != 0) {
                employee = userService.getEmployeeInfo(selectedMovement.getId_employee());
            }
            Movement[] movements = movementService.getMovementsForUser(selectedMovement.getId_student(),
                    selectedMovement.getId_employee(),
                    datesArray.get(0), datesArray.get(1));
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader(MainApplication.class
                    .getResource("/FXML/UserModalWindow.fxml"));
            //AnchorPane page = (AnchorPane) loader.load();
            Stage modalStage = new Stage();

            // Create the dialog Stage.
            Scene scene = new Scene(loader.load(), 900, 600);
            modalStage.setTitle("Информация о человеке");
            modalStage.setScene(scene);

            var controller = (UserModalController) loader.getController();

            if (student != null) {
                controller.setStudent(student);
            }
            else if (employee != null) {
                controller.setEmployee(employee);
            }
            controller.initDates();
            controller.setMovements(movements);
            modalStage.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void loadBuildings() {
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
    }

    public void initDates() {
        fromDate.setValue(LocalDate.now());
        fromHour.setText("00");
        fromMinute.setText("00");
        toDate.setValue(LocalDate.now().plusDays(1));
        toHour.setText("00");
        toMinute.setText("00");
    }

    public MainController() {
        movementService = new MovementService();
        buildingsService = new BuildingsService();
        userService = new UserService();
        dotenvProvider = new DotenvProvider();
    }
}
