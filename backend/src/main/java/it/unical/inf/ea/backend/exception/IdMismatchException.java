package it.unical.inf.ea.backend.exception;

public class IdMismatchException extends RuntimeException{
    public IdMismatchException(String s) {
        super("Id mismatch");
    }
}
