package br.com.compass.ms_event_management.exception;

public class EventCannotBeDeletedException extends RuntimeException {
    public EventCannotBeDeletedException(String message) {
        super(message);
    }
}
