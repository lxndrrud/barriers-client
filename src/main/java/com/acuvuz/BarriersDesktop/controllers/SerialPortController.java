package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.MainApplication;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.fazecast.jSerialComm.*;
import javafx.fxml.FXML;

import java.nio.charset.StandardCharsets;
import java.util.Timer;


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

    private SerialPortController() {
        movementService = new MovementService();
        loadSettings("/dev/ttyUSB1");
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
                while (port.bytesAvailable() == 0) {
                    Thread.sleep(30);
                }
                byte[] readBuffer = new byte[1024];
                int numRead = port.readBytes(readBuffer, readBuffer.length);
                String portData = new String(readBuffer, StandardCharsets.UTF_8);
                movementService.sendSkudCardInfo(portData);
                if (numRead > 0) {
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
        new Thread(new Runnable() {
            public void run()
            {
                // perform any operation
                System.out.println("Performing operation in Asynchronous Task");
                listenPort();
            }
        }).start();
    }

    public void writeToPort(String s) {
        port = SerialPort.getCommPort("/dev/ttyUSB0");
        port.setBaudRate(9600);

        if (port.isOpen()) {
            // Записываю в порт
            byte[] writeBuffer = s.getBytes(StandardCharsets.UTF_8);
            int bytesWrote = port.writeBytes(writeBuffer, writeBuffer.length);
            System.out.println("bytes wrote " + bytesWrote);
        }
        else System.out.println("Порт закрыт!");
    }



    public static void main(String[] args) {
        SerialPortController portController = new SerialPortController();
        portController.writeToPort("1");
    }
}
