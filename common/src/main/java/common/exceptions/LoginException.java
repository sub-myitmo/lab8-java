package common.exceptions;

public class LoginException extends Exception{
    @Override
    public String toString() {
        return "Неправильные имя пользователя или пароль, такого пользователя нет среди имеющихся!";
    }
}
