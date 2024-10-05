package common.exceptions;

public class UserAlreadyExistsException extends Exception{
    @Override
    public String toString() {
        return "Пользователь с таким именем уже существует!";
    }
}
