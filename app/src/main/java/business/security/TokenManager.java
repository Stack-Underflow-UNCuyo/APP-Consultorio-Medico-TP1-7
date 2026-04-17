package business.security;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_ROLE  = "user_role";
    private static final String KEY_EMAIL = "user_email";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String role, String email) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}