package common.exceptions;

public class EmptyNameException extends Exception {
    @Override
    public String toString() {
        return "Имя не может быть пустым!";
    }
}
