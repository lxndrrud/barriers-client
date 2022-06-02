package com.acuvuz.BarriersDesktop.utils;

import javafx.scene.control.Alert;

public class AlertModalCreator {
    public static void createAlertModalWindow(String title, String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
