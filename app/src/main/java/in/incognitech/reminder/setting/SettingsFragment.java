package in.incognitech.reminder.setting;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import in.incognitech.reminder.R;
import in.incognitech.reminder.service.ContactsProcessor;

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        Preference syncContacts = findPreference(getString(R.string.key_sync_contacts_settings));
        syncContacts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(SettingsFragment.this.getActivity(), "Sync process started.", Toast.LENGTH_LONG).show();
                Intent contactProcessorService = new Intent(SettingsFragment.this.getActivity(), ContactsProcessor.class);
                contactProcessorService.putExtra("context", "settings");
                SettingsFragment.this.getActivity().startService(contactProcessorService);
                return false;
            }
        });
    }

}
