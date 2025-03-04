package ru.anonymus.simplemessenger.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecureStorage {

    private static final String PREFS_NAME = "secure_prefs";
    private SharedPreferences sharedPreferences;

    public SecureStorage(Context context){
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        }catch (GeneralSecurityException | IOException e){
            Log.e("SECURITY", e.getMessage());
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString("auth_token", token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString("auth_token", null);
    }

    public void clearToken() {
        sharedPreferences.edit().remove("auth_token").apply();
    }

}
