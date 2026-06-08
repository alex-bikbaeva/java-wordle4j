package ru.yandex.practicum;

public class GameAlreadyFinishedException extends RuntimeException {
    public GameAlreadyFinishedException(String message) {
        super(message);
    }
}