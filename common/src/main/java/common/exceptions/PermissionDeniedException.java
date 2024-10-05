package common.exceptions;

public class PermissionDeniedException extends Exception{
    @Override
    public String toString() {
        return "Недостаточно прав!";
    }
}
