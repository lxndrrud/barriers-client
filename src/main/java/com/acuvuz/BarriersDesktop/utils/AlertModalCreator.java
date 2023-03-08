package com.acuvuz.BarriersDesktop.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertModalCreator {
    public void createAlertModalWindow(String title, String header, String content) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
