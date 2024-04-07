package main.customExceptions;

public class TaskIntersectedException extends RuntimeException {
    public TaskIntersectedException() {
    }

    public TaskIntersectedException(final String message) {
        super(message);
    }

    public TaskIntersectedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskIntersectedException(final Throwable cause) {
        super(cause);
    }
}