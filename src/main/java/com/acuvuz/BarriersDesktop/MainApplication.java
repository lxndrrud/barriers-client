package com.acuvuz.BarriersDesktop;

import java.io.IOException;

import com.acuvuz.BarriersDesktop.controllers.MainController;
import com.acuvuz.BarriersDesktop.controllers.SerialPortController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            SerialPortController portController = SerialPortController.getInstance();
            portController.run();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/FXML/MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            stage.setTitle("Турникеты ЛГПУ");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.stop();
            } catch (Exception exception) {
                System.out.println("Ошибка при закрытии приложения!");
            }

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
