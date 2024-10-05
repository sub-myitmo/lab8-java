package server.managers;

import common.actions.Console;

import java.sql.*;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger logger = Logger.getLogger( DatabaseConnectionManager.class.getName() );

    public static final String MODEL_TABLE = "model";
    public static final String USER_TABLE = "my_user";

    public static final String MODEL_TABLE_ID_COLUMN = "id";
    public static final String MODEL_TABLE_GROUP_NAME_COLUMN = "name";

    public static final String MODEL_TABLE_COORDINATES_X_COLUMN = "coor_x";
    public static final String MODEL_TABLE_COORDINATES_Y_COLUMN = "coor_y";

    public static final String MODEL_TABLE_GROUP_CREATION_DATE_COLUMN = "creation_date";
    public static final String MODEL_TABLE_GROUP_STUDENTS_COUNT_COLUMN = "students_count";
    public static final String MODEL_TABLE_GROUP_EXPELLED_STUDENTS_COLUMN = "expelled_students";
    public static final String MODEL_TABLE_GROUP_TRANSFERRED_STUDENTS_COLUMN = "transferred_students";
    public static final String MODEL_TABLE_GROUP_SEMESTER_ENUM_COLUMN = "semester";

    public static final String MODEL_TABLE_PERSON_NAME_COLUMN = "admin_name";
    public static final String MODEL_TABLE_PERSON_BIRTHDAY_COLUMN = "birthday";
    public static final String MODEL_TABLE_PERSON_WEIGHT_COLUMN = "weight";
    public static final String MODEL_TABLE_LOCATION_X_COLUMN = "loc_x";
    public static final String MODEL_TABLE_LOCATION_Y_COLUMN = "loc_y";
    public static final String MODEL_TABLE_LOCATION_Z_COLUMN = "loc_z";
    public static final String MODEL_TABLE_USER_ID_COLUMN = "user_id";

//    public static final String COORDINATES_TABLE = "coordinates";
//    public static final String LOCATION_TABLE = "location";
//    public static final String PERSON_TABLE = "person";

    //Worker columns

//    public static final String GROUP_TABLE_ID_COLUMN = "id";
//    public static final String GROUP_TABLE_NAME_COLUMN = "name";
//    public static final String GROUP_TABLE_COORDINATES_ID_COLUMN = "coordinates_id";
//    public static final String GROUP_TABLE_CREATION_DATE_COLUMN = "creation_date";
//    public static final String GROUP_TABLE_STUDENT_COUNT_COLUMN = "student_count";
//    public static final String GROUP_TABLE_EXPELLED_STUDENTS_COLUMN = "expelled_students";
//    public static final String GROUP_TABLE_TRANSFERRED_STUDENTS_COLUMN = "transferred_students";
//    public static final String GROUP_TABLE_SEMESTER_ENUM_COLUMN = "semester";
//    public static final String GROUP_TABLE_ADMIN_ID_COLUMN = "person_id";
//    public static final String WORKER_TABLE_USER_ID_COLUMN = "user_id";

    //User columns

    public static final String USER_TABLE_ID_COLUMN = "id";
    public static final String USER_TABLE_USERNAME_COLUMN = "username";
    public static final String USER_TABLE_PASSWORD_COLUMN = "password";

    //Coordinates columns

//    public static final String COORDINATES_TABLE_ID_COLUMN = "id";
//    public static final String COORDINATES_TABLE_X_COLUMN = "x";
//    public static final String COORDINATES_TABLE_Y_COLUMN = "y";

    //Person columns
//    public static final String PERSON_TABLE_ID_COLUMN = "id";
//    public static final String PERSON_TABLE_NAME_COLUMN = "name";
//    public static final String PERSON_TABLE_BIRTHDAY_COLUMN = "birthday";
//    public static final String PERSON_TABLE_WEIGHT_COLUMN = "weight";
//    public static final String PERSON_TABLE_LOCATION_ID_COLUMN = "location_id";

    //Location columns

//    public static final String LOCATION_TABLE_ID_COLUMN = "id";
//    public static final String LOCATION_TABLE_X_COLUMN = "x";
//    public static final String LOCATION_TABLE_Y_COLUMN = "y";
//    public static final String LOCATION_TABLE_Z_COLUMN = "z";

    private String url;
    private String host;
    private String password;
    private Connection connection;
    public DatabaseConnectionManager(String url, String host, String password){
        this.url = url;
        this.host = host;
        this.password = password;
        connectToDB();
    }

    private void connectToDB() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, host, password);
            Console.println("Соединение с базой данных установлено.");
            logger.info("Соединение с базой данных установлено.");
        } catch (SQLException e) {
            Console.printerror("Ошибка при подключении к базе данных!");
            logger.severe("Ошибка при подключении к базе данных!");
        } catch (ClassNotFoundException e) {
            Console.printerror("Драйвер управления базой данных не найден!");
            logger.severe("Драйвер управления базой данных не найден!");
        }
    }

    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
            Console.println("Соединение с базой данных разорвано.");
            logger.info("Соединение с базой данных разорвано.");
        } catch (SQLException exception) {
            Console.printerror("Произошла ошибка при разрыве соединения с базой данных!");
            logger.severe("Произошла ошибка при разрыве соединения с базой данных!");
        }
    }
    public void setSavepoint() {
        try {
            if (connection == null) throw new SQLException();
            connection.setSavepoint();
        } catch (SQLException exception) {
            logger.severe("Ошибка при сохранении состояния базы данных!");
        }
    }

    public void setCommitMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при установлении режима транзакции базы данных!");
        }
    }

    public void setNormalMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при установлении нормального режима базы данных!");
        }
    }

    public void rollback() {
        try {
            if (connection == null) throw new SQLException();
            connection.rollback();
        } catch (SQLException exception) {
            logger.severe("Ошибка при возврате исходного состояния базы данных!");
        }
    }

    public void commit() {
        try {
            if (connection == null) throw new SQLException();
            connection.commit();
        } catch (SQLException exception) {
            logger.severe("Ошибка при подтверждении нового состояния базы данных!");
        }
    }

    public PreparedStatement getPrepStat(String sqlStat, boolean flag) throws SQLException {
        PreparedStatement preparedStatement;
        try {
            if (connection == null) throw new SQLException();
            int autoGeneratedKeys = flag ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
            preparedStatement = connection.prepareStatement(sqlStat, autoGeneratedKeys);
            logger.info("SQL запрос '" + sqlStat + "' готов.");
            return preparedStatement;
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при составлении SQL запроса '" + sqlStat + "'.");
            if (connection == null) logger.severe("Соединение с базой данных не установлено или потеряно!");
            throw new SQLException(exception);
        }
    }

    public void closePrepStat(PreparedStatement sqlStat) {
        if (sqlStat == null) return;
        try {
            sqlStat.close();
            logger.info("SQL запрос '" + sqlStat + "' успешно закрыт.");
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при закрытии SQL запроса '" + sqlStat + "'.");
        }
    }
}
