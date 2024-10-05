package common.exceptions;

/**
 * Выбрасывается, если обращаются к несуществующей коллекции
 *
 * @author petrovviacheslav
 */
public class NoExistStudyGroupException extends Exception {

    @Override
    public String toString() {
        return "Группы с таким id не существует!";
    }
}
