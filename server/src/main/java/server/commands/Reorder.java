package server.commands;

import common.actions.User;
import server.managers.CollectionManager;
import common.actions.Console;
import common.exceptions.WrongCommandArgsException;
import server.managers.ResponseManager;

/**
 * Команда reorder - отсортировать коллекцию в порядке, обратном нынешнему
 *
 * @author petrovviacheslav
 */
public class Reorder extends Command {
    /**
     * Менеджер коллекции
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса Reorder
     *
     * @param collectionManager менеджер коллекции
     */
    public Reorder(CollectionManager collectionManager) {
        super("reorder", "отсортировать коллекцию в порядке, обратном нынешнему");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();
            collectionManager.reorder();
            ResponseManager.appendln("Коллекция отсортирована в обратном порядке");
            return true;

        } catch (WrongCommandArgsException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }
    }
}
