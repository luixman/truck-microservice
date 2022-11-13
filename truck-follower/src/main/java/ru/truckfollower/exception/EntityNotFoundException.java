package ru.truckfollower.exception;

public class EntityNotFoundException extends Exception{
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
