package ru.http;

public class ResponseErrorMessage {
    int code;
    String message;

    public ResponseErrorMessage(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
