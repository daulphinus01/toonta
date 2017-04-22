package com.toonta.app.model;

/**
 * Enumération du mode user
 *
 * Created by Marcellin RWEGO on 19/04/2017.
 */

public enum UserMode {
    USER(-1), SURVEYOR(1);

    private int mode;

    UserMode(int i) {
        this.mode = i;
    }

    public int getMode() {
        return mode;
    }
}
