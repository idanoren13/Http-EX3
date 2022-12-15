package server.exceptions;

public class NotEnoughArgumentsException extends Exception {
    private int numberOfRequiredArguments;
    public NotEnoughArgumentsException(String message) {
        super(message);
    }

    public NotEnoughArgumentsException(String message, int numberOfRequiredArguments) {
        super(message);
        this.numberOfRequiredArguments = numberOfRequiredArguments;
    }

    public int getNumberOfRequiredArguments() {
        return numberOfRequiredArguments;
    }
}
