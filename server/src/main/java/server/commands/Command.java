package server.commands;


import common.actions.User;

/**
 * Абстрактный класс команд
 *
 * @author petrovviacheslav
 */
public abstract class Command {

    /**
     * Название команды
     */
    private final String name;
    /**
     * Описание команды
     */
    private final String description;

    /**
     * Конструктор класса Command
     *
     * @param name        название команды
     * @param description описание команды
     */
    protected Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Получить название команды
     *
     * @return название команды
     */
    public String getName() {
        return name;
    }

    /**
     * Получить описание команды
     *
     * @return описание команды
     */
    public String getDescription() {
        return description;
    }

    /**
     * Вызов команды
     *
     * @return true - команда выполнена успешно, иначе false
     */
    public abstract boolean execute(String arg, Object otherArg, User user);

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + "'" +
                ", description='" + description + "'" +
                '}';
    }
}