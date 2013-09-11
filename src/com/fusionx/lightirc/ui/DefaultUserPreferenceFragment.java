package com.fusionx.lightirc.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.fusionx.lightirc.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DefaultUserPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.default_user_fragment);
    }
}