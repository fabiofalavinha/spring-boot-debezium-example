package com.faf.student.utils;

public enum Operation {

    READ("r"),
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String code;

    Operation(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public static Operation forCode(String code) {
        final Operation[] operations = values();

        for (final Operation operation : operations) {
            if (operation.code().equalsIgnoreCase(code)) {
                return operation;
            }
        }

        return null;
    }
}