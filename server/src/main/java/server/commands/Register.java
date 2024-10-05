package server.commands;

import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.exceptions.UserAlreadyExistsException;
import common.exceptions.WrongCommandArgsException;
import server.managers.DatabaseUserManager;
import server.managers.ResponseManager;

public class Register extends Command {
    private DatabaseUserManager databaseUserManager;

    public Register(DatabaseUserManager databaseUserManager) {
        super("register", "внутренняя команда");
        this.databaseUserManager = databaseUserManager;
    }

    @Override
    public boolean execute(String stringArg, Object otherArg, User user) {
        try {
            if (!stringArg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();
            if (!databaseUserManager.insertUser(user)) throw new UserAlreadyExistsException();

            ResponseManager.appendln("Пользователь " + user.getUsername() + " успешно зарегистрирован.");

            return true;
        } catch (WrongCommandArgsException | DatabaseHandlingException | UserAlreadyExistsException e) {
            ResponseManager.appendln(e.toString());
        }
        return false;
    }
}
