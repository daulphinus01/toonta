/*
 * Copyright - Toonta - All Rights Reserved www.heebari.com
 */
package com.toonta.app.utils;

import android.content.Context;

import com.toonta.app.R;
import com.toonta.app.ToontaDAO;
import com.toonta.app.forms.ToontaUser;

import java.util.List;

/**
 * @author Marcellin RWEGO
 * @since 1.0.0 [17/06/2016]
 */
public class ToontaAddressInterceptor {
    public interface ToontaAddressViewUpdater {
        void onToontaAddressGet(List<String> cities);
        void onFailure(String error);
    }

    ToontaAddressViewUpdater toontaAddressViewUpdater;
    Context context;

    public ToontaAddressInterceptor(Context context, ToontaAddressViewUpdater toontaAddressViewUpdater) {
        this.toontaAddressViewUpdater = toontaAddressViewUpdater;
        this.context = context;
    }




    public void updateToontaCitiesAdaptor(String country) {
        ToontaDAO.getCitiesByCountryName(country, new ToontaDAO.AddressNetworkCallInterface() {
            @Override
            public void onSuccess(List<String> cities) {
                toontaAddressViewUpdater.onToontaAddressGet(cities);
            }

            @Override
            public void onFailure(ToontaDAO.NetworkAnswer error) {
                toontaAddressViewUpdater.onFailure(getDescriptionForError(error));
            }
        });
    }

    //Own methods

    private String getDescriptionForError(ToontaDAO.NetworkAnswer networkAnswer) {
        if (networkAnswer == ToontaDAO.NetworkAnswer.AUTH_FAILURE) {
            return context.getString(R.string.string_error_auth_failure);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.NO_SERVER) {
            return context.getString(R.string.string_error_no_server);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.FAILED_UPDATING) {
            return "Failed to update modified fields";
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.FORBIDDEN) {
            return context.getString(R.string.no_modif_wright);
        } else if (networkAnswer == ToontaDAO.NetworkAnswer.OK_UPDATING) {
            return context.getString(R.string.profile_change_success);
        } else {
            return context.getString(R.string.string_error_no_network);
        }
    }
}
