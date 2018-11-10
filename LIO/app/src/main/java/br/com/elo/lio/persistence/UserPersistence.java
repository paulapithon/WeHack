package br.com.elo.lio.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.elo.lio.model.User;

/**
 * LG ELECTRONICS INC.
 * LGEBR - SCA R&D, Sao Paulo, SP, Brazil
 * LGEMR - Santa Clara, CA, USA
 * Copyright(c) 2017 by LG Electronics Inc.
 * <p>
 * <p>
 * All rights reserved. No part of this work may be reproduced, stored in a retrieval system, or transmitted by any
 * means without prior written Permission of LG Electronics Inc.
 */
public class UserPersistence {

    private static String APP_PREFERENCES = "elo.UserPersistence";
    private static String USER_LIST = "elo.users";

    private static SharedPreferences sPreferences;

    public static void newInstance (Context context) {
        if (sPreferences == null) {
            sPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        }
    }

    public static User getUser(String ID) {
        String users = sPreferences.getString(USER_LIST, "[]");

        try {
            JSONArray array = new JSONArray(users);
            for (int i = 0; i < array.length(); i++) {
                User user = new User();
                user.encode(array.getString(i));
                if (user.getID().equals(ID)) {
                    return user;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getAllUsers() {
        String users = sPreferences.getString(USER_LIST, "[]");
        try {
            JSONArray array = new JSONArray(users);
            List<User> userArray = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                User user = new User();
                user.encode(array.getString(i));
                userArray.add(user);
            }
            return userArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addUser(User user) {
        String users = sPreferences.getString(USER_LIST, "[]");

        try {
            JSONArray array = new JSONArray(users);
            array.put(user.decode());

            sPreferences
                    .edit()
                    .putString(USER_LIST, array.toString())
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
