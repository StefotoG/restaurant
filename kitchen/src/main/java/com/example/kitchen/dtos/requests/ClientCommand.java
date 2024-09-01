package com.example.kitchen.dtos.requests;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClientCommand {
    HUNGRY("I AM HUNGRY, GIVE ME BBQ"),
    NO_THANKS("NO THANKS"),
    I_TAKE_THAT("I TAKE THAT!!!");

    private final String value;

    public static ClientCommand fromValue(String value) {
        for (ClientCommand command : ClientCommand.values()) {
            if (command.value.equalsIgnoreCase(value)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Unknown command: " + value);
    }
}
