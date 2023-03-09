package com.acuvuz.BarriersDesktop.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class AlertModalCreator {
    public void createAlertModalWindow(String title, String header, String content) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
