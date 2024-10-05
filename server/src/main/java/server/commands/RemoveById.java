package server.commands;

import common.actions.User;
import common.exceptions.*;
import common.models.StudyGroup;
import server.managers.CollectionManager;
import common.actions.Console;
import server.managers.DatabaseCollectionManager;
import server.managers.ResponseManager;

import javax.xml.crypto.Data;

/**
 * Команда remove_by_id - удалить элемент из коллекции по его id
 *
 * @author petrovviacheslav
 */
public class RemoveById extends Command {
    /**
     * Менеджер коллекции
     */
    private CollectionManager collectionManager;
    private DatabaseCollectionManager dbCollectionManager;

    /**
     * Конструктор класса RemoveById
     *
     * @param collectionManager менеджер коллекции
     */
    public RemoveById(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_by_id", "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
        this.dbCollectionManager = databaseCollectionManager;
    }

    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();

            long id = Long.parseLong(arg.trim());
            StudyGroup possibleGroup = collectionManager.getById(id);
            if (possibleGroup == null) throw new NoExistStudyGroupException();

            if (!dbCollectionManager.checkUser(user)) throw new LoginException();
            if (!possibleGroup.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!dbCollectionManager.checkStudyGroupUserId(possibleGroup, user)) throw new ManualDatabaseEditException();

            collectionManager.removeGroup(possibleGroup);

            ResponseManager.appendln("Группа была успешно удалена");
            return true;

        } catch (WrongCommandArgsException | NoExistStudyGroupException | PermissionDeniedException | ManualDatabaseEditException |
                 DatabaseHandlingException | LoginException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }

    }
}
