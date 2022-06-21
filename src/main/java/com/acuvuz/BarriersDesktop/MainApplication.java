package com.acuvuz.BarriersDesktop;

import java.io.IOException;

import com.acuvuz.BarriersDesktop.controllers.MainController;
import com.acuvuz.BarriersDesktop.controllers.SerialPortController;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    SerialPortController barrier1Controller;
    SerialPortController barrier2Controller;
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class
                    .getResource("/FXML/MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            stage.setTitle("Турникеты ЛГПУ");
            stage.setScene(scene);
            MainController mainController = fxmlLoader.getController();
            loadPorts(mainController);
            mainController.loadBuildings();
            mainController.updateMovements();
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

    public void loadPorts(MainController mainController) {
        Dotenv dotenv = Dotenv.load();
        String barrier1String = dotenv.get("BARRIER_1_PORT", "default");
        if (!barrier1String.equals("default")) {
            barrier1Controller = new SerialPortController(barrier1String, mainController);
            mainController.setBarrier1PortController(barrier1Controller);
            barrier1Controller.run();
        }
        String barrier2String = dotenv.get("BARRIER_1_PORT", "default");
        if (barrier2String != "default") {
            barrier2Controller = new SerialPortController(barrier2String, mainController);
            mainController.setBarrier2PortController(barrier2Controller);
            barrier2Controller.run();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (barrier1Controller != null)
            barrier1Controller.closePort();
        if (barrier2Controller != null)
            barrier2Controller.closePort();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
