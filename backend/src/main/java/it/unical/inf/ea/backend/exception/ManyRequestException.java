package it.unical.inf.ea.backend.exception;

public class ManyRequestException extends RuntimeException  {
    public ManyRequestException(String message) {
        super(message);
    }
}
