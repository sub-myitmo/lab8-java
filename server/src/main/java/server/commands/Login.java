package server.commands;

import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.exceptions.LoginException;
import common.exceptions.WrongCommandArgsException;
import server.managers.DatabaseUserManager;
import server.managers.ResponseManager;

public class Login extends Command {
    private DatabaseUserManager databaseUserManager;

    public Login(DatabaseUserManager databaseUserManager) {
        super("login", "внутренняя команда для авторизации");
        this.databaseUserManager = databaseUserManager;
    }

    @Override
    public boolean execute(String stringArg, Object otherArg, User user) {
        try {
            if (!stringArg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();
            if (!databaseUserManager.checkUsernameAndPassword(user)) throw new LoginException();

            ResponseManager.appendln("Пользователь " + user.getUsername() + " успешно авторизован.");

            return true;
        } catch (WrongCommandArgsException | DatabaseHandlingException | LoginException e) {
            ResponseManager.appendln(e.toString());
        }
        return false;
    }
}
