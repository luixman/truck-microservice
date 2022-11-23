package ru.telegrambot.exception;

public class EntityNotFoundException extends Exception{
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
