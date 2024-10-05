package server.managers;

import common.actions.GroupMask;
import common.actions.User;
import common.exceptions.DatabaseHandlingException;
import common.models.*;

import java.awt.datatransfer.FlavorEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.NavigableSet;
import java.util.Stack;
import java.util.logging.Logger;

public class DatabaseCollectionManager {
    private final String SELECT_ALL_GROUPS = "SELECT * FROM " + DatabaseConnectionManager.MODEL_TABLE;
    private final String DELETE_GROUP_BY_ID = "DELETE FROM " + DatabaseConnectionManager.MODEL_TABLE +
            " WHERE " + DatabaseConnectionManager.MODEL_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_GROUPS_BY_USER_ID = SELECT_ALL_GROUPS + " WHERE " + DatabaseConnectionManager.MODEL_TABLE_USER_ID_COLUMN + " = ?";
    private final String DELETE_GROUPS_BY_USER_ID = "DELETE FROM " + DatabaseConnectionManager.MODEL_TABLE + " WHERE " + DatabaseConnectionManager.MODEL_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_MODEL = "INSERT INTO " +
            DatabaseConnectionManager.MODEL_TABLE + " (" +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_NAME_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_COORDINATES_X_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_COORDINATES_Y_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_CREATION_DATE_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_STUDENTS_COUNT_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_EXPELLED_STUDENTS_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_TRANSFERRED_STUDENTS_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_SEMESTER_ENUM_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_NAME_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_BIRTHDAY_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_WEIGHT_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_X_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_Y_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_Z_COLUMN + ", " +
            DatabaseConnectionManager.MODEL_TABLE_USER_ID_COLUMN +
            ") VALUES (?::text, ?::integer, ?::double precision," +
            "?::date, ?, ?, ?::integer, ?::text, " +
            "?::text, ?::timestamp, ?::double precision, ?::float, ?::integer, ?::double precision, ?::int)";

    private final String UPDATE_MODEL = "UPDATE " +
            DatabaseConnectionManager.MODEL_TABLE + " SET " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_NAME_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_COORDINATES_X_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_COORDINATES_Y_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_CREATION_DATE_COLUMN + " = ?," +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_STUDENTS_COUNT_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_EXPELLED_STUDENTS_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_TRANSFERRED_STUDENTS_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_GROUP_SEMESTER_ENUM_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_NAME_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_BIRTHDAY_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_PERSON_WEIGHT_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_X_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_Y_COLUMN + " = ?, " +
            DatabaseConnectionManager.MODEL_TABLE_LOCATION_Z_COLUMN + " = ? " + " WHERE " +
            DatabaseConnectionManager.MODEL_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_STUDY_GROUP_BY_ID = SELECT_ALL_GROUPS + " WHERE " +
            DatabaseConnectionManager.MODEL_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_STUDY_GROUP_BY_ID_AND_USER_ID = SELECT_STUDY_GROUP_BY_ID + " AND " +
            DatabaseConnectionManager.MODEL_TABLE_USER_ID_COLUMN + " = ?";

    private static final Logger logger = Logger.getLogger(DatabaseCollectionManager.class.getName());
    private DatabaseConnectionManager dbConnectionManager;
    private DatabaseUserManager dbUserManager;

    public DatabaseCollectionManager(DatabaseConnectionManager dbConnection, DatabaseUserManager dbUser) {
        this.dbConnectionManager = dbConnection;
        this.dbUserManager = dbUser;
    }

    private StudyGroup createStudyGroup(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(DatabaseConnectionManager.MODEL_TABLE_ID_COLUMN);
        String name = resultSet.getString(DatabaseConnectionManager.MODEL_TABLE_GROUP_NAME_COLUMN);

        Integer coordinateX = resultSet.getInt(DatabaseConnectionManager.MODEL_TABLE_COORDINATES_X_COLUMN);
        double coordinateY = resultSet.getDouble(DatabaseConnectionManager.MODEL_TABLE_COORDINATES_Y_COLUMN);

        Date creationDate = resultSet.getDate(DatabaseConnectionManager.MODEL_TABLE_GROUP_CREATION_DATE_COLUMN);
        Long studentsCount = resultSet.getLong(DatabaseConnectionManager.MODEL_TABLE_GROUP_STUDENTS_COUNT_COLUMN);
        Long expelledStudents = resultSet.getLong(DatabaseConnectionManager.MODEL_TABLE_GROUP_EXPELLED_STUDENTS_COLUMN);
        Integer transferredStudents = resultSet.getInt(DatabaseConnectionManager.MODEL_TABLE_GROUP_TRANSFERRED_STUDENTS_COLUMN);

        Semester semesterEnum = Semester.valueOf(resultSet.getString(DatabaseConnectionManager.MODEL_TABLE_GROUP_SEMESTER_ENUM_COLUMN));

        Float locationX = resultSet.getFloat(DatabaseConnectionManager.MODEL_TABLE_LOCATION_X_COLUMN);
        Integer locationY = resultSet.getInt(DatabaseConnectionManager.MODEL_TABLE_LOCATION_Y_COLUMN);
        double locationZ = resultSet.getDouble(DatabaseConnectionManager.MODEL_TABLE_LOCATION_Z_COLUMN);

        String nameAdmin = resultSet.getString(DatabaseConnectionManager.MODEL_TABLE_PERSON_NAME_COLUMN);
        LocalDateTime birthday = resultSet.getTimestamp(DatabaseConnectionManager.MODEL_TABLE_PERSON_BIRTHDAY_COLUMN).toLocalDateTime();
        double weight = resultSet.getDouble(DatabaseConnectionManager.MODEL_TABLE_PERSON_WEIGHT_COLUMN);

        User owner = dbUserManager.getUserById(resultSet.getLong(DatabaseConnectionManager.MODEL_TABLE_USER_ID_COLUMN));

        Coordinates coordinates = new Coordinates(coordinateX, coordinateY);
        Location location = new Location(locationX, locationY, locationZ);
        Person admin = new Person(nameAdmin, birthday, weight, location);


        return new StudyGroup(
                id,
                name,
                coordinates,
                creationDate,
                studentsCount,
                expelledStudents,
                transferredStudents,
                semesterEnum,
                admin,
                owner
        );
    }


    public void deleteStudyGroupById(long groupId) throws DatabaseHandlingException {
        PreparedStatement prepStat = null;
        try {
            prepStat = dbConnectionManager.getPrepStat(DELETE_GROUP_BY_ID, false);
            prepStat.setLong(1, groupId);
            logger.info("Выполнен запрос DELETE_GROUP_BY_ID.");
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при выполнении запроса DELETE_GROUP_BY_ID!");
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }

    public StudyGroup insertStudyGroup(GroupMask groupMask, User user) throws DatabaseHandlingException {
        StudyGroup studyGroup;
        PreparedStatement prepStat = null;
        try {
            dbConnectionManager.setCommitMode();
            dbConnectionManager.setSavepoint();

            Date creationTime = new Date();
            prepStat = dbConnectionManager.getPrepStat(INSERT_MODEL, true);

            prepStat.setString(1, groupMask.getName());
            prepStat.setInt(2, groupMask.getCoordinates().getX());
            prepStat.setDouble(3, groupMask.getCoordinates().getY());
            prepStat.setDate(4, new java.sql.Date(creationTime.getTime()));
            prepStat.setLong(5, groupMask.getStudentsCount());
            prepStat.setLong(6, groupMask.getExpelledStudents());
            prepStat.setInt(7, groupMask.getTransferredStudents());
            prepStat.setString(8, groupMask.getSemesterEnum().toString());
            prepStat.setString(9, groupMask.getGroupAdmin().getName());
            prepStat.setTimestamp(10, Timestamp.valueOf(groupMask.getGroupAdmin().getBirthday()));
            prepStat.setDouble(11, groupMask.getGroupAdmin().getWeight());
            prepStat.setFloat(12, groupMask.getGroupAdmin().getLocation().getX());
            prepStat.setInt(13, groupMask.getGroupAdmin().getLocation().getY());
            prepStat.setDouble(14, groupMask.getGroupAdmin().getLocation().getZ());
            prepStat.setInt(15, dbUserManager.getUserIdByUsername(user));

            logger.info(prepStat.getGeneratedKeys().toString());

            if (prepStat.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedKeys = prepStat.getGeneratedKeys();
            int groupId;
            if (generatedKeys.next()) {
                groupId = generatedKeys.getInt(1);
            } else throw new SQLException();
            logger.info("Выполнен запрос INSERT_MODEL (GROUP).");

            studyGroup = new StudyGroup(
                    groupId,
                    groupMask.getName(),
                    groupMask.getCoordinates(),
                    creationTime,
                    groupMask.getStudentsCount(),
                    groupMask.getExpelledStudents(),
                    groupMask.getTransferredStudents(),
                    groupMask.getSemesterEnum(),
                    groupMask.getGroupAdmin(),
                    user
            );

            dbConnectionManager.commit();
            return studyGroup;
        } catch (SQLException exception) {
            exception.printStackTrace();
            logger.severe("Произошла ошибка при выполнении группы запросов на добавление нового объекта!");
            dbConnectionManager.rollback();
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
            dbConnectionManager.setNormalMode();
        }
    }

    public boolean checkUser(User user) throws DatabaseHandlingException{
        return dbUserManager.checkUsernameAndPassword(user);
    }
    public void updateStudyGroupById(long groupId,  GroupMask groupMask) throws DatabaseHandlingException {
        PreparedStatement prepStat = null;
        try {
            dbConnectionManager.setCommitMode();
            dbConnectionManager.setSavepoint();

            Date creationTime = new Date();
            prepStat = dbConnectionManager.getPrepStat(UPDATE_MODEL, false);

            prepStat.setString(1, groupMask.getName());
            prepStat.setInt(2, groupMask.getCoordinates().getX());
            prepStat.setDouble(3, groupMask.getCoordinates().getY());
            prepStat.setDate(4, new java.sql.Date(creationTime.getTime()));
            prepStat.setLong(5, groupMask.getStudentsCount());
            prepStat.setLong(6, groupMask.getExpelledStudents());
            prepStat.setInt(7, groupMask.getTransferredStudents());
            prepStat.setString(8, groupMask.getSemesterEnum().toString());
            prepStat.setString(9, groupMask.getGroupAdmin().getName());
            prepStat.setTimestamp(10, Timestamp.valueOf(groupMask.getGroupAdmin().getBirthday()));
            prepStat.setDouble(11, groupMask.getGroupAdmin().getWeight());
            prepStat.setFloat(12, groupMask.getGroupAdmin().getLocation().getX());
            prepStat.setInt(13, groupMask.getGroupAdmin().getLocation().getY());
            prepStat.setDouble(14, groupMask.getGroupAdmin().getLocation().getZ());
            prepStat.setLong(15, groupId);

            if (prepStat.executeUpdate() == 0) throw new SQLException();

            logger.info("Выполнен запрос UPDATE_MODEL (GROUP).");
            dbConnectionManager.commit();
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при выполнении группы запросов на обновление объекта!");
            dbConnectionManager.rollback();
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
            dbConnectionManager.setNormalMode();
        }

    }

    public boolean checkStudyGroupUserId(StudyGroup studyGroup, User user) throws DatabaseHandlingException {
        PreparedStatement prepStat = null;
        try {
            prepStat = dbConnectionManager.getPrepStat(SELECT_STUDY_GROUP_BY_ID_AND_USER_ID, false);
            prepStat.setLong(1, studyGroup.getId());
            prepStat.setLong(2, dbUserManager.getUserIdByUsername(user));
            ResultSet resultSet = prepStat.executeQuery();
            logger.info("Выполнен запрос SELECT_STUDY_GROUP_BY_ID_AND_USER_ID.");
            return resultSet.next();
        } catch (SQLException exception) {
            logger.severe("Ошибка при выполнении запроса SELECT_Organization_BY_ID_AND_USER_ID!");
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }

    public Stack<StudyGroup> getCollection() throws DatabaseHandlingException {
        Stack<StudyGroup> groups = new Stack<>();
        PreparedStatement prepStat = null;
        try {
            prepStat = dbConnectionManager.getPrepStat(SELECT_ALL_GROUPS, false);
            ResultSet resultSet = prepStat.executeQuery();
            logger.info("Получены группы из таблицы:\n" + resultSet);
            while (resultSet.next()) {
                groups.add(createStudyGroup(resultSet));
            }
        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
        return groups;
    }

//    public Stack<StudyGroup> selectGroupsByIdUser(User user) throws DatabaseHandlingException {
//        Stack<StudyGroup> delGroups = new Stack<StudyGroup>();
//        PreparedStatement prepStat = null;
//        try {
//            int userId = dbUserManager.getUserIdByUsername(user);
//            prepStat = dbConnectionManager.getPrepStat(SELECT_GROUPS_BY_USER_ID, false);
//            prepStat.setLong(1, userId);
//            ResultSet resultSet = prepStat.executeQuery();
//
//            while (resultSet.next()) {
//                delGroups.add(createStudyGroup(resultSet));
//            }
//            return delGroups;
//
//        } catch (SQLException exception) {
//            throw new DatabaseHandlingException();
//        } finally {
//            dbConnectionManager.closePrepStat(prepStat);
//        }
//    }

    public void deleteGroupsByIdUser(User user) throws DatabaseHandlingException {

        PreparedStatement prepStat = null;
        try {
            int userId = dbUserManager.getUserIdByUsername(user);
            prepStat = dbConnectionManager.getPrepStat(DELETE_GROUPS_BY_USER_ID, false);
            prepStat.setLong(1, userId);
            prepStat.executeUpdate();

        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }

    public void clearCollection() throws DatabaseHandlingException {
        Stack<StudyGroup> groups = getCollection();
        for (StudyGroup group : groups) {
            deleteStudyGroupById(group.getId());
        }
    }
}
