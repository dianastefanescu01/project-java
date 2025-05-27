package org.example.utils;

public class ServerException extends RuntimeException {
    public ServerException(){}
    public ServerException(String message, Throwable cause) {super(message, cause);}
    public ServerException(String message) {
        super(message);
    }
}
