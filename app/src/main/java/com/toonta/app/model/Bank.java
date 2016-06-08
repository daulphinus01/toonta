/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;

import java.io.Serializable;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [07/06/2016]
 */
public class Bank extends Survey implements Serializable {

    private int totalToons;

    private static final String TOONS = "toons";

    public Bank(String title, String plusIcon, int totalToons) {
        super(title, plusIcon);
        this.totalToons = totalToons;
    }

    public int getTotalToons() {
        return totalToons;
    }

    public void setTotalToons(int totalToons) {
        this.totalToons = totalToons;
    }

    public String print() {
        return totalToons + " " + TOONS;
    }
}
