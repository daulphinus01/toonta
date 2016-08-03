/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [27/07/2016]
 */
public class ToontaBank {
    public int balance = 0;
    public String id;

    @Override
    public String toString() {
        return "Bank_{" +
                "balance=" + balance +
                ", id='" + id + '\'' +
                '}';
    }
}
