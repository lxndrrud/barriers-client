package com.acuvuz.BarriersDesktop.controllers;

import com.acuvuz.BarriersDesktop.DTO.ParsedPortData;
import com.acuvuz.BarriersDesktop.JSONMappers.User;
import com.acuvuz.BarriersDesktop.services.MovementService;
import com.fazecast.jSerialComm.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class SerialPortController {
    private final MainController mainController;
    private final MovementService movementService;
    private SerialPort port;

    private Thread thread;

    public SerialPortController(String portDescriptor, MainController mainController1) {
        movementService = new MovementService();
        mainController = mainController1;
        port = SerialPort.getCommPort(portDescriptor);
    }

    public void closePort() {
        port.closePort();
    }

    public void listenToPort() {
        port.setBaudRate(9600);
        port.openPort();
        try {
            while (port.isOpen()) {
                String portData = readFromPort();
                if (portData.contains("@Code") && portData.contains("@Direction")) {
                    var parsedData = new ParsedPortData(portData);

                    User user = movementService.sendSkudCardInfo(parsedData.getCode());
                    if (user.id == 0) {
                        alarmBarrier(parsedData.getReader());
                        continue;
                    }
                    mainController.setLastPersonInfo(user);

                    boolean actionPerfomed = false;
                    openBarrierForUser(parsedData.getReader());
                    // 350 * 10 = 3,5 секунды на проход,
                    // ~80 - оптимальная задержка, при которой сначала закрывается турникет, а потом шлется сигнал
                    // о неудачном проходе. Каждые 10 милисекунд прослушивается сериал порт
                    for (int i=0; i < 350 + 100; i++) {
                        String fromPort = readFromPort();
                        // enter or exit success
                        if (fromPort.equals(parsedData.getReader() + "-success")) {
                            int returnCode = movementService
                                    .createMovementAction(parsedData);
                            if (returnCode != 201) {
                                System.out.println("Произошла ошибка!");
                            }
                            actionPerfomed = true;
                            break;
                        }
                        /*
                        else if (fromPort.equals("exit-fail")) {
                            System.out.println("giga fail");
                            int returnCode = movementService.createFailMovementAction(portData);
                            if (returnCode != 201) {
                                System.out.println("Произошла ошибка!");
                            }
                            break;
                        }

                         */
                        Thread.sleep(10);
                    }
                    if (!actionPerfomed) {
                        movementService.createFailMovementAction(parsedData);
                    }
                    mainController.updateMovements();

                }
            }
        } catch (Exception e) {
            port.closePort();
            System.out.println("Произошла ошибка с считыванием с порта!");
        }
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
        else System.out.println("Порт закрыт!");
    }

    private String readFromPort() {
        byte[] readBuffer = new byte[1024];
        int numRead = port.readBytes(readBuffer, readBuffer.length);
        if (numRead == 0) {
            return "";
        }
        return new String(readBuffer, StandardCharsets.UTF_8).trim();
    }



    public static void main(String[] args) {
        SerialPortController portController = new SerialPortController("EXIT_PORT_PATH", null);
        portController.writeToPort("1");
    }
}
