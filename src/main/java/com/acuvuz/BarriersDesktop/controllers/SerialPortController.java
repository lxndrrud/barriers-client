package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.DTO.ParsedPortData;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.acuvuz.BarriersDesktop.services.UserService;
import com.acuvuz.BarriersDesktop.utils.AlertModalCreator;
import com.fazecast.jSerialComm.*;
import java.nio.charset.StandardCharsets;


public class SerialPortController {
    private final MainController mainController;
    private final MovementService movementService;
    private final UserService userService;

    private final AlertModalCreator alertModalCreator;
    private SerialPort port;
    private String portDescriptor;
    private Integer mainControllerPortNumber;

    private Thread thread;

    public SerialPortController(String portDescriptor, MainController mainController1, Integer mainControllerPortNumber) {
        movementService = new MovementService();
        userService = new UserService();
        alertModalCreator = new AlertModalCreator();
        mainController = mainController1;
        this.mainControllerPortNumber = mainControllerPortNumber;
        this.portDescriptor = portDescriptor;
        port = SerialPort.getCommPort(portDescriptor);
    }

    public void closePort() {
        port.closePort();
    }

    public void openPort() {
        if (this.thread == null || !thread.isAlive()) {
            run();
        }
    }

    public void listenToPort() {
        port.setBaudRate(9600);
        port.openPort();

        mainController.updateBarrierIndicator(mainControllerPortNumber, true);
        while (port.isOpen()) {
            try {
                String portData = readFromPort();
                if (!(portData.contains("@Code") && portData.contains("@Direction"))) continue;
                var parsedData = new ParsedPortData(portData);

                User user = userService.sendSkudCardInfo(parsedData.getCode());
                if (user == null) {
                    alarmBarrier(parsedData.getReader());
                    alertModalCreator.createAlertModalWindow(
                            "Ошибка", "Пользователь не найден!", ""
                    );
                    continue;
                }
                mainController.setLastPersonInfo(user);

                boolean actionPerfomed = false;
                openBarrierForUser(parsedData.getReader());
                // 350 * 10 = 3,5 секунды на проход,
                // ~80 - оптимальная задержка, при которой сначала закрывается турникет, а потом шлется сигнал
                // о неудачном проходе. Каждые 10 милисекунд прослушивается сериал порт
                for (int i = 0; i < 350 + 100; i++) {
                    String fromPort = readFromPort();
                    // enter or exit success
                    if (fromPort.equals(parsedData.getReader() + "-success")) {
                        try {
                            movementService.createMovementAction(parsedData);
                        } catch (Exception e) {
                            alertModalCreator.createAlertModalWindow(
                                    "Ошибка",
                                    "Ошибка во время работа турникета",
                                    "Перемещение человека не записано! " + e.getMessage());
                        }
                        actionPerfomed = true;
                        break;
                    } else if (fromPort.equals(parsedData.getReader() + "-fail")) {
                        try {
                            movementService.createFailMovementAction(parsedData);
                        } catch (Exception e) {
                            alertModalCreator.createAlertModalWindow(
                                    "Ошибка",
                                    "Ошибка во время работа турникета",
                                    "Неудача прохода человека не записано! " + e.getMessage());
                        }
                        actionPerfomed = true;
                        break;
                    }
                    Thread.sleep(10);
                }
                if (!actionPerfomed) {
                    movementService.createFailMovementAction(parsedData);
                }
                mainController.updateMovements();
            } catch (Exception e) {
                alertModalCreator.createAlertModalWindow(
                        "Ошибка",
                        "Ошибка во время работы турникета",
                        e.getMessage());
            }
        }
        mainController.updateBarrierIndicator(mainControllerPortNumber, false);
        alertModalCreator.createAlertModalWindow(
                "Ошибка",
                "Порт \"" + portDescriptor + "\" был закрыт!",
                "Для записи перемещений порт необходимо снова открыть.");

    }

    /*
    Открыть турникет на ~4 секунды
     */
    public void openBarrierForUser(String reader) {
        writeToPort("@Code=user-success;@reader=" + reader);
    }

    /*
    Подать сигнал о не найденном пользователе (звуковой сигнла - пищалка)
     */
    public void alarmBarrier(String reader) {
        writeToPort("@Code=user-not-found;@reader=both" + reader);
    }

    /*
    Закрыть турникет
     */
    public void lockBarrier() {
        writeToPort("@Code=lock;@reader=both");
    }

    /*
    Открыть турникет на постоянку
     */
    public void unlockBarrier() {
        writeToPort("@Code=unlock;@reader=both");
    }

    public void run() {
        this.thread = new Thread(() -> {
            // perform any operation
            System.out.println("Performing operation in Asynchronous Task");
            listenToPort();
            System.out.println("Serial port ends!");
        });
        this.thread.start();
    }

    private void writeToPort(String toWrite) {
        port.setBaudRate(9600);
        if (port.isOpen()) {
            // Записываю в порт
            byte[] writeBuffer = toWrite.trim().getBytes(StandardCharsets.UTF_8);
            int bytesWrote = port.writeBytes(writeBuffer, writeBuffer.length);
        }
    }

    private String readFromPort() {
        byte[] readBuffer = new byte[1024];
        int numRead = port.readBytes(readBuffer, readBuffer.length);
        if (numRead == 0) {
            return "";
        }
        return new String(readBuffer, StandardCharsets.UTF_8).trim();
    }
}
