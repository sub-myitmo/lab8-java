package server;


import common.actions.Console;

import server.managers.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final Logger logger = Logger.getLogger( Server.class.getName() );

    private int port;
    private DatagramSocket serverSocket;
    private CommandManager commandManager;

    private Semaphore semaphore;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public Server(int port, int maxClients, CommandManager commandManager) throws IOException {
        this.port = port;
        this.semaphore = new Semaphore(maxClients);
        this.commandManager = commandManager;

    }

    public void connection() {
        try {
            serverSocket = new DatagramSocket(port);
            logger.info("Сервер запущен.");

            while (true){
                byte[] receiveData = new byte[4096];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                logger.info("Ожидание пакета от клиента...");
                serverSocket.receive(receivePacket);
                logger.info("Пакет был принят от клиента");

                semaphore.acquire();
                cachedThreadPool.submit(new ConnectionHandler(receivePacket, commandManager, this));
            }
        } catch (IOException exception) {
            Console.printerror("Произошла ошибка при попытке использовать порт '" + port + "'!");
            logger.severe("Произошла ошибка при попытке использовать порт '" + port + "'!");
        } catch (InterruptedException exception) {
            Console.printerror("Произошла ошибка при получении разрешения на новое соединение!");
            logger.severe("Произошла ошибка при получении разрешения на новое соединение!");
        } finally {

            try {
                cachedThreadPool.awaitTermination(5, TimeUnit.NANOSECONDS);
                Console.println("Работа сервера завершена");
                logger.info("Работа сервера завершена");
            } catch (InterruptedException e) {
                logger.severe("InterruptedException");
            }
        }

    }

    public void releaseConnection() {
        semaphore.release();
        logger.info("Разрыв соединения с клиентом из-за временного бездействия");
    }


}
