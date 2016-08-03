/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;/**
 * Created by Kevin on 27/07/2016.
 */

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [27/07/2016]
 */
public class ToontaAddress {
    public String city;
    public String country;
    public String department;
    public String region;

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", department='" + department + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
