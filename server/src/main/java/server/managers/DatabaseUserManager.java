package server.managers;

import common.actions.User;
import common.exceptions.DatabaseHandlingException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseUserManager {
    // USER_TABLE
    private final String SELECT_USER_BY_ID = "SELECT * FROM " + DatabaseConnectionManager.USER_TABLE +
            " WHERE " + DatabaseConnectionManager.USER_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_USER_BY_USERNAME = "SELECT * FROM " + DatabaseConnectionManager.USER_TABLE +
            " WHERE " + DatabaseConnectionManager.USER_TABLE_USERNAME_COLUMN + " = ?";
    private final String SELECT_USER_BY_USERNAME_AND_PASSWORD = SELECT_USER_BY_USERNAME + " AND " +
            DatabaseConnectionManager.USER_TABLE_PASSWORD_COLUMN + " = ?";
    private final String INSERT_USER = "INSERT INTO " +
            DatabaseConnectionManager.USER_TABLE + " (" +
            DatabaseConnectionManager.USER_TABLE_USERNAME_COLUMN + ", " +
            DatabaseConnectionManager.USER_TABLE_PASSWORD_COLUMN + ") VALUES (?, ?)";

    private static final Logger logger = Logger.getLogger(DatabaseUserManager.class.getName());
    private DatabaseConnectionManager dbConnectionManager;

    public DatabaseUserManager(DatabaseConnectionManager dbConnection) {
        this.dbConnectionManager = dbConnection;
    }

    public DatabaseUserManager(String url, String login, String password) {
        this.dbConnectionManager = new DatabaseConnectionManager(url, login, password);
    }

    public User getUserById(long userId) throws SQLException {
        User user;
        PreparedStatement prepStat = null;
        try {
            prepStat =
                    dbConnectionManager.getPrepStat(SELECT_USER_BY_ID, false);
            prepStat.setLong(1, userId);
            ResultSet resultSet = prepStat.executeQuery();
            logger.info("Выполнен запрос SELECT_USER_BY_ID.");
            if (resultSet.next()) {
                user = new User(
                        resultSet.getString(DatabaseConnectionManager.USER_TABLE_USERNAME_COLUMN),
                        resultSet.getString(DatabaseConnectionManager.USER_TABLE_PASSWORD_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при исполнении запроса SELECT_USER_BY_ID!");
            throw new SQLException(exception);
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
        return user;
    }

    public boolean checkUsernameAndPassword(User user) throws DatabaseHandlingException {
        PreparedStatement prepStat= null;
        try {
            prepStat =
                    dbConnectionManager.getPrepStat(SELECT_USER_BY_USERNAME_AND_PASSWORD, false);
            prepStat.setString(1, user.getUsername());
            prepStat.setString(2, user.getPassword());
            ResultSet resultSet = prepStat.executeQuery();
            logger.info("Выполнен запрос SELECT_USER_BY_USERNAME_AND_PASSWORD.");
            return resultSet.next();
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при выполнении запроса SELECT_USER_BY_USERNAME_AND_PASSWORD!");
            throw new DatabaseHandlingException();

        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }

    public int getUserIdByUsername(User user) throws DatabaseHandlingException {
        PreparedStatement prepStat = null;
        try {
            prepStat =
                    dbConnectionManager.getPrepStat(SELECT_USER_BY_USERNAME, false);
            prepStat.setString(1, user.getUsername());
            ResultSet resultSet = prepStat.executeQuery();
            logger.info("Выполнен запрос SELECT_USER_BY_USERNAME.");
            if (resultSet.next()) {
                return resultSet.getInt(DatabaseConnectionManager.USER_TABLE_ID_COLUMN);
            } else return -1;
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при выполнении запроса SELECT_USER_BY_USERNAME!");
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }

    public boolean insertUser(User user) throws DatabaseHandlingException {
        PreparedStatement prepStat = null;
        try {
            if (getUserIdByUsername(user) != -1) return false;
            prepStat =
                    dbConnectionManager.getPrepStat(INSERT_USER, false);
            prepStat.setString(1, user.getUsername());
            prepStat.setString(2, user.getPassword());
            if (prepStat.executeUpdate() == 0) throw new SQLException();
            logger.info("Выполнен запрос INSERT_USER.");
            return true;
        } catch (SQLException exception) {
            logger.severe("Произошла ошибка при выполнении запроса INSERT_USER!");
            throw new DatabaseHandlingException();
        } finally {
            dbConnectionManager.closePrepStat(prepStat);
        }
    }
}
