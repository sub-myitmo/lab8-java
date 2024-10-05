package common.exceptions;

public class NotWithinEstablishedLimitsException extends Exception {
    @Override
    public String toString() {
        return "вышел за установленные пределы!";
    }
}
