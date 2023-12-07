package customExceptions;

public class ManagerReadException extends RuntimeException {
    public ManagerReadException() {
    }

    public ManagerReadException(final String message) {
        super(message);
    }

    public ManagerReadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ManagerReadException(final Throwable cause) {
        super(cause);
    }
}
