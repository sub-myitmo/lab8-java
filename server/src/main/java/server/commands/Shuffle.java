package server.commands;

import common.actions.User;
import server.managers.CollectionManager;
import common.actions.Console;
import common.exceptions.WrongCommandArgsException;
import server.managers.ResponseManager;

/**
 * Команда shuffle - перемешать элементы коллекции в случайном порядке
 *
 * @author petrovviacheslav
 */
public class Shuffle extends Command {
    /**
     * Менеджер коллекции
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса Shuffle
     *
     * @param collectionManager менеджер коллекции
     */
    public Shuffle(CollectionManager collectionManager) {
        super("shuffle", "перемешать элементы коллекции в случайном порядке");
        this.collectionManager = collectionManager;
    }


    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();
            collectionManager.shuffle();
            ResponseManager.appendln("Коллекция была перемешана");
            return true;

        } catch (WrongCommandArgsException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }
    }
}
