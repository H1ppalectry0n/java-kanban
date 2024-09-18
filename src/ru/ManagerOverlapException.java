package ru;

public class ManagerOverlapException extends RuntimeException {
    public ManagerOverlapException(String message) {
        super(message);
    }

    public ManagerOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

}
