package common.models;

import java.io.Serializable;

/**
 * Перечисление содержащее номера семестров
 *
 * @author petrovviacheslav
 */
public enum Semester implements Serializable {
    /**
     * первый
     */
    FIRST,
    /**
     * третий
     */
    THIRD,
    /**
     * четвертый
     */
    FOURTH,
    /**
     * восьмой
     */
    EIGHTH;

    /**
     * Получить все константы enum'а
     *
     * @return строка со всеми элементами enum'а через запятую
     */
    public static String[] getNames() {
        return new String[]{"FIRST", "THIRD","FOURTH","EIGHTH"};
    }
}