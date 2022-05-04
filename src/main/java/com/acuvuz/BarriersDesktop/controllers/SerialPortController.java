package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.fazecast.jSerialComm.*;
import javafx.fxml.FXML;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


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
                    Thread.sleep(3500);
                    String afterDelay = readFromPort();

                    if (afterDelay.contains("exit-success")) {
                        // Послать запрос на го-сервер
                        int returnCode = movementService.createMovementAction(portData);
                        if (returnCode != 201) {
                            System.out.println("Произошла ошибка!");
                        }
                    }
                    else {
                        writeToPort("@Code=lock;@reader=exit");
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
        });
        this.thread.start();
    }

    public void writeToPort(String s) {
        port.setBaudRate(9600);
        System.out.println(port.isOpen());
        if (port.isOpen()) {
            // Записываю в порт
            byte[] writeBuffer = s.getBytes(StandardCharsets.UTF_8);
            int bytesWrote = port.writeBytes(writeBuffer, writeBuffer.length);
            System.out.println("bytes wrote " + bytesWrote);
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
