package in.incognitech.reminder;

import android.os.Bundle;

public class SettingsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_settings, R.id.settings_toolbar, R.id.settings_nav_view);
    }
}
