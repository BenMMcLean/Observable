package cl.benm.observable.helpers;

public class IllegalExceptionOrValueException extends IllegalArgumentException {

    public static final String MESSAGE = "Value provided to %s must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!";

    String function;

    public IllegalExceptionOrValueException(String function) {
        super(String.format(MESSAGE, function));
    }

    public IllegalExceptionOrValueException(String function, Throwable cause) {
        super(String.format(MESSAGE, function), cause);
    }

    public IllegalExceptionOrValueException() {
        super(String.format(MESSAGE, "function"));
    }

}
