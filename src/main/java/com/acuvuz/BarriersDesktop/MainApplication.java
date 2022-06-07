package com.acuvuz.BarriersDesktop;

import java.io.IOException;
import java.util.ArrayList;

import com.acuvuz.BarriersDesktop.controllers.MainController;
import com.acuvuz.BarriersDesktop.controllers.SerialPortController;
import com.fazecast.jSerialComm.SerialPort;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    SerialPortController exitPortController;
    SerialPortController enterPortController;
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
        String exitString = dotenv.get("EXIT_PORT_PATH", "default");
        if (!exitString.equals("default")) {
            exitPortController = new SerialPortController(exitString, mainController);
            exitPortController.run();
        }
        String enterString = dotenv.get("ENTER_PORT_PATH", "default");
        if (enterString != "default") {
            enterPortController = new SerialPortController(enterString, mainController);
            enterPortController.run();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (exitPortController != null)
            exitPortController.closePort();
        if (enterPortController != null)
            enterPortController.closePort();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
