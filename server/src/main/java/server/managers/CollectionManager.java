package server.managers;

import common.actions.Console;
import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.models.StudyGroup;
import server.StartServer;
//import exceptions.NotUniqueIdException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
//import java.util.TreeMap;


/**
 * Класс для работы с коллекцией
 * Методы: добавление, удаление, сортировка, очистка, поиск по id, ...
 */
public class CollectionManager {
    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());

    private Stack<StudyGroup> stackCollection;
    private Date creationDate;
    private DatabaseCollectionManager dbCollectionManager;

    /**
     * Конструктор класса CollectionManager
     */
    public CollectionManager(DatabaseCollectionManager databaseCollectionManager) {
        stackCollection = new Stack<>();

        this.dbCollectionManager = databaseCollectionManager;
        loadCollectionFromDB();
    }

    /**
     * Получает время создания группы
     *
     * @return время создания
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Получает коллекцию групп
     *
     * @return stackCollection коллекция групп
     */
    public Stack<StudyGroup> getStackCollection() {
        return stackCollection;
    }
    public void setStackCollection(Stack<StudyGroup> stackCollection) {
        this.stackCollection = stackCollection;
    }


    /**
     * Выводит информацию о коллекции
     */
    public String infoAboutCollection() {
        String str = "";
        for (String s : Arrays.asList(("Тип данных: " + stackCollection.getClass().getName() + "\n"), ("Дата инициализации: " + creationDate + "\n"), ("Количество элементов: " + getSizeCollection()))) {
            str += s;
        }
        return str;
    }



    /**
     * Возвращает группу по id
     *
     * @param id id элемента коллекции
     * @return элемент коллекции
     */
    public StudyGroup getById(long id) {
        for (StudyGroup studyGroup : stackCollection) {
            if (Objects.equals(studyGroup.getId(), id)) return studyGroup;
        }
        return null;
    }

    /**
     * Получить первый элемент коллекции
     */
    public StudyGroup getFirstElementCollection() {
        return stackCollection.firstElement();
    }

    /**
     * Удаляет группу из коллекции
     *
     * @param elem удаляемая группа
     */
    public void removeGroup(StudyGroup elem) {
        stackCollection.remove(elem);
    }

    /**
     * Сортирует коллекцию в обратном порядке
     */
    public void reorder() {
        Collections.reverse(stackCollection);
    }

    /**
     * Возвращает размер коллекции
     *
     * @return размер коллекции
     */
    public int getSizeCollection() {
        return stackCollection.size();
    }

    /**
     * Очистка коллекции
     */
    public void clearCollection() {
        stackCollection.clear();
    }

    /**
     * Получить все элементы коллекции
     */
    public String getAllElements(User user) {
        String string;
        if (stackCollection.isEmpty()) {
            string = "Коллекция пустая";
        } else {
            string = "Элементы коллекции: " + stackCollection.size() + "\n";
            ArrayList<Long> userGroups = new ArrayList<Long>();
            for (StudyGroup studyGroup : stackCollection){
                if (Objects.equals(studyGroup.getOwner().getUsername(), user.getUsername())) userGroups.add(studyGroup.getId());
                string += studyGroup + "\n";
            }
            if (userGroups.isEmpty()) string += "Вы не владеете ни одной группой\n";
            else string += "Вы владеете группами с id:" + userGroups.toString().replaceAll("\\[", "").replaceAll("]", "");
            //string += stackCollection.toString().replaceAll(", StudyGroup", ",\nStudyGroup").replaceAll("\\[", "").replaceAll("]", "") + "\n";
        }
        return string;
    }

    /**
     * Метод перемешивает элементы коллекции групп в случайном порядке.
     */
    public void shuffle() {
        Collections.shuffle(stackCollection);
        Console.println(stackCollection.toString());
    }


    /**
     * Метод добавляет новую группу в коллекцию
     *
     * @param studyGroup добавляемая группа
     */
    public void addElementToCollection(StudyGroup studyGroup) {
        stackCollection.add(studyGroup);
    }

    /**
     * Метод обновляет элемент коллекции
     *
     * @param oldStudyGroup старая группа, которая будет заменена
     * @param newStudyGroup новая группа
     */
    public void updateElement(StudyGroup oldStudyGroup, StudyGroup newStudyGroup) {
        oldStudyGroup.update(newStudyGroup);
    }

    public void loadCollectionFromDB() {
        try {
            stackCollection = dbCollectionManager.getCollection();
            creationDate = new Date();
            Console.println("Коллекция успешно загружена.");
            logger.info("Коллекция успешно загружена.");
        } catch (DatabaseHandlingException e) {
            Console.printerror(e.toString());
            Console.printerror("Коллекция не может быть загружена!");
            logger.severe("Коллекция не может быть загружена!");
        }
    }

}