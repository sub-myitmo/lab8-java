package server.commands;

import common.actions.User;
import common.exceptions.*;
import common.models.StudyGroup;
import server.managers.CollectionManager;
import common.actions.Console;
import server.managers.DatabaseCollectionManager;
import server.managers.ResponseManager;

/**
 * Команда remove_first - удалить первый элемент из коллекции
 *
 * @author petrovviacheslav
 */
public class RemoveFirst extends Command {
    /**
     * Менеджер коллекции
     */
    private CollectionManager collectionManager;
    private DatabaseCollectionManager dbCollectionManager;

    /**
     * Конструктор класса RemoveFirst
     *
     * @param collectionManager менеджер коллекции
     */
    public RemoveFirst(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_first", "удалить первый элемент из коллекции");
        this.collectionManager = collectionManager;
        this.dbCollectionManager = databaseCollectionManager;
    }

    /**
     * Удаляет первый элемент из коллекции
     *
     * @return true - команда выполнена успешно, иначе false
     */
    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();


            StudyGroup possibleGroup = collectionManager.getFirstElementCollection();
            if (possibleGroup == null) throw new NoExistStudyGroupException();

            if (!dbCollectionManager.checkUser(user)) throw new LoginException();
            if (!possibleGroup.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!dbCollectionManager.checkStudyGroupUserId(possibleGroup, user)) throw new ManualDatabaseEditException();

            collectionManager.removeGroup(possibleGroup);

            ResponseManager.appendln("Первая группа была успешно удалена");
            return true;
        } catch (WrongCommandArgsException | NoExistStudyGroupException | PermissionDeniedException | ManualDatabaseEditException |
                 DatabaseHandlingException | LoginException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }
    }
}
