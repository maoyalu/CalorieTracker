package com.example.trackerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagement {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "CurrnetUserPref";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(int id, String name){
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);
    }

    public int getCurrentUserId(){
        return pref.getInt(KEY_ID, -1);
    }

    public String getCurrentUserFirstName(){
        return pref.getString(KEY_NAME, null);
    }


}
