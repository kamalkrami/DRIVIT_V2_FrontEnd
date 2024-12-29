package com.example.drivit_v2_frontend.Sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.drivit_v2_frontend.models.Users;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "userID";
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CIN = "cin";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STATUS = "status";

    public SessionManager(Context context) {
        this.context = context;
        this.usersSession = this.context.getSharedPreferences("usersLoginSession", Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String userID, String firstName, String lastName, String email, String cin, String phone, String username, String password, String status) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, userID);
        editor.putString(KEY_FIRSTNAME, firstName);
        editor.putString(KEY_LASTNAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CIN, cin);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_STATUS, status);
        editor.commit();
    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_ID, usersSession.getString(KEY_ID, null));
        userData.put(KEY_FIRSTNAME, usersSession.getString(KEY_FIRSTNAME, null));
        userData.put(KEY_LASTNAME, usersSession.getString(KEY_LASTNAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_CIN, usersSession.getString(KEY_CIN, null));
        userData.put(KEY_PHONE, usersSession.getString(KEY_PHONE, null));
        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_STATUS, usersSession.getString(KEY_STATUS, null));

        return userData;
    }

    public boolean checkLogin() {
        if (usersSession.getBoolean(IS_LOGIN, false)) {
            return true;
        }
        return false;
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }
}
