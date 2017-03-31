package com.toonta.app.activities.signup;

import android.content.Context;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.login.LoginInteractor;

/**
 * Created by Guillaume on 22/05/2016.
 */
public class SignupInteractor {

    public class SignupViewModel {
        public String phoneNumberError;
        public String passwordError;

        public SignupViewModel() {
            phoneNumberError = "";
            passwordError = "";
        }
    }

    public interface SignupViewUpdater {
        void updateView(SignupViewModel signupViewModel);
        void onSignupSuccess();
        void onSignupProgress();
        void onSignupFailure(String errorToDisplay);
    }

    SignupViewUpdater signupViewUpdater;
    Context context;

    String phoneNumberDisplayed;
    String passwordDisplayed;

    public SignupInteractor(Context context, SignupViewUpdater signupViewUpdater) {
        this.context = context;
        this.signupViewUpdater = signupViewUpdater;
    }

    //Used by activity

    public void submitSignup(String phoneNumber, String password) {
        phoneNumberDisplayed = phoneNumber;
        passwordDisplayed = password;
        SignupViewModel signupViewModel = generateViewModel();
        if (signupViewModel.phoneNumberError.equals("") && signupViewModel.passwordError.equals("")) {
            signupViewUpdater.onSignupProgress();
            ToontaDAO.signup(phoneNumberDisplayed, passwordDisplayed, new ToontaDAO.SimpleNetworkCallInterface() {
                @Override
                public void onSuccess() {
                    signupViewUpdater.onSignupSuccess();
                }

                @Override
                public void onFailure(ToontaDAO.NetworkAnswer error) {
                    if (error == ToontaDAO.NetworkAnswer.ACCOUNT_ALREADY_EXISTS) {
                        signupViewUpdater.onSignupFailure(context.getString(R.string.string_login_activity_failed_login));
                    } else if (error == ToontaDAO.NetworkAnswer.NO_SERVER) {
                        signupViewUpdater.onSignupFailure(context.getString(R.string.string_error_no_server));
                    } else {
                        signupViewUpdater.onSignupFailure(context.getString(R.string.string_error_no_network));
                    }
                }
            });
        } else {
            signupViewUpdater.updateView(generateViewModel());
        }
    }

    //Own methods

    private SignupViewModel generateViewModel() {
        SignupViewModel signupViewModel = new SignupViewModel();
        signupViewModel.phoneNumberError = validatePhoneNumber(passwordDisplayed) ? "" : context.getString(R.string.string_sign_up_activity_phone_number_error);
        signupViewModel.passwordError = LoginInteractor.validatePassword(passwordDisplayed) ? "" : context.getString(R.string.string_login_activity_password_error);
        return  signupViewModel;
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return !(phoneNumber.isEmpty() || (phoneNumber.length() < 8 && phoneNumber.length() > 12));
    }
}
