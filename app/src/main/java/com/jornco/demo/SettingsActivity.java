package com.jornco.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.jornco.controller.IronbotRule;
import com.jornco.controller.IronbotSearcher;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String uuid_read = sharedPreferences.getString(key, "0000fff4");
        final String uuid_writer = sharedPreferences.getString(key, "0000fff1");
        if (key.startsWith("uuid")) {
            new IronbotSearcher().setRule(new IronbotRule() {
                @Override
                public boolean isRead(String uuid) {
                    return uuid.startsWith(uuid_read);
                }

                @Override
                public boolean isWrite(String uuid) {
                    return uuid.startsWith(uuid_writer);
                }
            });
        }

    }
}
