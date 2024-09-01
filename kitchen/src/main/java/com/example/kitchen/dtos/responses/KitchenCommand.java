package com.example.kitchen.dtos.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KitchenCommand {
    WAIT("OK, WAIT"),
    CLOSED("CLOSED, BYE"),
    SERVED("SERVED, BYE"),
    CHICKEN("CHICKEN READY"),
    BEEF("BEEF READY"),
    LAST_MONTH("LAST MONTH MAMMOTH READY");

    private final String message;

    public static KitchenCommand fromMessage(String message) {
        for (KitchenCommand command : KitchenCommand.values()) {
            if (command.message.equalsIgnoreCase(message)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Unknown command: " + message);
    }

}
