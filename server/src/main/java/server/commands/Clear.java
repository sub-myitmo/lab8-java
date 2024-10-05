package server.commands;


import common.actions.Console;
import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.exceptions.ManualDatabaseEditException;
import common.exceptions.PermissionDeniedException;
import common.exceptions.WrongCommandArgsException;
import common.models.StudyGroup;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;
import server.managers.ResponseManager;

import java.util.Collections;
import java.util.Stack;

/**
 * Команда clear - очищает коллекцию
 *
 * @author petrovviacheslav
 */
public class Clear extends Command {

    /**
     * Менеджер коллекции
     */
    private CollectionManager collectionManager;
    private DatabaseCollectionManager dbCollectionManager;

    /**
     * Конструктор класса Clear
     *
     * @param collectionManager менеджер коллекции
     */
    public Clear(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("clear", "очищает коллекцию");
        this.collectionManager = collectionManager;
        this.dbCollectionManager = databaseCollectionManager;
    }

    /**
     * Очищает коллекцию
     *
     * @return true - команда выполнена успешно, иначе false
     */
    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();


            dbCollectionManager.deleteGroupsByIdUser(user);
            collectionManager.setStackCollection(dbCollectionManager.getCollection());

            ResponseManager.appendln("Ваши группы успешно удалены");
            return true;
        } catch (WrongCommandArgsException | DatabaseHandlingException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }

    }

}