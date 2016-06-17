package com.toonta.app.activities.login;

import android.content.Context;


import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.activities.signup.SignupInteractor;

/**
 * Created by Guillaume on 21/05/2016.
 */
public class LoginInteractor {

    public class LoginViewModel {
        public String emailError;
        public String passwordError;

        public LoginViewModel() {
            this.emailError = "";
            this.passwordError = "";
        }
    }

    public interface LoginViewUpdater {
        void updateView(LoginViewModel loginViewModel);
        void onLoginSuccess();
        void onLoginProgress();
        void onLoginFailure(String errorToDisplay);
    }

    LoginViewUpdater loginViewUpdater;
    Context context;

    String displayedPhoneNumber = "";
    String displayedPassword = "";

    public LoginInteractor(Context context, LoginViewUpdater loginViewUpdater) {
        this.context = context;
        this.loginViewUpdater = loginViewUpdater;
    }

    //Used by activity

    public void submitLogin(String phoneNumber, String password) {
        displayedPhoneNumber = phoneNumber;
        displayedPassword = password;
        LoginViewModel loginViewModel = generateViewModel();
        if (loginViewModel.passwordError.equals("") && loginViewModel.emailError.equals("")) {
            loginViewUpdater.onLoginProgress();
            ToontaDAO.login(phoneNumber, password, new ToontaDAO.SimpleNetworkCallInterface() {
                @Override
                public void onSuccess() {
                    loginViewUpdater.onLoginSuccess();
                }

                @Override
                public void onFailure(ToontaDAO.NetworkAnswer error) {
                    if (error == ToontaDAO.NetworkAnswer.FAILED_LOGIN) {
                        loginViewUpdater.onLoginFailure(context.getString(R.string.string_login_activity_failed_login));
                    } else if (error == ToontaDAO.NetworkAnswer.NO_SERVER) {
                        loginViewUpdater.onLoginFailure(context.getString(R.string.string_error_no_server));
                    } else {
                        loginViewUpdater.onLoginFailure(context.getString(R.string.string_error_no_network));
                    }
                }
            });
        } else {
            loginViewUpdater.updateView(loginViewModel);
        }
    }

    //Own methods

    private LoginViewModel generateViewModel() {
        LoginViewModel loginViewModel = new LoginViewModel();
        loginViewModel.emailError = SignupInteractor.validatePhoneNumber(displayedPhoneNumber) ? "" : context.getString(R.string.string_sign_up_activity_phone_number_error);
        loginViewModel.passwordError = LoginInteractor.validatePassword(displayedPassword) ? "" : context.getString(R.string.string_login_activity_password_error);
        return  loginViewModel;
    }

    public static boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePassword(String password) {
        return !(password.isEmpty() || password.length() < 4);
    }
}
