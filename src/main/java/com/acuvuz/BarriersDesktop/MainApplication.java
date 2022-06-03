package com.acuvuz.BarriersDesktop;

import java.io.IOException;
import java.util.ArrayList;

import com.acuvuz.BarriersDesktop.controllers.MainController;
import com.acuvuz.BarriersDesktop.controllers.SerialPortController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    ArrayList<SerialPortController> portControllers;
    @Override
    public void start(Stage stage) throws IOException {
        try {
            portControllers = new ArrayList<>();
            portControllers.add(new SerialPortController("EXIT_PORT_PATH"));
            for (var controller: portControllers) {
                controller.run();
            }
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

    @Override
    public void stop() throws Exception {
        super.stop();
        for (var controller: portControllers) {
            controller.closePort();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
