package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.fazecast.jSerialComm.*;
import javafx.fxml.FXML;

import javax.swing.*;
import java.nio.charset.StandardCharsets;


public class SerialPortController {
    private static SerialPortController instance;
    private MovementService movementService;
    public static SerialPortController getInstance() {
        if (instance == null) {
            instance = new SerialPortController();
        }
        return instance;
    }
    private SerialPort port;

    private Thread thread;

    private SerialPortController() {
        movementService = new MovementService();
        loadSettings("/dev/ttyUSB0");
    }

    private void loadSettings(String portDescriptor) {
        port = SerialPort.getCommPort(portDescriptor);
    }

    public void closePort() {
        port.closePort();
    }

    public void listenPort() {
        port.setBaudRate(9600);
        port.openPort();
        try {
            while (port.isOpen()) {
                /*
                while (port.bytesAvailable() == 0) {
                    Thread.sleep(30);
                }
                 */
                String portData = readFromPort();
                if (portData.contains("@Code")) {
                    User user = movementService.sendSkudCardInfo(portData);

                    if (user.id == 0) {
                        System.out.println("here");
                        writeToPort("@Code=user-not-found;@reader=exit");
                        continue;
                    }

                    writeToPort("@Code=user-success;@reader=exit");
                    boolean entered = false;
                    int change = 308;
                    int secondsToWait = 3;
                    var from = System.currentTimeMillis();
                    boolean canSleep = true;
                    for (int i = 0; i < secondsToWait * 1000; i++) {
                        Thread.sleep(change);
                        String afterDelay = readFromPort();
                        if (afterDelay.contains("exit-success")) {
                            System.out.println("EXIT GIGA SUCCESS");
                            // Послать запрос на го-сервер
                            int returnCode = movementService.createMovementAction(portData);
                            if (returnCode != 201) {
                                System.out.println("Произошла ошибка!");
                            }
                            System.out.println(i);
                            entered = true;
                            break;
                        }
                        i += change;

                    }
                    System.out.println(System.currentTimeMillis() - from);

                    if (!entered) {
                        System.out.println("EXIT GIGA FAIL");
                        from = System.currentTimeMillis();

                        writeToPort("@Code=lock;@reader=exit");

                        System.out.println(System.currentTimeMillis() - from);
                        String afterDelay = readFromPort();
                        if (afterDelay.contains("exit-success")) {
                            System.out.println("EXIT GIGA SUCCESS");
                            // Послать запрос на го-сервер
                            int returnCode = movementService.createMovementAction(portData);
                            if (returnCode != 201) {
                                System.out.println("Произошла ошибка!");
                            }
                        }
                        else {
                            int returnCode = movementService.createFailMovementAction(portData);
                            if (returnCode != 201) {
                                System.out.println("Произошла ошибка!");
                            }
                        }

                    }

                }

                if (!portData.equals("")) {
                    System.out.println("На сериал порт что-то пришло!!!");
                    System.out.println(portData);
                }
            }
        } catch (InterruptedException e) {
            port.closePort();
            System.out.println("Произошла ошибка с считыванием с порта!");
        }
    }

    public void run() {
        this.thread = new Thread(() -> {
            // perform any operation
            System.out.println("Performing operation in Asynchronous Task");
            listenPort();
            System.out.println("Serial port ends!");
        });
        this.thread.start();
    }

    public void writeToPort(String toWrite) {
        port.setBaudRate(9600);
        if (port.isOpen()) {
            // Записываю в порт
            byte[] writeBuffer = toWrite.trim().getBytes(StandardCharsets.UTF_8);
            int bytesWrote = port.writeBytes(writeBuffer, writeBuffer.length);
        }
        else System.out.println("Порт закрыт!");
    }

    public String readFromPort() {
        byte[] readBuffer = new byte[1024];
        int numRead = port.readBytes(readBuffer, readBuffer.length);
        if (numRead == 0) {
            return "";
        }
        return new String(readBuffer, StandardCharsets.UTF_8);
    }



    public static void main(String[] args) {
        SerialPortController portController = new SerialPortController();
        portController.writeToPort("1");
    }
}
