package common.exceptions;


public class IncorrectInputException extends Exception {
    public IncorrectInputException(String message) {
        super(message);
    }

    public IncorrectInputException() {
        super("Неверно введены данные!");
    }
}
