package common.actions;

import common.models.Coordinates;
import common.models.Person;
import common.models.Semester;

import java.io.Serializable;

public class GroupMask implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Long studentsCount; //Значение поля должно быть больше 0, Поле не может быть null
    private Long expelledStudents; //Значение поля должно быть больше 0, Поле не может быть null
    private Integer transferredStudents; //Значение поля должно быть больше 0, Поле не может быть null
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле не может быть null

    /**
     * Конструктор класса StudyGroup
     *
     * @param name                имя
     * @param coordinates         координаты
     * @param studentsCount       количество студентов
     * @param expelledStudents    количество отчисленных студентов
     * @param transferredStudents количество переведённых студентов
     * @param semesterEnum        номер семестра в качестве enum'а
     * @param groupAdmin          админ группы
     */
    public GroupMask(String name, Coordinates coordinates, Long studentsCount, Long expelledStudents, Integer transferredStudents, Semester semesterEnum, Person groupAdmin) {
        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.expelledStudents = expelledStudents;
        this.transferredStudents = transferredStudents;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
    }




    /**
     * Получить имя
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Получить координаты
     *
     * @return координаты
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }


    /**
     * Получить количество студентов
     *
     * @return количество студентов
     */
    public Long getStudentsCount() {
        return studentsCount;
    }

    /**
     * Получить количество отчисленных студентов
     *
     * @return количество отчисленных студентов
     */
    public Long getExpelledStudents() {
        return expelledStudents;
    }

    /**
     * Получить количество переведённых студентов
     *
     * @return количество переведённых студентов
     */
    public Integer getTransferredStudents() {
        return transferredStudents;
    }

    /**
     * Получить номер семестра в качестве enum'а
     *
     * @return номер семестра в качестве enum'а
     */
    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    /**
     * Получить админа группы
     *
     * @return админ группы
     */
    public Person getGroupAdmin() {
        return groupAdmin;
    }
}
