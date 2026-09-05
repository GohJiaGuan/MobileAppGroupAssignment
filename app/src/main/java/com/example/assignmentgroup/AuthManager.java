package com.example.assignmentgroup;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_USERS = "users_json";
    private static final String KEY_CURRENT_EMAIL = "current_email";
    private static final String KEY_CURRENT_NAME = "current_name";

    private final SharedPreferences prefs;

    public AuthManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean register(String name, String email, String password) {
        JSONArray users = loadUsers();
        if (findUser(users, email) != null) return false;

        try {
            JSONObject user = new JSONObject();
            user.put("name", name);
            user.put("email", email);
            user.put("passwordHash", hash(password));
            users.put(user);
            prefs.edit().putString(KEY_USERS, users.toString()).apply();
            setCurrentUser(name, email);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public boolean login(String email, String password) {
        JSONArray users = loadUsers();
        JSONObject user = findUser(users, email);
        if (user == null) return false;

        String storedHash = user.optString("passwordHash", "");
        if (!storedHash.equals(hash(password))) return false;

        setCurrentUser(user.optString("name", ""), email);
        return true;
    }

    public void logout() {
        prefs.edit().remove(KEY_CURRENT_EMAIL).remove(KEY_CURRENT_NAME).apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_CURRENT_EMAIL);
    }

    public String getCurrentUserName() {
        return prefs.getString(KEY_CURRENT_NAME, "");
    }

    public String getCurrentUserEmail() {
        return prefs.getString(KEY_CURRENT_EMAIL, "");
    }

    private void setCurrentUser(String name, String email) {
        prefs.edit()
                .putString(KEY_CURRENT_NAME, name)
                .putString(KEY_CURRENT_EMAIL, email)
                .apply();
    }

    private JSONArray loadUsers() {
        String json = prefs.getString(KEY_USERS, null);
        if (json == null) return new JSONArray();
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    private JSONObject findUser(JSONArray users, String email) {
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.optJSONObject(i);
            if (user != null && email.equalsIgnoreCase(user.optString("email", ""))) {
                return user;
            }
        }
        return null;
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return value;
        }
    }
}
