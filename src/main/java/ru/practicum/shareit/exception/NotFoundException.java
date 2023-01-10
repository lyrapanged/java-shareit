package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    private final static String ENTITY_NOT_EXIST = "This '%s' does not exist";

    public NotFoundException(String entity) {
        super(String.format(ENTITY_NOT_EXIST, entity));
    }
}
