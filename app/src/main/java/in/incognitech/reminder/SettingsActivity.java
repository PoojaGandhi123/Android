package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;

import in.incognitech.reminder.util.Utils;

public class SettingsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if ( ! Utils.isUserLoggedIn(this) ) {
            redirectToLogin();
        }

        super.onCreate(savedInstanceState);

        this.customSetup(R.layout.activity_settings, R.id.settings_toolbar, R.id.settings_nav_view);
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
