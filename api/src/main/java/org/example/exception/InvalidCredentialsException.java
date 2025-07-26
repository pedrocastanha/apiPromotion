package org.example.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("Credenciais inválidas"); }
}