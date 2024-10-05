package client;

import client.gui.*;
import client.helpers.CommunicationControl;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


import javax.swing.*;

public class StartClient {
    private static String host = "localhost";
    public static int port;

    private Client client;

    public static void main(String[] args) {
        try {
            StartClient runClientInstance = new StartClient();
            port = Integer.parseInt(args[0]);
            runClientInstance.client = new Client(host, port);
            System.out.println("Соединение выполнено, хост = " + host);

            int delay = 1; // Задержка в секундах
            AtomicBoolean timerFinished = new AtomicBoolean(false);

            // Создаем новый поток для выполнения действия
            Thread actionThread = new Thread(() -> {
                while (!timerFinished.get()) {
                    try {
                        runClientInstance.client.receiveResponse();
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            actionThread.start(); // Запуск потока с действием

            // Запускаем таймер, который остановит действие по истечении задержки
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                timerFinished.set(true);
                System.out.println("Таймер завершился, действие остановлено.");
                executor.shutdown(); // Остановка исполнителя таймера
            }, delay, TimeUnit.SECONDS);

            runClientInstance.startGUI();
        } catch (Exception e){
            System.out.println("Возникла ошибка");
        }
    }

    public void startGUI(){
        CommunicationControl communicationControl = new CommunicationControl();
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm(communicationControl);
            loginForm.setRunClient(this);
            loginForm.setClient(client);
            loginForm.setVisible(true);
        });
    }

}