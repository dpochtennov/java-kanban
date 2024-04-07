package main.customExceptions;

public class NoSubTaskEpicException extends RuntimeException {
    public NoSubTaskEpicException() {
    }

    public NoSubTaskEpicException(final String message) {
        super(message);
    }

    public NoSubTaskEpicException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoSubTaskEpicException(final Throwable cause) {
        super(cause);
    }
}
