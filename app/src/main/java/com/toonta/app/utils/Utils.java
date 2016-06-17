package com.toonta.app.utils;

import com.toonta.app.ToontaDAO;
import com.toonta.app.model.Bank;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String computeBanksTotalToons(ArrayList<ToontaDAO.SurveysListAnswer.SurveyElement> bankList) {
        assert bankList != null;
        int total = 0;
        for (ToontaDAO.SurveysListAnswer.SurveyElement bank : bankList) {
            total += bank.reward;
        }
        return total + " toons";
    }

    public static boolean bothPwdHaveToBeTheSame(String pwd, String pwdConfrm) {
        return pwd.equals(pwdConfrm);
    }
}
