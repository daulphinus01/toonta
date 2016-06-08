package com.toonta.app.utils;

import com.toonta.app.model.Bank;

import java.util.List;

public class Utils {

    public static String computeBanksTotalToons(List<Bank> bankList) {
        assert bankList != null;
        int total = 0;
        for (Bank bank : bankList) {
            total += bank.getTotalToons();
        }
        return total + " toons";
    }
}
