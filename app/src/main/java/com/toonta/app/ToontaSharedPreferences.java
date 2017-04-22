package com.toonta.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.toonta.app.model.UserMode;
import static com.toonta.app.utils.ToontaConstants.DEFAULT_NBR_SURVEYS;

/**
 * @since 31/03/2017 : MAJ pour ajouter l'état des notifications
 *
 * Created by Guillaume on 22/05/2016.
 */
public class ToontaSharedPreferences extends Application{

    private static String TAG = "ToontaSharedPreferences";




    public String userId;
    private Context context;
    public String requestToken;
    public String profilePicPath;
    private static String USER_ID = "USER_ID";
    private static String LOGGED_IN = "LOGGED_IN";
    private static String REQUEST_TOKEN = "REQUEST_TOKEN";

    private static String TOONTA_USER_MODE = "toonta_user_mode_int_000999";
    private static String TOONTA_PROFILE_PIC_PATH = "toonta_profile_picture_path";
    private static String FIRST_USE_SLIDER_VALIDATION = "FIRST_USE_SLIDER_VALIDATION";
    private final static String TOONTA_NUMBER_SURVEYS = "TOONTA_NUMBER_SURVEYS_99999998";
    private final static String TOONTA_STATE_NOTIF_PREF = "TOONTA_STATE_NOTIF_PREF_99999999";

    public static ToontaSharedPreferences toontaSharedPreferences;


    public static void init(Context context) {
        toontaSharedPreferences = new ToontaSharedPreferences();
        toontaSharedPreferences.context = context;
        if (isLoggedIn()) {
            toontaSharedPreferences.loadUserInformations();
        }
    }

    public static void validateFirstUseSlider() {
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(FIRST_USE_SLIDER_VALIDATION, true);
        editor.apply();
    }

    public static void setToontaProfilePicPath(String path) {
        toontaSharedPreferences.profilePicPath = path;
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putString(TOONTA_PROFILE_PIC_PATH, path);
        editor.apply();
    }

    public static boolean isFistUseSliderValidated() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIRST_USE_SLIDER_VALIDATION, false);
    }

    public static void validateLoggedIn(String token, String userId) {
        toontaSharedPreferences.requestToken = token;
        toontaSharedPreferences.userId = userId;
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOGGED_IN, true);
        editor.putString(REQUEST_TOKEN, token);
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    private void loadUserInformations() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        toontaSharedPreferences.requestToken = sharedPreferences.getString(REQUEST_TOKEN, "");
        toontaSharedPreferences.userId = sharedPreferences.getString(USER_ID, "");
    }

    public static void logOut() {
        toontaSharedPreferences.requestToken = "";
        toontaSharedPreferences.userId = "";
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOGGED_IN, false);
        editor.putString(REQUEST_TOKEN, null);
        editor.putString(USER_ID, null);
        editor.putInt(TOONTA_USER_MODE, UserMode.USER.getMode());
        editor.apply();
        ToontaDAO.isAsFriendUserLogged = false;

        // Suppression du path de la photo
        clearProfilePicPathInPreference();

        // Réinitialisation de l'état des notifications
        initNotificationsState();
    }

    /**
     * Met à jour le booléen permettant de savoir si les notifications sont activées ou pas dans
     * cette application
     */
    public static void updateNotificationsState() {
        boolean stateNotif = !getNotificationsState();
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TOONTA_STATE_NOTIF_PREF, stateNotif);
        editor.apply();
    }

    /**
     * Récupère l'état des notification
     *
     * @return true si les notifications sont activées, false sinon
     */
    public static boolean getNotificationsState() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(TOONTA_STATE_NOTIF_PREF, false);
    }

    public static int getSharedPreferencesSurveysNbr() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TOONTA_NUMBER_SURVEYS, DEFAULT_NBR_SURVEYS);
    }

    public static void setSharedPreferencesSurveysNbr(int nbrSurveys) {
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putInt(TOONTA_NUMBER_SURVEYS, nbrSurveys);
        editor.apply();
    }

    private static void clearProfilePicPathInPreference() {
        SharedPreferences.Editor editorPF = toontaSharedPreferences.context.getSharedPreferences("ToontaProfileActivity", Context.MODE_PRIVATE).edit();
        editorPF.putString(ToontaSharedPreferences.toontaSharedPreferences.userId + "profile_pic_name", null);
        editorPF.apply();
    }

    public static void setToontaUserMode(UserMode userMode) {
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putInt(TOONTA_USER_MODE, userMode.getMode());
        editor.apply();
    }

    /**
     * Retourne le mode de l'utilisateur
     * @return -1 si mode user
     *          1 si mode surveyor
     */
    public static int getUserMode() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TOONTA_USER_MODE, -1);
    }

    private static void initNotificationsState() {
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TOONTA_STATE_NOTIF_PREF, false);
        editor.apply();
    }
}
