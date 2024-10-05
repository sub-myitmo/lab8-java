package server.commands;

import common.actions.User;
import server.managers.CollectionManager;
import common.actions.Console;
import common.exceptions.WrongCommandArgsException;
import common.models.StudyGroup;
import server.managers.ResponseManager;

import java.util.*;

/**
 * Команда print_field_ascending_students_count - вывести значения поля studentsCount всех элементов в порядке возрастания
 *
 * @author petrovviacheslav
 */
public class PrintFieldAscendingStudentsCount extends Command {
    /**
     * Менеджер коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса Reorder
     *
     * @param collectionManager менеджер коллекции
     */
    public PrintFieldAscendingStudentsCount(CollectionManager collectionManager) {
        super("print_field_ascending_students_count", "вывести значения поля studentsCount всех элементов в порядке возрастания");
        this.collectionManager = collectionManager;
    }


    @Override
    public boolean execute(String arg, Object otherArg, User user) {
        try {
            if (!arg.isEmpty() || otherArg != null) throw new WrongCommandArgsException();

            if (collectionManager.getStackCollection().isEmpty()) {
                ResponseManager.appendln("Коллекция пуста!");
            } else {
                ResponseManager.appendln("Поля studentsCount: ");
                collectionManager.getStackCollection().stream()
                        .map(StudyGroup::getStudentsCount)
                        .sorted()
                        .forEach(count -> ResponseManager.append("- " + count));

            }
            return true;
        } catch (WrongCommandArgsException e) {
            ResponseManager.appendln(e.toString());
            return false;
        }

    }
}
