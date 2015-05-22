/*
 * Enable Viacam for Android, a camera based mouse emulator
 *
 * Copyright (C) 2015 Cesar Mauri Loba (CREA Software Systems)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crea_si.eviacam.service;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

/**
 * The settings activity
 */
public class SettingsActivity extends Activity {
    
    private static class ListPreferenceUpdate implements OnPreferenceChangeListener {

        private final ListPreference mListPreference;
        
        public ListPreferenceUpdate(ListPreference lp) {
            mListPreference = lp;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // Set the value as the new value
            mListPreference.setValue(newValue.toString());                    
            // Get the entry which corresponds to the current value and set as summary
            preference.setSummary(mListPreference.getEntry());
            return true;
        }
    }
    
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_fragment);
            
            /**
             * Listeners for list preference entries
             */
            ListPreference lp = (ListPreference) findPreference(Settings.KEY_DOCKING_PANEL_EDGE);
            lp.setOnPreferenceChangeListener(new ListPreferenceUpdate(lp));
            
            lp = (ListPreference) findPreference(Settings.KEY_TIME_WITHOUT_DETECTION);
            lp.setOnPreferenceChangeListener(new ListPreferenceUpdate(lp));
            
            lp = (ListPreference) findPreference(Settings.KEY_UI_ELEMENTS_SIZE);
            lp.setOnPreferenceChangeListener(new ListPreferenceUpdate(lp));
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
