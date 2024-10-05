package server.commands;

import common.actions.GroupMask;
import common.actions.User;
import server.managers.CollectionManager;
import common.exceptions.*;
import server.managers.DatabaseCollectionManager;
import server.managers.ResponseManager;
import common.models.StudyGroup;

import java.util.Date;

/**
 * Команда update id - обновить значение элемента коллекции, id которого равен заданному
 *
 * @author petrovviacheslav
 */
public class UpdateId extends Command {
    /**
     * Менеджер коллекции
     */
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager dbCollectionManager;


    /**
     * Конструктор класса Show
     *
     * @param collectionManager менеджер коллекции
     */
    public UpdateId(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("update_by_id {element}", "обновить значение элемента коллекции, id которого равен заданному");
        this.collectionManager = collectionManager;
        this.dbCollectionManager = databaseCollectionManager;
    }


    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (arg.isEmpty() || otherArg == null) throw new WrongCommandArgsException();

            GroupMask newGroup = (GroupMask) otherArg;

            long id = Long.parseLong(arg.trim());
            StudyGroup oldGroup = collectionManager.getById(id);
            if (oldGroup == null) throw new NoExistStudyGroupException();
            if (!dbCollectionManager.checkUser(user)) throw new LoginException();
            if (!oldGroup.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!dbCollectionManager.checkStudyGroupUserId(oldGroup, user)) throw new ManualDatabaseEditException();

            dbCollectionManager.updateStudyGroupById(id, newGroup);
            collectionManager.updateElement(oldGroup, new StudyGroup(id, newGroup.getName(), newGroup.getCoordinates(), new Date(), newGroup.getStudentsCount(), newGroup.getExpelledStudents(), newGroup.getTransferredStudents(), newGroup.getSemesterEnum(), newGroup.getGroupAdmin(), user));
            ResponseManager.appendln("Группа была заменена успешно");
            return true;

        } catch (WrongCommandArgsException | NoExistStudyGroupException | PermissionDeniedException | ManualDatabaseEditException |
                 DatabaseHandlingException | LoginException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }
    }
}
