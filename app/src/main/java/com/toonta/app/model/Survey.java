/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.model;


/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [06/06/2016]
 */
public class Survey {

    private String title;
    private String plusIcon;

    public Survey() {
    }

    public Survey (String title, String plusIcon) {
        this.title = title;
        this.plusIcon = plusIcon;
    }

    public String getTitle() {
        return title;
    }

    public String getPlusIcon() {
        return plusIcon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlusIcon(String plusIcon) {
        this.plusIcon = plusIcon;
    }
}
