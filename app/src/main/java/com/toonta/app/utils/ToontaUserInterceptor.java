/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.utils;

import android.content.Context;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.forms.ToontaUser;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [17/06/2016]
 */
public class ToontaUserInterceptor {
    public interface ToontaUserViewUpdater {
        void onToontaUserGet(ToontaUser toontaUser);
        void onFailure(String error);
    }

    ToontaUserViewUpdater toontaUserViewUpdater;
    Context context;

    public ToontaUserInterceptor(Context context, ToontaUserViewUpdater toontaUserViewUpdater) {
        this.toontaUserViewUpdater = toontaUserViewUpdater;
        this.context = context;
    }

    public void fetchToontaUser(String userId) {
        ToontaDAO.getToontaUser(userId, new ToontaDAO.ToontaUserNetworkCallInterface() {
            @Override
            public void onSuccess(ToontaUser toontaUser) {
                toontaUserViewUpdater.onToontaUserGet(toontaUser);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                toontaUserViewUpdater.onFailure(getDescriptionForError(error));
            }
        });
    }

    //Own methods

    private String getDescriptionForError(ToontaDAO.NetworkAnswer networkAnswer) {
        if (networkAnswer == ToontaDAO.NetworkAnswer.AUTH_FAILURE) {
            return context.getString(R.string.string_error_auth_failure);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.NO_SERVER) {
            return context.getString(R.string.string_error_no_server);
        } else {
            return context.getString(R.string.string_error_no_network);
        }
    }
}
