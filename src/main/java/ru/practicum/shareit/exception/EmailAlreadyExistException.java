package ru.practicum.shareit.exception;

public class EmailAlreadyExistException extends RuntimeException {
    private static final String EMAIL_EXIST = "User with this email '%s' already exist.";

    public EmailAlreadyExistException(String email) {
        super(String.format(EMAIL_EXIST, email));
    }
}
