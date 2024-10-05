package server;

import common.actions.Console;
import common.exceptions.NotWithinEstablishedLimitsException;
import common.exceptions.WrongCommandArgsException;
import server.commands.*;
import server.managers.*;
import common.models.StudyGroup;


import java.io.File;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Главный класс приложения, он запускается в первую очередь
 *
 * @author petrovviacheslav
 */
public class StartServer {

    private static final Logger logger = Logger.getLogger(StartServer.class.getName());
    private static int port;

    /**
     * Запуск приложения
     *
     * @param args аргументы запуска
     */
    public static void main(String[] args) {

        try {
            if (args.length != 3) throw new WrongCommandArgsException();
            if (Integer.parseInt(args[0]) < 0) throw new NotWithinEstablishedLimitsException();
            String dbHost = args[1];
            String dbPassword = args[2];
            //String dbAddress = "jdbc:postgresql://" + dbHost + ":5432/studs";

            String dbAddress = "jdbc:postgresql://localhost:5432/studs";

            //String dbAddress = "jdbc:postgresql://pg:5432/studs";

            DatabaseConnectionManager dbConnection = new DatabaseConnectionManager(dbAddress, dbHost, dbPassword);
            DatabaseUserManager dbUserManager = new DatabaseUserManager(dbConnection);
            DatabaseCollectionManager dbCollectionManager = new DatabaseCollectionManager(dbConnection, dbUserManager);

            CollectionManager collectionManager = new CollectionManager(dbCollectionManager);
            CommandManager commandManager = new CommandManager(
                    new Exit(), new SendNewStack(collectionManager), new Info(collectionManager), new Clear(collectionManager, dbCollectionManager), new RemoveById(collectionManager, dbCollectionManager),
                    new RemoveFirst(collectionManager, dbCollectionManager), new Shuffle(collectionManager),
                    new Reorder(collectionManager), new PrintAscending(collectionManager),
                    new PrintFieldAscendingStudentsCount(collectionManager), new ExecuteScript(),
                    new Add(collectionManager, dbCollectionManager), new UpdateId(collectionManager, dbCollectionManager),
                    new Login(dbUserManager), new Register(dbUserManager)
            );

            Server server = new Server(Integer.parseInt(args[0]), 1000, commandManager);

            server.connection();

            dbConnection.closeConnection();
        } catch (NotWithinEstablishedLimitsException e) {
            Console.printerror("Порт < 0" + e);
            logger.severe("Порт < 0" + e);
        } catch (WrongCommandArgsException exception) {
            String jarFileName = new File(StartServer.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            Console.println("Запускать надо так: 'java -jar " + jarFileName + " <port> <db_host> <db_password>'");
        } catch (NumberFormatException exception) {
            Console.printerror("Порт должен быть представлен числом!");
            logger.severe("Порт должен быть представлен числом!");
        } catch (Exception e) {
            e.printStackTrace();
            Console.printerror("Возникла неожиданная ошибка!");
            logger.severe("Возникла неожиданная ошибка!");
        }


    }

}