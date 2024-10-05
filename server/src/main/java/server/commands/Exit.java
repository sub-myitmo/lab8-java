package server.commands;

import common.actions.User;
import common.exceptions.WrongCommandArgsException;
import server.managers.ResponseManager;

public class Exit extends Command {
    public Exit(){
        super("exit", "завершение работы программы");
    }

    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongCommandArgsException();
            ResponseManager.appendln("Завершение работы программы");
            System.exit(0);
            return true;
        } catch (WrongCommandArgsException e){
            ResponseManager.appendln(e.toString());
        }
        return false;
    }
}
