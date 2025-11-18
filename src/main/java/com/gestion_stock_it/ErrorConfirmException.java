package com.gestion_stock_it;

import java.util.ArrayList;
import java.util.List;

public class ErrorConfirmException extends RuntimeException {
    private final List<String> messages;

    public ErrorConfirmException(String message) {
        super(message);
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    public ErrorConfirmException(List<String> messages) {
        super(String.join(", ", messages));
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
