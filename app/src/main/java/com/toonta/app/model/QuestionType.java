/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [08/06/2016]
 */
public enum QuestionType {
    BASIC("BASIC"), YES_NO("YES_NO"), MULTIPLE_CHOICE("MULTIPLE_CHOICE");

    private String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String getValueStr() {
        return value;
    }
}
