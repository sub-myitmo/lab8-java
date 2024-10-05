package server.commands;


import common.actions.GroupMask;
import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.exceptions.LoginException;
import common.exceptions.WrongCommandArgsException;
import common.models.StudyGroup;
import server.managers.CollectionManager;
import server.managers.DatabaseCollectionManager;
import server.managers.ResponseManager;

import java.util.Date;


/**
 * Команда add - добавить новый элемент в коллекцию
 *
 * @author petrovviacheslav
 */
public class Add extends Command {
    /**
     * Менеджер коллекции
     */
    private CollectionManager collectionManager;
    private DatabaseCollectionManager dbCollectionManager;

    /**
     * Конструктор класса Add
     *
     * @param collectionManager менеджер коллекции
     */
    public Add(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
        this.dbCollectionManager = databaseCollectionManager;
    }

    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg == null) throw new WrongCommandArgsException();
           // new StudyGroup(group.getName(), group.getCoordinates(), new Date(), group.getStudentsCount(), group.getExpelledStudents(), group.getTransferredStudents(), group.getSemesterEnum(), group.getGroupAdmin())
            GroupMask group = (GroupMask) otherArg;

            if (!dbCollectionManager.checkUser(user)) throw new LoginException();
            collectionManager.addElementToCollection(dbCollectionManager.insertStudyGroup(group, user));

            ResponseManager.appendln("Группа была создана/добавлена успешно");
            return true;

        } catch (WrongCommandArgsException | DatabaseHandlingException | LoginException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }
    }
}
