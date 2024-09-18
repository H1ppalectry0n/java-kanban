package ru.http;

public class ResponseErrorMessage {
    final Error error;

    public ResponseErrorMessage(final int code, final String message) {
        error = new Error(code, message);
    }
}

record Error(int code, String message) {
}