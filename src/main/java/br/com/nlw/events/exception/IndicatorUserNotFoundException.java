package br.com.nlw.events.exception;

public class IndicatorUserNotFoundException extends RuntimeException {
    public IndicatorUserNotFoundException(String message) {
        super(message);
    }
}
