package server.managers;

import common.actions.Console;
import common.actions.Request;
import common.actions.Response;
import common.actions.ResponseCode;
import common.models.StudyGroup;
import server.Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {
    private DatagramPacket receivePacket;
    private Server server;
    private CommandManager commandManager;
    private ExecutorService cachedPool1 = Executors.newCachedThreadPool(); // Для многопотчной обработки полученного запроса
    private ExecutorService cachedPool2 = Executors.newCachedThreadPool(); // Для многопоточной отправки ответа
    private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());


    public ConnectionHandler(DatagramPacket receivePacket, CommandManager commandManager, Server server) {
        this.receivePacket = receivePacket;
        this.commandManager = commandManager;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Чтение данных из пакета
            byte[] receiveData = receivePacket.getData();
            ByteArrayInputStream bis = new ByteArrayInputStream(receiveData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Request request = (Request) ois.readObject();

            // Обработка запроса в отдельном потоке
            Future<Response> futureResponse = cachedPool1.submit(new HandleRequestTask(request, commandManager));

            cachedPool2.submit(() -> {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);

                    Response response =  futureResponse.get();


                    if (response.getIsGoodResponse() == ResponseCode.PEAK_SIZE) {
                        ArrayList<Stack<StudyGroup>> ara = (ArrayList<Stack<StudyGroup>>) response.getResponseObject();
                        Response count = new Response(ara.size(), ResponseCode.PEAK_SIZE);
                        oos.writeObject(count);
                        oos.flush();
                        byte[] sendDataC = bos.toByteArray();
                        System.out.println(sendDataC.length);
                        DatagramPacket sendPacketC = new DatagramPacket(sendDataC, sendDataC.length, receivePacket.getAddress(), receivePacket.getPort());
                        socket.send(sendPacketC);
                        logger.info("Пакет был отправлен клиенту!");

                        for (int i = 0; i < ara.size(); i++) {
                            try (ByteArrayOutputStream tempBos = new ByteArrayOutputStream();
                                 ObjectOutputStream tempOos = new ObjectOutputStream(tempBos)) {
                                Response temp = new Response(ara.get(i), ResponseCode.PEAK_SIZE);
                                tempOos.writeObject(temp);
                                tempOos.flush();
                                byte[] sendData = tempBos.toByteArray();
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                                socket.send(sendPacket);

                                logger.info("Пакет был отправлен клиенту!");
                            } catch (IOException e) {
                                logger.severe("Ошибка при отправке пакета клиенту: " + e);
                            }
                        }
                    }else {
                        oos.writeObject(response);
                        oos.flush();
                        byte[] sendData = bos.toByteArray();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                        socket.send(sendPacket);
                        logger.info("Пакет был отправлен клиенту!");
                    }

//                    oos.writeObject(futureResponse.get());
//                    //oos.writeObject(cachedPool1.submit(new HandleRequestTask(request, commandManager)));
//                    oos.flush();
//                    byte[] sendData = bos.toByteArray();
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
//                    socket.send(sendPacket);
//                    logger.info("Пакет был отправлен клиенту!");

                    } catch (IOException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

            });

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Ошибка ConnectionHandler");
            e.printStackTrace();
        } finally {
            // Завершение работы пулов потоков
            //cachedPool1.shutdown();
            //try {
                // Дождаться завершения всех задач
                //cachedPool1.awaitTermination(5, TimeUnit.NANOSECONDS);
                Console.println("Клиент отключен от сервера");
                logger.info("Клиент отключен от сервера");
//            } catch (InterruptedException e) {
//                logger.severe("Ошибка c закрытием потоков");
//            }
            server.releaseConnection();
        }
    }
}
