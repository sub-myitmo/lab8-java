package common.exceptions;

public class ManualDatabaseEditException extends Exception{
    @Override
    public String toString() {
        return "Прямое изменение базы данных! Перезапустите клиент для избежания возможных ошибок.";
    }
}
