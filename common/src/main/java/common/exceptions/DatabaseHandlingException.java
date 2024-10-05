package common.exceptions;

/**
 * Выбрасывается при возникновении ошибки при обработке базы данных
 */
public class DatabaseHandlingException extends Exception{
    @Override
    public String toString() {
        return "Ошибка при работе с базой данных!";
    }
}
