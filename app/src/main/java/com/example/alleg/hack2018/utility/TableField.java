package com.example.alleg.hack2018.utility;

// boilerplate for nothing more than a tuple
public class TableField {
    private String fieldName;
    private String type;
    private String[] modifiers;

    public TableField(String f, String t, String[] m) {
        fieldName = f;
        type = t;
        modifiers = m;
    }

    public TableField(String f, String t) {
        fieldName = f;
        type = t;
        modifiers = new String[] {};
    }

    String getSchemaStatement() {
        String statement = fieldName + " " + type;

        for (String mod : modifiers) {
            statement += " " + mod;
        }

        return statement;
    }
}