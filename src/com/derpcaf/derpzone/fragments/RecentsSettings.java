package com.derpcaf.derpzone.fragments;

import com.android.internal.logging.nano.MetricsProto;
import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.settings.R;

import java.util.Arrays;
import java.util.HashSet;

import com.android.internal.util.derpcaf.DerpcafUtils;
import com.android.settings.SettingsPreferenceFragment;

public class RecentsSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String RECENTS_COMPONENT_TYPE = "recents_component";
    private static final String RECENTS_TYPE = "recents_layout_style";
    private static final int RECENTS_COMPONENT_OREO = 1;

    private ListPreference mRecentsComponentType;
    private ListPreference mImmersiveRecents;
    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsType;
    private PreferenceCategory oreoRecentsCat;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.derpzone_recents);

        ContentResolver resolver = getActivity().getContentResolver();

        // clear all recents
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);
        oreoRecentsCat = (PreferenceCategory)findPreference("recents_ui_oreo_recents_category");
        oreoRecentsCat.setEnabled(type == RECENTS_COMPONENT_OREO);

        // oreo recents type
        mRecentsType = (ListPreference) findPreference(RECENTS_TYPE);
        int style = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_LAYOUT_STYLE, 0, UserHandle.USER_CURRENT);
        mRecentsType.setValue(String.valueOf(style));
        mRecentsType.setSummary(mRecentsType.getEntry());
        mRecentsType.setOnPreferenceChangeListener(this);

        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                resolver, Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) objValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mImmersiveRecents) {
            Settings.System.putInt(getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) objValue));
            mImmersiveRecents.setValue(String.valueOf(objValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference == mRecentsType) {
            int style = Integer.valueOf((String) objValue);
            int index = mRecentsType.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_LAYOUT_STYLE, style, UserHandle.USER_CURRENT);
            mRecentsType.setSummary(mRecentsType.getEntries()[index]);
            DerpcafUtils.showSystemUiRestartDialog(getContext());
        return true;
        } else if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) objValue);
            int index = mRecentsComponentType.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            oreoRecentsCat.setEnabled(type == RECENTS_COMPONENT_OREO);
            if (type == RECENTS_COMPONENT_OREO) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            DerpcafUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERPZONE;
    }
}
