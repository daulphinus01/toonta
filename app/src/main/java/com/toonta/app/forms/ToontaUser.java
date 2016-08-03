/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.forms;

import com.toonta.app.model.ToontaAddress;
import com.toonta.app.model.ToontaBank;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [16/06/2016]
 */
public class ToontaUser {

    public ToontaUser() {
    }

    public ToontaUser(String birthdate, String email, String firstname, String id, String lastname, String name, String phoneNumber, String profession, String sexe) {
        this.birthdate = birthdate;
        this.email = email;
        this.firstname = firstname;
        this.id = id;
        this.lastname = lastname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profession = profession;
        this.sexe = sexe;
        this.address = address;
        this.bank_ = bank_;
    }

    public String birthdate = "";
    public String email = "";
    public String firstname = "";
    public String id = "";
    public String lastname = "";
    public String name = "";
    // Obligatoire a tout les coups
    public String phoneNumber;
    public String profession = "";
    public String sexe = "";

    public ToontaAddress address = new ToontaAddress();
    public ToontaBank bank_ = new ToontaBank();

    @Override
    public String toString() {
        return "User{" +
                "birthdate='" + birthdate + '\'' +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", id='" + id + '\'' +
                ", lastname='" + lastname + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profession='" + profession + '\'' +
                ", sexe='" + sexe + '\'' +
                ", address=" + address +
                ", bank_=" + bank_ +
                '}';
    }
}
